package managers;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Менеджер SSH-туннеля для безопасного подключения к PostgreSQL на сервере Helios.
 */
public class SshTunnelManager {
    private static final Logger logger = LoggerFactory.getLogger(SshTunnelManager.class);

    private static volatile SshTunnelManager instance;
    private static final String SSH_HOST = "helios.se.ifmo.ru";
    private static final int SSH_PORT = 2222;
    private static final String SSH_USER = "s502610";
    private static String SSH_PASSWORD = "";

    private static int LOCAL_PORT = 5433;
    private static final String REMOTE_HOST = "localhost";
    private static final int DB_PORT = 5432;

    private Session session;
    private boolean isInitialized = false;

    private SshTunnelManager() {}

    public void setSshPassword(String sshPassword) {
        SSH_PASSWORD = sshPassword;
    }

    public void setLocalPort(int localPort) {
        LOCAL_PORT = localPort;
    }

    public static SshTunnelManager getInstance() {
        SshTunnelManager localInstance = instance;
        if (localInstance == null) {
            synchronized (SshTunnelManager.class) {
                localInstance = instance;
                if (localInstance == null) {
                    instance = localInstance = new SshTunnelManager();
                }
            }
        }
        return localInstance;
    }

    public synchronized void ensureTunnel() {
        if (isInitialized && session != null && session.isConnected()) {
            try {
                session.sendKeepAliveMsg();
                return;
            } catch (Exception e) {
                logger.error("SSH сессия была активна, но проверка связи (Keep-Alive) провалилась. Причина: {}", e.getMessage());
                closeTunnel();
            }
        }
        startTunnel();
    }

    private void startTunnel() {
        try {
            logger.info("Попытка установить SSH соединение с {}:{}...", SSH_HOST, SSH_PORT);
            JSch jsch = new JSch();

            session = jsch.getSession(SSH_USER, SSH_HOST, SSH_PORT);
            session.setPassword(SSH_PASSWORD);

            Properties config = new Properties();
            config.put("StrictHostKeyChecking", "no");

            config.put("ServerAliveInterval", "30");
            config.put("ServerAliveCountMax", "3");

            session.setConfig(config);
            session.connect(15000);

            session.setPortForwardingL(LOCAL_PORT, REMOTE_HOST, DB_PORT);

            isInitialized = true;
            logger.info("SSH туннель УСПЕШНО запущен! Ссылка: localhost:{} -> {}:{}", LOCAL_PORT, SSH_HOST, DB_PORT);
        } catch (Exception e) {
            isInitialized = false;
            session = null;
            logger.error("КРИТИЧЕСКАЯ ОШИБКА: Не удалось поднять туннель! Причина: {}", e.getMessage(), e);
            throw new RuntimeException("Невозможно запустить SSH инфраструктуру для базы данных.", e);
        }
    }


    public synchronized void closeTunnel() {
        if (session != null) {
            try {
                if (session.isConnected()) {
                    session.disconnect();
                }
                logger.info("SSH туннель успешно остановлен.");
            } catch (Exception e) {
                logger.error("Ошибка при закрытии SSH сессии: {}", e.getMessage());
            } finally {
                session = null;
                isInitialized = false;
            }
        }
    }

    public int getLocalPort() {
        return LOCAL_PORT;
    }
}