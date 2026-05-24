package main;

import exceptions.PhysicalException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import utility.ConfigLoader;
import utility.HistoryParser;
import utility.loader.Loader;

public final class ServerBootstrap {
    private static final Logger logger = LoggerFactory.getLogger(ServerBootstrap.class);

    private ServerBootstrap() {
    }

    public static void init() throws PhysicalException {
        logger.info("Инициализация сервера: загрузка конфигурации и коллекции");
        String fileName = ConfigLoader.getXMLPath();
        Loader.setFileName(fileName);
        HistoryParser.parseToList();
        Loader.load();
        logger.info("Инициализация завершена, файл коллекции: {}", fileName);
    }
}
