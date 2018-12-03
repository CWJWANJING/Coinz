package com.example.wanjing.coinz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;

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
}
