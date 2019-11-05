package pablo.myexample.drivewayfinder;

import java.util.ArrayList;

public class SpotObjectClass {

    private ArrayList<String> timeSlots;
    private String ownerId, fullName, phoneNumber, drivewayLocation, drivwayImageUrl, rate, date;

    public SpotObjectClass(ArrayList<String> timeSlots, String ownerId, String fullName, String phoneNumber, String drivewayLocation, String drivwayImageUrl, String rate, String date) {
        this.timeSlots = timeSlots;
        this.ownerId = ownerId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.drivewayLocation = drivewayLocation;
        this.drivwayImageUrl = drivwayImageUrl;
        this.rate = rate;
        this.date = date;
    }

    public ArrayList<String> getTimeSlots() {
        return timeSlots;
    }

    public void setTimeSlots(ArrayList<String> timeSlots) {
        this.timeSlots = timeSlots;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getDrivewayLocation() {
        return drivewayLocation;
    }

    public void setDrivewayLocation(String drivewayLocation) {
        this.drivewayLocation = drivewayLocation;
    }

    public String getDrivwayImageUrl() {
        return drivwayImageUrl;
    }

    public void setDrivwayImageUrl(String drivwayImageUrl) {
        this.drivwayImageUrl = drivwayImageUrl;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}
