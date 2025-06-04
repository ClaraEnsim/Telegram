package fr.ensim.interop.introrest.controller;

import fr.ensim.interop.introrest.model.weather.*;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
public class OpenWeatherRestController {

    private final static String API_KEY = "17bfae4b92f752f0d32d089df115c48a";

    /**
     * Météo actuelle (ton code existant)
     */
    @GetMapping("/meteo")
    public ResponseEntity<Meteo> meteoByVille(
            @RequestParam("ville") String nomVille) {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<City[]> responseEntity = restTemplate.getForEntity("http://api.openweathermap.org/geo/1.0/direct?q={ville}&limit=1&appid=" + API_KEY,
                City[].class, nomVille);
        City[] cities = responseEntity.getBody();

        if (cities == null || cities.length == 0) {
            return ResponseEntity.notFound().build();
        }
        City city = cities[0];

        OpenWeather openWeather = restTemplate.getForObject("http://api.openweathermap.org/data/2.5/weather?lat={lat}&lon={lon}&units=metric&lang=fr&appid=" + API_KEY,
                OpenWeather.class, city.getLat(), city.getLon());

        if (openWeather == null || openWeather.getWeather() == null || openWeather.getWeather().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Weather weatherInfo = openWeather.getWeather().get(0);

        Meteo meteo = new Meteo();
        meteo.setMeteo(weatherInfo.getMain());
        meteo.setDetails(weatherInfo.getDescription());
        meteo.setTemperature(String.valueOf(Math.round(openWeather.getMain().getTemp())));

        return ResponseEntity.ok().body(meteo);
    }

    /**
     * NOUVELLE MÉTHODE : Météo des prochains jours
     */
    @GetMapping("/meteo/forecast")
    public ResponseEntity<MeteoForecast> meteoForecastByVille(
            @RequestParam("ville") String nomVille,
            @RequestParam(value = "days", defaultValue = "3") int days) {

        try {
            RestTemplate restTemplate = new RestTemplate();

            // 1. Récupère les coordonnées de la ville
            ResponseEntity<City[]> responseEntity = restTemplate.getForEntity(
                    "http://api.openweathermap.org/geo/1.0/direct?q={ville}&limit=1&appid=" + API_KEY,
                    City[].class, nomVille);
            City[] cities = responseEntity.getBody();

            if (cities == null || cities.length == 0) {
                return ResponseEntity.notFound().build();
            }
            City city = cities[0];

            // 2. Récupère les prévisions météo (API 5 jours / 3 heures)
            OpenWeatherForecast forecast = restTemplate.getForObject(
                    "http://api.openweathermap.org/data/2.5/forecast?lat={lat}&lon={lon}&units=metric&lang=fr&appid=" + API_KEY,
                    OpenWeatherForecast.class, city.getLat(), city.getLon());

            if (forecast == null || forecast.getList() == null || forecast.getList().isEmpty()) {
                return ResponseEntity.notFound().build();
            }

            // 3. Traite les données pour grouper par jour
            MeteoForecast meteoForecast = processForecastData(forecast, nomVille, days);

            return ResponseEntity.ok().body(meteoForecast);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * Traite les données de l'API forecast pour les grouper par jour
     */
    private MeteoForecast processForecastData(OpenWeatherForecast forecast, String ville, int days) {
        MeteoForecast meteoForecast = new MeteoForecast();
        meteoForecast.setVille(ville);

        // Groupe les prévisions par jour
        Map<String, List<OpenWeatherForecastItem>> forecastByDay = forecast.getList().stream()
                .collect(Collectors.groupingBy(item -> {
                    LocalDateTime dateTime = LocalDateTime.ofEpochSecond(item.getDt(), 0, ZoneOffset.UTC);
                    return dateTime.toLocalDate().toString();
                }));

        // Trie par date et prend les X premiers jours
        List<Meteo> dailyForecasts = forecastByDay.entrySet().stream()
                .sorted(Map.Entry.comparingByKey())
                .limit(days)
                .map(entry -> {
                    String date = entry.getKey();
                    List<OpenWeatherForecastItem> dayItems = entry.getValue();

                    // Prend la prévision du milieu de journée (vers 12h) ou la première disponible
                    OpenWeatherForecastItem midDayItem = dayItems.stream()
                            .min((item1, item2) -> {
                                int hour1 = LocalDateTime.ofEpochSecond(item1.getDt(), 0, ZoneOffset.UTC).getHour();
                                int hour2 = LocalDateTime.ofEpochSecond(item2.getDt(), 0, ZoneOffset.UTC).getHour();
                                return Integer.compare(Math.abs(hour1 - 12), Math.abs(hour2 - 12));
                            })
                            .orElse(dayItems.get(0));

                    Meteo dayMeteo = new Meteo();
                    dayMeteo.setDate(date);
                    dayMeteo.setMeteo(midDayItem.getWeather().get(0).getMain());
                    dayMeteo.setDetails(midDayItem.getWeather().get(0).getDescription());
                    dayMeteo.setTemperature(String.valueOf(Math.round(midDayItem.getMain().getTemp())));

                    return dayMeteo;
                })
                .collect(Collectors.toList());

        meteoForecast.setForecasts(dailyForecasts);
        return meteoForecast;
    }
}