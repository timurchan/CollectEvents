package ru.timurchan.utils;

import ru.timurchan.vkdata.VkEventsManager;

/**
 * Created by Timur on 29.01.2017.
 */
public class VkUrlProvider {
    static final String mDomain = "https://api.vk.com/method/";

    public VkUrlProvider() {}

    public static String requestGroups(final String userId) {
        String url = mDomain + "groups.get?extended=1&fields=start_date,finish_date,description,place,members_count,can_post&uid=";
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
        return url + "&access_token=930dd0dd171eb219ba55834382d4a76aace9fc77f7549e6b5ee79b634dfa2f15a14c3bfaf81dac3b6ddc0";
    }
}
