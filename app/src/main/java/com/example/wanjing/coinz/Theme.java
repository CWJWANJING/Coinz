package com.example.wanjing.coinz;

import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.Button;

public class Theme extends AppCompatActivity {

    private Button btn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        setTheme(getFlag() ? R.style.AppTheme: R.style.coinz);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_theme);

        btn = (Button) findViewById(R.id.theme);
        btn.setOnClickListener((view) -> {
            saveFlag(!getFlag());

            Intent intent = new Intent(Theme.this,Theme.class);
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

}
