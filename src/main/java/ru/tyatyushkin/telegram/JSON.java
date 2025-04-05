package ru.tyatyushkin.telegram;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;


public class JSON {
    private static final ObjectMapper objectMapper = new ObjectMapper();
    private static String mpn = "5103942483";

    private static boolean isCheck(String messages) {
        return messages != null;
    }

    private static JsonNode getResult(String messages) throws JsonProcessingException {
        if (isCheck(messages)) {
            JsonNode rootNode = objectMapper.readTree(messages);
            return rootNode.get("result");
        }
        return null;
    }

    private static ArrayNode parseMessages(JsonNode resultArray) {
        ArrayNode test = objectMapper.createArrayNode();
        for (JsonNode update : resultArray) {
            test.add(update.get("message"));
        }
        return test;
    }

    private static void testAnswer(ArrayNode test) {
        for (JsonNode jsonNode : test) {
            if (jsonNode.has("text")) {
                String text = jsonNode.get("text").asText();
                String chatId = jsonNode.get("chat").get("id").asText();
                if (text.equalsIgnoreCase("hello")) {
                    Telegram.getSendMessage(chatId, "how are you?");
                }
            }
        }
    }

    private static String getMessage(String chatId, String text) throws JsonProcessingException {
        ObjectNode message = objectMapper.createObjectNode();
        message.put("chat_id", chatId);
        message.put("text", text);
        return objectMapper.writeValueAsString(message);
    }

    private static String getMessageWithPicture(String chatId, String photoUrl, String text) throws JsonProcessingException {
        ObjectNode message = objectMapper.createObjectNode();
        message.put("chat_id", chatId);
        message.put("photo", photoUrl);
        message.put("caption", text);
        return objectMapper.writeValueAsString(message);
    }

    private static void testReplyMessage(JsonNode message) throws JsonProcessingException {
        for (JsonNode jsonNode : message) {
            if (jsonNode.has("channel_post")) {
                JsonNode channelPost = jsonNode.get("channel_post");
                if (channelPost.has("text")) {
                    String text = channelPost.get("text").asText();
                    Telegram.sendReplyMessage(getMessage(mpn, text));
                }
                if (channelPost.has("photo")) {
                    Telegram.sendPhotoWithCaption(getMessageWithPicture(mpn, "AgACAgIAAx0Cc4tUlwADIWdXM5S1SLtCo80nxo4qfA-2GS-lAAJ85DEbh_yBShn-Hq8y3M42AQADAgADeQADNgQ", "сиське"));
                }
            }
        }
    }

    private static boolean checkResult(JsonNode resultArray) {
        return resultArray != null && resultArray.isArray();
    }

    private static void setTelegramUpdateId(JsonNode resultArray) {
        for (JsonNode update : resultArray) {
            int updateId = update.get("update_id").asInt();
            Telegram.setLastUpdateId(updateId);
        }
    }

    public static void testTelegramParse(String getUpdates) throws JsonProcessingException {
        JsonNode result = getResult(getUpdates);
        if (checkResult(result)) {
            testAnswer(parseMessages(result));
            testReplyMessage(result);
            setTelegramUpdateId(result);
        }
    }
}
