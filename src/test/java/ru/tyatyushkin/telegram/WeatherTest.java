package ru.tyatyushkin.telegram;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockedConstruction;
import org.mockito.junit.jupiter.MockitoExtension;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WeatherTest {
    
    private Weather weather;
    private static final String TEST_TOKEN = "test_token";
    private static final String TEST_CITY = "Москва";
    private static final String TEST_LAT = "55.7558";
    private static final String TEST_LON = "37.6173";
    
    @Mock
    private HttpURLConnection mockConnection;
    
    @BeforeEach
    void setUp() {
        weather = new Weather(TEST_TOKEN);
    }
    
    @Test
    @DisplayName("Успешное получение погоды")
    void testGetWeatherSuccess() throws Exception {
        String mockResponse = "{" +
            "\"weather\": [{\"description\": \"ясно\"}]," +
            "\"main\": {\"temp\": 15.5}," +
            "\"wind\": {\"speed\": 3.5}" +
        "}";
        
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(mockResponse.getBytes()));
        
        try (MockedConstruction<URL> ignored = mockConstruction(URL.class, 
            (mock, context) -> when(mock.openConnection()).thenReturn(mockConnection))) {
            
            String result = weather.getWeather(TEST_CITY, TEST_LAT, TEST_LON);
            
            assertNotNull(result);
            assertTrue(result.contains("Погода в городе - Москва"));
            assertTrue(result.contains("температура: 15.5"));
            assertTrue(result.contains("скорость ветра: 3.5"));
            assertTrue(result.contains("Состояние: ясно"));
        }
    }
    
    @Test
    @DisplayName("Обработка ошибки HTTP")
    void testGetWeatherHttpError() throws Exception {
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_UNAUTHORIZED);
        when(mockConnection.getResponseMessage()).thenReturn("Unauthorized");
        
        try (MockedConstruction<URL> ignored = mockConstruction(URL.class, 
            (mock, context) -> when(mock.openConnection()).thenReturn(mockConnection))) {
            
            String result = weather.getWeather(TEST_CITY, TEST_LAT, TEST_LON);
            
            assertNull(result);
        }
    }
    
    @Test
    @DisplayName("Обработка исключения при запросе")
    void testGetWeatherException() {
        try (MockedConstruction<URL> ignored = mockConstruction(URL.class, 
            (mock, context) -> when(mock.openConnection()).thenThrow(new IOException("Connection failed")))) {
            
            String result = weather.getWeather(TEST_CITY, TEST_LAT, TEST_LON);
            
            assertNull(result);
        }
    }
    
    @Test
    @DisplayName("Корректная обработка пустого JSON")
    void testGetWeatherEmptyJson() throws Exception {
        String mockResponse = "{}";
        
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(mockResponse.getBytes()));
        
        try (MockedConstruction<URL> ignored = mockConstruction(URL.class, 
            (mock, context) -> when(mock.openConnection()).thenReturn(mockConnection))) {
            
            String result = weather.getWeather(TEST_CITY, TEST_LAT, TEST_LON);
            
            assertNull(result);
        }
    }
    
    @Test
    @DisplayName("Проверка формирования правильного URL")
    void testCorrectUrlFormation() throws Exception {
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream("{}".getBytes()));
        
        try (MockedConstruction<URL> ignored = mockConstruction(URL.class, 
            (mock, context) -> {
                String urlString = (String) context.arguments().get(0);
                assertTrue(urlString.contains("lat=" + TEST_LAT));
                assertTrue(urlString.contains("lon=" + TEST_LON));
                assertTrue(urlString.contains("appid=" + TEST_TOKEN));
                assertTrue(urlString.contains("lang=ru"));
                assertTrue(urlString.contains("units=metric"));
                when(mock.openConnection()).thenReturn(mockConnection);
            })) {
            
            weather.getWeather(TEST_CITY, TEST_LAT, TEST_LON);
        }
    }
    
    @Test
    @DisplayName("Проверка конструктора")
    void testWeatherConstructor() {
        Weather testWeather = new Weather("test_api_key");
        assertNotNull(testWeather);
    }
    
    @Test
    @DisplayName("Обработка неправильного формата JSON")
    void testGetWeatherInvalidJson() throws Exception {
        String mockResponse = "{invalid json}";
        
        when(mockConnection.getResponseCode()).thenReturn(HttpURLConnection.HTTP_OK);
        when(mockConnection.getInputStream()).thenReturn(new ByteArrayInputStream(mockResponse.getBytes()));
        
        try (MockedConstruction<URL> ignored = mockConstruction(URL.class, 
            (mock, context) -> when(mock.openConnection()).thenReturn(mockConnection))) {
            
            String result = weather.getWeather(TEST_CITY, TEST_LAT, TEST_LON);
            
            assertNull(result);
        }
    }
}