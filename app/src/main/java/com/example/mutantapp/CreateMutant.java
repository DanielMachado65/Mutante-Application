package com.example.mutantapp;

import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.example.mutantapp.modals.Mutant;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

public class CreateMutant extends AppCompatActivity {

    public static final int IMAGE_GALLERY_REQUEST = 20;
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
        imageMutant = (ImageView) findViewById(R.id.imagemMutant);

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

    // ------------------ IMAGE
    public void onImageGalleryClicked(View view){
        Intent intent = new Intent(Intent.ACTION_PICK);
        File pictureDirectory = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        String pictureDirectoryPath = pictureDirectory.getPath();

        // URI representation
        Uri data = Uri.parse(pictureDirectoryPath);

        // set the data and type
        intent.setDataAndType(data, "image/*");
        startActivityForResult(intent, IMAGE_GALLERY_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            if(requestCode == IMAGE_GALLERY_REQUEST){
                // the address of the image on the sd Card;
                Uri imageUri = data.getData();
                // read the image data from the SD Card
                InputStream inputStream;
                try{
                    if (checkPermissionREAD_EXTERNAL_STORAGE(this)) {
                        inputStream = getContentResolver().openInputStream(imageUri);
                        Bitmap image = BitmapFactory.decodeStream(inputStream);
                        imageMutant.setImageBitmap(image);
                    }
                }catch (FileNotFoundException e){
                    AlertMessageError(e.getMessage(), true);
                }
            }
        }
    }

    public static final int MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE = 123;

    public boolean checkPermissionREAD_EXTERNAL_STORAGE(
            final Context context) {
        int currentAPIVersion = Build.VERSION.SDK_INT;
        if (currentAPIVersion >= android.os.Build.VERSION_CODES.M) {
            if (ContextCompat.checkSelfPermission(context,
                    Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
                if (ActivityCompat.shouldShowRequestPermissionRationale(
                        (Activity) context,
                        Manifest.permission.READ_EXTERNAL_STORAGE)) {
                    showDialog("External storage", context,
                            Manifest.permission.READ_EXTERNAL_STORAGE);

                } else {
                    ActivityCompat
                            .requestPermissions(
                                    (Activity) context,
                                    new String[] { Manifest.permission.READ_EXTERNAL_STORAGE },
                                    MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                }
                return false;
            } else {
                return true;
            }

        } else {
            return true;
        }
    }

    public void showDialog(final String msg, final Context context,
                           final String permission) {
        AlertDialog.Builder alertBuilder = new AlertDialog.Builder(context);
        alertBuilder.setCancelable(true);
        alertBuilder.setTitle("Permission necessary");
        alertBuilder.setMessage(msg + " permission is necessary");
        alertBuilder.setPositiveButton(android.R.string.yes,
                new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        ActivityCompat.requestPermissions((Activity) context,
                                new String[] { permission },
                                MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE);
                    }
                });
        AlertDialog alert = alertBuilder.create();
        alert.show();
    }

    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case MY_PERMISSIONS_REQUEST_READ_EXTERNAL_STORAGE:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    System.out.println("Ola");
                } else {
                    Toast.makeText(CreateMutant.this, "GET_ACCOUNTS Denied",
                            Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }
}
