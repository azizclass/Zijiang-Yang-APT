package com.josh.connexus.viewContents;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.josh.connexus.R;
import com.josh.connexus.ViewStream;
import com.josh.connexus.elements.BitmapFetcher;
import com.josh.connexus.elements.Stream;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;


public class ViewStreamsContent extends ViewContent implements SliderAdapter{
    private static String TAG = "ViewStreamsContent";
    private String tagOfView;
    private List<Stream> streams;
    private ImageHandler imageHandler;
    private boolean active;
    private boolean[] isImageLoading;
    private boolean[] isImageActive;
    private Set<ImageView> imageViews = new HashSet<ImageView>();
    private DynamicSlider slider;
    private View left_arrow;
    private View right_arrow;


    public ViewStreamsContent(Context context, ViewGroup parentLayout, List<Stream> streams, String tag){
        super(context, parentLayout);
        this.tagOfView = tag;
        this.streams = streams;
        imageHandler = new ImageHandler(this);
        active = false;
        if(streams!=null) {
            isImageActive = new boolean[streams.size()];
            isImageLoading = new boolean[streams.size()];
        }
        else {
            isImageActive = new boolean[0];
            isImageLoading = new boolean[0];
        }
        for(int i=0; i<isImageActive.length; i++) {
            isImageActive[i] = false;
            isImageLoading[i] = false;
        }
    }

    @Override
    public void show(){
        if(active) return;
        active = true;
        if(streams == null) return;
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_streams, parentLayout, true);
        slider = (DynamicSlider)parentLayout.findViewById(R.id.streams_slider_view);
        ((TextView)parentLayout.findViewById(R.id.view_streams_total_number)).setText(streams.size() + "");
        ((TextView)parentLayout.findViewById(R.id.view_streams_index)).setText(streams.size() == 0 ? "0" : "1");
        ((TextView)parentLayout.findViewById(R.id.view_streams_tag)).setText(tagOfView);
        left_arrow = parentLayout.findViewById(R.id.view_streams_left_arrow);
        right_arrow = parentLayout.findViewById(R.id.view_streams_right_arrow);
        left_arrow.setOnClickListener(left_arrow_listener);
        right_arrow.setOnClickListener(right_arrow_listener);
        left_arrow.setVisibility(View.GONE);
        if(streams.size()<=1)
            right_arrow.setVisibility(View.GONE);
        slider.post(new Runnable() {
            @Override
            public void run() {
                int width_box = slider.getWidth();
                ViewGroup container = (ViewGroup) parentLayout.findViewById(R.id.stream_container);
                for (Stream stream : streams) {
                    ViewGroup stream_box = (ViewGroup) inflater.inflate(R.layout.stream_overview, container, false);
                    ViewGroup.LayoutParams params = stream_box.getLayoutParams();
                    params.width = width_box;
                    stream_box.setLayoutParams(params);
                    ((TextView) stream_box.findViewById(R.id.stream_overview_name)).setText(stream.name);
                    ((TextView) stream_box.findViewById(R.id.stream_overview_owner)).setText(stream.user);
                    ((TextView) stream_box.findViewById(R.id.stream_overview_num_pic)).setText(stream.picNum + "");
                    ((TextView) stream_box.findViewById(R.id.stream_overview_num_view)).setText(stream.views + "");
                    stream_box.findViewById(R.id.stream_overview_image_area).setOnClickListener(view_stream_listener);
                    container.addView(stream_box);
                }
                slider.setSlideWidth(width_box);
                slider.configure(2, ViewStreamsContent.this);
                slider.setOnSlideChangeListener(listener);
            }
        });
    }

    private OnSlideChangeListener listener = new OnSlideChangeListener(){
        @Override
        public void onSlideChange(int index, boolean isLeftSwiping){
            ((TextView)parentLayout.findViewById(R.id.view_streams_index)).setText(index+1+"");
            if(index == 0)
                left_arrow.setVisibility(View.GONE);
            else
                left_arrow.setVisibility(View.VISIBLE);
            if(index == streams.size()-1)
                right_arrow.setVisibility(View.GONE);
            else
                right_arrow.setVisibility(View.VISIBLE);
        }
    };

    private View.OnClickListener left_arrow_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            slider.slideTo(0);
        }
    };

    private View.OnClickListener right_arrow_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            slider.slideTo(streams.size()-1);
        }
    };

    private View.OnClickListener view_stream_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ViewStream.class);
            intent.putExtra("streamId", streams.get(slider.getCurentSlideIndex()).id);
            context.startActivity(intent);
        }
    };


    @Override
    public void clear(){
        if(!active) return;
        active = false;
        for(ImageView imageView : imageViews)
            imageView.setImageDrawable(null);
        imageViews = new HashSet<ImageView>();
        parentLayout.removeAllViews();
    }

    @Override
    public void loadResource(int position, View view){
        isImageActive[position] = true;
        if(isImageLoading[position]) return;
        isImageLoading[position] = true;
        view.findViewById(R.id.stream_overview_progress_bar).setVisibility(View.VISIBLE);
        view.findViewById(R.id.stream_overview_error).setVisibility(View.GONE);
        view.findViewById(R.id.stream_overview_cover).setVisibility(View.GONE);
        new FetchImageThread((ViewGroup)view, position).start();
    }

    @Override
    public void releaseResource(int position, View view){
        isImageActive[position] = false;
        ImageView imageView = (ImageView) view.findViewById(R.id.stream_overview_cover);
        imageViews.remove(imageView);
        imageView.setImageDrawable(null);
    }


    public boolean isActive(){
        return active;
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
            int index = (Integer) data.get("index");
            content.isImageLoading[index] = false;
            ViewGroup stream_box = (ViewGroup) data.get("stream_box");
            stream_box.findViewById(R.id.stream_overview_progress_bar).setVisibility(View.GONE);
            if(!content.isImageActive[index]) return;
            if((Boolean)data.get("success")) {
                ImageView imageView = (ImageView) stream_box.findViewById(R.id.stream_overview_cover);
                imageView.setVisibility(View.VISIBLE);
                Bitmap bitmap = (Bitmap) data.get("bitmap");
                imageView.setImageBitmap(bitmap);
                content.imageViews.add(imageView);
            }else
                stream_box.findViewById(R.id.stream_overview_error).setVisibility(View.VISIBLE);
        }
    }

    class FetchImageThread extends Thread{

        private int index;
        private ViewGroup stream_box;

        public FetchImageThread(ViewGroup stream_box, int index){
            this.index = index;
            this.stream_box = stream_box;
        }

        @Override
        public void run(){
            Message msg = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("stream_box", stream_box);
            data.put("index", index);
            msg.obj = data;
            Bitmap bitmap = BitmapFetcher.fetchBitmap(streams.get(index).coverImageURL);
            if(bitmap != null){
                data.put("bitmap", bitmap);
                data.put("success", true);
            }else
                data.put("success", false);
            imageHandler.sendMessage(msg);
        }
    }
}
