package com.example.highstrangeness.ui.location_picker;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.SearchView;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.highstrangeness.R;
import com.example.highstrangeness.utilities.LocationUtility;
import com.example.highstrangeness.utilities.PermissionsUtility;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.Locale;

public class LocationPickerFragment extends Fragment {

    public static final String TAG = "LocationPickerFragment";

    private OnMapReadyCallback callback = new OnMapReadyCallback() {
        @Override
        public void onMapReady(GoogleMap googleMap) {
            Log.d(TAG, "onMapReady: ready");
            map = googleMap;
            map.setMinZoomPreference(1);
            map.setMaxZoomPreference(20);
            LatLng sydney = new LatLng(-34, 151);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 2));
            map.setOnMapClickListener(new GoogleMap.OnMapClickListener() {
                @Override
                public void onMapClick(LatLng latLng) {
                    if (latLng != null) {
                        LocationUtility.getAddress(context, latLng, (LocationPickerActivity) context);

                    }else {
                        Log.d(TAG, "onMapLongClick: no latlng");
                    }
                }
            });

            if (getActivity() != null && PermissionsUtility.checkForGPSPermission(getActivity())) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, new LocationListener() {
                    @Override
                    public void onLocationChanged(@NonNull Location location) {
                        if (shouldUpdateLocation) {
                            getLocation();
                        }
                    }
                });
            }else {
                PermissionsUtility.requestLocationPermissions(getActivity());
                Log.d(TAG, "onMapReady: no location");
            }
        }
    };

    private void getLocation() {
        PermissionsUtility.checkForGPSPermission(getActivity());
        _location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if  (_location != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(_location.getLatitude(), _location.getLongitude()), 6));
            shouldUpdateLocation = false;
        }
        Log.d(TAG, "onMapReady: permissions here");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0) {
            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(_location.getLatitude(), _location.getLongitude())));
        }
    }

    GoogleMap map;
    Location _location;
    LocationManager locationManager;
    androidx.appcompat.widget.SearchView searchView;
    TextView textViewNoResults;
    Geocoder geocoder;
    Context context;
    boolean shouldUpdateLocation;
    Address[] listLocationResults;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_location_picker, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        SupportMapFragment mapFragment =
                (SupportMapFragment) getChildFragmentManager().findFragmentById(R.id.map);

        locationManager = (LocationManager) requireActivity().getSystemService(Context.LOCATION_SERVICE);
        if (mapFragment != null) {
            Log.d(TAG, "onViewCreated: map here");
            mapFragment.getMapAsync(callback);
        }
        shouldUpdateLocation = true;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        if (getActivity() != null) {
            context = getActivity();
            geocoder = new Geocoder(getActivity(), Locale.getDefault());
            textViewNoResults = getActivity().findViewById(R.id.textViewNoResults);
            searchView = getActivity().findViewById(R.id.searchViewLocation);
            searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
                @Override
                public boolean onQueryTextSubmit(String query) {
                    Log.d(TAG, "onQueryTextChange: " + query);
                    if (query.isEmpty()) {
                        textViewNoResults.setVisibility(View.GONE);
                    }else{
                        try {
                            listLocationResults = geocoder.getFromLocationName(query, 20).toArray(new Address[0]);
                            Log.d(TAG, "onQueryTextChange: " + listLocationResults.length);
                            if (listLocationResults.length > 0) {
                                textViewNoResults.setVisibility(View.GONE);
                                for (Address address: listLocationResults) {
                                    Log.d(TAG, "onQueryTextChange: gle" + address.toString());
                                    CameraUpdate cameraUpdate =  CameraUpdateFactory.newLatLngZoom(new LatLng(address.getLatitude(), address.getLongitude()), 12);
                                    map.animateCamera(cameraUpdate);
                                }
                            }else {
                                textViewNoResults.setVisibility(View.VISIBLE);
                            }
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                    return false;
                }

                @Override
                public boolean onQueryTextChange(String newText) {
                    return false;
                }
            });
        }
    }
}