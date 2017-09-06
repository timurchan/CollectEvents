package ru.timurchan.vkdata;

/**
 * Created by Timur on 29.01.2017.
 */
public class VkUrlProvider {
    static final String mDomain = "https://api.vk.com/method/";

    public VkUrlProvider() {}

    public static String requestGroups(final String userId) {

        String fields = "contacts,start_date,finish_date,description,place,members_count,can_post";

        String url = mDomain + "groups.get?extended=1&fields=" + fields + "&uid=";
        url += userId;
        url = addToken(url);
        if(VkEventsManager.VK_VERSION_USING) {
            url = addVersion(url);
        }
        return url;
    }

    public static String requestFriends(final String userId) {
        String url = mDomain + "friends.get?user_id=";
        url += userId;
        url += "&fields=city,domain,country";
        return common(url);
    }

    public static String common(final String url) {
        String res = addToken(url);
        res = addVersion(res);
        return res;
    }

    public static String addVersion(final String url) {
        return url + "&v=5.62";
    }

    public static String addToken(final String url) {
        //return url + "&access_token=6996008948abf8a78a514e2c11827927dd76f82005823c44a75c4f1b47561ddade3eeb7d32290e5fa595b"; // tester's account
        return url + "&access_token=d57321f54a92126034280f60a92a77d1f6ce9377a0c115483acaad9cc9e929888b686fbecf9d558a8230f"; // Tims's account

    }
}
