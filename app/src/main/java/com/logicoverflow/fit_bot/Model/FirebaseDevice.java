package com.logicoverflow.fit_bot.Model;

import java.io.Serializable;

public class FirebaseDevice implements Serializable {
    private String device_manufacturer;
    private String device_model;

    public FirebaseDevice(String device_manufacturer, String device_model) {
        this.device_manufacturer = device_manufacturer;
        this.device_model = device_model;
    }

    public String getDevice_manufacturer() {
        return device_manufacturer;
    }

    public void setDevice_manufacturer(String device_manufacturer) {
        this.device_manufacturer = device_manufacturer;
    }

    public String getDevice_model() {
        return device_model;
    }

    public void setDevice_model(String device_model) {
        this.device_model = device_model;
    }
}
