package ru.timurchan.fedata;

/**
 * Created by HP on 18.07.2016.
 */
public class RequestResult {

    static final public int ET_NULL = 0;            // object ApiRequest or it's inheritor is null after parsing json
    static final public int ET_NOT_SUCCESS = 1;     // ApiRequest::res_code != API_CODE_OK
    static final public int ET_SUCCESS = 2;

    public RequestResult(boolean isOk, int errorType) {
        this.isOk = isOk;
        this.errorType = errorType;
    }

    public RequestResult(boolean isOk, int errorType, final String errorMessage) {
        this.isOk = isOk;
        this.errorType = errorType;
        this.errorMessage = errorMessage;
    }

    public boolean isOk = false;
    public int errorType;
    public String errorMessage = "";
}
