package com.example.wanjing.coinz;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;


public class Home extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
    }

    public void Setting(View view){
        Intent startNewActivity = new Intent(this,Setting.class);
        startActivity(startNewActivity);
    }
}
