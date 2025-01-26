package com.example.mango_disease_detection_app;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

import javax.security.auth.login.LoginException;

public class LoginActivity extends AppCompatActivity {
    private TextView label ,tv_text,tv_btn;
    private Button btn_login_signup;
    private EditText ed_email,ed_password;

    private FirebaseAuth auth;
    DatabaseReference profile_rootRef;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        auth = FirebaseAuth.getInstance();

        if (auth.getCurrentUser() != null) {
//            LOGS USER IN ONCE IT FINDS HE HAD LOGGED IN!
            startActivity(new Intent(getApplicationContext(), MainActivity.class));
            finish();
        }

        setContentView(R.layout.activity_login);

        label = (TextView) findViewById(R.id.label);
        tv_text = (TextView) findViewById(R.id.tv_text);
        tv_btn = (TextView) findViewById(R.id.tv_btn);

        btn_login_signup = (Button) findViewById(R.id.btn_sign_login);

        ed_email = (EditText) findViewById(R.id.et_email);
        ed_password = (EditText) findViewById(R.id.et_password);


        auth = FirebaseAuth.getInstance();



        tv_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String value = tv_btn.getText().toString();
                if (value.equals("Sign Up")){
                    tv_btn.setText("login");
                    tv_text.setText("Already have an account   ");
                    label.setText("Sign Up");
                    btn_login_signup.setText("Sign Up");
                }
                else {
                    tv_btn.setText("Sign Up");
                    tv_text.setText("Don't have an Account   ");
                    label.setText("Login");
                    btn_login_signup.setText("login");
                }

            }
        });

        btn_login_signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {


                String email = ed_email.getText().toString();
                final String password = ed_password.getText().toString();

                if (TextUtils.isEmpty(email)) {
                    Toast.makeText(getApplicationContext(), "Enter email address!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (TextUtils.isEmpty(password)) {
                    Toast.makeText(getApplicationContext(), "Enter password!", Toast.LENGTH_SHORT).show();
                    return;
                }

                if (password.length() < 6) {
                    Toast.makeText(getApplicationContext(), "Password too short, enter minimum 6 characters!", Toast.LENGTH_SHORT).show();
                    return;
                }

                String value = btn_login_signup.getText().toString();
                if (value.equals("login")){
                    // Login Logic

//                    Toast.makeText(loginActivity.this, "Login Btn", Toast.LENGTH_SHORT).show();


                    auth.signInWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {

                                    if (!task.isSuccessful()) {
                                        // there was an error
                                        if (password.length() < 6) {
                                            ed_password.setError("set password correctly more than 8 character ");
                                        } else {
                                            Toast.makeText(getApplicationContext(),"Auth error", Toast.LENGTH_LONG).show();
                                        }

                                    } else {
                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();
                                    }

                                }
                            });

                }
                else {
                    // Sign Up Logic

//                    Toast.makeText(loginActivity.this, "Sign Up Btn", Toast.LENGTH_SHORT).show();

                    auth.createUserWithEmailAndPassword(email, password)
                            .addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                @Override
                                public void onComplete(@NonNull Task<AuthResult> task) {
//                                    Toast.makeText(loginActivity.this, "createUserWithEmail:onComplete:" + task.isSuccessful(), Toast.LENGTH_SHORT).show();
//
                                    if (!task.isSuccessful()) {
                                        Toast.makeText(getApplicationContext(), "Authentication failed." + task.getException(),
                                                Toast.LENGTH_SHORT).show();
                                    }else{

                                        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                                        startActivity(intent);
                                        finish();

                                    }
                                }
                            });

                }

            }
        });


    }
}