package fr.ensim.interop.introrest.model.weather;

import fr.ensim.interop.introrest.model.Weather;

import java.util.List;

public class OpenWeather {

    private List<Weather> weather;
    private Main main;

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public List<Weather> getWeather() {
        return weather;
    }

    public void setWeather(List<Weather> weather) {
        this.weather = weather;
    }
}
