package com.dnitinverma.locationlibrary.interfaces;



import android.location.Address;
import android.location.Location;



public interface LocationsCallback {
    void setLocation(Location mCurrentLocation);
    void setAddress(Address address);
    void setLatAndLong(Address location);
    void disconnect();
}
