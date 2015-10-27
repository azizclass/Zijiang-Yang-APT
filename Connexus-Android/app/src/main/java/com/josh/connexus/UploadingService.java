package com.josh.connexus;

import android.app.Service;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.util.Log;
import android.widget.Toast;

import com.josh.connexus.elements.BackEndAPI;
import com.josh.connexus.elements.Credential;
import com.josh.connexus.elements.LocationFetcher;

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
import java.util.concurrent.ConcurrentLinkedQueue;

public class UploadingService extends Service {

    private final ConcurrentLinkedQueue<Task> undoTasks = new ConcurrentLinkedQueue<Task>();
    private final ConcurrentLinkedQueue<Task> runningTasks = new ConcurrentLinkedQueue<Task>();
    private MyHandler handler = new MyHandler(this);

    public UploadingService() {
        super();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId){
        Toast.makeText(UploadingService.this, "Start uploading...", Toast.LENGTH_SHORT).show();
        undoTasks.add(new Task(intent.getStringExtra("path"), intent.getLongExtra("streamId", -1), intent.getStringExtra("tag")));
        new LocationFetcher(this){
            @Override
            public void onLocationGot(Location location){
                while(!undoTasks.isEmpty()) {
                    Task task = undoTasks.poll();
                    runningTasks.add(task);
                    if(location != null)
                        new UploadThread(task, location.getLatitude(), location.getLongitude()).start();
                    else
                        new UploadThread(task, Double.MAX_VALUE, Double.MAX_VALUE).start();
                }
            }
        }.getLocation(10000);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }


    class UploadThread extends Thread{

        private static final String TAG = "UploadThread";

        private final double latitude;
        private final double longitude;
        private final Task task;

        public UploadThread(Task task, double latitude, double longitude){
            this.latitude = latitude;
            this.longitude = longitude;
            this.task = task;
        }

        @Override
        public void run(){
            Message msg = new Message();
            try (CloseableHttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build()){
                String url = BackEndAPI.getUploadURL(task.id, Credential.getCredential());
                if(url == null)
                    throw new Exception("Unable to fetch the upload url, got null pointer!");
                Log.i(TAG, "Got uploading url " + url);
                HttpContext localContext = new BasicHttpContext();
                HttpPost httppost = new HttpPost(url);
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                builder.addPart("img", new FileBody(new File(task.path)));
                builder.addPart("latitude", new StringBody(String.valueOf(latitude), ContentType.TEXT_PLAIN));
                builder.addPart("longitude", new StringBody(String.valueOf(longitude), ContentType.TEXT_PLAIN));
                builder.addPart("tag", new StringBody(task.tag, ContentType.TEXT_PLAIN));
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
            runningTasks.poll();
            handler.sendMessage(msg);
        }
    }

    static class MyHandler extends Handler{

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
            if(service.undoTasks.isEmpty() && service.runningTasks.isEmpty())
                service.stopSelf();
        }
    };
}



class Task{
    public final String path;
    public final long id;
    public final String tag;

    public Task(String path, long id, String tag){
        this.path = path;
        this.id = id;
        this.tag = tag;
    }

}
