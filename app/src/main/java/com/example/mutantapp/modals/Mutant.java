package com.example.mutantapp.modals;

import java.io.Serializable;

public class Mutant implements Serializable {
    private int id;
    private String name;
    private String hability;

    public Mutant(int id, String name, String hability) {
        this.id = id;
        this.name = name;
        this.hability = hability;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getHability() {
        return hability;
    }

    public void setHability(String hability) {
        this.hability = hability;
    }
}
