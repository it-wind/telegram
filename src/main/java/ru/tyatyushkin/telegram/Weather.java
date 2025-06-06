package ru.tyatyushkin.telegram;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class Weather {
    private final String token;

    public Weather(String w_token) {
        this.token = w_token;
    }

    public String getWeather(String city, String lat, String lon) {
        try {
            StringBuilder content = new StringBuilder();
            //URL url = new URL("https://api.weather.yandex.ru/v2/forecast?lat=53.40716171&lon=58.98028946&lang=ru_RU");
            URL url = new URL("https://api.openweathermap.org/data/2.5/weather?lat=" + lat + "&lon="+ lon + "&appid=" + token + "&lang=ru&units=metric");
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            int responseCode = connection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                String inputLine;

                while ((inputLine = in.readLine()) != null) {
                    content.append(inputLine);
                }

                in.close();
            } else {
                System.out.println("Error: " + responseCode + " - " + connection.getResponseMessage());
                connection.disconnect();
                return null;
            }
            connection.disconnect();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(content.toString());

            // Check if required fields exist
            if (rootNode.get("weather") == null || rootNode.get("weather").get(0) == null ||
                rootNode.get("main") == null || rootNode.get("wind") == null) {
                return null;
            }

            String condition = rootNode.get("weather").get(0).get("description").asText();
            String temp = rootNode.get("main").get("temp").asText();
            String windSpeed = rootNode.get("wind").get("speed").asText();

            return "Погода в городе - " + city + " - температура: " + temp
                    + ", скорость ветра: " + windSpeed + ", Состояние: " + condition;
        } catch (Exception e) {
            e.printStackTrace(System.out);
        }
        return null;
    }
}
