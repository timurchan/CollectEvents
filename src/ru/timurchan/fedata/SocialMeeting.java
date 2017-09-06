package ru.timurchan.fedata;

import java.util.ArrayList;

/**
 * Created by tiga1115 on 17.01.2017.
 */

public class SocialMeeting extends Meeting {

    public enum SocialType {
        None, VK, FB
    }

    public SocialType socialType = SocialType.None;     // VK, FB, OD and others
    public int socialCityId = 0;
    public int socialCountryId = 0;
    public int membersCount = 0;                       // count of member in the social event
    public int canPost = 0;                             // could you post on the wall of event
    public int socialEventId = 0;

    public ArrayList<String> imageUrls;                 // набор картинок, описывающих встречу

    public String tag = "";

    public SocialMeeting(final SocialType socialType) {
        super();
        this.socialType = socialType;
    }

    public ArrayList<Contact> contacts;
}
