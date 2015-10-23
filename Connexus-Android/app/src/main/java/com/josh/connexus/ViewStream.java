package com.josh.connexus;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.josh.connexus.elements.BackEndAPI;
import com.josh.connexus.elements.Stream;
import com.josh.connexus.viewContents.ViewOneStreamContent;

import java.io.IOException;
import java.util.HashMap;

public class ViewStream extends Activity {

    private Stream stream;
    private RelativeLayout content_layout;
    private ViewOneStreamContent content;
    private ProgressBar progressBar;
    private LinearLayout error_sign;
    private boolean isLoading;
    private boolean isActive;

    private StreamDataHandler handler = new StreamDataHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stream);
        stream = (Stream)getIntent().getSerializableExtra("stream");
        ((TextView)findViewById(R.id.view_stream_title)).setText(stream.name);
        content_layout = (RelativeLayout) findViewById(R.id.view_stream_content);
        progressBar = (ProgressBar) findViewById(R.id.view_stream_progress_bar);
        error_sign = (LinearLayout) findViewById(R.id.view_stream_error);
    }


    @Override
    public void onStart(){
        super.onStart();
        isActive = true;
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        if(content == null)
            loadStream();
    }

    @Override
    public void onStop(){
        super.onStop();
        isActive = false;
    }

    private void loadStream(){
        if(content != null)
            content.clear();
        progressBar.setVisibility(View.VISIBLE);
        error_sign.setVisibility(View.GONE);
        isLoading = true;
        new LoadingThread().start();
    }

    public void onRefreshClick(View v){
        if(!isLoading)
            loadStream();
    }

    public void onBackClick(View v){
        finish();
    }

    private class LoadingThread extends Thread{
        @Override
        public void run(){
            HashMap<String, Object> data = new HashMap<String, Object>();
            try{
                data.put("stream", BackEndAPI.getStream(stream.id));
                data.put("success", true);
            } catch (IOException e) {
                e.printStackTrace();
                data.put("success", false);
            }
            Message msg = new Message();
            msg.obj = data;
            handler.sendMessage(msg);
        }
    }

    static class StreamDataHandler extends Handler {

        private ViewStream activity;

        public StreamDataHandler(ViewStream activity){
            this.activity = activity;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg){
            HashMap<String, Object> data = (HashMap<String, Object>)msg.obj;
            activity.progressBar.setVisibility(View.GONE);
            activity.isLoading = false;
            if(!activity.isActive) return;
            if((Boolean) data.get("success")) {
                activity.content = new ViewOneStreamContent(activity, activity.content_layout, activity.stream);
                activity.content.show();
            }else
                activity.error_sign.setVisibility(View.VISIBLE);
        }
    }

}
