package fr.ensim.interop.introrest.model.weather;

import java.util.List;

public class MeteoForecast {
    private String ville;
    private List<Meteo> forecasts;

    public MeteoForecast() {}

    public String getVille() {
        return ville;
    }

    public void setVille(String ville) {
        this.ville = ville;
    }

    public List<Meteo> getForecasts() {
        return forecasts;
    }

    public void setForecasts(List<Meteo> forecasts) {
        this.forecasts = forecasts;
    }
}