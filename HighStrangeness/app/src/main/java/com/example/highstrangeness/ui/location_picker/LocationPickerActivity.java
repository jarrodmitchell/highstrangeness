package com.example.highstrangeness.ui.location_picker;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.os.Bundle;
import android.os.Message;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import com.example.highstrangeness.R;
import com.example.highstrangeness.ui.account.edit_account.EditAccountActivity;
import com.example.highstrangeness.ui.main.new_post.NewPostActivity;
import com.example.highstrangeness.utilities.LocationUtility;

import java.util.Objects;

public class LocationPickerActivity extends AppCompatActivity implements LocationUtility.ReturnAddressListener {

    public static final String TAG = "LocationPickerActivity";

    @Override
    public void returnAddress(final Address address) {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        if (address != null) {
            alertDialog.setTitle("Save Location");
            alertDialog.setMessage(address.getAddressLine(0));
            alertDialog.setButton(DialogInterface.BUTTON_NEGATIVE, "Cancel", (Message) null);
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Save", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    Intent resultIntent = getIntent();
                    resultIntent.putExtra(NewPostActivity.EXTRA_ADDRESS_RETURN, new double[]{address.getLatitude(), address.getLongitude()});
                    setResult(Activity.RESULT_OK, resultIntent);
                    Log.d(TAG, "onClick: finish");
                    finish();
                }
            });
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
                    alertDialog.getButton(DialogInterface.BUTTON_NEGATIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.colorPrimaryDark));
                }
            });
        }else {
            alertDialog.setTitle("Invalid address");
            alertDialog.setMessage("Select a location on land");
            alertDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Ok", (Message) null);
            alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                @Override
                public void onShow(DialogInterface dialogInterface) {
                    alertDialog.getButton(DialogInterface.BUTTON_POSITIVE).setTextColor(ContextCompat.getColor(getApplicationContext(), R.color.pink));
                }
            });
        }
        alertDialog.show();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location_picker);
        Objects.requireNonNull(getSupportActionBar()).hide();
        getSupportFragmentManager().beginTransaction().replace(R.id.frameLayoutLocationPicker, new LocationPickerFragment()).commit();;
    }
}