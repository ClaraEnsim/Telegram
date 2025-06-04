package fr.ensim.interop.introrest.model.weather;

import java.util.List;

public class OpenWeatherForecast {
    private String cod;
    private int message;
    private int cnt;
    private List<OpenWeatherForecastItem> list;

    public OpenWeatherForecast() {}

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public int getMessage() {
        return message;
    }

    public void setMessage(int message) {
        this.message = message;
    }

    public int getCnt() {
        return cnt;
    }

    public void setCnt(int cnt) {
        this.cnt = cnt;
    }

    public List<OpenWeatherForecastItem> getList() {
        return list;
    }

    public void setList(List<OpenWeatherForecastItem> list) {
        this.list = list;
    }
}
