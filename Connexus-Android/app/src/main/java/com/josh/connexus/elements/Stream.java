package com.josh.connexus.elements;

import java.util.Arrays;
import java.util.Calendar;

public class Stream {
    public final long id;
    public final String user;
    public final String name;
    public final Calendar createTime;
    public final Calendar  lastNewpicTime;
    public final String tags;
    public final long picNum;
    public final String[] subscribers;
    public final String coverImageURL;
    public final long views;
    public Image[] images;

    public Stream(long id, String user, String name, Calendar  createTime, Calendar  lastNewpicTime,
                  String tags, long picNum, String[] subscribers, String coverImageURL, long views){
        this.id = id;
        this.user = user;
        this.name = name;
        this.createTime = createTime;
        this.lastNewpicTime = lastNewpicTime;
        this.tags = tags;
        this.picNum = picNum;
        this.subscribers = subscribers;
        this.coverImageURL = coverImageURL;
        this.views = views;
        this.images = null;
    }

    public Image[] fetchImages(){
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
                "subscribers: "+ (subscribers==null?"N/A":Arrays.toString(subscribers))+"\n"+
                "cover image: "+coverImageURL+"\n"+
                "views: "+views+"\n"+
                "images: "+(images==null?"N/A":Arrays.toString(images));

    }

}
