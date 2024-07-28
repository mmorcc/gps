package com.lianxin.location.demo;

import java.io.Serializable;

public class SatelliteInfoBean implements Serializable {
    private String name;
    private float signal;
    private String angle;
    private String azimuthAngle;

    public SatelliteInfoBean(){}

    public SatelliteInfoBean(String name, float signal){
        this.name = name;
        this.signal = signal;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public float getSignal() {
        return signal;
    }

    public void setSignal(float signal) {
        this.signal = signal;
    }

    public String getAngle() {
        return angle;
    }

    public void setAngle(String angle) {
        this.angle = angle;
    }

    public String getAzimuthAngle() {
        return azimuthAngle;
    }

    public void setAzimuthAngle(String azimuthAngle) {
        this.azimuthAngle = azimuthAngle;
    }

}
