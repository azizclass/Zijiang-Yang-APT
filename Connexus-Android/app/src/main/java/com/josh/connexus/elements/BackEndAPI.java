package com.josh.connexus.elements;

import android.support.annotation.Nullable;

import com.appspot.connexus_1078.connexusAPI.model.ConnexusImageInfo;
import com.appspot.connexus_1078.connexusAPI.model.ConnexusStreamRequest;
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

    public static List<Stream> getOwnedStreams(GoogleAccountCredential credential) throws IOException{
        List<ConnexusStreamInfo> Info = buildAPI(credential).getOwnedStreams().execute().getStreams();
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

    public static List<String> getSearchSuggestion(String keyWord) throws IOException{
        List<String> suggestions = buildAPI(null).getSearchSuggestion(keyWord).execute().getSuggestions();
        if(suggestions == null)
            return new ArrayList<String>();
        return suggestions;
    }

    public static Stream getStream(long streamId) throws IOException{
        return convertToStream(buildAPI(null).getStream(streamId).execute().getStream());
    }

    public static List<Image> getImages(long id) throws IOException {
        List<ConnexusImageInfo> images = buildAPI(null).getImages(id).execute().getImages();
        List<Image> ret = new ArrayList<Image>();
        if(images != null)
            for(ConnexusImageInfo info : images) {
                Calendar createTime = Calendar.getInstance();
                createTime.setTimeInMillis(info.getCreateTime());
                ret.add(new Image(info.getUrl(), createTime, info.getLatitude(), info.getLongitude()));
            }
        return ret;
    }

    public static boolean subscribeStream(long streamId, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        ConnexusStreamRequest request = new ConnexusStreamRequest();
        request.setStreamId(streamId);
        return buildAPI(credential).subscribe(request).execute().getValue();
    }

    public static boolean unsubscribeStream(long streamId, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return false;
        ConnexusStreamRequest request = new ConnexusStreamRequest();
        request.setStreamId(streamId);
        return buildAPI(credential).unsubscribe(request).execute().getValue();
    }

    public static String getUploadURL(long streamId, GoogleAccountCredential credential) throws IOException{
        if(credential == null) return null;
        return buildAPI(credential).getUploadURL(streamId).execute().getValue();
    }

    private static Stream convertToStream(ConnexusStreamInfo info) {
        if(info == null) return  null;
        Calendar createTime = Calendar.getInstance();
        createTime.setTimeInMillis(info.getCreateTime());
        Calendar lastNewTime = null;
        if (info.getLastNewpicTime() != null){
            lastNewTime = Calendar.getInstance();
            lastNewTime.setTimeInMillis(info.getLastNewpicTime());
        }
        return new Stream(info.getStreamId(), info.getUser(), info.getName(), createTime,
                lastNewTime, info.getTag(), info.getPicNum(), info.getSubscribers(), info.getCoverImageUrl(),
                info.getViewCount());
    }

}
