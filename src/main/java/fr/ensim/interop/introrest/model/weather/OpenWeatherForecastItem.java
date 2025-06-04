package fr.ensim.interop.introrest.model.weather;

import java.util.List;

public class OpenWeatherForecastItem {
    private long dt;
    private Main main;
    private List<Weather> weather;
    private String dt_txt;

    public OpenWeatherForecastItem() {}

    public long getDt() {
        return dt;
    }

    public void setDt(long dt) {
        this.dt = dt;
    }

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

    public String getDt_txt() {
        return dt_txt;
    }

    public void setDt_txt(String dt_txt) {
        this.dt_txt = dt_txt;
    }
}