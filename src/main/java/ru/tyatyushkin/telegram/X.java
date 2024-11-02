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
                String userId = parseUserIdWithJackson(jsonResponse);
                return userId;
            } else {
                System.out.println("Error: " + responseCode + " - " + connection.getResponseMessage() + "limit"  + remainingRequests);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    private static String parseUserIdWithJackson(String jsonResponse) {
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