package ru.timurchan.fedata;

import java.io.Serializable;

public class User extends UserBase implements Serializable {

    public String sid = "";
    public String email = "";
    public String birthday = "";
    public Boolean isFriend = false;
    public Boolean isOrg = false;
    public int city = 0;
    public int sex = 0;
    public String midPhoto = "";
    public String cityName = "";
    public String metroName = "";
    public String about = "";
    public Double lat = 0.0;
    public Double lng = 0.0;

    public Integer attended = 0;
    public Integer passed = 0;

    public boolean equals(final User other) {
        if(this == other)
            return true;

        boolean res = sid.equals(other.sid);
        res = res && name.equals(other.name);
        res = res && email.equals(other.email);
        res = res && birthday.equals(other.birthday);
        res = res && uid == other.uid;
        res = res && city == other.city;
        res = res && sex == other.sex;
        res = res && midPhoto.equals(other.midPhoto);
        res = res && cityName.equals(other.cityName);
        res = res && lat == other.lat;
        res = res && lng == other.lng;

        boolean equalPhoto = false;
        if(photo == null && other.photo == null)
            equalPhoto = true;
        else if(photo != null && other.photo != null && photo.equals(other.photo))
            equalPhoto = true;
        res = res && equalPhoto;

        return res;
    }


    public String getBirthday() { return birthday; }
    public void setBirthday(String date)        {
        birthday = date;
    }

    public void updatePhoto(final String photo) {
        this.photo = photo;
        midPhoto = getMidPhotoUrl();
    }
}
