package com.example.corey.locationadvertising;

/**
 * Created by corey on 28/02/2018.
 */
import android.content.Context;
import android.content.res.Resources;
import android.util.Log;

import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.location.GeofenceStatusCodes;

/**
 * Geofence error codes mapped to error messages.
 */
class GeofenceErrorMessages {
    /**
     * Prevents instantiation.
     */
    private GeofenceErrorMessages() {}

    /**
     * Returns the error string for a geofencing exception.
     */
    public static String getErrorString(Context context, Exception e) {
        if (e instanceof ApiException) {
            return getErrorString(context, ((ApiException) e).getStatusCode());
        } else {
            return String.valueOf( Log.d("Error","Some Error") );
        }
    }

    /**
     * Returns the error string for a geofencing error code.
     */
    public static String getErrorString(Context context, int errorCode) {
        Resources mResources = context.getResources();
        switch (errorCode) {
            case GeofenceStatusCodes.GEOFENCE_NOT_AVAILABLE:
                return String.valueOf( Log.d("Error","Geofence not available") );
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_GEOFENCES:
                return String.valueOf( Log.d("Error","Too many geofences") );
            case GeofenceStatusCodes.GEOFENCE_TOO_MANY_PENDING_INTENTS:
                return String.valueOf( Log.d("Error","Too mnay pending intents") );
            default:
                return String.valueOf( Log.d("Error","Something wrong") );
        }
    }
}