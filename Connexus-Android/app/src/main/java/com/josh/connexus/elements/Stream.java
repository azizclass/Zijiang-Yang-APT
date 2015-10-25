package com.josh.connexus.elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class Stream {
    public final long id;
    public final String user;
    public final String name;
    public final Calendar createTime;
    public final Calendar  lastNewpicTime;
    public final String tags;
    public final long picNum;
    public final List<String> subscribers;
    public final String coverImageURL;
    public final long views;
    public List<Image> images;

    public Stream(long id, String user, String name, Calendar  createTime, Calendar  lastNewpicTime,
                  String tags, long picNum, List<String> subscribers, String coverImageURL, long views){
        this.id = id;
        this.user = user;
        this.name = name;
        this.createTime = createTime;
        this.lastNewpicTime = lastNewpicTime;
        this.tags = tags;
        this.picNum = picNum;
        this.subscribers = subscribers == null? new ArrayList<String>(): subscribers;
        this.coverImageURL = coverImageURL;
        this.views = views;
        this.images = null;
    }

    public List<Image> fetchImages()  throws IOException {
        images = BackEndAPI.getImages(id);
        return images;
    }

    @Override
    public String toString(){
        return name+"("+id+"):\n"+
                "user: "+user+"\n"+
                "create time: "+createTime.getTime()+"\n"+
                "last new picture: "+(lastNewpicTime != null? lastNewpicTime.getTime():"N/A")+"\n"+
                "tags: "+tags+"\n"+
                "picture number: "+picNum+"\n"+
                "subscribers: "+ (subscribers==null?"N/A":subscribers.toString())+"\n"+
                "cover image: "+coverImageURL+"\n"+
                "views: "+views+"\n"+
                "images: "+(images==null?"N/A": images);

    }

}
