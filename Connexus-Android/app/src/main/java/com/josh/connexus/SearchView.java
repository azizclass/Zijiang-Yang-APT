package com.josh.connexus;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Message;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import android.os.Handler;

import com.josh.connexus.elements.BackEndAPI;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class SearchView extends Dialog implements Runnable{

    private SuggestionHandler suggestionHandler = new SuggestionHandler(this);
    private ImageView remove;
    private EditText search;
    private ArrayAdapter<String> adapter;
    private HashMap<String, List<String>> cache = new HashMap<String, List<String>>();

    public SearchView(Context context){
        super(context, R.style.theme_search_view);
        setContentView(R.layout.search_view);
        Window window = getWindow();
        WindowManager.LayoutParams wmlp = window.getAttributes();
        wmlp.gravity = Gravity.TOP;
        wmlp.width = WindowManager.LayoutParams.MATCH_PARENT;
        window.setAttributes(wmlp);
        search = (EditText)findViewById(R.id.search_input);
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_SEARCH && search.getText().length() > 0){
                    Intent intent = new Intent(getContext(), SearchResult.class);
                    intent.putExtra("key", search.getText().toString());
                    getContext().startActivity(intent);
                    dismiss();
                }
                return true;
            }
        });
        search.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                }
            }
        });
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                suggestionHandler.removeCallbacks(SearchView.this);
                suggestionHandler.postDelayed(SearchView.this, 300);
                if (s.length() > 0) {
                    remove.setVisibility(View.VISIBLE);
                }
                else
                    remove.setVisibility(View.INVISIBLE);
            }

            @Override
            public void afterTextChanged(Editable s) {
            }
        });
        remove = (ImageView)findViewById(R.id.search_clear_btn);
        remove.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                search.setText("");
            }
        });
        final ListView suggestionList = (ListView) findViewById(R.id.suggestion_list);
        adapter = new ArrayAdapter<String>(getContext(), R.layout.auto_complete_item, new ArrayList<String>());
        suggestionList.setAdapter(adapter);
        suggestionList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                search.setText(adapter.getItem(position));
            }
        });
    }

    @Override
    public void run(){
        new Thread(){
            @Override
            public void run(){
                String key = search.getText().toString();
                Message msg = new Message();
                if(key.length() <= 0) {
                    msg.obj = new ArrayList<String>();
                    suggestionHandler.sendMessage(msg);
                }
                else {
                    List<String> sug = cache.get(key);
                    if(sug == null)
                        try {
                            sug = BackEndAPI.getSearchSuggestion(key);
                            cache.put(key, sug);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    if (search.getText().toString().equals(key)) {
                        if(sug != null) {
                            sug.remove(search.getText().toString());
                            msg.obj = sug;
                        }
                        else
                            msg.obj = new ArrayList<String>();
                        suggestionHandler.sendMessage(msg);
                    }
                }

            }
        }.start();
    }

    static class SuggestionHandler extends Handler{

        private SearchView searchView;

        public SuggestionHandler(SearchView searchView){
            this.searchView = searchView;
        }

        @Override
        @SuppressWarnings("unchecked")
        public void handleMessage(Message msg){
            searchView.adapter.clear();
            for(String str : (ArrayList<String>)msg.obj)
                searchView.adapter.add(str);
            searchView.adapter.notifyDataSetChanged();
        }
    }

}
