package ru.tyatyushkin.telegram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.json.JSONArray;
import org.json.JSONObject;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;


public class Telegram {
    private static final String API_URL = "https://api.telegram.org/bot";
    private static String s_token;
    private final String token;
    private static int lastUpdateId = 0;


    public Telegram(String token) {
        this.token = token;
        s_token = token;
    }

    public static void setLastUpdateId(int updateId) {
        lastUpdateId = updateId;
    }


    public String getUpdates() {
        StringBuilder content = new StringBuilder();

        try {
            URL url = new URL(API_URL + token + "/getUpdates?offset=" + (lastUpdateId + 1));
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setDoOutput(true);
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                in.close();
                conn.disconnect();
                return content.toString();
            } else {
                System.out.println("Error: " + responseCode + " - " + conn.getResponseMessage());
                conn.disconnect();
                return null;
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }

        return null;
    }

    public static void getSendMessage(String chatId, String message) {
        try {
            URL url = new URL(API_URL + s_token + "/sendMessage?chat_id=" + chatId + "&text=" + message);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.getInputStream().close();
            conn.disconnect();
        } catch (Exception e) {
            LoggerConfig.logger.error("Неправильный запрос: ", e );
        }
    }

    public static void sendReplyMessage(String message) {
        try {
            URL url = new URL(API_URL + s_token + "/sendMessage");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = message.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                LoggerConfig.logger.info("Reply Message sent successfully.");
            } else {
                LoggerConfig.logger.error("Failed to send reply message. Response Code: {}", responseCode);
            }

        } catch (Exception e) {
            LoggerConfig.logger.error("Ошибке: ", e);
        }
    }

    public void sendMessage(String chatId, String message) {
        try {
            // Create JSON
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("chat_id", chatId);
            jsonObject.put("text", message);
            jsonObject.put("disable_notification", "TRUE");
            //jsonObject.put("parse_mode", "MarkdownV2");

            // Show JSON
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonObject.toString());
            System.out.println("---==SHOW JSON==--");
            System.out.println(rootNode.toPrettyString());
            System.out.println("--==END==--");

            // Создаём URL для метода sendMessage
            URL url = new URL(API_URL + token + "/sendMessage");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Настраиваем параметры запроса
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            // Отправляем JSON
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = jsonObject.toString().getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            // Проверяем код ответа сервера
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Message sent successfully.");
            } else {
                System.out.println("Failed to send message. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public void sendPostMessage(String json) {
        try {
            URL url = new URL(API_URL + token + "/sendMessage");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();

            // Настраиваем параметры запроса
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            // Отправляем JSON
            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = json.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }



            // Проверяем код ответа сервера
            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                System.out.println("Message sent successfully.");
            } else {
                System.out.println("Failed to send message. Response Code: " + responseCode);
            }
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public void sendPhoto(String chatId, String photoUrl) {
        try {
            URL url = new URL(API_URL + token + "/sendPhoto?chat_id=" + chatId + "&photo=" + photoUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.getInputStream().close();
            conn.disconnect();
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
    }

    public static void sendPhotoWithCaption(String message) {
        try {
            URL url = new URL(API_URL + s_token + "/sendPhoto");
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setRequestProperty("Content-Type", "application/json; utf-8");
            conn.setDoOutput(true);

            try (OutputStream os = conn.getOutputStream()) {
                byte[] input = message.getBytes(StandardCharsets.UTF_8);
                os.write(input, 0, input.length);
            }

            int responseCode = conn.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                LoggerConfig.logger.info("Reply Message sent successfully.");
            } else {
                LoggerConfig.logger.error("Failed to send reply message. Response Code: ", responseCode);
            }

        } catch (Exception e) {
            LoggerConfig.logger.error("Ошибке: ", e);
        }
    }

    public void sendInlineButton(String chatId) {
        try {
        JSONObject message = new JSONObject();
        message.put("chat_id", chatId);
        message.put("text", "Привет! Нажми кнопку ниже:");

        // Создаем разметку для inline-кнопки
        JSONObject replyMarkup = new JSONObject();
        JSONArray inlineKeyboard = new JSONArray();

        // Кнопка с ссылкой
        JSONArray row = new JSONArray();
        JSONObject button = new JSONObject();
        button.put("text", "Посетить Google");
        button.put("url", "https://google.com");
        row.put(button);

        // Кнопка с callback_data
        JSONArray row1 = new JSONArray();
        JSONObject button1 = new JSONObject();
        button1.put("text", "INFO");
        button1.put("callback_data", "info");
        // вторая кнопка с callback_data
        JSONObject button2 = new JSONObject();
        button2.put("text", "VPN");
        button2.put("callback_data", "vpn");
        row1.put(button1);
        row1.put(button2);


        inlineKeyboard.put(row);
        inlineKeyboard.put(row1);
        replyMarkup.put("inline_keyboard", inlineKeyboard);
        message.put("reply_markup", replyMarkup);
        ObjectMapper objectMapper = new ObjectMapper();
        JsonNode node = objectMapper.readTree(message.toString());
        System.out.println(node.toPrettyString());
        sendPostMessage(message.toString());
        } catch (JsonProcessingException e) {
            e.printStackTrace(System.out);
        }
    }

    public void inlineButton(String chatId) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            //Создаем кнопку
            ObjectNode linkButton = objectMapper.createObjectNode();
            linkButton.put("text", "Перейти на tyatyushkin.ru");
            linkButton.put("url", "https://tyatyushkin.ru");
            // Создаем строку для кнопок
            ArrayNode buttonNode = objectMapper.createArrayNode();
            buttonNode.add(linkButton);
            // Создаем массив для кнопок
            ArrayNode inlineKeyboardArray = objectMapper.createArrayNode();
            inlineKeyboardArray.add(buttonNode);
            // Создаем корневой узел JSON - InlineKeyboardMarkup
            ObjectNode inlineKeyboardMarkup = objectMapper.createObjectNode();
            inlineKeyboardMarkup.set("inline_keyboard",inlineKeyboardArray);

            System.out.println(inlineKeyboardMarkup.toPrettyString());
        } catch (Exception e) {
            LoggerConfig.logger.error("Ошибка: {}", String.valueOf(e));
        }

    }

    public void sendReplyButton(String chatId) {
        JSONObject message = new JSONObject();
        message.put("chat_id", chatId);
        message.put("text", "test");

        JSONObject replyMarkup = new JSONObject();
        JSONArray keyboard = new JSONArray();
        JSONArray row = new JSONArray();

        JSONObject button = new JSONObject();
        JSONObject button1 = new JSONObject();
        JSONObject button2 = new JSONObject();
        button.put("text", "test");
        button1.put("text", "boobs");
        button2.put("text", "погода");
        row.put(button);
        row.put(button1);
        row.put(button2);


        keyboard.put(row);
        replyMarkup.put("keyboard", keyboard);
        replyMarkup.put("resize_keyboard", true);
        replyMarkup.put("one_time_keyboard", true);

        message.put("reply_markup", replyMarkup);

        sendPostMessage(message.toString());
    }
}
