package models;

import java.util.Objects;

public class Foodtype {
    private String name;
    private int id;

    public Foodtype(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Foodtype foodtype = (Foodtype) o;
        return getId() == foodtype.getId() &&
                Objects.equals(getName(), foodtype.getName());
    }

    @Override
    public int hashCode() {
        return Objects.hash(getName(), getId());
    }
}
