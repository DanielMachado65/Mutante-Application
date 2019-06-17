package com.example.mutante;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

public class CreateUser extends AppCompatActivity implements Response.Listener, Response.ErrorListener {

    private EditText loginText, passwordText;
    private Button btnCreate;
    private static final String Request_TAG_CREATE = "UserCreation";
    private static final String urlCreate = "http://10.0.2.2:8030/create";

    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_user);

        loginText = findViewById(R.id.loginText);
        passwordText = findViewById(R.id.passwordText);
        btnCreate = findViewById(R.id.btnCreate);

        btnCreate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (loginText != null){
                    JSONObject user = new JSONObject();
                    try {
                        user.put("login", loginText.getText().toString());
                        user.put("password", passwordText.getText().toString());

                        requestQueue = CustomVolleyRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
                        CustomJSONObjectRequest jsonObjectRequest = new CustomJSONObjectRequest(Request.Method.POST,
                                urlCreate,
                                user,
                                CreateUser.this, CreateUser.this);
                        requestQueue.add(jsonObjectRequest);
                    }catch (JSONException e){
                        Toast.makeText(CreateUser.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        System.out.println(error);
    }

    @Override
    public void onResponse(Object response) {
        startActivity(new Intent(getApplicationContext(), MainActivity.class));
        finish();
    }
}
