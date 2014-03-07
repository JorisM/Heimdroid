package com.heimcontrol.mobile;

/**
 * Created by root on 12/26/13.
 */
public class RCSwitch {

    private String _id;
    private String description;
    private boolean value;
    private String pin;
    private String code;


    public RCSwitch(String _id, String description, boolean value, String pin, String code)
    {
        this._id = _id;
        this.description = description;
        this.value = value;
        this.pin = pin;
        this.code = code;
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

    public boolean getValue() {
        return value;
    }

    public void setValue(boolean value) {
        this.value = value;
    }

    public String getPin() {
        return pin;
    }

    public void setPin(String pin) {
        this.pin = pin;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }
}
