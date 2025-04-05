package ru.tyatyushkin.telegram;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class LoggerConfig {
    public static final Logger logger = LogManager.getLogger(LoggerConfig.class);

    public static void initialize() {
        logger.info("Logger initialized");
    }
}
