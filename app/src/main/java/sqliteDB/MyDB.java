package sqliteDB;

import java.util.List;


public class MyDB {

    private int id;
    private String username;
    private double mob;
    private String city;
    private List<String> groups;
    public MyDB() {

    }
    public MyDB(String username, double mob, String city, String group){
        this.username = username;
        this.mob = mob;
        this.city= city;
        this.groups.add(group);

    }
    public MyDB(String username, int mob, String city){
        this.username = username;
        this.mob = mob;
        this.city= city;

    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public double getMob() {
        return mob;
    }

    public void setMob(double mob) {
        this.mob = mob;
    }

    public String getCity() {
        return city;
    }

    public void setCity(String city) {
        this.city = city; }

    public List<String> getGroups() {
        return groups;
    }

    public void setGroups(List<String> groups){
        this.groups = groups;
    }
}
