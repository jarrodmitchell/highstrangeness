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


    public static class GetAddressAsyncTask extends AsyncTask<LatLng, Void, Address> {
        private WeakReference<Context> contextWeakReference;

        public GetAddressAsyncTask(final Context context) {
            contextWeakReference = new WeakReference<>(context);
            returnAddressListener = (ReturnAddressListener) contextWeakReference.get();
        }

        public interface ReturnAddressListener {
            void returnAddress(final Address address);
        }

        ReturnAddressListener returnAddressListener;

        @Override
        protected Address doInBackground(LatLng... latLngs) {
            LatLng latLng = latLngs[0];

            Geocoder geocoder = new Geocoder(contextWeakReference.get(), Locale.getDefault());
            try {
                if (latLng != null) {
                    List<Address> addressList = geocoder.getFromLocation(latLng.latitude, latLng.longitude, 20);
                    if (addressList.size() > 0) {
                        Address address = addressList.get(0);
                        Log.d(TAG, "onMapClick: " + address.toString());
                        return address;
                    }else {
                        Log.d(TAG, "onMapLongClick: no address");
                    }
                }else {
                    Log.d(TAG, "onMapLongClick: no latlng");
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Address address) {
            super.onPostExecute(address);
            returnAddressListener.returnAddress(address);
        }
    }
}
