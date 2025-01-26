package com.example.mango_disease_detection_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Bitmap;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.github.dhaval2404.imagepicker.ImagePicker;

import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import android.os.Handler;
public class MainActivity extends AppCompatActivity {
    private TextView sample;
    private Button btn_submit,btn_login;

    private ImageView imageView;

    int IMAGE_SIZE = 180;

    Dialog loadingdialog;

    private StorageReference storageReference;
    private FirebaseAuth mAuth;

    public MainActivity() {
    }

    @SuppressLint({"SetTextI18n", "MissingInflatedId"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        mAuth = FirebaseAuth.getInstance();

        btn_submit = (Button) findViewById(R.id.btn_submit);
        btn_login = (Button) findViewById(R.id.btn_logout);

        imageView = (ImageView) findViewById(R.id.imageView);

        storageReference = FirebaseStorage.getInstance().getReference();

        loadingdialog = new Dialog(MainActivity.this);


        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImagePicker.with(MainActivity.this)
                        .crop()	    			//Crop image(Optional), Check Customization for more option
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .galleryMimeTypes(  //Exclude gif images
                                new String[]{
                                        "image/png",
                                        "image/jpg",
                                        "image/jpeg"
                                }
                        )
                        .start();
            }
        });

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                startActivity(new Intent(getApplicationContext(),LoginActivity.class));
                finish();
            }
        });

    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {

        if(resultCode == RESULT_OK){
            if(requestCode == 3){

                if (data != null && data.getExtras() != null) {
                    Bitmap imageBitmap = (Bitmap) data.getExtras().get("data");
                    if (imageBitmap != null) {
                        // Display the original image in the ImageView
                        imageView.setImageBitmap(imageBitmap);

                        // Call a method to perform any additional processing (e.g., classification)
                        classifyImage(imageBitmap);
                    }
                } else {
                    // Handle error
                    Toast.makeText(this, "Error capturing image", Toast.LENGTH_SHORT).show();
                }
            }else{
                Uri dat = data.getData();
                imageView.setImageURI(dat);
                Bitmap image = null;
                try {
                    image = MediaStore.Images.Media.getBitmap(this.getContentResolver(), dat);
                } catch (IOException e) {
                    e.printStackTrace();
                }
//                imageView.setImageBitmap(image);

                image = Bitmap.createScaledBitmap(image, IMAGE_SIZE, IMAGE_SIZE, false);
                classifyImage(image);
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    private void classifyImage(Bitmap image1) {

        Bitmap image = Bitmap.createScaledBitmap(image1, IMAGE_SIZE, IMAGE_SIZE, false);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        image.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] data = baos.toByteArray();

        // Define a unique name for the image file in Firebase Storage
//        String imageName = "image_" + System.currentTimeMillis() + ".jpg";  // Aspergillus_137.jpg
        String imageName = "detect_fruit.jpg";

        // Create a reference to the Firebase Storage location
        StorageReference imageRef = storageReference.child(imageName);

        // invoking startLoadingDialog method
        loadingdialog.startLoadingdialog();

        // using handler class to set time delay methods
        Handler handler = new Handler();
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                // after 4 seconds
                loadingdialog.dismissdialog();

                btn_submit.setText("Loading .....");

                // Upload the image to Firebase Storage
                UploadTask uploadTask = imageRef.putBytes(data);
                uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {

                        // Image uploaded successfully
                        Bitmap image = Bitmap.createScaledBitmap(image1, IMAGE_SIZE, IMAGE_SIZE, false);
                        Toast.makeText(MainActivity.this, "Redirecting ....", Toast.LENGTH_SHORT).show();
                        Intent in = new Intent(MainActivity.this,ResultActivity.class);
                        in.putExtra("IMAGE_BITMAP", image1);
                        startActivity(in);

                        btn_submit.setText("Select Image and Predict");
                    }
                });

            }
        }, 4000); // 4 seconds
    }


    @Override
    protected void onStart() {
        super.onStart();

        FirebaseUser mFirebaseuser = mAuth.getCurrentUser();
        if (mFirebaseuser != null){
            // there is some user
//            tv = (TextView)findViewById(R.id.);
//            tv.setText(mFirebaseuser.getEmail());
            Toast.makeText(this, "name is : "+mFirebaseuser.getEmail() +" /", Toast.LENGTH_SHORT).show();

        }else {
            // no one logout
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }

    }



}