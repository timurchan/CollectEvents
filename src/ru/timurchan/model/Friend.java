package ru.timurchan.model;

/**
 * Created by Timur on 30.01.2017.
 */
public class Friend {
    // {"id":588,"first_name":"Лена","last_name":"Остапенко","domain":"id588","city":{"id":2,"title":"Санкт-Петербург"},"online":1}
    public String id;
    public String domain;

    public String firstName;
    public String lastName;

    public int cityId;
    public String cityName;

    public int countryId;
    public String countryName;

    @Override
    public String toString() {
        final String sep = ", ";
        return "{" +
                id + sep +
                domain + sep +
                firstName + sep +
                lastName + sep +
                cityId + sep +
                cityName + sep +
                countryId + sep +
                countryName +
                "}";
    }
}
