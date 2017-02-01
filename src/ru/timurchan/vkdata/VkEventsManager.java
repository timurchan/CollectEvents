package ru.timurchan.vkdata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.timurchan.fedata.FeDataManager;
import ru.timurchan.fedata.ImportMeetingCreator;
import ru.timurchan.fedata.Meeting;
import ru.timurchan.model.Event;
import ru.timurchan.model.Friend;
import ru.timurchan.utils.MyHttpURLConnection;
import ru.timurchan.utils.MyUtils;
import ru.timurchan.utils.VkUrlProvider;

import java.util.*;

/**
 * Created by Timur on 30.01.2017.
 */
public class VkEventsManager implements MyHttpURLConnection.ConnectionListener  {
    private static final int ERROR_TOO_MANY_REQUESTS = 6;
    private static final int ERROR_PERMISSION_DENIED = 7;
    private static final int ERROR_USER_WAS_DELETED_OR_BANNED = 18;
    static public final boolean VK_VERSION_USING = false;

    private int MEETINGS_COUNT_TO_SEND = 5;
    private int STEP_PERCENT = 5;

    private int mPermissionDeniedCounter = 0;
    private int mCounterAgain = 0;
    private int mEmptyGroupsAnswer = 0;
    private int mFriendsProcessedCounter = 0;
    private int mUserUnavailable = 0;
    private int mQueueCounter = 1;

    private int mFriendsCount = 0;
    private Map<String, Event> mEvents = new TreeMap<>();
    private ArrayList<Meeting> mMeetings = new ArrayList<>();

    private Thread mCollectEventsThread;

    private FeDataManager feDataManager = new FeDataManager();

    private EventsListener mListener;

    public interface EventsListener {
        void OnUpdateEventsCount(int count);
        void OnUpdateFEProcessedCount(int count);
    }

    public VkEventsManager(EventsListener listener) {
        mListener = listener;
    }

    public void setMeetingsPackCount(int count) {
        MEETINGS_COUNT_TO_SEND = count;
    }

    public void collectEvents(final Collection<String> friendsIds) {
        mCollectEventsThread = new Thread(new Runnable() {
            @Override
            public void run() {
                collectEventsInternal(friendsIds);
            }
        });
        mCollectEventsThread.start();
    }

    public void collectEventsInternal(final Collection<String> friendsIds) {
        System.out.println("Total friends: " + friendsIds.size());
        mFriendsCount = friendsIds.size();
        mEvents.clear();
        int maxFriends = 1;
        Integer percent = 0;
        for(String id : friendsIds) {
            try {
                MyUtils.sleepT();
            } catch (InterruptedException e) {
                System.out.println("Stop collecting events. Total events: " + mEvents.size());
                return;
            }
            getEvent(id);
//            if(mFriendsProcessedCounter > maxFriends)
//                break;
            mFriendsProcessedCounter++;
            Integer oldPercent = percent;
            percent = 100 * mFriendsProcessedCounter / mFriendsCount;
            if(percent % STEP_PERCENT == 0 && percent != oldPercent) {
                System.out.println(percent + "% processed");
            }
            if (percent % 1 == 0 && percent != oldPercent) {
                if(mListener != null)
                    mListener.OnUpdateFEProcessedCount(percent);
            }
        }
        sendMeetings();
        System.out.println("All friends processed");
    }

    private void getEvent(final String user_id) {
        //System.out.println("Start getEvent() for " + user_id);
        String targetURL = VkUrlProvider.requestGroups(user_id);
        MyHttpURLConnection.executeSendAsync(targetURL, this, MyHttpURLConnection.GROUPS_TYPE, user_id);
    }

    private void parseEventsResponse(final String answer, final String userId) {
        if(answer.equals("{\"response\":[0]}")) {
            mEmptyGroupsAnswer++;
            //System.out.println("parseEventsResponse() : mEmptyGroupsAnswer = " + mEmptyGroupsAnswer);
        } else {
            try {
                parseEvents(answer, userId);
            } catch (JSONException e) {
                String errUserId = parseErrorResponse(answer);

                if(errUserId == null)
                    System.out.println(answer);

                if (errUserId != null && !errUserId.isEmpty()) {
                    MyUtils.sleep();
                    getEvent(errUserId);
                }

            }
        }
    }

    public String parseErrorResponse(final String answer) {
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
                    return "";
                }
            } else if(errorCode == ERROR_PERMISSION_DENIED) {
                mPermissionDeniedCounter++;
                return "";
            } else if(errorCode == ERROR_USER_WAS_DELETED_OR_BANNED) {
                mUserUnavailable++;
                return "";
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    private void parseEvents(final String answer, final String userId) throws JSONException {
        JSONObject jsonObj;
        if(!VK_VERSION_USING) {
            String correctAnswer = correctAnswer(answer);
            jsonObj = new JSONObject(correctAnswer);
        } else {
            jsonObj = new JSONObject(answer);
        }
        JSONObject jsonResponse = jsonObj.getJSONObject("response");
        JSONArray jsonArray = jsonResponse.getJSONArray("items");
        int length = jsonArray.length();

        int counter = 0;
        long unixTime = System.currentTimeMillis() / 1000L;

        for (int i = 0; i < length; i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            if(obj.has("type")) {
                if (obj.getString("type").equals("event")) {
                    Event event = null;
                    if(VK_VERSION_USING) {
                        event = parseOneEvent(obj);
                    } else {
                        event = parseOneEventWithoutVersion(obj);
                    }

                    int curStartTime = 0;
                    try {
                        curStartTime = Integer.valueOf(event.start_date);
                    } catch (Exception e) {
                        System.out.println("Error of start_date");
                        //e.printStackTrace();
                    }
                    if (curStartTime > unixTime) {
                        counter++;
                        String eventId = Integer.toString(event.id);
                        if(!mEvents.containsKey(eventId)) {
                            System.out.println(" - FE - " + event.name);
                            mEvents.put(eventId, event);
                            mMeetings.add(ImportMeetingCreator.createMeeting(event, mQueueCounter));
                        } else {
                            mCounterAgain++;
                        }

                        if(mMeetings.size() >= MEETINGS_COUNT_TO_SEND) {
                            sendMeetings();
                            mQueueCounter++;
                        }
                    }
                }
            }
        }

        if(mListener != null)
            mListener.OnUpdateEventsCount(mEvents.size());

        if(counter > 0) {
            System.out.println("parseEvents() for " + userId + " : collected " + counter + " future events. Total Events: " +
                    mEvents.size() + ". Duplicates: " + mCounterAgain + ". PermissionDenied: " + mPermissionDeniedCounter +
                    ". Banned/Deleted: " + mUserUnavailable + ". Empty groupes: " + mEmptyGroupsAnswer +
                    ". Friends processed: " + mFriendsProcessedCounter + " / " + mFriendsCount +
                    " (" + mFriendsProcessedCounter/mFriendsCount + ")");
//        System.out.println("-------------------------------------------------------------");
        }
    }

    public void sendMeetings() {
        ArrayList<Meeting> meetings = new ArrayList<>();
        meetings.addAll(mMeetings);
        feDataManager.sendMeetings(meetings);
        mMeetings.clear();
    }

    static private String correctAnswer(final String answer) {
        String res = answer;
        try {
            int idx_body_start = answer.indexOf('{', 2);
            int idx_count_start = answer.indexOf('[', 2) + 1;
            int idx_count_end = answer.indexOf(',', idx_count_start);
            String count_str = answer.substring(idx_count_start, idx_count_end);
            String body = answer.substring(idx_body_start);

            String s = "{\"response\":{\"count\":%s,\"items\":[";
            String head = String.format(s, count_str);

            res = head + body + "}";

        } catch (Exception e) {
            System.out.println("correctAnswer() : answer = " + answer);
            e.printStackTrace();
        }
        return res;
    }

    @Override
    public void OnFriendsResponse(String response, final String arg1) {

    }

    @Override
    public void OnGroupsResponse(String response, final String arg1) {
        parseEventsResponse(response, arg1);
    }


    private Event parseOneEventWithoutVersion(JSONObject jsonResponse) throws JSONException {
        Event event = new Event();
        if(jsonResponse.has("gid")) {
            event.id = jsonResponse.getInt("gid");
        }
        if(jsonResponse.has("name")) {
            event.name = jsonResponse.getString("name");
        }
        if(jsonResponse.has("photo_big")) {
            event.photo = jsonResponse.getString("photo_big");
        }

        // fields' values:
        // fields=start_date,finish_date,description,city,place,members_count,can_post
        if (jsonResponse.has("start_date")) {
            event.start_date = jsonResponse.getString("start_date");
        }
        if (jsonResponse.has("finish_date")) {
            event.finish_date = jsonResponse.getString("finish_date");
        }
        if(jsonResponse.has("description")) {
            event.description = jsonResponse.getString("description");
        }

        String place = "";
        if(jsonResponse.has("place")) {
            place = jsonResponse.getString("place");
            if(place != null) {
                JSONObject jsonObj = new JSONObject(place);
                if (jsonObj.has("title")) {
                    event.placeTitle = jsonObj.getString("title");
                }
                if (jsonObj.has("latitude")) {
                    String value = jsonObj.getString("latitude");
                    event.placeLat = string2Double(value);

                }
                if (jsonObj.has("longitude")) {
                    String value = jsonObj.getString("longitude");
                    event.placeLon = string2Double(value);
                }
                if (jsonObj.has("type")) {
                    int type = jsonObj.getInt("type");
                    // чтобы не вводить в Place int categoryId -
                    // записываем строку в placeCategory, которая потом запишется в Meeting::Place::category
                    try {
                        String s = String.valueOf(type);
                        event.placeCategory = s;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (jsonObj.has("address")) {
                    event.placeAddress = jsonObj.getString("address");
                }
                if (jsonObj.has("country")) {
                    event.socialCountryId = jsonObj.getInt("country");
                }
                if (jsonObj.has("city")) {
                    event.socialCityId = jsonObj.getInt("city");
                }
            }
        }

        if(jsonResponse.has("members_count")) {
            event.membersCount = jsonResponse.getInt("members_count");
        }
        if(jsonResponse.has("can_post")) {
            event.canPost = jsonResponse.getInt("can_post");
        }

        return event;
    }

    private Event parseOneEvent(JSONObject jsonResponse) throws JSONException {
        Event event = new Event();
        if(jsonResponse.has("id")) {
            event.id = jsonResponse.getInt("id");
        }
        if(jsonResponse.has("name")) {
            event.name = jsonResponse.getString("name");
        }
        if(jsonResponse.has("photo_200")) {
            event.photo = jsonResponse.getString("photo_200");
        }

        // fields' values:
        // fields=start_date,finish_date,description,city,place,members_count,can_post
        if (jsonResponse.has("start_date")) {
            event.start_date = jsonResponse.getString("start_date");
        }
        if (jsonResponse.has("finish_date")) {
            event.finish_date = jsonResponse.getString("finish_date");
        }
        if(jsonResponse.has("description")) {
            event.description = jsonResponse.getString("description");
        }

        String city = "";
        if(jsonResponse.has("city")) {
            city = jsonResponse.getString("city");
            if(city != null) {
                JSONObject jsonObj = new JSONObject(city);
                if (jsonObj.has("id")) {
                    event.socialCityId = jsonObj.getInt("id");
                }
                if (jsonObj.has("title")) {
                    event.cityName = jsonObj.getString("title");
                }
            }
        }
        String place = "";
        if(jsonResponse.has("place")) {
            place = jsonResponse.getString("place");
            if(place != null) {
                JSONObject jsonObj = new JSONObject(place);
                if (jsonObj.has("title")) {
                    event.placeTitle = jsonObj.getString("title");
                }
                if (jsonObj.has("latitude")) {
                    String value = jsonObj.getString("latitude");
                    event.placeLat = string2Double(value);

                }
                if (jsonObj.has("longitude")) {
                    String value = jsonObj.getString("longitude");
                    event.placeLon = string2Double(value);
                }
                if (jsonObj.has("type")) {
                    int type = jsonObj.getInt("type");
                    // чтобы не вводить в Place int categoryId -
                    // записываем строку в placeCategory, которая потом запишется в Meeting::Place::category
                    try {
                        String s = String.valueOf(type);
                        event.placeCategory = s;
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                if (jsonObj.has("address")) {
                    event.placeAddress = jsonObj.getString("address");
                }
                if (jsonObj.has("country")) {
                    event.socialCountryId = jsonObj.getInt("country");
                }
            }
        }

        if(jsonResponse.has("members_count")) {
            event.membersCount = jsonResponse.getInt("members_count");
        }
        if(jsonResponse.has("can_post")) {
            event.canPost = jsonResponse.getInt("can_post");
        }

        return event;
    }

    private Double string2Double(final String value) {
        Double res = 0.0;
        try {
            res = Double.parseDouble(value);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }
        return  res;
    }

    public void stopGettingEvents() {
        if(mCollectEventsThread != null)
            mCollectEventsThread.interrupt();
        mFriendsProcessedCounter = 0;
    }
}
