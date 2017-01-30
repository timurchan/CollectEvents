package ru.timurchan.fedata;


import java.io.Serializable;

public class UserBase implements Serializable, Cloneable {
    public String name = "";
    public int uid = 0;
    public int vkuid = 0;
    public String photo = "";

    public String getPhotoUrl() {
        if(uid == 0 && vkuid != 0)
            return photo;
        else if(photo != null)
            return "http://www.lookfun.ru/files/users/" + Integer.toString(uid) + "/tmb_" + photo;
        else
            return "http://www.lookfun.ru/img/nophoto.jpg";
    }

    public String getMidPhotoUrl() {
        if(photo != null)
            return "http://www.lookfun.ru/files/users/" + Integer.toString(uid) + "/mid_" + photo;
        else
            return "http://www.lookfun.ru/img/nophoto.jpg";
    }

    public User clone() throws CloneNotSupportedException {
        return (User)super.clone();
    }
}
