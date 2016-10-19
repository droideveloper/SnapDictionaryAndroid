package org.fs.android.dictionary.backend.model;

/**
 * Created by Fatih on 30/11/14.
 */
public class DeviceResponse {

    private int code;

    private String message;

    private int errorCode;

    private Device data;

    public int getCode() {
        return code;
    }

    public void setCode(int code) {
        this.code = code;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public int getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(int errorCode) {
        this.errorCode = errorCode;
    }

    public Device getData() {
        return data;
    }

    public void setData(Device data) {
        this.data = data;
    }
}
