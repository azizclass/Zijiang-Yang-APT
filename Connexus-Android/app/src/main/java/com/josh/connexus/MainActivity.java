package com.josh.connexus;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.josh.connexus.elements.Credential;
import com.josh.connexus.viewContents.ViewContent;
import com.josh.connexus.viewContents.ViewStreamsContent;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainActivity extends Activity {
    public static final int MANAGEMENT = 0;
    public static final int VIEW_ALL_STREAMS = 1;
    public static final int NEARBY_STREAMS = 2;
    public static final int LOG_IN = 3;
    public static final int LOG_OUT = 4;
    private static final String[] view_names = new String[] {"Management", "View All Streams", "Nearby Streams", "Log in", "Log out"};

    private DrawerLayout drawerLayout;
    private TextView title;
    private RelativeLayout content_layout;
    private MenuListAdapter menu_list_adapter;
    private int cur_view;
    private ViewContent content;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        cur_view = getIntent().getIntExtra("view", VIEW_ALL_STREAMS);
        ((TextView)findViewById(R.id.drawer_user)).setText(Credential.isLoggedIn()?Credential.getCredential().getSelectedAccountName():"");
        ListView drawerList = (ListView) findViewById(R.id.drawer_list);
        menu_list_adapter = new MenuListAdapter(getMenuData());
        drawerList.setAdapter(menu_list_adapter);
        drawerList.setOnItemClickListener(listItemListener);
        drawerLayout = (DrawerLayout)findViewById(R.id.main_activity_drawerLayout);
        title = (TextView)findViewById(R.id.main_activity_title);
        content_layout = (RelativeLayout)findViewById(R.id.main_activity_content);
    }

    @Override
    public void onWindowFocusChanged (boolean hasFocus){
        super.onWindowFocusChanged(hasFocus);
        switchContent(cur_view);
    }

    private List<Map<String, Object>> getMenuData(){
        List<Map<String, Object>> data = new ArrayList<Map<String, Object>>();
        HashMap<String, Object> item = null;
        if(Credential.isLoggedIn()) {
            item = new HashMap<String, Object>();
            item.put("view_id", MANAGEMENT);
            item.put("icon", R.drawable.management);
            data.add(item);
        }
        item = new HashMap<String, Object>();
        item.put("icon", R.drawable.picture);
        item.put("view_id", VIEW_ALL_STREAMS);
        data.add(item);
        item = new HashMap<String, Object>();
        item.put("icon", R.drawable.location);
        item.put("view_id", NEARBY_STREAMS);
        data.add(item);
        item = new HashMap<String, Object>();
        item.put("view_id", Credential.isLoggedIn()?LOG_OUT:LOG_IN);
        item.put("icon", R.drawable.user);
        data.add(item);
        return data;
    }

    private void switchContent(int view_id){
        if(view_id >NEARBY_STREAMS || view_id < MANAGEMENT) return;
        title.setText(view_names[view_id]);
        if(content != null)
            content.clear(); //Clear existing content firstly
        switch (view_id){
            case MANAGEMENT:
                break;
            case VIEW_ALL_STREAMS:
                content = new ViewStreamsContent(this, content_layout, null);
                break;
            case NEARBY_STREAMS:
                break;
            default:
                return;
        }
        content.show();
    }

    public void onMenuIconClick(View v){
        drawerLayout.openDrawer(GravityCompat.START);
    }

    private AdapterView.OnItemClickListener listItemListener = new AdapterView.OnItemClickListener(){
        @Override
        public void onItemClick(AdapterView parent, View view, int position, long id) {
            if(cur_view == (int)id) return;
            if((int)id > LOG_OUT) return;
            if((int)id == LOG_IN || (int)id == LOG_OUT){
                Credential.logout();
                startActivity(new Intent(MainActivity.this, LogIn.class));
                finish();
                return;
            }
            drawerLayout.closeDrawer(GravityCompat.START);
            cur_view = (int)id;
            menu_list_adapter.notifyDataSetChanged();
            switchContent(cur_view);
        }
    };

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
            return (long)((Integer)menuData.get(pos).get("view_id"));
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = getLayoutInflater().inflate(R.layout.drawer_list_item, parent, false);
                ((TextView) convertView.findViewById(R.id.item_text)).setText(view_names[(int)getItemId(position)]);
                ((ImageView) convertView.findViewById(R.id.item_icon)).setImageResource((Integer)menuData.get(position).get("icon"));
            }
            if(getItemId(position) == cur_view)
                convertView.setBackgroundResource(R.color.menu_selected);
            else
                convertView.setBackgroundResource(0);
            return convertView;
        }
    }
}

