package com.example.wanjing.coinz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
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


    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        setTheme(getFlag() ? R.style.AppTheme: R.style.coinz);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);

        btn = (Button) findViewById(R.id.theme);
        btn.setOnClickListener((view) -> {
            saveFlag(!getFlag());

            Intent intent = new Intent(Setting.this,Setting.class);
            startActivity(intent);
            finish();
        });
    }

    private void saveFlag(boolean flag){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean("green",flag);
        editor.commit();
    }

    private boolean getFlag(){
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(this);
        return preferences.getBoolean("green",false);
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
