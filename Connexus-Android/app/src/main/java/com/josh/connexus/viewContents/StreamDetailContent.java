package com.josh.connexus.viewContents;


import android.content.Context;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.josh.connexus.R;
import com.josh.connexus.elements.BitmapFetcher;
import com.josh.connexus.elements.Stream;

import java.util.Calendar;
import java.util.HashMap;

public class StreamDetailContent extends ViewContent {

    private final Stream stream;
    private boolean active;
    private ImageHandler imageHandler = new ImageHandler(this);

    public StreamDetailContent(Context context, ViewGroup parentLayout, Stream stream){
        super(context, parentLayout);
        this.stream = stream;
        active = false;
    }

    @Override
    public void show() {
        if(active) return;
        active = true;
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.stream_detail, parentLayout, true);
        ((TextView) parentLayout.findViewById(R.id.stream_detail_name)).setText(stream.name);
        ((TextView) parentLayout.findViewById(R.id.stream_detail_owner)).setText(stream.user);
        ((TextView) parentLayout.findViewById(R.id.stream_detail_picture_num)).setText(String.valueOf(stream.picNum));
        ((TextView) parentLayout.findViewById(R.id.stream_detail_subscriber_num)).setText(String.valueOf(stream.subscribers.size()));
        ((TextView) parentLayout.findViewById(R.id.stream_detail_views)).setText(String.valueOf(stream.views));
        ((TextView) parentLayout.findViewById(R.id.stream_detail_tags)).setText((stream.tags == null || stream.tags.length()==0) ? "N/A" : stream.tags);
        ((TextView) parentLayout.findViewById(R.id.stream_detail_create_time)).setText(stream.getCreateTime());
        if(stream.picNum == 0)
            ((TextView) parentLayout.findViewById(R.id.stream_detail_last_new_picture_time)).setText("N/A");
        else
            ((TextView) parentLayout.findViewById(R.id.stream_detail_last_new_picture_time)).setText(stream.getLastNewPicTime());
        parentLayout.findViewById(R.id.stream_detail_progress_bar).setVisibility(View.VISIBLE);
        new FetchImageThread().start();
    }

    @Override
    public void clear() {
        active = false;
        parentLayout.removeAllViews();
    }

    public boolean isActive(){
        return active;
    }

    static class ImageHandler extends Handler {

        private StreamDetailContent content;

        public ImageHandler(StreamDetailContent content){
            this.content = content;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg){
            if(!content.isActive()) return;
            HashMap<String, Object> data = (HashMap<String,Object>) msg.obj;
            content.parentLayout.findViewById(R.id.stream_detail_progress_bar).setVisibility(View.GONE);
            if((Boolean)data.get("success")) {
                ImageView imageView = (ImageView) content.parentLayout.findViewById(R.id.stream_detail_cover);
                imageView.setVisibility(View.VISIBLE);
                Bitmap bitmap = (Bitmap) data.get("bitmap");
                imageView.setImageBitmap(bitmap);
                imageView.setVisibility(View.VISIBLE);
            }else
                content.parentLayout.findViewById(R.id.stream_detail_error).setVisibility(View.VISIBLE);
        }
    }

    class FetchImageThread extends Thread{

        @Override
        public void run(){
            Message msg = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            msg.obj = data;
            Bitmap bitmap = BitmapFetcher.fetchBitmap(stream.coverImageURL);
            if(bitmap != null){
                data.put("bitmap", bitmap);
                data.put("success", true);
            }else
                data.put("success", false);
            imageHandler.sendMessage(msg);
        }
    }
}
