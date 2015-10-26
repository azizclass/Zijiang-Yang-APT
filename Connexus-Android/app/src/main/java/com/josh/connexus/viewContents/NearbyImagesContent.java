package com.josh.connexus.viewContents;


import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.location.Location;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.josh.connexus.R;
import com.josh.connexus.ViewStream;
import com.josh.connexus.elements.BitmapFetcher;
import com.josh.connexus.elements.Image;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class NearbyImagesContent extends ViewContent implements SliderAdapter  {

    private final List<Image> images;
    private boolean active;
    private boolean[] isImageLoading;
    private boolean[] isImageActive;
    private ImageHandler imageHandler;
    private DynamicSlider slider;
    private View left_arrow;
    private View right_arrow;
    private Set<ImageView> imageViews = new HashSet<ImageView>();
    private double latitude;
    private double longitude;

    public NearbyImagesContent(Context context, ViewGroup parentLayout, List<Image> images, double latitude, double longitude){
        super(context, parentLayout);
        this.images = images;
        this.latitude = latitude;
        this.longitude = longitude;
        imageHandler = new ImageHandler(this);
        if(images!=null) {
            isImageActive = new boolean[images.size()];
            isImageLoading = new boolean[images.size()];
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
        inflater.inflate(R.layout.nearby_images, parentLayout, true);
        slider = (DynamicSlider) parentLayout.findViewById(R.id.near_by_images_slider_view);
        left_arrow = parentLayout.findViewById(R.id.near_by_images_left_arrow);
        right_arrow = parentLayout.findViewById(R.id.near_by_images_right_arrow);
        left_arrow.setOnClickListener(left_arrow_listener);
        right_arrow.setOnClickListener(right_arrow_listener);
        left_arrow.setVisibility(View.GONE);
        if(images.size() <= 1)
            right_arrow.setVisibility(View.GONE);
        slider.post(new Runnable(){
            @Override
            public void run(){
                int width_box = slider.getWidth();
                ViewGroup container = (ViewGroup) parentLayout.findViewById(R.id.near_by_images_container);
                for(Image img : images){
                    ViewGroup picture_box = (ViewGroup) inflater.inflate(R.layout.nearby_image_overview, container, false);
                    ViewGroup.LayoutParams params = picture_box.getLayoutParams();
                    params.width = width_box;
                    picture_box.setLayoutParams(params);
                    ((TextView) picture_box.findViewById(R.id.nearby_images_distance)).setText(getDistance(img.latitude, img.longitude));
                    ((TextView) picture_box.findViewById(R.id.nearby_images_location)).setText("Loading...");
                    ((TextView) picture_box.findViewById(R.id.nearby_images_stream_name)).setText(img.streamName);
                    ((TextView) picture_box.findViewById(R.id.nearby_images_owner)).setText(img.owner);
                    picture_box.findViewById(R.id.nearby_images_image_area).setOnClickListener(image_listener);
                    container.addView(picture_box);
                }
                slider.setSlideWidth(width_box);
                slider.configure(2, NearbyImagesContent.this);
                slider.setOnSlideChangeListener(listener);
            }
        });
    }

    private String getDistance(double latitude, double longitude){
        Location location = new Location("");
        location.setLatitude(this.latitude);
        location.setLongitude(this.longitude);
        Location to = new Location("");
        to.setLatitude(latitude);
        to.setLongitude(longitude);
        float dis = location.distanceTo(to);
        if(dis < 5000)
            return ((int)dis) + "m";
        else
            return ((int)dis/1000) + "km";
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
        view.findViewById(R.id.nearby_images_progress_bar).setVisibility(View.VISIBLE);
        view.findViewById(R.id.nearby_images_error).setVisibility(View.GONE);
        view.findViewById(R.id.nearby_images_image).setVisibility(View.GONE);
        new FetchImageThread((ViewGroup)view, position).start();
    }

    @Override
    public void releaseResource(int position, View view){
        isImageActive[position] = false;
        ImageView imageView = (ImageView) view.findViewById(R.id.nearby_images_image);
        imageViews.remove(imageView);
        imageView.setImageDrawable(null);
    }

    public boolean isActive(){
        return active;
    }

    private OnSlideChangeListener listener = new OnSlideChangeListener(){
        @Override
        public void onSlideChange(int index, boolean isLeftSwiping){
            if(index == 0)
                left_arrow.setVisibility(View.GONE);
            else
                left_arrow.setVisibility(View.VISIBLE);
            if(index == images.size()-1)
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
            slider.slideTo(images.size() - 1);
        }
    };

    private View.OnClickListener image_listener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(context, ViewStream.class);
            intent.putExtra("streamId", images.get(slider.getCurentSlideIndex()).parentId);
            context.startActivity(intent);
        }
    };

    static class ImageHandler extends Handler {

        private NearbyImagesContent content;

        public ImageHandler(NearbyImagesContent content){
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
            picture_box.findViewById(R.id.nearby_images_progress_bar).setVisibility(View.GONE);
            if(!content.isImageActive[index]) return;
            if((Boolean)data.get("image_success")) {
                ImageView imageView = (ImageView) picture_box.findViewById(R.id.nearby_images_image);
                imageView.setVisibility(View.VISIBLE);
                Bitmap bitmap = (Bitmap) data.get("bitmap");
                imageView.setImageBitmap(bitmap);
                content.imageViews.add(imageView);
            }else
                picture_box.findViewById(R.id.nearby_images_error).setVisibility(View.VISIBLE);
            if((Boolean) data.get("location_success"))
                ((TextView)picture_box.findViewById(R.id.nearby_images_location)).setText((String)data.get("location"));
            else
                ((TextView)picture_box.findViewById(R.id.nearby_images_location)).setText("Unknown");
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
            Image img = images.get(index);
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
