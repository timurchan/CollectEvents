package ru.timurchan.fedata;

public class ApiRequest {
    public static int API_CODE_OK = 1;
    private static int API_CODE_UNDEFINED = -1;

    public int resCode = API_CODE_UNDEFINED;
    public String resMessage;

    public String toString() {
        return "resCode = " + Integer.toString(resCode) + ", msg = " + resMessage;
    }
}
