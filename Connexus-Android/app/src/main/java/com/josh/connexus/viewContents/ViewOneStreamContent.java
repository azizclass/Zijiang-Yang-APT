package com.josh.connexus.viewContents;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.josh.connexus.R;
import com.josh.connexus.elements.Stream;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ViewOneStreamContent extends ViewContent {

    private static final int VIEW_PICTURES = 0;
    private static final int VIEW_INFO = 1;

    private Stream stream;
    private boolean active;
    private ViewContent content;
    private RelativeLayout content_layout;

    public ViewOneStreamContent(Context context, ViewGroup parentLayout, Stream stream){
        super(context, parentLayout);
        this.stream = stream;
        active = false;
    }

    @Override
    public void show(){
        if(active) return;
        active = true;
        if(stream == null) return;
        final LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        inflater.inflate(R.layout.view_one_stream, parentLayout, true);
        Spinner spinner = (Spinner) parentLayout.findViewById(R.id.view_one_stream_menu);
        final MenuListAdapter adapter = new MenuListAdapter(getMenuData());
        spinner.setAdapter(adapter);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                adapter.select(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        Button btn = (Button) parentLayout.findViewById(R.id.view_one_stream_btn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
        content_layout = (RelativeLayout) parentLayout.findViewById(R.id.view_one_stream_content);
        if(spinner.getSelectedItemId() == VIEW_PICTURES) {
            content = new ViewPicturesContent(context, content_layout, stream);
            content.show();
        }
    }


    @Override
    public void clear(){

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

        public void select(int pos){
            menuData.add(0, menuData.remove(pos));
            notifyDataSetChanged();
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(convertView == null)
                convertView = ((LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE)).inflate(R.layout.view_one_stream_menu_item, parent, false);
            ((TextView) convertView.findViewById(R.id.view_one_stream_menu_text)).setText((String)menuData.get(position).get("text"));
            ((ImageView) convertView.findViewById(R.id.view_one_stream_menu_icon)).setImageResource((Integer)menuData.get(position).get("icon"));
            return convertView;
        }
    }

}
