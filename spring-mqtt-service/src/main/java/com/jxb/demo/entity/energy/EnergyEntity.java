package com.jxb.demo.entity.energy;

import java.io.Serializable;

public class EnergyEntity implements Serializable {

    private int projectId;
    private String deviceType;
    private String deviceName;
    private String eventDate;
    private Double planValue;
    private Double eventValue;
    private Double voltageaValue;
    private Double voltagebValue;
    private Double voltagecValue;
    private Double electricaValue;
    private Double electricbValue;
    private Double electriccValue;
    private String unit;

    public int getProjectId() {
        return projectId;
    }

    public void setProjectId(int projectId) {
        this.projectId = projectId;
    }

    public String getDeviceType() {
        return deviceType;
    }

    public void setDeviceType(String deviceType) {
        this.deviceType = deviceType;
    }

    public String getDeviceName() {
        return deviceName;
    }

    public void setDeviceName(String deviceName) {
        this.deviceName = deviceName;
    }

    public String getEventDate() {
        return eventDate;
    }

    public void setEventDate(String eventDate) {
        this.eventDate = eventDate;
    }

    public Double getPlanValue() {
        return planValue;
    }

    public void setPlanValue(Double planValue) {
        this.planValue = planValue;
    }

    public Double getEventValue() {
        return eventValue;
    }

    public void setEventValue(Double eventValue) {
        this.eventValue = eventValue;
    }

    public Double getVoltageaValue() {
        return voltageaValue;
    }

    public void setVoltageaValue(Double voltageaValue) {
        this.voltageaValue = voltageaValue;
    }

    public Double getVoltagebValue() {
        return voltagebValue;
    }

    public void setVoltagebValue(Double voltagebValue) {
        this.voltagebValue = voltagebValue;
    }

    public Double getVoltagecValue() {
        return voltagecValue;
    }

    public void setVoltagecValue(Double voltagecValue) {
        this.voltagecValue = voltagecValue;
    }

    public Double getElectricaValue() {
        return electricaValue;
    }

    public void setElectricaValue(Double electricaValue) {
        this.electricaValue = electricaValue;
    }

    public Double getElectricbValue() {
        return electricbValue;
    }

    public void setElectricbValue(Double electricbValue) {
        this.electricbValue = electricbValue;
    }

    public Double getElectriccValue() {
        return electriccValue;
    }

    public void setElectriccValue(Double electriccValue) {
        this.electriccValue = electriccValue;
    }

    public String getUnit() {
        return unit;
    }

    public void setUnit(String unit) {
        this.unit = unit;
    }
}
