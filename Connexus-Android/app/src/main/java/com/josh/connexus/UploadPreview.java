package com.josh.connexus;

import android.app.Activity;
import android.database.Cursor;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.josh.connexus.elements.BackEndAPI;
import com.josh.connexus.elements.Credential;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.InputStreamBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.LaxRedirectStrategy;
import org.apache.http.protocol.BasicHttpContext;
import org.apache.http.protocol.HttpContext;
import org.apache.http.util.EntityUtils;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStreamReader;


public class UploadPreview extends Activity {

    private Uri fileUri;
    private long streamId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_upload_preview);
        fileUri = (Uri) getIntent().getParcelableExtra("uri");
        streamId = getIntent().getLongExtra("streamId", -1);
        ((ImageView) findViewById(R.id.upload_picture_preview)).setImageURI(fileUri);
    }

    private final LocationListener mLocationListener = new LocationListener() {

        @Override
        public void onLocationChanged(Location location) {
            LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
            try {
                manager.removeUpdates(this);
            } catch (SecurityException e){
                e.printStackTrace();
            }
            Toast.makeText(UploadPreview.this,location.toString(), Toast.LENGTH_LONG).show();
            new UploadThread(location.getLatitude(), location.getLongitude()).start();
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    public void onUploadClick(View v) {
        LocationManager manager = (LocationManager) getSystemService(LOCATION_SERVICE);
        if(manager.isProviderEnabled(LocationManager.GPS_PROVIDER))
            try {
                manager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000L, 100.0f, mLocationListener);
                return;
            }catch (SecurityException e){
                e.printStackTrace();
            }
        Toast.makeText(UploadPreview.this, "Cannot access GPS!", Toast.LENGTH_SHORT).show();
    }


    private String getPath(Uri uri) {

        // Will return "image:x*"
        String wholeID = DocumentsContract.getDocumentId(uri);

// Split at colon, use second item in the array
        String id = wholeID.split(":")[1];

        String[] column = { MediaStore.Images.Media.DATA };

// where id is equal to
        String sel = MediaStore.Images.Media._ID + "=?";

        Cursor cursor = getContentResolver().
                query(MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                        column, sel, new String[]{ id }, null);

        String filePath = "";

        int columnIndex = cursor.getColumnIndex(column[0]);

        if (cursor.moveToFirst()) {
            filePath = cursor.getString(columnIndex);
        }

        cursor.close();
        return filePath;
    }

     class UploadThread extends Thread{

        private static final String TAG = "UploadThread";

        private final double latitude;
        private final double longitude;

        public UploadThread(double latitude, double longitude){
            this.latitude = latitude;
            this.longitude = longitude;
        }

        @Override
        public void run(){
            try (CloseableHttpClient httpClient = HttpClientBuilder.create().setRedirectStrategy(new LaxRedirectStrategy()).build()){
                String url = BackEndAPI.getUploadURL(streamId, Credential.getCredential());
                if(url == null)
                    throw new Exception("Unable to fetch the upload url, got null pointer!");
                Log.i(TAG, "Got uploading url "+url);
                HttpContext localContext = new BasicHttpContext();
                HttpPost httppost = new HttpPost(url);
                MultipartEntityBuilder builder = MultipartEntityBuilder.create();
                builder.setMode(HttpMultipartMode.BROWSER_COMPATIBLE);
                String path = getPath(fileUri);
                Log.i(TAG, "File path is " + path);
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
            }catch (Exception e){
                e.printStackTrace();
            }
        }
    }
}
