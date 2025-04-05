package ru.tyatyushkin.telegram;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class X {
    private final String bearerToken;

    public X(String bearerToken) {
        this.bearerToken = bearerToken;
    }

    public String getUserIdByUsername(String username) {
        try {
            URL url = new URL("https://api.twitter.com/2/users/by/username/" + username);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + bearerToken);

            int responseCode = connection.getResponseCode();
            String remainingRequests = connection.getHeaderField("x-rate-limit-remaining");
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();
                return parseUserIdWithJackson(jsonResponse) ;
            } else {
                System.out.println("Error: " + responseCode + " - " + connection.getResponseMessage() + "limit"  + remainingRequests);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getLastTweetByUserId(String userId) {
        try {
            URL url = new URL("https://api.twitter.com/2/users/" + userId + "/tweets?max_results=5&tweet.fields=created_at");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + bearerToken);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(jsonResponse);
                JsonNode metaNode = rootNode.path("meta");
                String newestId = metaNode.path("newest_id").asText();
                System.out.println(newestId);
                return newestId;
            } else {
                System.out.println("Error: " + responseCode + " - " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    public String getMessageByMessageId(String messageId) {

        try {
            URL url = new URL("https://api.twitter.com/2/tweets/" + messageId
                    + "?tweet.fields=created_at,author_id,text,public_metrics,entities"
                    + "&expansions=attachments.media_keys,referenced_tweets.id"
                    + "&media.fields=url,preview_image_url");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setRequestProperty("Authorization", "Bearer " + bearerToken);

            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;
                StringBuilder response = new StringBuilder();

                while ((inputLine = in.readLine()) != null) {
                    response.append(inputLine);
                }
                in.close();

                String jsonResponse = response.toString();
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode rootNode = objectMapper.readTree(jsonResponse);

                return rootNode.get("data").get("text").asText();
            } else {
                System.out.println("Error: " + responseCode + " - " + connection.getResponseMessage());
            }
        } catch (Exception e) {
            e.printStackTrace();

        }
        return null;
    }

    private static String parseUserIdWithJackson (String jsonResponse) {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(jsonResponse);
            System.out.println(rootNode.toPrettyString());
            return rootNode.path("data").path("id").asText();  // Доступ к id в JSON-ответе
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

}
