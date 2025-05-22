package fr.ensim.interop.introrest.model.weather;

import fr.ensim.interop.introrest.model.telegram.Update;

import java.util.List;

public class UpdateResponse {

    private boolean success;
    private List<Update> updates;

    public List<Update> getResult(){
        return updates;
    }

    public void setResult(List<Update> updates){
        this.updates = updates;
    }
}
