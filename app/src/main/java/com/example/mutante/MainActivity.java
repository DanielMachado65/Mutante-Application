package com.example.mutante;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class MainActivity extends AppCompatActivity implements Response.Listener, Response.ErrorListener {

    private static final String Request_TAG_LOGIN = "UserAutentication";
    private static final String urlLogin = "http://10.0.2.2:8030/login";
    private EditText name;
    private EditText password;
    private Button login;

    // fila para a validação
    private RequestQueue requestQueue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        getSupportActionBar().hide();
        // mapeamento

        name = findViewById(R.id.etName);
        password = findViewById(R.id.etPassword);
        login = findViewById(R.id.btnLogin);
    }

    public void createUser(View view){
        startActivity(new Intent(getApplicationContext(), CreateUser.class));
    }

    @Override
    protected void onStart() {
        super.onStart();
        requestQueue = CustomVolleyRequestQueue.getInstance(this.getApplicationContext()).getRequestQueue();

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(name.getText().toString() != "" && password.getText().toString() != ""){
                    JSONObject user = new JSONObject();
                    try {
                        user.put("login", name.getText().toString());
                        user.put("password", password.getText().toString());

                        requestQueue = CustomVolleyRequestQueue.getInstance(getApplicationContext()).getRequestQueue();
                        CustomJSONObjectRequest jsonObjectRequest = new CustomJSONObjectRequest(Request.Method.POST,
                                urlLogin,
                                user,
                                MainActivity.this, MainActivity.this);
                        requestQueue.add(jsonObjectRequest);
                    }catch (JSONException e){
                        Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (requestQueue != null)
            requestQueue.cancelAll(Request_TAG_LOGIN);
    }

    @Override
    public void onResponse(Object response) {
        try {
            String retu = ((JSONObject) response).getString("msg");
            Toast.makeText(this, retu, Toast.LENGTH_SHORT).show();
            Bundle params = new Bundle();
            params.putString("login", name.getText().toString());
            params.putString("password", password.getText().toString());
            startActivity(new Intent(getApplicationContext(), Dashboard.class).putExtras(params));
            finish();
        } catch (JSONException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onErrorResponse(VolleyError error) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Usuário e senha inválidos!");
        AlertDialog alert = builder.create();
        alert.show();
    }
}
