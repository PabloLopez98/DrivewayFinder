package pablo.myexample.drivewayfinder;

import pablo.myexample.drivewayfindertwo.DriverProfileObject;

public interface TransferObjectInterface {

    void transferOwnerProfileObject(OwnerProfileObject ownerProfileObject);
    void transferSpotObject(SpotObjectClass spotObject);
    void transferDriverProfileObject(DriverProfileObject driverProfileObject);
}
