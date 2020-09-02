package com.example.highstrangeness.ui.main.map;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.location.Location;
import android.location.LocationManager;
import android.media.Image;
import android.os.Bundle;
import android.text.SpannableString;
import android.text.style.UnderlineSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.Fragment;

import com.example.highstrangeness.R;
import com.example.highstrangeness.objects.MapState;
import com.example.highstrangeness.objects.Post;
import com.example.highstrangeness.ui.main.MainActivity;
import com.example.highstrangeness.utilities.StorageUtility;
import com.google.android.gms.maps.CameraUpdate;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class MapFragment extends Fragment implements OnMapReadyCallback {

    public static final String TAG = "MapFragment";

    @Override
    public void onMapReady(GoogleMap googleMap) {
        Log.d(TAG, "onMapReady: ");
        map = googleMap;

        if (MapState.savedState != null) {
            map.setMapType(MapState.savedState.getMapType());
            map.setMaxZoomPreference(MapState.savedState.getMaxZoom());
            map.setMinZoomPreference(MapState.savedState.getMinZoom());
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(MapState.savedState.getCameraPosition());
            map.moveCamera(cameraUpdate);
            MapState.savedState = null;
            addMarkersForPosts();
        }else{
            map.setMinZoomPreference(1);
            map.setMaxZoomPreference(20);
            LatLng sydney = new LatLng(-34, 151);
            map.animateCamera(CameraUpdateFactory.newLatLngZoom(sydney, 2));
        }
        map.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {
            @Override
            public View getInfoWindow(Marker marker) {
                Post post = (Post) marker.getTag();
                if (post != null) {
                    View markerInfoView = getLayoutInflater().inflate(R.layout.recycle_row_post, null);
                    ImageView imageViewUserPic = markerInfoView.findViewById(R.id.imageViewUserPost);
                    TextView textViewUsername = markerInfoView.findViewById(R.id.textViewUserNamePost);
                    TextView textViewDate = markerInfoView.findViewById(R.id.textViewDatePost);
                    TextView textViewTitle = markerInfoView.findViewById(R.id.textViewTitlePost);
                    TextView textViewTags = markerInfoView.findViewById(R.id.textViewTagsPost);
                    TextView textViewDescription = markerInfoView.findViewById(R.id.textViewDescriptionPost);
                    TextView textViewContentTypes = markerInfoView.findViewById(R.id.textViewContentTypes);
                    TextView textViewFirstHand = markerInfoView.findViewById(R.id.textViewFirstHandPost);
                    markerInfoView.findViewById(R.id.dividerPostRow).setVisibility(View.GONE);
                    markerInfoView.setPadding(24, 24, 24, 24);
                    markerInfoView.setBackground(ContextCompat.getDrawable(context, R.drawable.info_view_back));

                    StorageUtility.setProfileImage(getActivity(), post.getUserId(), 0, imageViewUserPic);
                    imageViewUserPic.setImageResource(R.drawable.user);

                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(post.getUsername());
                    stringBuilder.append(" ·");
                    String username = stringBuilder.toString();
                    String date = DateFormat.getDateInstance().format(post.getDate());
                    String title = post.getTitle();
                    stringBuilder = new StringBuilder();

                    ArrayList<String> tags = post.getTags();
                    for (int i = 0; i < tags.size(); i ++) {
                        stringBuilder.append(tags.get(i));
                        if (i != tags.size() - 1) {
                            stringBuilder.append(", ");
                        }
                    }
                    String tagsString = stringBuilder.toString();
                    SpannableString spannableString = new SpannableString(tagsString);
                    spannableString.setSpan(new UnderlineSpan(), 0, tagsString.length(), 0);
                    String description = post.getDescription();

                    ArrayList<String> contentTypesList = post.getContentTypes();
                    stringBuilder = new StringBuilder();

                    for (int i = 0; i < contentTypesList.size(); i ++) {
                        stringBuilder.append(contentTypesList.get(i));
                        if (i != contentTypesList.size() - 1) {
                            stringBuilder.append(" · ");
                        }
                    }
                    String contentTypes = stringBuilder.toString();
                    boolean firstHand = post.isFirstHand();

                    textViewUsername.setText(username);
                    textViewDate.setText(date);

                    textViewTitle.setText(title);
                    if (!spannableString.toString().isEmpty()) {
                        textViewTags.setVisibility(View.VISIBLE);
                        textViewTags.setText(spannableString);
                    }else {
                        textViewTags.setVisibility(View.GONE);
                    }

                    textViewDescription.setText(description);
                    if (!contentTypes.trim().isEmpty()) {
                        textViewContentTypes.setVisibility(View.VISIBLE);
                        textViewContentTypes.setText(contentTypes);
                    }else {
                        textViewContentTypes.setVisibility(View.GONE);
                    }

                    if(firstHand) {
                        textViewFirstHand.setVisibility(View.VISIBLE);
                    }else {
                        textViewFirstHand.setVisibility(View.GONE);
                    }
                    return markerInfoView;
                }
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {
                return null;
            }
        });
        map.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                infoViewClickListener.onClick((Post) marker.getTag());
            }
        });
    }

    public interface GetPostsListener {
        List<Post> getPosts();
    }

    public interface OnInfoViewClickListener {
        void onClick(Post post);
    }

    GetPostsListener getPostsListener;
    OnInfoViewClickListener infoViewClickListener;
    UpdatedListReceiver updatedListReceiver;
    LocationManager locationManager;
    boolean isFirstRun = true;
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
            locationManager = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
            getPostsListener = (GetPostsListener) getActivity();
            infoViewClickListener = (OnInfoViewClickListener) getActivity();
            updatedListReceiver = new UpdatedListReceiver();
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

    @Override
    public void onResume() {
        super.onResume();
        context.registerReceiver(updatedListReceiver, new IntentFilter(MainActivity.ACTION_LIST_UPDATED));

    }

    @Override
    public void onPause() {
        super.onPause();
        context.unregisterReceiver(updatedListReceiver);
    }

    @Override
    public void onStop() {
        super.onStop();
        MapState.savedState = new MapState(map.getCameraPosition(), map.getMaxZoomLevel(), map.getMinZoomLevel(), map.getMapType());
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);
        Log.d(TAG, "onSaveInstanceState: ");
        outState.putParcelable("Map", map.getCameraPosition());
    }

    @Override
    public void onViewStateRestored(@Nullable Bundle savedInstanceState) {
        super.onViewStateRestored(savedInstanceState);
        if (savedInstanceState != null) {
            CameraPosition  cameraPosition = savedInstanceState.getParcelable("Map");
            CameraUpdate cameraUpdate = CameraUpdateFactory.newCameraPosition(cameraPosition);
            map.moveCamera(cameraUpdate);
        }
    }

    private void addMarkersForPosts() {
        Log.d(TAG, "addMarkersForPosts: ");
        List<Post> posts = getPostsListener.getPosts();
        if (posts != null && posts.size() > 0) {
            map.clear();
            for (Post post: posts) {
                setMarker(new LatLng(post.getLatitude(), post.getLongitude()), post);
            }
        }
    }

    private void setMarker(LatLng latLng, Post post) {
        MarkerOptions markerOptions = new MarkerOptions();
        markerOptions.position(latLng);
        markerOptions.icon(BitmapDescriptorFactory.defaultMarker(337));
        map.addMarker(markerOptions).setTag(post);
    }

    private class UpdatedListReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            if (intent.getAction() != null && intent.getAction().equals(MainActivity.ACTION_LIST_UPDATED)) {
                Log.d(TAG, "onReceive: update");
                addMarkersForPosts();
            }
        }
    }
}