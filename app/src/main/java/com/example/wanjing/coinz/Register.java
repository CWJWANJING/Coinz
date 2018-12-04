package com.example.wanjing.coinz;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class Register extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private Context mContext;
    private String email,username,password;
    private EditText mEmail,mPassword,mUsername;
    private Button bRegister;
    private firebaseMethods fm;

    public static final String TAG = "RegisterActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        mContext = Register.this;
        fm = new firebaseMethods(mContext);
        initWidget();
        init();
        setupFirebaseAuth();

    }

    private void init(){
        bRegister.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view){
                email = mEmail.getText().toString();
                password = mPassword.getText().toString();

                if(checkInputs(email,password)){
                    fm.registerNewEmail(email,password);
                }
            }
        });
    }



    private boolean checkInputs(String email,String password){
        Log.d(TAG,"Checking inputs");
        if(email.equals("") || password.equals("")){
            Toast.makeText(getApplicationContext(),

                    "There are null fields.",
                    Toast.LENGTH_SHORT).show();
            return false;
        }else{
            return true;
        }
    }

    private void initWidget(){
        Log.d(TAG,"initializing widgt");
        mEmail = (EditText) findViewById(R.id.input_email);
        mPassword = (EditText) findViewById(R.id.input_password);
        bRegister = (Button) findViewById(R.id.Register);
        mContext = Register.this;
    }

    private void setupFirebaseAuth(){
        Log.d(TAG,"setting up firebase auth");

        mAuth = FirebaseAuth.getInstance();
        mAuthListener = (FirebaseAuth.AuthStateListener)(firebaseAuth)->{
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null){
                Log.d(TAG,"setupfirebaseauth:Set up" + user.getUid());
                Toast.makeText(getApplicationContext(),

                        "Successfully setted up " + user.getEmail(),
                        Toast.LENGTH_SHORT).show();
            }else{
                Log.d(TAG,"setupfirebaseauth:not set up yet" + user.getUid());
                Toast.makeText(getApplicationContext(),

                        "not setted up " + user.getEmail(),
                        Toast.LENGTH_SHORT).show();
            }
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }
}
