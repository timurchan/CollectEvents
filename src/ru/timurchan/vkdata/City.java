package ru.timurchan.vkdata;

/**
 * Created by Timur on 31.01.2017.
 */
public class City implements Comparable {
    public Integer count;
    public Integer id;
    public String name;


    public City(int id, final String name) {
        this.id = id;
        this.name = name;
        count = 1;
    }

    public City(final City city) {
        this.id = city.id;
        this.name = city.name;
        this.count = city.count;
    }

    public int increment() { return ++count; }

    public boolean equals(Object o) {
        return (o instanceof City) && (((City)o).id == this.id && ((City)o).count == this.count);
    }

    public int hashCode() {
        return name.hashCode();
    }

    @Override
    public int compareTo(Object o) {
        City city = (City)o;
        if(this.count == city.count) {
            return id.compareTo(city.id);
        } else {
            return city.count.compareTo(count);
        }
    }
}
