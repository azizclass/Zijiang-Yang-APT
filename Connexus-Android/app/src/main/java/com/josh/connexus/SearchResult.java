package com.josh.connexus;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.josh.connexus.elements.BackEndAPI;
import com.josh.connexus.elements.Stream;
import com.josh.connexus.viewContents.ViewStreamsContent;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;

public class SearchResult extends Activity {

    private String search_key;
    private RelativeLayout content_layout;
    private ViewStreamsContent content;
    private ProgressBar progressBar;
    private LinearLayout error_sign;
    private boolean isLoading;
    private boolean isActive;

    private SearchHandler handler = new SearchHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);
        search_key = getIntent().getStringExtra("key");
        ((TextView)findViewById(R.id.search_result_title)).setText("Search for \""+search_key+"\"");
        content_layout = (RelativeLayout) findViewById(R.id.search_result_content);
        progressBar = (ProgressBar) findViewById(R.id.search_result_progress_bar);
        error_sign = (LinearLayout) findViewById(R.id.search_result_error);
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
            search();
    }

    @Override
    public void onStop(){
        super.onStop();
        isActive = false;
    }

    private void search(){
        if(content != null)
            content.clear();
        progressBar.setVisibility(View.VISIBLE);
        error_sign.setVisibility(View.GONE);
        isLoading = true;
        new LoadingThread().start();
    }

    public void onRefreshClick(View v){
        if(!isLoading)
            search();
    }

    public void onBackClick(View v){
        finish();
    }

    private class LoadingThread extends Thread{
        @Override
        public void run(){
            HashMap<String, Object> data = new HashMap<String, Object>();
            try{
                data.put("streams", BackEndAPI.searchStreams(search_key));
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

    static class SearchHandler extends Handler {

        private SearchResult activity;

        public SearchHandler(SearchResult activity){
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
                activity.content = new ViewStreamsContent(activity, activity.content_layout, (List<Stream>) data.get("streams"), "results found");
                activity.content.show();
            }else
                activity.error_sign.setVisibility(View.VISIBLE);
        }
    }
}
