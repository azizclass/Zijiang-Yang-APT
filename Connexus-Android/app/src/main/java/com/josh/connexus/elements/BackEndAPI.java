package com.josh.connexus.elements;

import android.support.annotation.Nullable;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import com.appspot.connexus_1078.connexusAPI.ConnexusAPI;
import com.appspot.connexus_1078.connexusAPI.model.ConnexusStreamInfo;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Calendar;

public class BackEndAPI {

    private static ConnexusAPI buildAPI(@Nullable GoogleAccountCredential credential){
        ConnexusAPI.Builder helloWorld = new ConnexusAPI.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), credential);
        return helloWorld.build();
    }

    public static List<Stream> getAllStreams() throws IOException{
        List<ConnexusStreamInfo> Info = buildAPI(null).getAllStreams().execute().getStreams();
        List<Stream> ret = new ArrayList<Stream>();
        if(Info != null)
            for(ConnexusStreamInfo info : Info)
                ret.add(convertToStream(info));
        return ret;
    }

    public static List<Stream> getStreams(GoogleAccountCredential credential) throws IOException{
        List<ConnexusStreamInfo> Info = buildAPI(credential).getStream().execute().getStreams();
        List<Stream> ret = new ArrayList<Stream>();
        if(Info != null)
            for(ConnexusStreamInfo info : Info)
                ret.add(convertToStream(info));
        return ret;
    }

    public static List<Stream> searchStreams(String keyWord) throws IOException{
        List<ConnexusStreamInfo> Info = buildAPI(null).searchStreams(keyWord).execute().getStreams();
        List<Stream> ret = new ArrayList<Stream>();
        if(Info != null)
            for(ConnexusStreamInfo info : Info)
                ret.add(convertToStream(info));
        return ret;
    }

    public static List<Image> getImages(long id) throws IOException {
        List<String> urls = buildAPI(null).getImages(id).execute().getImageUrls();
        List<Image> ret = new ArrayList<Image>();
        if(urls != null)
            for(String url : urls)
                ret.add(new Image(url));
        return ret;
    }

    private static Stream convertToStream(ConnexusStreamInfo info) {
        if(info == null) return  null;
        Calendar createTime = Calendar.getInstance();
        createTime.setTimeInMillis(info.getCreateTime().getValue());
        Calendar lastNewTime = null;
        if (info.getLastNewpicTime() != null){
            lastNewTime = Calendar.getInstance();
            lastNewTime.setTimeInMillis(info.getLastNewpicTime().getValue());
        }
        String[] subscribers = null;
        if(info.getSubscribers() != null)
            subscribers = info.getSubscribers().toArray(new String[info.getSubscribers().size()]);
        return new Stream(info.getStreamId(), info.getUser(), info.getName(), createTime,
                lastNewTime, info.getTag(), info.getPicNum(), subscribers, info.getCoverImageUrl(),
                info.getViewCount());
    }

}
