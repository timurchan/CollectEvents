package ru.timurchan.fedata;

import ru.timurchan.model.Event;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 * Created by Timur on 14.01.2017.
 */

public class ImportMeetingCreator {
    private String MEETING_TAG = "may_2017";

    private Date mDate;
    private String mDateString;

    public ImportMeetingCreator() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
        mDate = new Date();
        mDateString = dateFormat.format(mDate);
    }

    /**
     public String header;
     public String photo;
     public String message;
     private String date;
     private String time;
     private String cityName = "";

     public String venue;       ?
     public String phone;       ?
     public int users;
     public int uid;
     public int tid;
     public int city = 0;       ?
     public Boolean expired = false;
     public Boolean closed = false;

     private UserStatus status = null;
     public AccessType accessType = AccessType.Public;

     public Place place = new Place();      ?
     public ArrayList<Interest> interests;
     public User owner = new User();
     */



    public SocialMeeting createMeeting(final Event event, final String tag) {
        SocialMeeting.SocialType type = SocialMeeting.SocialType.VK;
        SocialMeeting m = new SocialMeeting(type);

        if(tag == null || tag.isEmpty()) {
            m.tag = mDateString;
        } else {
            m.tag = tag;
        }

        m.header = event.name;
        m.message = event.description;

        m.startUxTime = event.start_date;
        m.finishUxTime = event.finish_date;

        m.setDate(HumanDateConverter.convertDateEpoch(event.start_date, HumanDateConverter.Format.Simple));
        m.setTime(HumanDateConverter.convertTimeEpoch(event.start_date));

        m.setCityName(event.cityName);
        m.socialCityId = event.socialCityId;
        m.socialCountryId = event.socialCountryId;

        // add picture from evenet from Vkontakte
        m.imageUrls = new ArrayList<>();
        m.imageUrls.add(event.photo);


        m.place = new Place();
        m.place.lat = event.placeLat;
        m.place.lng = event.placeLon;
        m.place.category = event.placeCategory;
        m.place.setTitle(event.placeTitle);
        m.place.setAddress(event.placeAddress);

        // VK API bug hack
        if(event.name.equals(event.placeTitle) && event.placeAddress != null) {
            int comma = event.placeAddress.lastIndexOf(",");
            if(comma > 0) {
                m.place.setAddress(event.placeAddress.substring(0, comma).trim());
                m.place.setTitle(event.placeAddress.substring(comma + 1).trim());
            }
        }

        m.membersCount = event.membersCount;
        m.canPost = event.canPost;
        m.socialEventId = event.id;

        m.contacts = event.contacts;

        System.out.println(m.socialEventId + ", " +
                m.startUxTime + ", " +
                m.finishUxTime + ", " +
                m.getDateTime()
        );

        return m;
    }
}
