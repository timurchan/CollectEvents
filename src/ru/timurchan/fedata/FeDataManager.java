package ru.timurchan.fedata;

import com.google.gson.Gson;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

/**
 * Created by tiga1115 on 30.01.2017.
 */
public class FeDataManager implements ConnectionV2.ConnectionListener {
    private static final int REQUEST_SAVE_SOCIAL_EVENTS = 1;

    public FeDataManager() {

    }

    public void sendMeetings(final ArrayList<Meeting> meetings) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                sendMeetingsInternal(meetings);
            }
        }).start();
    }

    public void sendMeetingsInternal(final ArrayList<Meeting> meetings) {
        System.out.println("sendMeetingsInternal() : sent " + meetings.size() + " mettings");
        String url = FeUrlProvider.saveSocialEvents();
        Gson gson = new Gson();
        Map<String, String> postData = new HashMap<>();
        postData.put("meetings", gson.toJson(meetings));
        ConnectionV2.<ApiRequest>getApiDataAsync(url, postData, REQUEST_SAVE_SOCIAL_EVENTS, this, ApiRequest.class);
    }

    @Override
    public <T> void onDataLoaded(T result, int request_id, Type typeOfT, final RequestResult answer) {
        switch (request_id) {
            case REQUEST_SAVE_SOCIAL_EVENTS: {
                if (!answer.isOk) {
                    System.out.println("FeDataManager.onDataLoaded() : Save social events error. " + answer.errorMessage);
                }
            }
            break;
        }
    }
}
