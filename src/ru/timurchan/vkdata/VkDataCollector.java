package ru.timurchan.vkdata;


import org.json.*;
import ru.timurchan.GetEvents;


/*
Error codes:
1 Unknown error occured.
2 Application is disabled. Enable your application or use test mode.
3 Unknown method passed.
4 Incorrect signature.
5 User authorization failed.
6 Too many requests per second.
7 Permission to perform this action is denied by user.
*/

/**
 * Created by Timur on 29.01.2017.
 */
public class VkDataCollector implements VkFriendsManager.FriendsListener, VkEventsManager.EventsListener {
    private static final int ERROR_TOO_MANY_REQUESTS = 6;
    private static final int ERROR_PERMISSION_DENIED = 7;

    static private int mPermissionDeniedCounter = 0;

    VkFriendsManager mFriendsManager = new VkFriendsManager(this);
    VkEventsManager mEventsManager = new VkEventsManager(this);

    GetEvents window;

    public VkDataCollector() {

    }

    public void setWindow(GetEvents window) {
        this.window = window;
    }

    static public String parseErrorResponse(final String answer) {
        JSONObject jsonResponse;
        try {
            JSONObject jsonObj = new JSONObject(answer);
            jsonResponse = jsonObj.getJSONObject("error");
            int errorCode = jsonResponse.optInt("error_code");
            if(errorCode == ERROR_TOO_MANY_REQUESTS) {
                JSONArray jsonArray = jsonResponse.getJSONArray("request_params");
                int length = jsonArray.length();
                for (int i = 0; i < length; i++) {
                    if(jsonArray.getJSONObject(i).has("user_id")){
                        return jsonArray.getJSONObject(i).getString("user_id");
                    }
                    JSONObject obj = new JSONObject(jsonArray.optString(i));
                    if(obj.has("user_id")) {
                        return obj.optString("user_id");
                    }
                }
            } else if(errorCode == ERROR_PERMISSION_DENIED) {
                mPermissionDeniedCounter++;
            }
        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    static public int getPermissionDeniedCounter() { return mPermissionDeniedCounter; }

    public void saveFriends() {
        mFriendsManager.saveFriends();
    }

    public void loadFriends() { mFriendsManager.loadFriends(); }

    public void collectFriends() {
        mFriendsManager.collectFriends();
    }

    public void collectEvents() {
        mEventsManager.collectEvents(mFriendsManager.friendIds());
    }

    public void stopGettingFriends() {
        mFriendsManager.stopGettingFriends();
    }

    public void stopGettingEvents() {
        mEventsManager.stopGettingEvents();
    }

    public void stopAll() {
        stopGettingFriends();
        stopGettingEvents();
        saveFriends();
    }

    public void setMeetingsPackCount(int count) {
        mEventsManager.setMeetingsPackCount(count);
    }

    public void setInitilFriendId(final String initialId) {
        mFriendsManager.setInitilFriendId(initialId);
    }


    @Override
    public void OnUpdateFriendsCount(int count) {
        window.setFriendsCount(count);
    }

    @Override
    public void OnUpdateEventsCount(int count) {
        window.setEventsCount(count);
    }

    @Override
    public void OnUpdateFEProcessedCount(int count) {
        window.setFEProcessedCount(count);
    }
}
