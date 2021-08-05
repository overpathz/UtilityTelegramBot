package com.pathz.tgbot.util;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpClientErrorException;
import org.json.JSONObject;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.logging.Level;
import java.util.logging.Logger;

@Component("weatherFinder")
public class WeatherFinderApi {

    @Value("${open.weather.api.token}")
    private String TOKEN;

    private final RestTemplate restTemplate;
    private final Logger logger = Logger.getLogger("WeatherFinderApi");

    public WeatherFinderApi(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public Optional<Map<String, String>> getWeather(String city) {

        String API_URL = getParametrizedUrl(city);

        ResponseEntity<String> responseEntity;

        try {
            responseEntity = restTemplate.exchange(API_URL, HttpMethod.GET, null,
                    new ParameterizedTypeReference<>() {
                    });
        } catch (HttpClientErrorException exception) {
            logger.log(Level.WARNING, "City not found");
            return Optional.empty();
        }

        String jsonString = responseEntity.getBody();
        JSONObject object = new JSONObject(jsonString);

        Map<String, String> map = new HashMap<>();

        map.put("temp", String.valueOf(object.getJSONObject("main").getDouble("temp")));
        map.put("feelsLike", String.valueOf(object.getJSONObject("main").getDouble("feels_like")));
        map.put("tempMin", String.valueOf(object.getJSONObject("main").getDouble("temp_min")));
        map.put("tempMax", String.valueOf(object.getJSONObject("main").getDouble("temp_max")));
        map.put("description", object.getJSONArray("weather").getJSONObject(0).getString("description"));

        return Optional.of(map);
    }

    private String getParametrizedUrl(String city) {
        String URL = "https://api.openweathermap.org/";
        StringBuilder stringBuilder = new StringBuilder(URL);

        stringBuilder
                .append("data/2.5/weather?q=")
                .append(city)
                .append("&appid=")
                .append(TOKEN)
                .append("&units=metric");

        return stringBuilder.toString();
    }
}
