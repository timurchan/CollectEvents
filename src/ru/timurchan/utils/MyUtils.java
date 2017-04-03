package ru.timurchan.utils;

import com.google.gson.Gson;
import ru.timurchan.model.Friend;
import ru.timurchan.vkdata.City;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * Created by Timur on 29.01.2017.
 */
public class MyUtils {
    private static final int deltaTime = 370; // msec

    private static final String FRIENDS_PROCESSED_IDS_FNAME = "friends_processed_ids.txt";
    private static final String EVENTS_PROCESSED_IDS_FNAME = "events_processed_ids.txt";
    private static final String FRIENDS_FNAME = "friends.txt";
    private static final String FRIENDS_IDS_FNAME = "friends_ids.txt";
    private static final String FRIENDS_CITIES_FNAME = "friends_cities.txt";
    private static final String FRIENDS_CITIES_ORDERED_FNAME = "friends_cities_ordered.txt";


    static public String getCurrentTimeStamp() {
        //return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(new Date());
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss.SSS").format(Calendar.getInstance().getTime());
    }

    static public void sleep() {
        try {
            Thread.sleep(deltaTime);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    static public void sleepT() throws InterruptedException {
        Thread.sleep(deltaTime);
    }

    static public Map<String, Friend> loadFriends() {
        Map<String, Friend> res = new TreeMap<>();
        FastReader reader = new FastReader();
        Gson gson = new Gson();
        try {
            while (true) {
                String json_string = reader.nextLine();
                if (json_string == null || json_string.isEmpty())
                    break;
                Friend friend = gson.fromJson(json_string, Friend.class);
                res.put(friend.id, friend);
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    static public void saveFriends(final Map<String, Friend> friends) {
        int counter = 0;
        Gson gson = new Gson();
        try {
            PrintWriter out = new PrintWriter(FRIENDS_FNAME);
            for (Map.Entry<String, Friend> entry : friends.entrySet()) {
                String resultJson = gson.toJson(entry.getValue());
                out.println(resultJson);
                counter++;
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(counter + " friends saved to " + FRIENDS_FNAME + ". mFriends.size() = " + friends.size());

        saveFriendsIds(friends);
    }

    static public void saveFriendsIds(final Map<String, Friend> friends) {
        Gson gson = new Gson();
        try {
            PrintWriter out = new PrintWriter(FRIENDS_IDS_FNAME);
            String resultJson = gson.toJson(friends.keySet());
            out.println(resultJson);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(friends.size() + " friends saved to " + FRIENDS_IDS_FNAME);
    }

    static public void saveCities(final Map<Integer, City> cities) {
        City[] cityArray = cities.values().toArray(new City[cities.size()]);

        int counter = 0;
        Gson gson = new Gson();
        try {
            PrintWriter out = new PrintWriter(FRIENDS_CITIES_FNAME);
            for (int i = 0; i < cityArray.length; i++) {
                String resultJson = gson.toJson(cityArray[i]);
                out.println(resultJson);
                counter++;
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(counter + " cities save to " + FRIENDS_CITIES_FNAME + ". cities.size() = " + cities.size());
    }

    static public void saveCitiesOrdered(City... cityArray) {
        int counter = 0;
        Gson gson = new Gson();
        try {
            PrintWriter out = new PrintWriter(FRIENDS_CITIES_ORDERED_FNAME);
            for (int i = 0; i < cityArray.length; i++) {
                String resultJson = gson.toJson(cityArray[i]);
                out.println(resultJson);
                counter++;
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(counter + " cities save to " + FRIENDS_CITIES_ORDERED_FNAME + ". cities.size() = " + cityArray.length);
    }

    static public void saveCitiesOrdered(final Map<Integer, City> cities) {
        Set<City> set = new TreeSet<>();

        for (Map.Entry<Integer, City> entry : cities.entrySet()) {
            set.add(entry.getValue());
        }

        int counter = 0;
        Gson gson = new Gson();
        try {
            PrintWriter out = new PrintWriter(FRIENDS_CITIES_ORDERED_FNAME);
            for (City city : set) {
                String resultJson = gson.toJson(city);
                out.println(resultJson);
                counter++;
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(counter + " cities saved to " + FRIENDS_CITIES_ORDERED_FNAME + ". cities.size() = " + cities.size());
    }

    static public Set<Integer> loadProcessedIds(final String fname) {
        Set<Integer> res = new HashSet<>();
        Gson gson = new Gson();

        try {
            Integer[] ids = gson.fromJson(new FileReader(fname), Integer[].class);
            if(ids == null) {
                res = new HashSet<>();
            } else {
                res = new HashSet<>(Arrays.asList(ids));
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        System.out.println(res.size() + " ids loaded from " + fname);
        return res;
    }


    static public Set<Integer> loadProcessedFriends() {
        System.out.println("Loading processed friends ids");
        return loadProcessedIds(FRIENDS_PROCESSED_IDS_FNAME);
    }

    static public Set<Integer> loadProcessedEventIds() {
        System.out.println("Loading processed events ids");
        return loadProcessedIds(EVENTS_PROCESSED_IDS_FNAME);
    }

    // сохраняет id френдов, для которых уже были просмотрены события
    static public void saveProcessedFriends(final Collection<Integer> ids) {
        System.out.println("Saving processed friends ids");
        new Thread(new Runnable() {
            @Override
            public void run() {
                saveProcessedIds(ids, FRIENDS_PROCESSED_IDS_FNAME);
            }
        }).start();
    }

    // сохраняет id эвентов, для которые уже отправлялись на сервер
    static public void saveProcessedEventIds(final Collection<Integer> ids) {
        System.out.println("Saving processed events ids");
        new Thread(new Runnable() {
            @Override
            public void run() {
                saveProcessedIds(ids, EVENTS_PROCESSED_IDS_FNAME);
            }
        }).start();
    }

    static public void saveProcessedIds(final Collection<Integer> ids, final String fname) {
        Integer[] array = ids.toArray(new Integer[ids.size()]);

        Gson gson = new Gson();
        try {
            PrintWriter out = new PrintWriter(fname);
            String resultJson = gson.toJson(array);
            out.println(resultJson);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(ids.size() + " ids saved to " + fname);
    }

    static class FastReader {
        FileInputStream fis;
        BufferedReader br;
        StringTokenizer st;

        public FastReader() {
            try {
                fis = new FileInputStream(FRIENDS_FNAME);
                br = new BufferedReader(new InputStreamReader(fis));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //br = new BufferedReader(new InputStreamReader(System.in));
        }

        String nextLine() {
            if (br != null) {
                String str = "";
                try {
                    str = br.readLine();
                } catch (IOException e) {
                    e.printStackTrace();
                }
                return str;
            } else {
                System.out.println("Reader.nextLine() : br is null");
            }
            return "";
        }
    }
}
