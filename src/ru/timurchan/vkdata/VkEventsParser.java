package ru.timurchan.vkdata;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import ru.timurchan.fedata.Contact;
import ru.timurchan.model.Event;
import ru.timurchan.utils.MyUtils;

import java.util.ArrayList;

/**
 * Created by Timur on 04.09.2017.
 */
public class VkEventsParser {
    private static final int ERROR_TOO_MANY_REQUESTS = 6;
    private static final int ERROR_PERMISSION_DENIED = 7;
    private static final int ERROR_USER_WAS_DELETED_OR_BANNED = 18;

    private int mPermissionDeniedCounter = 0;
    private int mUserUnavailable = 0;

    public VkEventsParser() {

    }

    public int getPermissionDeniedCounter() {
        return mPermissionDeniedCounter;
    }

    public int getUserUnavailable() {
        return mUserUnavailable;
    }

    public String parseErrorResponse(final String answer) {
        JSONObject jsonResponse;
        try {
            JSONObject jsonObj = new JSONObject(answer);
            jsonResponse = jsonObj.getJSONObject("error");
            int errorCode = jsonResponse.optInt("error_code");
            if (errorCode == ERROR_TOO_MANY_REQUESTS) {
                JSONArray jsonArray = jsonResponse.getJSONArray("request_params");
                int length = jsonArray.length();
                for (int i = 0; i < length; i++) {
                    if (jsonArray.getJSONObject(i).has("user_id")) {
                        return jsonArray.getJSONObject(i).getString("user_id");
                    }
                    JSONObject obj = new JSONObject(jsonArray.optString(i));
                    if (obj.has("user_id")) {
                        return obj.optString("user_id");
                    }
                    return "";
                }
            } else if (errorCode == ERROR_PERMISSION_DENIED) {
                mPermissionDeniedCounter++;
                return "";
            } else if (errorCode == ERROR_USER_WAS_DELETED_OR_BANNED) {
                mUserUnavailable++;
                return "";
            }

        } catch (JSONException e) {
            e.printStackTrace();
            return null;
        }
        return null;
    }

    public ArrayList<Event> parseEvents(final String answer, final String userId) throws JSONException {
        JSONObject jsonObj;
        if (!VkEventsManager.VK_VERSION_USING) {
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

        ArrayList<Event> futureEvents = new ArrayList<>();
        for (int i = 0; i < length; i++) {
            JSONObject obj = jsonArray.getJSONObject(i);
            if (obj.has("type")) {
                if (obj.getString("type").equals("event")) {
                    Event event = null;
                    if (VkEventsManager.VK_VERSION_USING) {
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
                        //processFutureEvent(event);
                        futureEvents.add(event);
                        counter++;
                    }
                }
            }
        }

        return futureEvents;
    }

    private Event parsePlace(JSONObject jsonResponse, Event event) throws JSONException {
        String place = "";
        if (jsonResponse.has("place")) {
            place = jsonResponse.getString("place");
            if (place != null) {
                JSONObject jsonObj = new JSONObject(place);
                if (jsonObj.has("title")) {
                    event.placeTitle = jsonObj.getString("title");
                }
                if (jsonObj.has("latitude")) {
                    String value = jsonObj.getString("latitude");
                    event.placeLat = MyUtils.string2Double(value);

                }
                if (jsonObj.has("longitude")) {
                    String value = jsonObj.getString("longitude");
                    event.placeLon = MyUtils.string2Double(value);
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
        return event;
    }

    private Event parseContact(JSONObject jsonResponse, Event event) throws JSONException {
        String contacts = "";
        if (jsonResponse.has("contacts")) {
            JSONArray jsonArray = jsonResponse.getJSONArray("contacts");
            int length = jsonArray.length();

            ArrayList<Contact> contactsArray = new ArrayList<>();

            for (int i = 0; i < length; i++) {
                Contact contact = new Contact();
                JSONObject jsonObj = jsonArray.getJSONObject(i);
                if (jsonObj.has("user_id")) {
                    contact.userId = jsonObj.getInt("user_id");
                }
                if (jsonObj.has("desc")) {
                    contact.desc = jsonObj.getString("desc");
                }
                if (jsonObj.has("phone")) {
                    contact.phone = jsonObj.getString("phone");
                }
                if (jsonObj.has("email")) {
                    contact.email = jsonObj.getString("email");
                }
                contactsArray.add(contact);
            }

            event.contacts = contactsArray;
        }
        return event;
    }

    private Event parseOneEventWithoutVersion(JSONObject jsonResponse) throws JSONException {
        Event event = new Event();
        if (jsonResponse.has("gid")) {
            event.id = jsonResponse.getInt("gid");
        }
        if (jsonResponse.has("name")) {
            event.name = jsonResponse.getString("name");
        }
        if (jsonResponse.has("photo_big")) {
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
        if (jsonResponse.has("description")) {
            event.description = jsonResponse.getString("description");
        }

        event = parsePlace(jsonResponse, event);

        event = parseContact(jsonResponse, event);

        if (jsonResponse.has("members_count")) {
            event.membersCount = jsonResponse.getInt("members_count");
        }
        if (jsonResponse.has("can_post")) {
            event.canPost = jsonResponse.getInt("can_post");
        }

        return event;
    }

    private Event parseOneEvent(JSONObject jsonResponse) throws JSONException {
        Event event = new Event();
        if (jsonResponse.has("id")) {
            event.id = jsonResponse.getInt("id");
        }
        if (jsonResponse.has("name")) {
            event.name = jsonResponse.getString("name");
        }
        if (jsonResponse.has("photo_200")) {
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
        if (jsonResponse.has("description")) {
            event.description = jsonResponse.getString("description");
        }

        String city = "";
        if (jsonResponse.has("city")) {
            city = jsonResponse.getString("city");
            if (city != null) {
                JSONObject jsonObj = new JSONObject(city);
                if (jsonObj.has("id")) {
                    event.socialCityId = jsonObj.getInt("id");
                }
                if (jsonObj.has("title")) {
                    event.cityName = jsonObj.getString("title");
                }
            }
        }

        event = parsePlace(jsonResponse, event);

        event = parseContact(jsonResponse, event);

        if (jsonResponse.has("members_count")) {
            event.membersCount = jsonResponse.getInt("members_count");
        }
        if (jsonResponse.has("can_post")) {
            event.canPost = jsonResponse.getInt("can_post");
        }

        return event;
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
}
