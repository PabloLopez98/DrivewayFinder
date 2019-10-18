package pablo.myexample.drivewayfinder;

public class CardDetailsRecyclerViewObject {

    private String time, name, status;

    void CardDetailsRecyclerView(){}

    public CardDetailsRecyclerViewObject(String time, String name, String status) {
        this.time = time;
        this.name = name;
        this.status = status;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
