package com.heimcontrol.mobile;

/**
 * Created by root on 12/26/13.
 */
public class GPIO {

    private String _id;
    private String description;
    private String direction;
    private String value;
    private String pin;

    public GPIO(String _id, String  description, String direction, String value, String pin)
    {
        this._id = _id;
        this.description = description;
        this.direction = direction;
        this.value = value;
        this.pin = pin;
    }


    public String get_id() {
        return _id;
    }

    public void set_id(String _id) {
        this._id = _id;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDirection() {
        return direction;
    }

    public void setDirection(String direction) {
        this.direction = direction;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }
}
