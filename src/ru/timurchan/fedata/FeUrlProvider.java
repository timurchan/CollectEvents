package ru.timurchan.fedata;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.Set;

/**
 * Created by tiga1115 on 30.01.2017.
 */
public class FeUrlProvider {
    static final String mDomain = "http://friendevent.org/api/";

    static public String saveSocialEvents() {
        String url = mDomain + "meetings/saveSocialEvents";
        return addSid(url);
    }

    static private String addSid(final String _url) {
        //String sid = "ec3935cd977b62ec192b7b42fa971859";
        String sid = "e4248195eb7d4419208e100582b40d0b";
        String url = _url;
        if(url != null && sid != null && !sid.isEmpty()) {
            url += "?sid=";
            url += sid;
        }

        return common(url);
    }

    static private String common(final String url) {
        return url + "&v=1.0";
    }

    static public String encode(final String str) {
        String str_utf8 = "";
        try {
            str_utf8 = URLEncoder.encode(str, "utf-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return str_utf8;
    }

}
