package com.example.highstrangeness.utilities;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;
import android.os.AsyncTask;
import android.util.Log;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.List;
import java.util.Locale;

public class LocationUtility {

    public static final String TAG = "LocationUtility";

    public interface ReturnAddressListener {
        void returnAddress(final Address address);
    }

    public static void getAddress(Context context, LatLng latLng, ReturnAddressListener returnAddressListener) {
        Geocoder geocoder = new Geocoder(context, Locale.getDefault());
        try {
            if (latLng != null) {
                List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 20);
                if (addressList.size() > 0) {
                    Address address = addressList.get(0);
                    Log.d(TAG, "onMapClick: " + address.toString());

                    returnAddressListener.returnAddress(address);
                }else {
                    Log.d(TAG, "onMapLongClick: no address");
                }
            }else {
                Log.d(TAG, "onMapLongClick: no latlng");
            }

        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
