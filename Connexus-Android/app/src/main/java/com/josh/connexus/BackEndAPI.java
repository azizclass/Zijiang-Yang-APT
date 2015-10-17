package com.josh.connexus;

import com.appspot.connexus_1078.connexusAPI.ConnexusAPI;
import com.appspot.connexus_1078.connexusAPI.model.ConnexusStreamInfo;
import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.josh.connexus.elements.Image;
import com.josh.connexus.elements.Stream;

import java.io.IOException;
import java.util.List;
import java.util.Calendar;

public class BackEndAPI {
    private static ConnexusAPI api = new ConnexusAPI.Builder(AndroidHttp.newCompatibleTransport(),
            new AndroidJsonFactory(), null).build();

    public static Stream[] getAllStreams() throws IOException{
        List<ConnexusStreamInfo> Info = api.getAllStreams().execute().getStreams();
        Stream[] ret = new Stream[Info.size()];
        int i=0;
        for(ConnexusStreamInfo info : Info)
            ret[i++] = convertToStream(info);
        return ret;
    }

    public static Stream[] getStreams(String user) throws IOException{
        List<ConnexusStreamInfo> Info = api.getStream(user).execute().getStreams();
        Stream[] ret = new Stream[Info.size()];
        int i = 0;
        for(ConnexusStreamInfo info : Info)
            ret[i++] = convertToStream(info);
        return ret;
    }

    public static Image[] getImages(long id) throws IOException {
        List<String> urls = api.getImages(id).execute().getImageUrls();
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
