package ru.timurchan.fedata;


import java.io.Serializable;
import java.util.ArrayList;

public class Meeting  {

    private int participantsCount = 0;

    public enum AccessType {
        Public, Moderated, Friends, Group, Private
    }

    public enum UserStatus {
        Unknown, Attended, Passed, Invited, Confirmed, Pending, Declined, Owner
    }

    public String startUxTime;
    public String finishUxTime;

    public String header;
    public String photo;        // photo of organizer
    public String message;
    private String date;
    private String time;
    private String cityName = "";

    public String venue;
    public String phone;
    public int users;
    public int uid;
    public int tid;
    public int city = 0;
    public Boolean expired = false;
    public Boolean closed = false;

    private UserStatus status = null;
    public AccessType accessType = AccessType.Public;

    public Place place = new Place();
    public ArrayList<Interest> interests;
    public User owner = new User();
    public String invitingName = "";

    public Meeting() {
    }

    public Meeting(int city, String cityName) {
        this.city = city;
        this.cityName = cityName;
    }

    public void addUser() { users++; };
    public void removeUser() { if(users > 0) users--; };

    public int getParticipantsCount() {
        return participantsCount;
    }

    public void setParticipantsCount(int participantsCount) {
        this.participantsCount = participantsCount;
    }

    public String getDate() { return date; }

    public void setDate(String date) {
        this.date = date;
    }

    public UserStatus getStatus() { return status; }

    public void setStatus(UserStatus status) {
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getCityName() {
        return cityName;
    }

    public void setCityName(String cityName) {
        this.cityName = cityName;
    }

    public Place getPlace() {
        return place;
    }

    public void setPlace(Place place) {
        this.place = place;
    }

    public void setPhoto(final String photo) {
        this.photo = photo;
    }

    public String getPhotoUrl() {
        if (photo != null)
            return "http://www.lookfun.ru/files/users/" + Integer.toString(uid) + "/tmb_" + photo;
        else
            return "http://www.lookfun.ru/img/nophoto.jpg";
    }

    static private boolean isEmptyData(final String data) {
        return data == null || data.isEmpty();
    }

    public boolean reportRequired(int uid) {
        return users > 1 && expired && this.uid == uid && !closed;
    }

//    public boolean isEmptyData() {
//        boolean res = isEmptyData(header);
//        res = res && isEmptyData(message);
//        res = res && isEmptyData(date);
//        res = res && isEmptyData(time);
//        res = res && place.isEmptyData();
//        res = res && (interests == null || interests.isEmpty());
//        res = res && isEmptyData(venue);
//        res = res && isEmptyData(phone);
//        return res;
//    }

    public boolean isEmptyNecessarilyData() {
        boolean res = isEmptyData(header);
        res = res || isEmptyData(message);
        res = res || isEmptyData(date);
        res = res || (interests == null || interests.isEmpty());
        return res;
    }

//    public boolean dataExpired() {
//        String currDate = DateFormatter.getDate();
//        String currTime = DateFormatter.getTime();
//        Meeting m = new Meeting();
//        m.setDate(currDate);
//        m.setTime(currTime);
//        return this.before(m, true);
//    }

    public boolean before(final Meeting other, boolean equalFlag) {
        if(date == null)
            return false;
        else if(other.date == null)
            return true;
        else {
            if (date.equals(other.date)) {
                if (time == null)
                    return false;
                else if (other.time == null)
                    return true;
                else {
                    if(equalFlag)
                        return time.compareTo(other.time) <= 0;
                    else
                        return time.compareTo(other.time) < 0;
                }
            }
            if(equalFlag)
                return date.compareTo(other.date) <= 0;
            else
                return date.compareTo(other.date) < 0;
        }
    }
    public boolean before(final Meeting other) {
        return before(other, false);
    }

    public String getDateTime() {

        if (date == null) return null;

        String ret = date;
        if (time != null) ret += " " + time;
        return ret;
    }

    public Meeting clone() throws CloneNotSupportedException {
        return (Meeting)super.clone();
    }

    public String accessType2String() {
        String res = "";
        switch (accessType) {
            case Moderated:
                res = "модерируемая встреча";
                break;
            case Friends:
                res = "встреча для друзей";
                break;
            case Group:
                res = "встреча для групп";
                break;
            case Private:
                res = "частная встреча";
                break;
            default:
                res = "";
        }
        return res;
    }
}