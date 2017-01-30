package ru.timurchan.fedata;

import java.io.Serializable;

public class CityBase  {

    public CityBase() {}

    public CityBase(int id, final String label) {
        this.id = id;
        this.label = label;
    }

    public int id = 0;
    public String label;
}
