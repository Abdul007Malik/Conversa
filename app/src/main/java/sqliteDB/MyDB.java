package sqliteDB;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class MyDB {

    private String id;
    private String username;
    private double mob;
    private String city;
    private List<String> groups;
    public MyDB() {

    }
    public MyDB(String username, double mob, String city, List<String> groups){
        this.username = username;
        this.mob = mob;
        this.city= city;
        this.groups = groups;

    }

    public MyDB(String username, int mob, String city){
        this.username = username;
        this.mob = mob;
        this.city= city;

    }

    public void setMyDB(String username, double mob, String city, List<String> groups){
        this.username = username;
        this.mob = mob;
        this.city= city;
        this.groups = groups;
    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
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

    public void addGroup(String group){
        this.groups.add(group);
    }
}
