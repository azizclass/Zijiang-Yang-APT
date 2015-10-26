package com.josh.connexus.elements;

import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;

public class Image{

    public final String url;
    public final Calendar createTime;
    public final double latitude;
    public final double longitude;
    private String location;
    public final long parentId;
    public final String owner;
    public final String streamName;

    public Image(String url, Calendar createTime, double latitude, double longitude, long parentId, String owner, String streamName){
        this.url = url;
        this.createTime = createTime;
        this.latitude = latitude;
        this.longitude = longitude;
        this.parentId = parentId;
        this.owner = owner;
        this.streamName = streamName;
    }

    public String getLocation(Context context) throws IOException {
        if(location != null) return location;
        if(latitude < -90.0 || latitude > 90.0 || longitude < -180.0 || longitude > 180.0)
            return location = "Unavailable";
        Geocoder geocoder =  new Geocoder(context, Locale.getDefault());
        List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
        if(addresses.isEmpty()){
            return location = "Unknown";
        }
        Address address = addresses.get(0);
        String locality = address.getLocality();
        String adminArea = address.getAdminArea();
        String country = address.getCountryName();
        location = "";
        if(locality != null)
            location += locality;
        if(adminArea != null) {
            if (location.length() != 0)
                location += ", ";
            location += adminArea;
        }
        if(country != null){
            if(location.length() != 0)
                location += ", ";
            location += country;
        }
        if(location.length() == 0)
            location = "Unknown";
        return location;
    }

    public String getTime(){
        String yy = createTime.get(Calendar.YEAR) + "";
        String mm = createTime.get(Calendar.MONTH) + "";
        if(mm.length() == 1) mm = "0" + mm;
        String dd = createTime.get(Calendar.DAY_OF_MONTH) + "";
        if(dd.length() == 1) dd = "0" + dd;
        String hh = createTime.get(Calendar.HOUR_OF_DAY)+"";
        if(hh.length() == 1) hh = "0" + hh;
        String min = createTime.get(Calendar.MINUTE)+"";
        if(min.length() == 1) min = "0" + min;
        return yy+"-"+mm+"-"+dd+" "+hh+":"+min;
    }

    @Override
    public String toString(){
        return "{\n"+url + '\n' + createTime.getTime()+'\n'+latitude+'\n'+longitude+"\n}";
    }
}
