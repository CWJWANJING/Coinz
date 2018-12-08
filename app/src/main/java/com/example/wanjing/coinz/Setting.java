package com.example.wanjing.coinz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class Setting extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
    }


    public void firebaseLogin(View view){
        Intent startNewActivity = new Intent(this,firebaseLogin.class);
        startActivity(startNewActivity);
    }

    public void Register(View view){
        Intent startNewActivity = new Intent(this,Register.class);
        startActivity(startNewActivity);
    }

    public void Communicaz(View view){
        Intent startNewActivity = new Intent(this,Communicaz_Activity.class);
        startActivity(startNewActivity);
    }

    public void Transfer(View view){
        Intent startNewActivity = new Intent(this,Transfer.class);
        startActivity(startNewActivity);
    }
}
