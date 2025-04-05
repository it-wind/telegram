package ru.tyatyushkin.telegram;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Utils {

    private static String readTelegramToken() {
        return System.getenv("TG_TOKEN");
    }

    private static String readYouTubeToken() {
        return System.getenv("Y_TOKEN");
    }

    private static String readWeatherToken() {
        return System.getenv("W_TOKEN");
    }

    private static String readXToken() {
        return System.getenv("X_TOKEN");
    }
    public static void checkTelegramToken() {
        if (readTelegramToken() == null) {
            LoggerConfig.logger.error("Ошибка: Задайте значение переменной TG_TOKEN");
            System.exit(1);
        }
    }

    private static void checkYouTubeToken() {
        if (readYouTubeToken() == null) {
            LoggerConfig.logger.error("Ошибка: Задайте значение переменной Y_TOKEN");
            System.exit(1);
        }
    }

    private static void checkWeatherToken() {
        if (readWeatherToken() == null) {
            LoggerConfig.logger.error("Ошибка: Задайте значение переменной W_TOKEN");
            System.exit(1);
        }
    }

    private static void checkXToken() {
        if ( readXToken() == null) {
            LoggerConfig.logger.error("");
            System.exit(1);
        }
    }
    public static void checkTelegramDir() {
        Path telegramDir = Paths.get("/opt/telegram");
        if (!Files.exists(telegramDir)) {
            try {
                Files.createDirectories(telegramDir);
                LoggerConfig.logger.info("/opt/telegram directory created.");
            } catch (IOException e) {
                LoggerConfig.logger.error("Failed to create /opt/telegram directory: ", e);
                System.exit(1);
            }
        } else {
            LoggerConfig.logger.info("/opt/telegram directory already exists.");
        }
    }

    public static void checkLastVideoIdFile() {
        Path lastVideoIdFile = Paths.get("/opt/telegram/lastVideoID.txt");
        if (!Files.exists(lastVideoIdFile)) {
            try {
                Files.createFile(lastVideoIdFile);
                LoggerConfig.logger.info("lastVideoID.txt file created.");
            } catch (IOException e) {
                LoggerConfig.logger.error("Failed to create lastVideoID.txt file: ", e);
                System.exit(1);
            }
        } else {
            LoggerConfig.logger.info("lastVideoID.txt file already exists.");
        }
    }
    private static void checkTokens() {
        checkTelegramToken();
        checkYouTubeToken();
        checkWeatherToken();
        checkXToken();
    }
    public static void init () {
        checkTokens();
        checkTelegramDir();
        checkLastVideoIdFile();
    }
}
