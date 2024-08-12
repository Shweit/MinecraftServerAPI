package com.shweit.serverapi.api.v1.models;

public class StatisticModel {
    private String name;
    private Object value;
    private String unit;

    public StatisticModel(String name, Object value, String unit) {
        this.name = name;
        this.value = value;
        this.unit = unit;
    }

    // Getters and Setters
    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object getValue() {
        return value;
    }

    public void setValue(Object value) {
        this.value = value;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
