package com.example.mutante;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.mutante.modals.Mutant;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateMutant extends AppCompatActivity {

    private static final String Request_TAG_CREATE = "CRUDMutant";
    private static String urlMutant = "http://10.0.2.2:8030/mutantes";
    AlertDialog.Builder builder;
    private String action;
    // Erros
    private AlertDialog alert;
    private String loginUser, passwordUser;
    private int idUser;
    private RequestQueue requestQueue;

    // Campos
    private EditText nameMutant, habilityMutant;
    private ImageView imageMutant;

    private Mutant mutant;

    // Interações
    private Button buttonDelete;
    private Button buttonUpdate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_mutant);

        nameMutant = findViewById(R.id.nameEditText);
        habilityMutant = findViewById(R.id.habilityEditText);

        buttonDelete = findViewById(R.id.btnDeleteMutant);
        buttonUpdate = findViewById(R.id.btnUpdateMutant);

    }

    @Override
    protected void onStart() {
        super.onStart();
        requestQueue = CustomVolleyRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
        Intent intent = getIntent();
        if (intent != null) {
            Bundle params = intent.getExtras();
            if (params != null) {
                loginUser = params.getString("login");
                passwordUser = params.getString("password");
                idUser = params.getInt("id");
                action = params.getString("action");
                switch (action) {
                    case "show":
                        buttonUpdate.setText("Update");
                        requestMutant(Request.Method.GET, formatUrl(urlMutant, idUser, loginUser, passwordUser));
                        break;
                }
            }
        }
    }

    public void deleteMutant(View view) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Confirmação");
        builder.setMessage("Você tem certeza disso? ");
        builder.setPositiveButton("Sim", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                requestMutant(Request.Method.DELETE, formatUrl(urlMutant, idUser, loginUser, passwordUser));
                finish();
            }
        }).setNegativeButton("Não", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        alert = builder.create();
        alert.show();
    }

    public void updateMutant(View view) {
        JSONObject json = new JSONObject();
        try {
            json.put("login", loginUser);
            json.put("password", passwordUser);
            json.put("name", nameMutant.getText().toString());
            json.put("hability", habilityMutant.getText().toString());

            // Method
            int method = (action.equals("show")) ? Request.Method.PUT : Request.Method.POST;

            // url
            String url = (action.equals("show")) ? urlMutant + "/" + mutant.getId() : urlMutant;
            requestQueue = CustomVolleyRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
            CustomJSONObjectRequest jsonObjectRequest = new CustomJSONObjectRequest(method,
                    url,
                    json, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    AlertMessageError("Foi criado", true);
                    finish();
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    AlertMessageError(error.toString(), true);
                }
            });
            requestQueue.add(jsonObjectRequest);
        } catch (JSONException e) {
            AlertMessageError("Algum campo não foi bme preenchido", true);
        }
    }

    private void requestMutant(int operation, String url) {
        System.out.println(url);

        CustomJSONObjectRequest jsonObjectRequest = new CustomJSONObjectRequest(operation,
                url,
                null, new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    System.out.println(response);
                    mutant = new Mutant(response.getInt("id"),
                            response.getString("name"),
                            response.getString("hability"));
                    nameMutant.setText(mutant.getName());
                    habilityMutant.setText(mutant.getHability());
                } catch (JSONException e) {
                    AlertMessageError(e.getMessage(), true);
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                System.out.println("Deu ruim");
            }
        });
        requestQueue.add(jsonObjectRequest);
    }

    private String formatUrl(String baseUrl, int id, String login, String password) {
        return baseUrl + "/" + String.valueOf(id) + "?login=" + login + "&password=" + password;
    }


    private void AlertMessageError(String error, boolean cancelable) {
        builder = new AlertDialog.Builder(this);
        builder.setMessage(error)
                .setCancelable(cancelable);
        AlertDialog alert = builder.create();
        alert.show();
    }
}
