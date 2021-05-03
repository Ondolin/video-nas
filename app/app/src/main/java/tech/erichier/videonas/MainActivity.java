package tech.erichier.videonas;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import tech.erichier.videonas.R;

public class MainActivity extends Activity {

    static LinearLayout spinner;
    static CustomListView listview;
    final String SHARED_PREFERENCES = "general";
    SharedPreferences sharedPreferences;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        final Button refresh = findViewById(R.id.refresh);

        spinner = findViewById(R.id.spinner);
        listview = findViewById(R.id.listview);

        listview.setOnKeyDownListener(new CustomOnKeyDownListener() {
            @Override
            public void onKeyDown(KeyEvent keyEvent) {
                if (keyEvent.getKeyCode() == KeyEvent.KEYCODE_DPAD_UP && listview.getSelectedItemPosition() == 0)
                    refresh.requestFocus();
            }
        });

        // load default shared preferences
        sharedPreferences = getSharedPreferences(SHARED_PREFERENCES, MODE_PRIVATE);

        final EditText url = findViewById(R.id.url);

        // save url on every change in edittext
        url.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void afterTextChanged(Editable editable) {
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.putString("url", url.getText().toString());
                editor.apply();
            }
        });

        // watch for enter key in edittext and refresh automatically
        url.setOnKeyListener(new View.OnKeyListener() {
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if ((event.getAction() == KeyEvent.ACTION_DOWN) && (keyCode == KeyEvent.KEYCODE_ENTER)) {
                    loadVideos();
                    return true;
                }
                return false;
            }
        });

        // restore saved url from shared preferences on startup
        url.setText(sharedPreferences.getString("url", ""));

        // load videos on refresh button click
        refresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                loadVideos();
            }
        });

        // load videos on launch
        loadVideos();
    }

    void loadVideos() {
        if (sharedPreferences.getString("url", "").isEmpty())
            return;

        // set visibility of loading spinner and listview
        spinner.setVisibility(View.VISIBLE);
        listview.setVisibility(View.GONE);

        RequestQueue queue = Volley.newRequestQueue(this);
        String url = sharedPreferences.getString("url", "");

        StringRequest stringRequest = new StringRequest(Request.Method.GET, url + "/files",
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        ObjectMapper mapper = new ObjectMapper();
                        try {
                            // convert json to videos array
                            String[] videos = mapper.readValue(response, String[].class);

                            // put videos into listview
                            setupListView(videos);

                            // set visibility if everything was successful
                            spinner.setVisibility(View.GONE);
                            listview.setVisibility(View.VISIBLE);

                            // select the first element
                            listview.setSelection(0);
                            listview.requestFocus();
                        } catch (IOException e) {
                            Toast.makeText(getApplicationContext(), "Could not parse videos", Toast.LENGTH_LONG).show();
                            spinner.setVisibility(View.GONE);
                        }

                    }
                }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(), "Could not fetch videos", Toast.LENGTH_LONG).show();
                spinner.setVisibility(View.GONE);
            }
        });

        queue.add(stringRequest);
    }

    void setupListView(final String[] videos) {

        final StableArrayAdapter adapter = new StableArrayAdapter(this, android.R.layout.simple_list_item_1, Arrays.asList(videos));
        listview.setAdapter(adapter);

        // show the video on click
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view,
                                    int position, long id) {
                String url = sharedPreferences.getString("url", "");

                Intent i = new Intent(MainActivity.this, VideoActivity.class);
                i.putExtra("videourl", url + "/file/" + videos[position]);
                i.putExtra("videoname", videos[position]);
                startActivity(i);
            }
        });
    }

    private class StableArrayAdapter extends ArrayAdapter<String> {
        HashMap<String, Integer> mIdMap = new HashMap<>();

        public StableArrayAdapter(Context context, int textViewResourceId, List<String> objects) {
            super(context, textViewResourceId, objects);
            for (int i = 0; i < objects.size(); ++i) {
                mIdMap.put(objects.get(i), i);
            }
        }

        @Override
        public long getItemId(int position) {
            String item = getItem(position);
            return mIdMap.get(item);
        }

        @Override
        public boolean hasStableIds() {
            return true;
        }

    }

}
