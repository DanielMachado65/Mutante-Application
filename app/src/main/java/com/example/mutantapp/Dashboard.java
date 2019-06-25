package com.example.mutantapp;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.example.mutantapp.modals.Mutant;
import com.example.mutantapp.modals.MutantAdapter;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

public class Dashboard extends AppCompatActivity {

    private static final String Request_TAG_CREATE = "IndexMutant";
    private static String urlIndex = "http://10.0.2.2:8030/mutantes";
    AlertDialog.Builder builder;
    private String loginUser, passwordUser;
    private RequestQueue requestQueue;
    private List<Mutant> mutantList = new ArrayList<>();
    private ListView listView;
    private EditText searchText;
    private Button btnCreateMutant;

    private ArrayAdapter adapter;

    private String urlSearch = "";
    private String searchSampleText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        btnCreateMutant = findViewById(R.id.btnCreateMutant);
        searchText = findViewById(R.id.searchEditText);

        listView = findViewById(R.id.listViewMutant);
        adapter = new MutantAdapter(this, (ArrayList<Mutant>) mutantList);
        listView.setAdapter(adapter);

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle params = new Bundle();
                params.putString("login", loginUser);
                params.putString("password", passwordUser);
                params.putString("action", "show");
                params.putInt("id", ((Mutant) parent.getItemAtPosition(position)).getId());
                startActivity(new Intent(getApplicationContext(), CreateMutant.class).putExtras(params));
            }
        });

        searchText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {
            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
            }

            @Override
            public void afterTextChanged(Editable s) {
                adapter.clear();
                Timer timer = new Timer();
                searchSampleText = s.toString();
                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        try {
                            // pegar os outros números
                            urlSearch = "http://10.0.2.2:8030/mutantes/search/" + searchSampleText.trim() + "?login=" + loginUser + "&password=" + passwordUser;
                            System.out.println(urlSearch);
                            requestQueue = CustomVolleyRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
                            JsonArrayRequest arrayreq = new JsonArrayRequest(Request.Method.GET,
                                    urlSearch,
                                    null, new Response.Listener<JSONArray>() {
                                @Override
                                public void onResponse(JSONArray response) {
                                    try {
                                        for (int i = 0; i < response.length(); i++) {
                                            JSONObject json = response.getJSONObject(i);
                                            Mutant mutant = new Mutant(json.getInt("id"),
                                                    json.getString("name"),
                                                    json.getString("hability"));
                                            mutantList.add(mutant);
                                        }
                                        adapter.notifyDataSetChanged();
                                    } catch (JSONException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }, new Response.ErrorListener() {
                                @Override
                                public void onErrorResponse(VolleyError error) {
                                    AlertMessageError(error.toString(), true);
                                }
                            });
                            requestQueue.add(arrayreq);
                        } catch (Exception e) {
                            AlertMessageError(e.getMessage(), true);
                        }
                    }
                }, 1000);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.clear();
        Intent it = getIntent();
        if (it != null) {
            Bundle params = it.getExtras();
            if (params != null) {
                loginUser = params.getString("login");
                passwordUser = params.getString("password");
                try {
                    // pegar os outros números
                    urlIndex = urlIndex + "?login=" + loginUser + "&password=" + passwordUser;
                    requestQueue = CustomVolleyRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
                    JsonArrayRequest arrayreq = new JsonArrayRequest(Request.Method.GET,
                            urlIndex,
                            null, new Response.Listener<JSONArray>() {
                        @Override
                        public void onResponse(JSONArray response) {
                            try {
                                for (int i = 0; i < response.length(); i++) {
                                    JSONObject json = response.getJSONObject(i);
                                    Mutant mutant = new Mutant(json.getInt("id"),
                                            json.getString("name"),
                                            json.getString("hability"));
                                    mutantList.add(mutant);
                                }
                                adapter.notifyDataSetChanged();
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        }
                    }, new Response.ErrorListener() {
                        @Override
                        public void onErrorResponse(VolleyError error) {
                            AlertMessageError(error.toString(), true);
                        }
                    });
                    requestQueue.add(arrayreq);
                } catch (Exception e) {
                    AlertMessageError(e.getMessage(), true);
                }
            }
        } else {
            AlertMessageError("Error 404, por favor, abra novamente o aplicativo", false);
        }
    }

    public void createMutant(View view) {
        Bundle params = new Bundle();
        params.putString("login", loginUser);
        params.putString("password", passwordUser);
        params.putString("action", "create");
        startActivity(new Intent(getApplicationContext(), CreateMutant.class).putExtras(params));
    }

    private void AlertMessageError(String error, boolean cancelable) {
        builder = new AlertDialog.Builder(this);
        builder.setMessage(error)
                .setCancelable(cancelable);
        AlertDialog alert = builder.create();
        alert.show();
    }


    // MENUS

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.mainmenu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        finish();
        return super.onOptionsItemSelected(item);
    }

    public void exitApp(View view){
        finish();
    }
}