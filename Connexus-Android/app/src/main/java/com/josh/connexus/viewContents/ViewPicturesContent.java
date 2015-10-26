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
import com.josh.connexus.elements.Image;
import com.josh.connexus.elements.Stream;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class ViewPicturesContent extends ViewContent implements SliderAdapter {

    private Stream stream;
    private boolean active;
    private boolean[] isImageLoading;
    private boolean[] isImageActive;
    private ImageHandler imageHandler;
    private DynamicSlider slider;
    private View left_arrow;
    private View right_arrow;
    private Set<ImageView> imageViews = new HashSet<ImageView>();

    public ViewPicturesContent(Context context, ViewGroup parentLayout, Stream stream){
        super(context, parentLayout);
        this.stream = stream;
        imageHandler = new ImageHandler(this);
        if(stream!=null) {
            isImageActive = new boolean[stream.images.size()];
            isImageLoading = new boolean[stream.images.size()];
        } else {
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
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_pictures, parentLayout, true);
        slider = (DynamicSlider) parentLayout.findViewById(R.id.pictures_slider_view);
        ((TextView)parentLayout.findViewById(R.id.view_pictures_title)).setText(stream.name);
        ((TextView)parentLayout.findViewById(R.id.view_pictures_total_number)).setText(stream.images.size() + "");
        ((TextView)parentLayout.findViewById(R.id.view_pictures_index)).setText(stream.images.size() == 0 ? "0" : "1");
        left_arrow = parentLayout.findViewById(R.id.view_picture_left_arrow);
        right_arrow = parentLayout.findViewById(R.id.view_picture_right_arrow);
        left_arrow.setOnClickListener(left_arrow_listener);
        right_arrow.setOnClickListener(right_arrow_listener);
        left_arrow.setVisibility(View.GONE);
        if(stream.images.size() <= 1)
            right_arrow.setVisibility(View.GONE);
        slider.post(new Runnable(){
           @Override
            public void run(){
               int width_box = slider.getWidth();
               ViewGroup container = (ViewGroup) parentLayout.findViewById(R.id.picture_container);
               for(Image img : stream.images){
                   ViewGroup picture_box = (ViewGroup) inflater.inflate(R.layout.picture_overview, container, false);
                   ViewGroup.LayoutParams params = picture_box.getLayoutParams();
                   params.width = width_box;
                   picture_box.setLayoutParams(params);
                   ((TextView) picture_box.findViewById(R.id.picture_overview_time)).setText(img.getTime());
                   ((TextView) picture_box.findViewById(R.id.picture_overview_location)).setText("Loading...");
                   container.addView(picture_box);
               }
               slider.setSlideWidth(width_box);
               slider.configure(2, ViewPicturesContent.this);
               slider.setOnSlideChangeListener(listener);
           }
        });
    }

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
        view.findViewById(R.id.picture_overview_progress_bar).setVisibility(View.VISIBLE);
        view.findViewById(R.id.picture_overview_error).setVisibility(View.GONE);
        view.findViewById(R.id.picture_overview_image).setVisibility(View.GONE);
        new FetchImageThread((ViewGroup)view, position).start();
    }

    @Override
    public void releaseResource(int position, View view){
        isImageActive[position] = false;
        ImageView imageView = (ImageView) view.findViewById(R.id.picture_overview_image);
        imageViews.remove(imageView);
        imageView.setImageDrawable(null);
    }

    public boolean isActive(){
        return active;
    }

    private OnSlideChangeListener listener = new OnSlideChangeListener(){
        @Override
        public void onSlideChange(int index, boolean isLeftSwiping){
            ((TextView)parentLayout.findViewById(R.id.view_pictures_index)).setText(index+1+"");
            if(index == 0)
                left_arrow.setVisibility(View.GONE);
            else
                left_arrow.setVisibility(View.VISIBLE);
            if(index == stream.images.size()-1)
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
            slider.slideTo(stream.images.size() - 1);
        }
    };

    static class ImageHandler extends Handler {

        private ViewPicturesContent content;

        public ImageHandler(ViewPicturesContent content){
            this.content = content;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg){
            if(!content.isActive()) return;
            HashMap<String, Object> data = (HashMap<String,Object>) msg.obj;
            int index = (Integer) data.get("index");
            content.isImageLoading[index] = false;
            ViewGroup picture_box = (ViewGroup) data.get("picture_box");
            picture_box.findViewById(R.id.picture_overview_progress_bar).setVisibility(View.GONE);
            if(!content.isImageActive[index]) return;
            if((Boolean)data.get("image_success")) {
                ImageView imageView = (ImageView) picture_box.findViewById(R.id.picture_overview_image);
                imageView.setVisibility(View.VISIBLE);
                Bitmap bitmap = (Bitmap) data.get("bitmap");
                imageView.setImageBitmap(bitmap);
                content.imageViews.add(imageView);
            }else
                picture_box.findViewById(R.id.picture_overview_error).setVisibility(View.VISIBLE);
            if((Boolean) data.get("location_success"))
                ((TextView)picture_box.findViewById(R.id.picture_overview_location)).setText((String)data.get("location"));
            else
                ((TextView)picture_box.findViewById(R.id.picture_overview_location)).setText("Unknown");
        }
    }

    class FetchImageThread extends Thread{
        private int index;
        private ViewGroup picture_box;

        public FetchImageThread(ViewGroup picture_box, int index){
            this.index = index;
            this.picture_box = picture_box;
        }

        @Override
        public void run(){
            Message msg = new Message();
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("picture_box", picture_box);
            data.put("index", index);
            msg.obj = data;
            Image img = stream.images.get(index);
            Bitmap bitmap = BitmapFetcher.fetchBitmap(img.url);
            if(bitmap != null){
                data.put("bitmap", bitmap);
                data.put("image_success", true);
            }else
                data.put("image_success", false);
            try{
                data.put("location", img.getLocation(context));
                data.put("location_success", true);
            } catch (IOException e){
                e.printStackTrace();
                data.put("location_success", false);
            }
            imageHandler.sendMessage(msg);
        }
    }

}
