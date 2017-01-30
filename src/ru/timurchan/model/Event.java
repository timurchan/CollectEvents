package ru.timurchan.model;

/**
 * Created by Timur on 30.01.2017.
 */
public class Event {

    public int id;
    public String name;
    public String photo;

    public String start_date;
    public String finish_date;
    public String description;

    public String cityName;
    public int socialCityId = 0;
    public int socialCountryId = 0;

    public String placeTitle;
    public double placeLat = 0;
    public double placeLon = 0;
    public String placeAddress;
    public String placeCategory;

    public int membersCount = 0;
    public int canPost = 0;

    public int socialType = 5; // VK

    public Event() {}


    @Override
    public String toString() {
        return "SocialGroup{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", start_date='" + start_date + '\'' +
                ", finish_date='" + finish_date + '\'' +

                ", cityName='" + cityName + '\'' +
                ", socialCityId='" + socialCityId + '\'' +
                ", socialCountryId='" + socialCountryId + '\'' +

                ", placeTitle='" + placeTitle + '\'' +
                ", placeLat='" + placeLat + '\'' +
                ", placeLon='" + placeLon + '\'' +
                ", placeAddress='" + placeAddress + '\'' +

                ", photo='" + photo + '\'' +

                ", placeCategory='" + placeCategory + '\'' +
                ", members_count='" + membersCount + '\'' +
                ", can_post='" + canPost + '\'' +

                ", description='" + description + '\'' +
                '}';
    }
}