package com.josh.connexus.elements;

import java.util.Timer;
import java.util.TimerTask;
import android.content.Context;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

public class LocationFetcher {
    private Timer timer1;
    private LocationManager lm;
    private boolean gps_enabled=false;
    private boolean network_enabled=false;
    private Location networkLocation;
    private GetLastLocation timerTask;
    private Context context;

    public LocationFetcher(Context context){
        this.context = context;
    }

    public void getLocation(long timeOut)
    {
        //I use onLocationGot callback class to pass location value from MyLocation to user code.
        if(lm==null)
            lm = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);

        //exceptions will be thrown if provider is not permitted.
        try{gps_enabled=lm.isProviderEnabled(LocationManager.GPS_PROVIDER);}catch(Exception ex){}
        try{network_enabled=lm.isProviderEnabled(LocationManager.NETWORK_PROVIDER);}catch(Exception ex){}

        //don't start listeners if no provider is enabled
        if(!gps_enabled && !network_enabled) {
            onLocationGot(null);
            return;
        }

        try {
            if(gps_enabled)
                lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0, locationListenerGps);
            if(network_enabled)
                lm.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 0, 0, locationListenerNetwork);
        } catch (SecurityException e) {
            e.printStackTrace();
            onLocationGot(null);
        }
        timer1=new Timer();
        timerTask = new GetLastLocation();
        timer1.schedule(timerTask, timeOut);
    }

    LocationListener locationListenerGps = new LocationListener() {
        public void onLocationChanged(Location location) {
            timer1.cancel();
            onLocationGot(location);
            try {
                lm.removeUpdates(this);
                lm.removeUpdates(locationListenerNetwork);
            }catch(SecurityException e){
                e.printStackTrace();
            }
        }
        public void onProviderDisabled(String provider) {
            gps_enabled = false;
            try {
                lm.removeUpdates(this);
            }catch(SecurityException e){
                e.printStackTrace();
            }
            if(networkLocation != null){
                timer1.cancel();
                onLocationGot(networkLocation);
            }else if(!network_enabled){
                timer1.cancel();
                timerTask.run();
            }
        }
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    LocationListener locationListenerNetwork = new LocationListener() {
        public void onLocationChanged(Location location) {
            networkLocation = location;
            try {
                lm.removeUpdates(this);
            }catch (SecurityException e){
                e.printStackTrace();
            }
            if(!gps_enabled){
                timer1.cancel();
                onLocationGot(networkLocation);
            }
        }
        public void onProviderDisabled(String provider) {
            network_enabled = false;
            try {
                lm.removeUpdates(this);
            }catch(SecurityException e){
                e.printStackTrace();
            }
            if(!gps_enabled){
                timer1.cancel();
                timerTask.run();
            }
        }
        public void onProviderEnabled(String provider) {}
        public void onStatusChanged(String provider, int status, Bundle extras) {}
    };

    class GetLastLocation extends TimerTask {
        @Override
        public void run() {
            try {
                lm.removeUpdates(locationListenerGps);
                lm.removeUpdates(locationListenerNetwork);
            }catch (SecurityException e){
                e.printStackTrace();
            }
            if(networkLocation != null){
                onLocationGot(networkLocation);
                return;
            }
            Location net_loc=null, gps_loc=null;
            if(gps_enabled)
                try {
                    gps_loc = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
                }catch (SecurityException e){
                    e.printStackTrace();
                }
            if(network_enabled)
                try {
                    net_loc = lm.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
                }catch (SecurityException e){
                    e.printStackTrace();
                }

            //if there are both values use the latest one
            if(gps_loc!=null && net_loc!=null){
                if(gps_loc.getTime()>net_loc.getTime())
                    onLocationGot(gps_loc);
                else
                    onLocationGot(net_loc);
                return;
            }

            if(gps_loc!=null){
                onLocationGot(gps_loc);
                return;
            }
            if(net_loc!=null){
                onLocationGot(net_loc);
                return;
            }
            onLocationGot(null);
        }
    }

    public void onLocationGot(Location location){

    }
}
