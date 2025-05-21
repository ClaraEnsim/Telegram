package fr.ensim.interop.introrest.controller;

import fr.ensim.interop.introrest.model.weather.City;
import fr.ensim.interop.introrest.model.weather.Meteo;
import fr.ensim.interop.introrest.model.weather.OpenWeather;
import fr.ensim.interop.introrest.model.weather.Weather;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

@RestController
public class OpenWeatherRestController {

    private final static String API_KEY="17bfae4b92f752f0d32d089df115c48a";


    @GetMapping("/v2/meteo")
    public ResponseEntity<Meteo> meteoByVilleV2(
            @RequestParam("ville") String nomVille) {

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<City[]> responseEntity = restTemplate.getForEntity("http://api.openweathermap.org/geo/1.0/direct?q={ville}&limit=3"
                        + "&appid=" + API_KEY,
                City[].class, nomVille);
        City[] cities = responseEntity.getBody();
        City city = cities[0];

        OpenWeather openWeather = restTemplate.getForObject("http://api.openweathermap.org/data/2.5/weather?lat={lat}"
                        + "&lon={longitude}&units=metric&lang=fr&appid=" + API_KEY,
                OpenWeather.class, city.getLat(), city.getLon()); //à modifier pour récupérer une liste

        Meteo meteo = new Meteo(); //à modifier pour récupérer une liste
        openWeather.getWeather().get(0).setMain(meteo.getMeteo());
        meteo.setDetails(openWeather.getWeather().get(0).getDescription());
        meteo.setTemperature(openWeather.getMain().getTemp());

        return ResponseEntity.ok().body(meteo);
    }

}