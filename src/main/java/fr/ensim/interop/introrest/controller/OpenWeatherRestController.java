package fr.ensim.interop.introrest.controller;

import fr.ensim.interop.introrest.model.weather.City;
import fr.ensim.interop.introrest.model.weather.Meteo;
import fr.ensim.interop.introrest.model.weather.OpenWeather;
import fr.ensim.interop.introrest.model.weather.Weather;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class OpenWeatherRestController {

    private final static String API_KEY="17bfae4b92f752f0d32d089df115c48a";


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
                OpenWeather.class, city.getLat(), city.getLon()); //à modifier pour récupérer une liste

        if (openWeather == null || openWeather.getWeather() == null || openWeather.getWeather().isEmpty()) {
            return ResponseEntity.notFound().build();
        }

        Weather weatherInfo = openWeather.getWeather().get(0);

        Meteo meteo = new Meteo(); //à modifier pour récupérer une liste
        meteo.setMeteo(weatherInfo.getMain());
        meteo.setDetails(weatherInfo.getDescription());
        meteo.setTemperature(String.valueOf(openWeather.getMain().getTemp()));

        return ResponseEntity.ok().body(meteo);
    }

}