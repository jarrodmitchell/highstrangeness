package com.example.highstrangeness.ui.main.map;

import android.content.Context;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.example.highstrangeness.R;
import com.example.highstrangeness.utilities.PermissionsUtility;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;

import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = "MapFragment";

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        map = googleMap;
        map.setMinZoomPreference(1);
        map.setMaxZoomPreference(20);
        LatLng sydney = new LatLng(-34, 151);
        map.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 2));
    }

    Location _location;
    LocationManager locationManager;
    boolean shouldUpdateLocation;
    GoogleMap map;
    MapView mapView;
    Context context;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView: ");
        return inflater.inflate(R.layout.fragment_map, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        Log.d(TAG, "onViewCreated: ");
        if (getActivity() != null) {
            context = getActivity();
            mapView = getActivity().findViewById(R.id.mapMain);
            mapView.onCreate(null);
            mapView.onResume();
            mapView.getMapAsync(this);
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
    }

    private void getLocation() {
        PermissionsUtility.checkForGPSPermission(getActivity());
        _location = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if  (_location != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(_location.getLatitude(), _location.getLongitude()), 6));
        }
        shouldUpdateLocation = false;
        Log.d(TAG, "onMapReady: permissions here");
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (grantResults.length == 0) {
            map.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(_location.getLatitude(), _location.getLongitude())));
        }
    }
}