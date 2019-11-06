package pablo.myexample.drivewayfindertwo;

import pablo.myexample.drivewayfinder.DriverRoute;

public class DriverProfileObject {

    private String driverName, driverPhoneNumber, driverLicensePlates, driverCarModel;

    DriverProfileObject() {}

    public DriverProfileObject(String driverName, String driverPhoneNumber, String driverLicensePlates, String driverCarModel) {
        this.driverName = driverName;
        this.driverPhoneNumber = driverPhoneNumber;
        this.driverLicensePlates = driverLicensePlates;
        this.driverCarModel = driverCarModel;
    }

    public String getDriverName() {
        return driverName;
    }

    public void setDriverName(String driverName) {
        this.driverName = driverName;
    }

    public String getDriverPhoneNumber() {
        return driverPhoneNumber;
    }

    public void setDriverPhoneNumber(String driverPhoneNumber) {
        this.driverPhoneNumber = driverPhoneNumber;
    }

    public String getDriverLicensePlates() {
        return driverLicensePlates;
    }

    public void setDriverLicensePlates(String driverLicensePlates) {
        this.driverLicensePlates = driverLicensePlates;
    }

    public String getDriverCarModel() {
        return driverCarModel;
    }

    public void setDriverCarModel(String driverCarModel) {
        this.driverCarModel = driverCarModel;
    }
}
