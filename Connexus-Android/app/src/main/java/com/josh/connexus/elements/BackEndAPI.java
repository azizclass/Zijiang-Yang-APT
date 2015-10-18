package com.josh.connexus.elements;

import android.support.annotation.Nullable;
import com.google.api.client.googleapis.extensions.android.gms.auth.GoogleAccountCredential;

import com.appspot.connexus_1078.connexusAPI.ConnexusAPI;
import com.appspot.connexus_1078.connexusAPI.model.ConnexusStreamInfo;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;

import java.io.IOException;
import java.util.List;
import java.util.Calendar;

public class BackEndAPI {

    private static ConnexusAPI buildAPI(@Nullable GoogleAccountCredential credential){
        ConnexusAPI.Builder helloWorld = new ConnexusAPI.Builder(AndroidHttp.newCompatibleTransport(),
                new AndroidJsonFactory(), credential);
        return helloWorld.build();
    }

    public static Stream[] getAllStreams() throws IOException{
        List<ConnexusStreamInfo> Info = buildAPI(null).getAllStreams().execute().getStreams();
        Stream[] ret = new Stream[Info.size()];
        int i=0;
        for(ConnexusStreamInfo info : Info)
            ret[i++] = convertToStream(info);
        return ret;
    }

    public static Stream[] getStreams(GoogleAccountCredential credential) throws IOException{
        List<ConnexusStreamInfo> Info = buildAPI(credential).getStream().execute().getStreams();
        Stream[] ret = new Stream[Info.size()];
        int i = 0;
        for(ConnexusStreamInfo info : Info)
            ret[i++] = convertToStream(info);
        return ret;
    }

    public static Image[] getImages(long id) throws IOException {
        List<String> urls = buildAPI(null).getImages(id).execute().getImageUrls();
        Image[] ret = new Image[urls.size()];
        int i = 0;
        for(String url : urls)
            ret[i++] = new Image(url);
        return ret;
    }

    private static Stream convertToStream(ConnexusStreamInfo info) {
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
