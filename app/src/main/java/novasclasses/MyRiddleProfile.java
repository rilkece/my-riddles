package novasclasses;

import java.util.Calendar;

public class MyRiddleProfile {

    private String id;
    private String name;
    private String date;
    private String desc;
    private String added;
    private String completed;
    private String fases;
    private String author;

    public MyRiddleProfile(){

    }

    public MyRiddleProfile(String id, String name, String date, String desc, String added, String completed, String fases, String author) {
        this.id = id;
        this.name = name;
        this.date = date;
        this.desc = desc;
        this.added = added;
        this.completed = completed;
        this.fases = fases;
        this.author = author;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getAdded() {
        return added;
    }

    public void setAdded(String added) {
        this.added = added;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }

    public String getFases() {
        return fases;
    }

    public void setFases(String fases) {
        this.fases = fases;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String author) {
        this.author = author;
    }

}
