package ru.timurchan.utils;

import com.google.gson.Gson;
import ru.timurchan.model.Friend;
import ru.timurchan.vkdata.City;

import java.io.*;
import java.util.*;

/**
 * Created by Timur on 29.01.2017.
 */
public class MyUtils {
    static final int deltaTime = 370; // msec

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
            while(true) {
                String json_string = reader.nextLine();
                if(json_string == null || json_string.isEmpty())
                    break;
                Friend friend = gson.fromJson(json_string, Friend.class);
                res.put(friend.id, friend);
            }
            return res;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return  null;
    }


    static public void saveFriends(final Map<String, Friend> friends) {
        String fname = "friends.txt";
        int counter = 0;
//        try {
//            PrintWriter out = new PrintWriter( fname );
//            for (Map.Entry<String, Friend> entry : friends.entrySet()) {
//                out.println(entry.getValue().toString());
//                counter++;
//            }
//        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//        }
//        System.out.println(counter + " friends save to " + fname + ". mFriends.size() = " + friends.size());

        //---------------------------------------------------------------------------------------------------
        counter = 0;
        Gson gson = new Gson();
        fname = "friends_json.txt";
        try {
            PrintWriter out = new PrintWriter( fname );
            for (Map.Entry<String, Friend> entry : friends.entrySet()) {
                String resultJson = gson.toJson(entry.getValue());
                out.println(resultJson);
                counter++;
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(counter + " friends save to " + fname + ". mFriends.size() = " + friends.size());


        //---------------------------------------------------------------------------------------------------
        counter = 0;
        fname = "friends_id_json.txt";
        try {
            PrintWriter out = new PrintWriter( fname );
            String resultJson = gson.toJson(friends.keySet());
            out.println(resultJson);
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(counter + " friends save to " + fname + ". mFriends.size() = " + friends.size());
    }

    static public void saveCities(final Map<Integer, City> cities) {
        String fname = "friends_cities.txt";
        int counter = 0;
        Gson gson = new Gson();
        try {
            PrintWriter out = new PrintWriter( fname );
            for (Map.Entry<Integer, City> entry : cities.entrySet()) {
                City city = new City(entry.getValue());
                String resultJson = gson.toJson(city);
                out.println(resultJson);
                counter++;
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(counter + " cities save to " + fname + ". cities.size() = " + cities.size());

        saveCities2(cities);
    }

    static public void saveCities2(final Map<Integer, City> cities) {
        Set<City> set = new TreeSet<>();

        for (Map.Entry<Integer, City> entry : cities.entrySet()) {
            City city = new City(entry.getValue());
            set.add(city);
            //set.add(entry.getValue());
        }

        String fname = "friends_cities2.txt";
        int counter = 0;
        Gson gson = new Gson();
        try {
            PrintWriter out = new PrintWriter( fname );
            for (City city : set) {
                String resultJson = gson.toJson(city);
                out.println(resultJson);
                counter++;
            }
            out.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println(counter + " cities save to " + fname + ". cities.size() = " + cities.size());
    }


    static class FastReader
    {
        FileInputStream fis;
        BufferedReader br;
        StringTokenizer st;

        public FastReader()
        {
            try {
                fis = new FileInputStream("friends_json.txt");
                br = new BufferedReader(new InputStreamReader(fis));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            //br = new BufferedReader(new InputStreamReader(System.in));
        }

        String nextLine()
        {
            if(br != null) {
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
