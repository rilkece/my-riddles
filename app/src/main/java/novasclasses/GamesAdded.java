package novasclasses;

public class GamesAdded {
    private String id;
    private String addedDate;
    private String completed;

    public GamesAdded(){}

    public GamesAdded(String id, String addedDate, String completed) {
        this.id = id;
        this.addedDate = addedDate;
        this.completed = completed;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAddedDate() {
        return addedDate;
    }

    public void setAddedDate(String addedDate) {
        this.addedDate = addedDate;
    }

    public String getCompleted() {
        return completed;
    }

    public void setCompleted(String completed) {
        this.completed = completed;
    }
}
