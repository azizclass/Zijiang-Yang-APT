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

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;


public class ViewStreamsContent extends ViewContent{
    private Iterable<Stream> streams;
    private ImageHandler imageHandler;
    private boolean active;
    private List<ImageView> imageViews = new LinkedList<ImageView>();

    public ViewStreamsContent(Context context, ViewGroup parentLayout, Iterable<Stream> streams){
        super(context, parentLayout);
        this.streams = streams;
        imageHandler = new ImageHandler(this);
        active = false;
    }


    public void show(){
        if(active) return;
        active = true;
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        inflater.inflate(R.layout.view_streams, parentLayout, true);
        ViewGroup container = (ViewGroup)parentLayout.findViewById(R.id.stream_container);
        int width_box = parentLayout.getWidth();
        int height_box = parentLayout.getHeight();
        int width_stream = Math.min(width_box, height_box * 3/4);
        int height_stream = Math.min(height_box, width_box * 4/3);
        ((SlidableHorizontalSchrollView)parentLayout.findViewById(R.id.streams_slider_view)).setSlideWidth(width_box);
        if(streams == null) return;
        for(Stream stream : streams) {
            ViewGroup stream_box = (ViewGroup)inflater.inflate(R.layout.stream_overview, container, false);
            ViewGroup.LayoutParams params =  stream_box.getLayoutParams();
            params.width = width_box;
            stream_box.setLayoutParams(params);
            View stream_view = stream_box.findViewById(R.id.stream);
            params = stream_view.getLayoutParams();
            params.width = width_stream;
            params.height = height_stream;
            stream_view.setLayoutParams(params);
            ((TextView) stream_view.findViewById(R.id.stream_overview_name)).setText(stream.name);
            ((TextView) stream_view.findViewById(R.id.stream_overview_owner)).setText(stream.user);
            ((TextView)stream_view.findViewById(R.id.stream_overview_num_pic)).setText(stream.picNum+"");
            ((TextView)stream_view.findViewById(R.id.stream_overview_num_view)).setText(stream.views + "");
            new FetchImageThread(stream_box, stream.coverImageURL).start();
            container.addView(stream_box);
        }
    }

    public boolean isActive(){
        return active;
    }

    public void clear(){
        if(!active) return;
        active = false;
        for(ImageView imageView : imageViews)
            imageView.setImageDrawable(null);
        imageViews = new LinkedList<ImageView>();
        parentLayout.removeAllViews();
    }

    static class ImageHandler extends Handler{

        private ViewStreamsContent content;

        public ImageHandler(ViewStreamsContent content){
            this.content = content;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg){
            if(!content.isActive()) return;
            HashMap<String, Object> data = (HashMap<String,Object>) msg.obj;
            ViewGroup stream_box = (ViewGroup) data.get("stream_box");
            View progress_bar = stream_box.findViewById(R.id.stream_overview_progress_bar);
            ((ViewGroup)progress_bar.getParent()).removeView(progress_bar);
            if((Boolean)data.get("success")) {
                ImageView imageView = (ImageView) stream_box.findViewById(R.id.stream_overview_cover);
                Bitmap bitmap = (Bitmap) data.get("bitmap");
                imageView.setImageBitmap(bitmap);
                content.imageViews.add(imageView);
            }else
                stream_box.findViewById(R.id.stream_overview_error).setVisibility(View.VISIBLE);
        }
    }

    class FetchImageThread extends Thread{

        private String url;
        private ViewGroup stream_box;

        public FetchImageThread(ViewGroup stream_box, String url){
            this.url = url;
            this.stream_box = stream_box;
        }

        @Override
        public void run(){
            if(url == null) return;
            Message msg = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("stream_box", stream_box);
            msg.obj = data;
            Bitmap bitmap = BitmapFetcher.fetchBitmap(url);
            if(bitmap != null){
                data.put("bitmap", bitmap);
                data.put("success", true);
            }else
                data.put("success", false);
            imageHandler.sendMessage(msg);
        }
    }
}
