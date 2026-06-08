package main;

import exceptions.PhysicalException;
import managers.CollectionManager;
import managers.DBManager;
import managers.SshTunnelManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.ConfigLoader;

import java.sql.SQLException;

public final class ServerBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(ServerBootstrap.class);

    private ServerBootstrap() {
    }

    /**
     * Полная инициализация всей инфраструктуры сервера перед запуском сетевого цикла.
     */
    public static void init() throws PhysicalException {
        logger.info("Инициализация сервера: загрузка конфигурации и коллекции");

        try {
            SshTunnelManager.getInstance().setSshPassword(ConfigLoader.SshPassword());
            DBManager.getInstance().setDbPassword(ConfigLoader.DBPassword());

            DBManager.getInstance().init();

            SshTunnelManager.getInstance().ensureTunnel();

            DBManager.getInstance().getConnection().close();

            CollectionManager.init();

            registerShutdownHook();

            logger.info("Инициализация завершена");

        } catch (SQLException e) {
            logger.error("Не удалось установить первичное соединение с базой данных через пул: {}", e.getMessage());
            throw new PhysicalException("Критическая ошибка инициализации пула СУБД", e);
        } catch (Exception e) {
            logger.error("Критический сбой при старте инфраструктуры сервера: {}", e.getMessage());
            throw new PhysicalException("Ошибка поднятия окружения сервера", e);
        }
    }

    private static void registerShutdownHook() {
        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            logger.info("Получен сигнал остановки (Shutdown Hook). Начинается контролируемое закрытие ресурсов...");

            try {
                DBManager.getInstance().close();
            } catch (Exception e) {
                logger.error("Ошибка при остановке пула базы данных: {}", e.getMessage());
            }
            try {
                SshTunnelManager.getInstance().closeTunnel();
            } catch (Exception e) {
                logger.error("Ошибка при уничтожении SSH-туннеля: {}", e.getMessage());
            }

            logger.info("Все системные ресурсы успешно освобождены. Сервер выключен.");
        }, "Server-Shutdown-Hook"));
    }
}