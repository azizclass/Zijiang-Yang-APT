package com.josh.connexus.viewContents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.josh.connexus.R;
import com.josh.connexus.elements.Stream;
import com.josh.connexus.viewContents.ViewContent;

public class ViewStreamsContent extends ViewContent {
    private Iterable<Stream> streams;

    public ViewStreamsContent(Context context, ViewGroup parentLayout, Iterable<Stream> streams){
        super(context, parentLayout);
        this.streams = streams;
    }

    public void show(){
        LayoutInflater inflater = (LayoutInflater) context.getSystemService( Context.LAYOUT_INFLATER_SERVICE );
        inflater.inflate(R.layout.view_streams, parentLayout, true);
        ViewGroup container = (ViewGroup)parentLayout.findViewById(R.id.stream_container);
        int width_box = parentLayout.getWidth();
        int height_box = parentLayout.getHeight();
        int width_stream = Math.min(width_box, height_box * 3/4);
        int height_stream = Math.min(height_box, width_box * 4/3);
        for(int i=0; i<10; i++) {
            View stream_box = inflater.inflate(R.layout.stream_overview, container, false);
            ViewGroup.LayoutParams params =  stream_box.getLayoutParams();
            params.width = width_box;
            stream_box.setLayoutParams(params);
            View stream = stream_box.findViewById(R.id.stream);
            params = stream.getLayoutParams();
            params.width = width_stream;
            params.height = height_stream;
            stream.setLayoutParams(params);
            container.addView(stream_box);
        }
        ((SlidableHorizontalSchrollView)parentLayout.findViewById(R.id.streams_slider_view)).setSlideWidth(width_box);
    }

    public void clear(){
        parentLayout.removeAllViews();
    }
}
