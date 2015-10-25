package com.josh.connexus;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.LocationProvider;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import com.josh.connexus.elements.BackEndAPI;
import com.josh.connexus.elements.Credential;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;
import java.util.LinkedList;

public class UploadingService extends Service {

    private final LinkedList<Task> undoTasks = new LinkedList<Task>();
    private int runningTasks = 0;
    private boolean isGettingLocation = false;
    private final Object GPSlock = new Object();
    private final Object runningTaskLock = new Object();
    private MyHandler handler = new MyHandler(this);

    public UploadingService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Toast.makeText(UploadingService.this, "Start uploading...", Toast.LENGTH_SHORT).show();
        synchronized (GPSlock) {
            undoTasks.add(new Task(intent.getStringExtra("path"), intent.getLongExtra("streamId", -1)));
            if (!isGettingLocation) {
                LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
                try {
                    manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 100.0f, mLocationListener);
                    isGettingLocation = true;
                } catch (SecurityException e) {
                    e.printStackTrace();
                    for (Task task : undoTasks) {
                        synchronized (runningTaskLock) {
                            runningTasks++;
                        }
                        new UploadThread(task.path, task.id, Double.MAX_VALUE, Double.MAX_VALUE).start();
                    }
                    undoTasks.clear();
                }
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
            synchronized (GPSlock) {
                    for (Task task : undoTasks) {
                        synchronized (runningTaskLock) {
                            runningTasks++;
                        }
                        new UploadThread(task.path, task.id, location.getLatitude(), location.getLongitude()).start();
                    }
                    undoTasks.clear();
                try {
                    manager.removeUpdates(this);
                } catch (SecurityException e) {
                    e.printStackTrace();
                }
                isGettingLocation = false;
            }
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
            if(status != LocationProvider.AVAILABLE){
                synchronized (undoTasks){
                    for (Task task : undoTasks) {
                        synchronized (runningTaskLock) {
                            runningTasks++;
                        }
                        new UploadThread(task.path, task.id, Double.MAX_VALUE, Double.MAX_VALUE).start();
                    }
                    undoTasks.clear();
                }
            }
        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {
            synchronized (undoTasks){
                for (Task task : undoTasks) {
                    synchronized (runningTaskLock) {
                        runningTasks++;
                    }
                    new UploadThread(task.path, task.id, Double.MAX_VALUE, Double.MAX_VALUE).start();
                }
                undoTasks.clear();
            }
        }
    };


    class UploadThread extends Thread{

        private static final String TAG = "UploadThread";

        private final double latitude;
        private final double longitude;
        private final String path;
        private final long streamId;

        public UploadThread(String path, long streamId, double latitude, double longitude){
            this.latitude = latitude;
            this.longitude = longitude;
            this.path = path;
            this.streamId = streamId;
        }

        @Override
        public void run(){
            Message msg = new Message();
            try (CloseableHttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build()){
                String url = BackEndAPI.getUploadURL(streamId, Credential.getCredential());
                if(url == null)
                    throw new Exception("Unable to fetch the upload url, got null pointer!");
                Log.i(TAG, "Got uploading url " + url);
                HttpContext localContext = new BasicHttpContext();
                HttpPost httppost = new HttpPost(url);
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                builder.addPart("img", new FileBody(new File(path)));
                builder.addPart("latitude", new StringBody(String.valueOf(latitude), ContentType.TEXT_PLAIN));
                builder.addPart("longitude", new StringBody(String.valueOf(longitude), ContentType.TEXT_PLAIN));
                httppost.setEntity(builder.build());
                HttpResponse response = httpClient.execute(httppost, localContext);
                Log.i(TAG, response.getStatusLine().toString());
                BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));
                String line = null;
                while ((line = reader.readLine()) != null)
                    Log.i(TAG, line);
                msg.obj = true;
            }catch (Exception e){
                e.printStackTrace();
                msg.obj = false;
            }
            handler.sendMessage(msg);
            synchronized (GPSlock){
                synchronized (runningTaskLock){
                    runningTasks --;
                    if(undoTasks.isEmpty() && runningTasks==0)
                        stopSelf();
                }
            }
        }
    }
}

class MyHandler extends Handler{

    private final UploadingService service;

    public MyHandler(UploadingService service){
        this.service = service;
    }

    @Override
    public void handleMessage(Message msg){
        if((Boolean) msg.obj)
            Toast.makeText(service, "File is successfully uploaded.", Toast.LENGTH_SHORT).show();
        else
            Toast.makeText(service, "There is an error when uploading file.", Toast.LENGTH_SHORT).show();
    }
};

class Task{
    public final String path;
    public final long id;

    public Task(String path, long id){
        this.path = path;
        this.id = id;
    }

}
