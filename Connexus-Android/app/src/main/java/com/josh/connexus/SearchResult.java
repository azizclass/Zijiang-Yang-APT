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
    private LinearLayout warning_sign;
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
        warning_sign = (LinearLayout) findViewById(R.id.search_result_warning);
        search();
    }

    @Override
    public void onStart(){
        super.onStart();
        isActive = true;
        if(content != null && !content.isActive())
            content.show();
    }

    @Override
    public void onStop(){
        super.onStop();
        isActive = false;
    }

    private void search(){
        if(content != null) {
            content.clear();
            content = null;
        }
        progressBar.setVisibility(View.VISIBLE);
        error_sign.setVisibility(View.GONE);
        warning_sign.setVisibility(View.GONE);
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
            isLoading = true;
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
            if((Boolean) data.get("success")) {
                List<Stream> streams = (List<Stream>) data.get("streams");
                if(streams == null || streams.size() == 0){
                    activity.warning_sign.setVisibility(View.VISIBLE);
                    ((TextView)activity.warning_sign.findViewById(R.id.search_result_warning_text)).setText(
                            "Oops! There is no result for \""+activity.search_key+"\"!");
                }else {
                    activity.content = new ViewStreamsContent(activity, activity.content_layout, streams, "results found");
                    if(activity.isActive)
                        activity.content.show();
                }
            }else
                activity.error_sign.setVisibility(View.VISIBLE);
        }
    }
}
