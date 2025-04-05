package ru.tyatyushkin.telegram;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Comparator;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.StreamSupport;

public class Youtube {
    private final String token;
    private static final String FILE_PATH = "/opt/telegram/lastVideoID.txt";
    private static String lastVideoID = null;

    public Youtube(String token) {
        this.token = token;
    }

    public String lastVideo(String channelId) {
        try {
            String urlString = String.format(
                    "https://www.googleapis.com/youtube/v3/search?key=%s&channelId=%s&part=snippet&order=date&maxResults=5",
                    token, channelId);
            URL url = new URL(urlString);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.setRequestProperty("Accept", "application/json");

            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String inputLine;
            StringBuilder content = new StringBuilder();
            while ((inputLine = reader.readLine()) != null) {
                content.append(inputLine);
            }
            reader.close();
            conn.disconnect();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonResponse = objectMapper.readTree(content.toString());
            JsonNode items = jsonResponse.get("items");

            if (items != null && items.isArray() && !items.isEmpty()) {
                JsonNode latestVideo = StreamSupport.stream(items.spliterator(), false)
                        .max(Comparator.comparing(item -> item.get("snippet").get("publishedAt").asText()))
                        .orElse(null);
                if (latestVideo != null) {
                    JsonNode snippet = latestVideo.get("snippet");
                    String videoId = latestVideo.get("id").get("videoId").asText();
                    String title = snippet.get("title").asText();

                    // Check if the videoId is already in the file
                    Set<String> videoIds = new HashSet<>();
                    try (BufferedReader fileReader = new BufferedReader(new FileReader(FILE_PATH))) {
                        String line;
                        while ((line = fileReader.readLine()) != null) {
                            videoIds.add(line.trim());
                        }
                    }

                    if (lastVideoID == null || !lastVideoID.equals(videoId) || !videoIds.contains(videoId)) {
                        try(PrintWriter out = new PrintWriter(new FileWriter(FILE_PATH, true))) {
                            out.println(videoId);
                        }
                        lastVideoID = videoId;

                        // Печать информации о последнем видео
                        System.out.println("Новое видео обнаружено:");
                        System.out.println("Название последнего видео: " + title);
                        System.out.println("ID видео: " + videoId);
                        System.out.println("URL видео: https://www.youtube.com/watch?v=" + videoId);
                        return "https://www.youtube.com/watch?v=" + videoId;
                    } else {
                        System.out.println("Нет новых видео.");
                    }
                }
            }
        } catch (Exception e) {
            LoggerConfig.logger.error("An error occurred while fetching the last video: ", e);
        }
        return null;
    }
}

