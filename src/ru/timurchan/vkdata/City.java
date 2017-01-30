package ru.timurchan.vkdata;

/**
 * Created by Timur on 31.01.2017.
 */
public class City {
    public int id;
    public String name;
    public int count;

    public City(int id, final String name) {
        this.id = id;
        this.name = name;
        count = 1;
    }

    public int increment() { return ++count; }
}
