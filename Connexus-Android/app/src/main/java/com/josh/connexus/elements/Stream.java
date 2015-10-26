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

    public String getCreateTime(){
        String yy = createTime.get(Calendar.YEAR) + "";
        String mm = createTime.get(Calendar.MONTH) + "";
        if(mm.length() == 1) mm = "0" + mm;
        String dd = createTime.get(Calendar.DAY_OF_MONTH) + "";
        if(dd.length() == 1) dd = "0" + dd;
        String hh = createTime.get(Calendar.HOUR_OF_DAY)+"";
        if(hh.length() == 1) hh = "0" + hh;
        String min = createTime.get(Calendar.MINUTE)+"";
        if(min.length() == 1) min = "0" + min;
        return yy+"-"+mm+"-"+dd+" "+hh+":"+min;
    }

    public String getLastNewPicTime(){
        String yy = lastNewpicTime.get(Calendar.YEAR) + "";
        String mm = lastNewpicTime.get(Calendar.MONTH) + "";
        if(mm.length() == 1) mm = "0" + mm;
        String dd = lastNewpicTime.get(Calendar.DAY_OF_MONTH) + "";
        if(dd.length() == 1) dd = "0" + dd;
        String hh = lastNewpicTime.get(Calendar.HOUR_OF_DAY)+"";
        if(hh.length() == 1) hh = "0" + hh;
        String min = lastNewpicTime.get(Calendar.MINUTE)+"";
        if(min.length() == 1) min = "0" + min;
        return yy+"-"+mm+"-"+dd+" "+hh+":"+min;
    }

    @Override
    public String toString(){
        return name+"("+id+"):\n"+
                "user: "+user+"\n"+
                "create time: "+createTime.getTime()+"\n"+
                "last new picture: "+(lastNewpicTime != null? lastNewpicTime.getTime():"N/A")+"\n"+
                "tags: "+tags+"\n"+
                "picture number: "+picNum+"\n"+
                "subscribers: "+ subscribers.toString()+"\n"+
                "cover image: "+coverImageURL+"\n"+
                "views: "+views+"\n"+
                "images: "+(images==null?"N/A": images);

    }

}
