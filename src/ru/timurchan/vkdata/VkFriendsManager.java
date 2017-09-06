package ru.timurchan.vkdata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.timurchan.model.Friend;
import ru.timurchan.utils.MyHttpURLConnection;
import ru.timurchan.utils.MyUtils;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by Timur on 30.01.2017.
 */
public class VkFriendsManager implements MyHttpURLConnection.ConnectionListener {
    private Map<String, Friend> mFriends = new TreeMap<>();
    private Map<Integer, City> mCities = new TreeMap<>();

    private int STEP_PERCENT = 1;
    private int mFriendsRequestsCounter = 0;
    private Thread mGollectFriendsThread;
    private FriendsListener mListener;
    private VkEventsManager.EventsListener mShowFriendsListener;
    private VkDataCollector mDataCollector;

    private String initialUserrId = "69822";

    public interface FriendsListener {
        void OnUpdateFriendsCount(int count);
    }

    public VkFriendsManager(FriendsListener friendsListener,
                            VkEventsManager.EventsListener showFriendsListener,
                            VkDataCollector dataCollector) {
        mListener = friendsListener;
        mShowFriendsListener = showFriendsListener;
        mDataCollector = dataCollector;
    }

    public void setInitilFriendId(final String initialId) {
        initialUserrId = initialId;
    }

    public Collection<String> friendIds() {
        return mFriends.keySet();
    }

    public void collectFriends() {
        mFriends.clear();
        mCities.clear();
        getFriends(initialUserrId);            // get 1-st layer of friends of user with id = timurId
    }

    private boolean getFriends(final String id) {
        System.out.println("Start getFriends() for " + id);
        if (Thread.currentThread().isInterrupted()) {
            System.out.println("[Interruption Thread] bye**");
            return false;
        }
        String targetURL = VkUrlProvider.requestFriends(id);
        MyHttpURLConnection.executeSendAsync(targetURL, this, MyHttpURLConnection.FRIENDS_TYPE, id);
        return true;
    }

    private void parseFriendsShort(final String answer) throws JSONException {
        JSONObject jsonResponse;
        JSONObject jsonObj = new JSONObject(answer);
        jsonResponse = jsonObj.getJSONObject("response");
        JSONArray jsonArray = jsonResponse.getJSONArray("items");
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            //mFriends.add(jsonArray.optString(i));
        }
    }

    // {"response":{"count":150,"items":[{"id":588,"first_name":"Лена","last_name":"Остапенко","domain":"id588","city":{"id":2,"title":"Санкт-Петербург"},"online":1},
    private void parseFriendsFull(final String answer) throws JSONException {
        JSONObject jsonResponse;
        JSONObject jsonObj = new JSONObject(answer);
        jsonResponse = jsonObj.getJSONObject("response");
        JSONArray jsonArray = jsonResponse.getJSONArray("items");
        int length = jsonArray.length();
        for (int i = 0; i < length; i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            Friend friend = parseOneFriend(obj);
            if (!mFriends.containsKey(friend.id)) {
                mFriends.put(friend.id, friend);
                if (!mCities.containsKey(friend.cityId)) {
                    mCities.put(friend.cityId, new City(friend.cityId, friend.cityName));
                } else {
                    mCities.get(friend.cityId).increment();
                }
            }
        }
        if (mListener != null) {
            mListener.OnUpdateFriendsCount(mFriends.size());
        }
        if (mShowFriendsListener != null) {
            mShowFriendsListener.OnUpdateFEProcessedFriendsCount(mFriends.size());
        }
    }

    private Friend parseOneFriend(final JSONObject obj) throws JSONException {
        Friend user = new Friend();

        if (obj.has("id")) {
            user.id = obj.getString("id");
        }
        if (obj.has("domain")) {
            user.domain = obj.getString("domain");
        }
        if (obj.has("first_name")) {
            user.firstName = obj.getString("first_name");
        }
        if (obj.has("last_name")) {
            user.lastName = obj.getString("last_name");
        }

        String city = "";
        if (obj.has("city")) {
            city = obj.getString("city");
            if (city != null) {
                JSONObject jsonObj = new JSONObject(city);
                if (jsonObj.has("id")) {
                    user.cityId = jsonObj.getInt("id");
                }
                if (jsonObj.has("title")) {
                    user.cityName = jsonObj.getString("title");
                }
            }
        }

        String country = "";
        if (obj.has("country")) {
            country = obj.getString("country");
            if (country != null) {
                JSONObject jsonObj = new JSONObject(country);
                if (jsonObj.has("id")) {
                    user.countryId = jsonObj.getInt("id");
                }
                if (jsonObj.has("title")) {
                    user.countryName = jsonObj.getString("title");
                }
            }
        }

        return user;
    }

    private void parseFriendsResponse(final String answer) {
        try {
            //parseFriendsShort(answer);
            parseFriendsFull(answer);
        } catch (JSONException e) {
            System.out.println(answer);
            String userId = VkDataCollector.parseErrorResponse(answer);
            if (userId != null) {
                MyUtils.sleep();
                getFriends(userId);
            }
        }
    }

    private void getFriendsByFriends(final ArrayList<String> friends) {
        int friendsCount = friends.size();
        Integer counter = 0;
        Integer percent = 0;
        System.out.println("getFriendsByFriends: " + friendsCount + " friends will be process");
        try {
            for (String friendId : friends) {
                MyUtils.sleepT();
                getFriends(friendId);
                counter++;
                Integer oldPercent = percent;
                percent = 100 * counter / friends.size();
                if (percent % STEP_PERCENT == 0 && percent != oldPercent) {
                    //System.out.println(percent + "% processed. Time is " + MyUtils.getCurrentTimeStamp());
                    if(mShowFriendsListener != null) {
                        mShowFriendsListener.OnUpdateFEProcessedFriendsPercent(percent);
                    }
                }
            }
        } catch (InterruptedException e) {
            System.out.println("Stop collectiong friends. Total friends: " + mFriends.size());
            saveFriendsCities();
            return;
        }
        System.out.println("All friends processed");
        saveFriendsCities();
    }

    public void saveFriendsCities() {
        MyUtils.saveCitiesOrdered(mCities);
    }


    public void stopGettingFriends() {
        if (mGollectFriendsThread != null)
            mGollectFriendsThread.interrupt();
        mFriendsRequestsCounter = 0;
    }

    public void saveFriends() {
        MyUtils.saveFriends(mFriends);
    }

    public void loadFriends() {
        mFriends = MyUtils.loadFriends();
        if (mListener != null)
            mListener.OnUpdateFriendsCount(mFriends.size());
        System.out.println("loadFriends(): mFriends.size() = " + mFriends.size());
    }


    @Override
    public void OnFriendsResponse(String response, final String arg1) {
        parseFriendsResponse(response);
        mFriendsRequestsCounter++;
        if (mDataCollector.isFriendsLevel2() && arg1.equals(initialUserrId)) {
            mGollectFriendsThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    ArrayList<String> m1stLevelFriends = new ArrayList<>();
                    m1stLevelFriends.addAll(mFriends.keySet());
                    getFriendsByFriends(m1stLevelFriends);
                }
            });
            mGollectFriendsThread.start();
        }
    }

    @Override
    public void OnGroupsResponse(String response, final String arg1) {
        System.out.println("Total friends 1 and 2 layer = " + mFriends.size());
    }
}
