package com.example.highstrangeness.utilities;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.widget.Toast;

import javax.net.ssl.HttpsURLConnection;

public class NetworkUtility {

    public static boolean CheckNetworkConnection(Context context) {
        ConnectivityManager cm =
                (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);

        Network activeNetwork = cm.getActiveNetwork();

        if (activeNetwork != null) {
            return true;
        }
        Toast.makeText(context, "No Network Connection", Toast.LENGTH_LONG).show();
        return false;
    }



}
