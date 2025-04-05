package ru.tyatyushkin.telegram;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.json.JSONArray;
import org.json.JSONObject;
import java.util.concurrent.TimeUnit;


public class Bot {
    private final String token;
    private String chatID;
    private String w_token;
    private String y_token;
    private String channelId;
    private final Telegram telegram;


    Weather weather;

    public Bot(String token) {
        this.token = token;
        this.telegram = new Telegram(this.token);
    }

    public void initialize() {
        LoggerConfig.logger.info("--==START VARS INITIALIZE==--");
        // Check and create /opt/telegram directory if id does not exist
        Utils.checkTelegramDir();
        Utils.checkLastVideoIdFile();
        // Init Vars
        String app_token = System.getenv("TG_TOKEN");
        String chatId = System.getenv("CHAT_ID");
        String x_token = System.getenv("X_TOKEN");
        String x_username = System.getenv("X_USERNAME");
        String w_token = System.getenv("W_TOKEN");
        String y_token = System.getenv("Y_TOKEN");
        String channelId = System.getenv("CHANNEL_ID");
        if (app_token == null) {
            LoggerConfig.logger.error("Ошибка: Задайте значение переменной TG_TOKEN");
            System.exit(1);
        } else {
            LoggerConfig.logger.info("TG_TOKEN - прочитан");
        }
        if (chatId == null) {
            LoggerConfig.logger.error("Ошибка: Задайте значение переменной CHAT_ID");
            System.exit(1);
        } else {
            LoggerConfig.logger.info("CHAT_ID - прочитан");
            this.chatID = chatId;
        }
        if (x_token == null) {
            LoggerConfig.logger.error("Ошибка: Задайте значение переменной X_TOKEN");
            System.exit(1);
        }
        else {
            LoggerConfig.logger.info("X_TOKEN - прочитан");
        }
        if (x_username == null) {
            LoggerConfig.logger.error("Ошибка: Задайте значение переменной X_USERNAME");
            System.exit(1);
        } else {
            LoggerConfig.logger.info("X_USERNAME - прочитан");
        }
        if (w_token == null) {
            LoggerConfig.logger.error("Ошибка: Задайте значение переменной W_TOKEN");
            System.exit(1);
        } else {
            LoggerConfig.logger.info("W_TOKEN - прочитан");
            this.w_token = w_token;
        }
        if (y_token == null) {
            LoggerConfig.logger.error("Ошибка: Задайте значение переменной Y_TOKEN");
            System.exit(1);
        } else {
            LoggerConfig.logger.info("Y_TOKEN - прочитан");
            this.y_token = y_token;
        }
        if (channelId == null) {
            LoggerConfig.logger.error("Ошибка: Задайте значение переменной CHANNEL_ID");
            System.exit(1);
        } else {
            LoggerConfig.logger.info("CHANNEL_ID - прочитан");
            this.channelId = channelId;
        }
        LoggerConfig.logger.info("--==END INITIALIZE==--");
    }

    public void createBot() {
        initialize();
        Scheduler scheduler = new Scheduler();

        Runnable getUpdates = () -> {
            try {
                String updates = telegram.getUpdates();
                if (updates != null) {
                    ObjectMapper objectMapper = new ObjectMapper();
                    JsonNode jsonNode = objectMapper.readTree(updates);
                    JsonNode resultArray = jsonNode.get("result");
                    for (JsonNode update : resultArray) {
                        int updateId = update.get("update_id").asInt();
                        JsonNode messageNode = update.get("message");
                        if (messageNode != null && messageNode.get("text") != null) {
                            String text = messageNode.get("text").asText();
                            String chatId = messageNode.get("chat").get("id").asText();

                            if (text.toLowerCase().startsWith("gpt")) {
                                telegram.sendMessage(chatId, "Адвокат когда ты уже сделаешь меня умным?");
                            }
                            if (text.toLowerCase().startsWith("хуй")) {
                                telegram.sendMessage(chatId, "трусы свои пожуй!");
                            }
                            if (text.toLowerCase().contains("python")) {
                                telegram.sendMessage(chatId, "Кому, что а ебуняке лишь бы питона душить");
                            }
                            if (text.toLowerCase().startsWith("red")) {
                                telegram.sendMessage(chatId, "Рыжие ушли на покой, попробуй написать нюдсы");
                            }
                            if (text.toLowerCase().startsWith("нюдсы")) {
                                telegram.sendPhoto(chatId, "https://pbs.twimg.com/media/GVjuLO-WwAAzjU_?format=jpg&name=large");
                            }
                        }
                        telegram.setLastUpdateId(updateId);
                    }

                }
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        };
        //TODO добавить интеграцию с X
        scheduler.addTaskDaily(() -> telegram.sendMessage(chatID,"Утро, мешки с костями!"), 7, 0);
        scheduler.addTaskAtFixedRate(getUpdates, 0, 5, TimeUnit.SECONDS);
    }

    public void createTestBot() {
        // Инициализация и проверка переменных
        initialize();
        // Создаем новый планировщик
        Scheduler scheduler = new Scheduler();
        weather = new Weather(w_token);
        // Включем интеграцию с youtube
        Youtube youtube = new Youtube(y_token);
        scheduler.addTaskAtFixedRate(() -> {
            String newVideo = youtube.lastVideo(channelId);
            if (newVideo != null) {
                telegram.sendMessage(chatID, "Новое видео от Каца: " + newVideo);
            }
        }, 0, 3, TimeUnit.HOURS);


        scheduler.addTaskDaily(() -> telegram.sendMessage(chatID, "Пиздуйте спать, жалкие людишки"), 20, 0);
        scheduler.addTaskAtFixedRate(() -> {
            try {
                processUpdates(telegram.getUpdates(), telegram);
                } catch (Exception e) {
                    throw new RuntimeException(e);
                }
            }, 0, 5, TimeUnit.SECONDS);
    }

    public void createAlphaBot() {
        initialize();
        Utils.init();
        Scheduler scheduler = new Scheduler();
        scheduler.addTaskAtFixedRate(() -> {
            try {
                String messages = telegram.getUpdates();
                JSON.testTelegramParse(messages);
                
//                if (messages != null) {
//                    ObjectMapper objectMapper = new ObjectMapper();
//                    JsonNode rootNode = objectMapper.readTree(messages);
//                    JsonNode resultArray = rootNode.get("result");
//
//                    if (resultArray != null && resultArray.isArray()) {
//                        for (JsonNode update : resultArray) {
//                            int updateId = update.get("update_id").asInt();
//                            JsonNode channelPost = update.get("channel_post");
//                            Telegram.setLastUpdateId(updateId);
//                            System.out.println(channelPost.toPrettyString());
//                        }
//                    }
//                }
            } catch (Exception e) {
                LoggerConfig.logger.error("Ошибке: ", e);
            }

        },0, 5, TimeUnit.SECONDS);
    }


    public void answerMessages(JsonNode message) {
        if(message != null && message.get("text") != null) {
            String text = message.get("text").asText();
            String chatId = message.get("chat").get("id").asText();
            if (text.toLowerCase().contains("сиськи")) {
                telegram.sendPhoto(chatId, "https://64.media.tumblr.com/ff05749b6c4319b01aa4266e62bba191/9540d1c5f001612f-ed/s400x600/bd4cd96e60106569ab2e0b6c9f5a3c5afa9903aa.jpg");
            }
            if (text.toLowerCase().contains("quiz")) {
                telegram.sendMessage(chatId, "Слабоумие и отвага - это команда что надо!");
            }
            if (text.toLowerCase().startsWith("ref")) {
                telegram.sendMessage(chatId, "Проверка рефакторинга прошла успешно");
            }
            if (text.toLowerCase().contains("адвокат")) {
                telegram.sendMessage(chatId, "Адвокат - всегда пиву рад!");
            }
            if (text.toLowerCase().contains("погода")) {
                telegram.sendMessage(chatId, weather.getWeather());
            }
        }
    }

    public void processUpdates(String getUpdates, Telegram telegram){
        //Попробовать переписать на JSON
        if (getUpdates != null) {
            JSONObject json = new JSONObject(getUpdates);
            if (json.getBoolean("ok")) {
                JSONArray results = json.getJSONArray("result");
                for (int i = 0; i < results.length(); i++) {
                    JSONObject updates = results.getJSONObject(i);
                    if (updates.has("message")) {
                        JSONObject message = updates.getJSONObject("message");
                        if (message.has("text")) {
                            String text = message.getString("text");
                            System.out.println(text);
                            long chat = message.getJSONObject("chat").getLong("id");
                            String ch = Long.toString(chat);
                            System.out.println(chat);

                            if (text.startsWith("json")) {
                                telegram.sendMessage(ch,"тест новой обработки сообщений");
                            }
                            if (text.startsWith("/test")) {
                                telegram.sendInlineButton(ch);
                            }
                            if (text.startsWith("/menu")) {
                                telegram.sendReplyButton(ch);
                            } else if (text.equals("test")) {
                                telegram.sendMessage(ch, "Проверка reply button");
                            } else if (text.equals("boobs")) {
                                telegram.sendPhoto(ch,"https://64.media.tumblr.com/ff05749b6c4319b01aa4266e62bba191/9540d1c5f001612f-ed/s400x600/bd4cd96e60106569ab2e0b6c9f5a3c5afa9903aa.jpg" );
                            }
                        }
                    } else if (updates.has("callback_query")) {
                        JSONObject callbackQuery = updates.getJSONObject("callback_query");
                        String callbackData = callbackQuery.getString("data");
                        String ch = Long.toString(callbackQuery.getJSONObject("message").getJSONObject("chat").getLong("id"));

                        if (callbackData.equals("info")) {
                            telegram.sendMessage(ch, "Я бот для тестирования");
                        }
                        if (callbackData.equals("vpn")) {
                            telegram.sendMessage(ch, "Если хотите приобрести VPN, обращайтесь к @mplane");
                        }
                        
                    }
                }
            }
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode jsonNode = objectMapper.readTree(getUpdates);
                JsonNode resultArray = jsonNode.get("result");

                for (JsonNode update : resultArray) {
                    int updateId = update.get("update_id").asInt();
                    JsonNode messageNode = update.get("message");
                    answerMessages(messageNode);
                    Telegram.setLastUpdateId(updateId);
                }
            } catch (Exception e) {
                e.printStackTrace(System.out);
            }
        }
    }
}
