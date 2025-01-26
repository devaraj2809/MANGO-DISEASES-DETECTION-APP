package com.example.mango_disease_detection_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import org.jetbrains.annotations.NotNull;
import org.json.JSONObject;

import java.io.IOException;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class ResultActivity extends AppCompatActivity {
    String url = "http://192.168.42.10:5000/detect";
    //    String url = "http://127.0.0.1:5000/detect";
    private TextView tv_disease_type1,tv_diagnosis,tv_precautions,tv_symptoms,tv_description ;
    private TextView tv_disease_type1_title,tv_diagnosis_title,tv_precautions_title,tv_symptoms_title,tv_description_title ;

    private TextView loading ;
    private ImageView imageView;
    private Bitmap bitmap_image;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_result);

        tv_disease_type1 = (TextView) findViewById(R.id.tv_disease_type);
        tv_disease_type1.setVisibility(View.INVISIBLE);
        tv_diagnosis = (TextView) findViewById(R.id.tv_dignosis);
        tv_diagnosis.setVisibility(View.INVISIBLE);
        tv_precautions = (TextView) findViewById(R.id.tv_precautions);
        tv_precautions.setVisibility(View.INVISIBLE);
        tv_symptoms = (TextView) findViewById(R.id.tv_symptoms);
        tv_symptoms.setVisibility(View.INVISIBLE);
        tv_description = (TextView) findViewById(R.id.tv_description);
        tv_description.setVisibility(View.INVISIBLE);

        tv_disease_type1_title = (TextView) findViewById(R.id.tv_disease_type_title);
        tv_disease_type1_title.setVisibility(View.INVISIBLE);
        tv_diagnosis_title = (TextView) findViewById(R.id.tv_dignosis_title);
        tv_diagnosis_title.setVisibility(View.INVISIBLE);
        tv_precautions_title = (TextView) findViewById(R.id.tv_precautions_title);
        tv_precautions_title.setVisibility(View.INVISIBLE);
        tv_symptoms_title = (TextView) findViewById(R.id.tv_symptoms_title);
        tv_symptoms_title.setVisibility(View.INVISIBLE);
        tv_description_title = (TextView) findViewById(R.id.tv_description_title);
        tv_description_title.setVisibility(View.INVISIBLE);

        loading = (TextView) findViewById(R.id.loading);


        imageView =(ImageView) findViewById(R.id.imageView2);

        // Inside the ReceiverActivity's onCreate() or wherever you want to retrieve the image
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("IMAGE_BITMAP")) {
            Bitmap receivedImage = (Bitmap) intent.getParcelableExtra("IMAGE_BITMAP");

            // Now, you can use "receivedImage" as needed (e.g., display it in an ImageView)
            imageView.setImageBitmap(receivedImage);
        }

        detectDisease(bitmap_image);

    }

    private void detectDisease(Bitmap bitmap_image) {

//        tv_disease_type1.setText("Loading ....");
        // creating a client
        OkHttpClient okHttpClient = new OkHttpClient();

        // building a request
        Request request = new Request.Builder().url(url).build();

        // making call asynchronously
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            // called if server is unreachable
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(ResultActivity.this, "server down"+e.getMessage(), Toast.LENGTH_SHORT).show();
                        tv_disease_type1.setText(e.getMessage());
                    }
                });
            }

            @Override
            // called if we get a
            // response from the server
            public void onResponse(
                    @NotNull Call call,
                    @NotNull Response response)
                    throws IOException {

                String data = response.body().string();

                try {
                    JSONObject jsonObject = new JSONObject(data);
                    String disease_type = jsonObject.getString("Disease_Type");
                    String Description = jsonObject.getString("Description");
                    String Symptoms = jsonObject.getString("Symptoms");
                    String daignosis = jsonObject.getString("Diagnosis");
                    String precautions = jsonObject.getString("Precautions");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            updateUI();

                            // Update UI elements here
                            tv_disease_type1.setText(disease_type);
                            tv_description.setText(Description);
                            tv_symptoms.setText(Symptoms);
                            tv_diagnosis.setText(daignosis);
                            tv_precautions.setText(precautions);
                        }
                    });
                }
                catch (Exception e){
                    e.printStackTrace();
                }
            }

        });
    }

    private void updateUI() {
        tv_disease_type1.setVisibility(View.VISIBLE);
        tv_diagnosis.setVisibility(View.VISIBLE);
        tv_precautions.setVisibility(View.VISIBLE);
        tv_symptoms.setVisibility(View.VISIBLE);
        tv_description.setVisibility(View.VISIBLE);

        tv_disease_type1_title.setVisibility(View.VISIBLE);
        tv_diagnosis_title.setVisibility(View.VISIBLE);
        tv_precautions_title.setVisibility(View.VISIBLE);
        tv_symptoms_title.setVisibility(View.VISIBLE);
        tv_description_title.setVisibility(View.VISIBLE);

        loading.setVisibility(View.INVISIBLE);
    }
}