package com.josh.connexus;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.josh.connexus.elements.BackEndAPI;
import com.josh.connexus.elements.Credential;
import com.josh.connexus.elements.Stream;
import com.josh.connexus.viewContents.ViewContent;
import com.josh.connexus.viewContents.ViewPicturesContent;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewStream extends Activity {

    private static final int SELECT_PICTURE = 0;

    private static final int VIEW_PICTURES = 0;
    private static final int VIEW_INFO = 1;

    private Stream stream;
    private RelativeLayout content_layout;
    private ViewContent content;
    private ProgressBar progressBar;
    private LinearLayout error_sign;
    private LinearLayout warning_sign;

    private boolean isLoading;
    private boolean isActive;
    private int cur_view;

    private StreamDataHandler handler = new StreamDataHandler(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_stream);
        stream = (Stream)getIntent().getSerializableExtra("stream");
        Spinner menu = (Spinner)findViewById(R.id.view_stream_menu);
        menu.setAdapter(new MenuListAdapter(getMenuData()));
        menu.setOnItemSelectedListener(menuSelectedListener);
        content_layout = (RelativeLayout) findViewById(R.id.view_stream_content);
        progressBar = (ProgressBar) findViewById(R.id.view_stream_progress_bar);
        error_sign = (LinearLayout) findViewById(R.id.view_stream_error);
        warning_sign = (LinearLayout) findViewById(R.id.view_stream_warning);
        cur_view = VIEW_PICTURES;
        menu.setSelection(cur_view);
        switchContent(cur_view);
    }


    @Override
    public void onStart(){
        super.onStart();
        isActive = true;
    }

    @Override
    public void onStop(){
        super.onStop();
        isActive = false;
    }

    private List<Map<String, Object>> getMenuData(){
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        HashMap<String, Object> item = null;
        item = new HashMap<String, Object>();
        item.put("icon", R.drawable.picture);
        item.put("text", "View Pictures");
        item.put("id", VIEW_PICTURES);
        data.add(item);
        item = new HashMap<String, Object>();
        item.put("icon", R.drawable.image_info);
        item.put("text", "Stream Details");
        item.put("id", VIEW_INFO);
        data.add(item);
        return data;
    }

    private List<String> getOptionData(){
        ArrayList<String> ret = new ArrayList<String>();
        if(!Credential.isLoggedIn()) return ret;
        String email = Credential.getCredential().getSelectedAccountName();
        if(email.equals(stream.user)){
            ret.add("Upload Picture");
        }else{
            if(stream.subscribers != null && stream.subscribers.contains(email))
                ret.add("Subscribe");
            else
                ret.add("Unsubscribe");
        }
        return ret;
    }

    private void switchContent(int view_id){
        if(view_id < VIEW_PICTURES || view_id > VIEW_INFO) return;
        if(content != null)
            content.clear();
        progressBar.setVisibility(View.VISIBLE);
        error_sign.setVisibility(View.GONE);
        warning_sign.setVisibility(View.GONE);
        isLoading = true;
        new LoadingThread(view_id).start();
    }

    public void onRefreshClick(View v){
        if(!isLoading)
            switchContent(cur_view);
    }

    public void onBackClick(View v){
        finish();
    }

    public void onOptionsClick(View v){
        PopupMenu window = new PopupMenu(this, v);
        Menu menu = window.getMenu();
        final int UPLOAD = 0;
        final int UNSUBSCRIBE = 1;
        final int SUBSCRIBE = 2;
        if(Credential.isLoggedIn()) {
            String email = Credential.getCredential().getSelectedAccountName();
            if (stream.user.equals(email)) {
                menu.add(0, UPLOAD, 0, "Upload Picture");
            } else{
                if (stream.subscribers != null && stream.subscribers.contains(email))
                    menu.add(0, UNSUBSCRIBE, 0, "Unsubscribe");
                else
                    menu.add(0, SUBSCRIBE, 0, "Subscribe");
            }
        }
        window.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                switch (item.getItemId()){
                    case UPLOAD:
                        final Dialog dialog = new Dialog(ViewStream.this, R.style.theme_pic_source_selection);
                        dialog.setContentView(R.layout.select_picture_source);
                        dialog.findViewById(R.id.select_source_gallery).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                Intent intent = new Intent();
                                intent.setType("image/*");
                                intent.setAction(Intent.ACTION_GET_CONTENT);
                                startActivityForResult(Intent.createChooser(intent, "Select Picture"), SELECT_PICTURE);
                                dialog.dismiss();
                            }
                        });
                        dialog.findViewById(R.id.select_source_camera).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                dialog.dismiss();
                            }
                        });
                        dialog.show();
                        break;
                    case UNSUBSCRIBE:
                        break;
                    case SUBSCRIBE:
                        break;
                }
                return true;
            }
        });
        window.show();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            if (requestCode == SELECT_PICTURE) {
                Uri selectedImageUri = data.getData();
                Toast.makeText(this, selectedImageUri.toString() , Toast.LENGTH_SHORT).show();
            }
        }
    }



    private AdapterView.OnItemSelectedListener menuSelectedListener = new AdapterView.OnItemSelectedListener() {
        @Override
        public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

        }

        @Override
        public void onNothingSelected(AdapterView<?> parent) {

        }
    };

    private class LoadingThread extends Thread{

        private final int type;

        public LoadingThread(int type){
            this.type = type;
        }

        @Override
        public void run(){
            HashMap<String, Object> data = new HashMap<String, Object>();
            data.put("type", type);
            try{
                Stream theStream = BackEndAPI.getStream(stream.id);
                if(theStream != null)
                    theStream.fetchImages();
                data.put("stream", theStream);
                data.put("success", true);
            } catch (IOException e) {
                e.printStackTrace();
                data.put("success", false);
            }
            Message msg = new Message();
            msg.obj = data;
            handler.sendMessage(msg);
        }
    }

    static class StreamDataHandler extends Handler {

        private ViewStream activity;

        public StreamDataHandler(ViewStream activity){
            this.activity = activity;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg){
            HashMap<String, Object> data = (HashMap<String, Object>)msg.obj;
            activity.progressBar.setVisibility(View.GONE);
            activity.isLoading = false;
            if(!activity.isActive) return;
            if((Boolean) data.get("success")) {
                Stream stream = (Stream) data.get("stream");
                if(stream == null) {
                    activity.warning_sign.setVisibility(View.VISIBLE);
                    ((TextView)activity.warning_sign.findViewById(R.id.view_stream_warning_text)).setText(
                            "This stream does not exist! It seems that it is deleted!");
                    return;
                }
                switch ((Integer)data.get("type")){
                    case ViewStream.VIEW_PICTURES:
                        if(stream.picNum == 0){
                            activity.warning_sign.setVisibility(View.VISIBLE);
                            ((TextView)activity.warning_sign.findViewById(R.id.view_stream_warning_text)).setText(
                                    "Oops! This stream is empty!");
                        }else {
                            activity.content = new ViewPicturesContent(activity, activity.content_layout, stream);
                            activity.content.show();
                        }
                        break;
                    case ViewStream.VIEW_INFO:
                        break;
                    default:
                        break;
                }

            }else
                activity.error_sign.setVisibility(View.VISIBLE);
        }
    }

    class MenuListAdapter extends BaseAdapter
    {
        private List<Map<String, Object>> menuData;

        public MenuListAdapter(List<Map<String, Object>> data){
            this.menuData = data;
        }

        @Override
        public int getCount() {
            return menuData.size();
        }

        @Override
        public Object getItem(int pos) {
            return menuData.get(pos);
        }

        @Override
        public long getItemId(int pos) {
            return (long)((Integer)menuData.get(pos).get("id"));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
                convertView = getLayoutInflater().inflate(R.layout.view_stream_menu_item, parent, false);
            ((TextView) convertView.findViewById(R.id.view_one_stream_menu_text)).setText((String) menuData.get(position).get("text"));
            ((ImageView) convertView.findViewById(R.id.view_one_stream_menu_icon)).setImageResource((Integer)menuData.get(position).get("icon"));
            return convertView;
        }
    }
}
