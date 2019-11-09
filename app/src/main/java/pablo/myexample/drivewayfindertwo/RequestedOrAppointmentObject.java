package pablo.myexample.drivewayfindertwo;

public class RequestedOrAppointmentObject {

    private String date, location, imageUrl, ownerName, ownerId, ownerPhoneNumber, rate, driverName, driverCarModel, driverLicensePlates, driverPhoneNumber, timeSlot;

    RequestedOrAppointmentObject() {
    }

    public RequestedOrAppointmentObject(String date, String location, String imageUrl, String ownerName, String ownerId, String ownerPhoneNumber, String rate, String driverName, String driverCarModel, String driverLicensePlates, String driverPhoneNumber, String timeSlot) {
        this.date = date;
        this.location = location;
        this.imageUrl = imageUrl;
        this.ownerName = ownerName;
        this.ownerId = ownerId;
        this.ownerPhoneNumber = ownerPhoneNumber;
        this.rate = rate;
        this.driverName = driverName;
        this.driverCarModel = driverCarModel;
        this.driverLicensePlates = driverLicensePlates;
        this.driverPhoneNumber = driverPhoneNumber;
        this.timeSlot = timeSlot;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public String getOwnerName() {
        return ownerName;
    }

    public void setOwnerName(String ownerName) {
        this.ownerName = ownerName;
    }

    public String getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(String ownerId) {
        this.ownerId = ownerId;
    }

    public String getOwnerPhoneNumber() {
        return ownerPhoneNumber;
    }

    public void setOwnerPhoneNumber(String ownerPhoneNumber) {
        this.ownerPhoneNumber = ownerPhoneNumber;
    }

    public String getRate() {
        return rate;
    }

    public void setRate(String rate) {
        this.rate = rate;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverCarModel() {
        return driverCarModel;
    }

    public void setDriverCarModel(String driverCarModel) {
        this.driverCarModel = driverCarModel;
    }

    public String getDriverLicensePlates() {
        return driverLicensePlates;
    }

    public void setDriverLicensePlates(String driverLicensePlates) {
        this.driverLicensePlates = driverLicensePlates;
    }

    public String getDriverPhoneNumber() {
        return driverPhoneNumber;
    }

    public void setDriverPhoneNumber(String driverPhoneNumber) {
        this.driverPhoneNumber = driverPhoneNumber;
    }

    public String getTimeSlot() {
        return timeSlot;
    }

    public void setTimeSlot(String timeSlot) {
        this.timeSlot = timeSlot;
    }
}
