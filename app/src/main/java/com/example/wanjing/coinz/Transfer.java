package com.example.wanjing.coinz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;

import java.util.HashMap;
import java.util.Map;

public class Transfer extends AppCompatActivity {


    private EditText memail;
    private Button mstore;
    private EditText mCoins;

    private static final String email = "Name";
    private static final String coins = "Text";

    private DocumentReference firestoreBank;

    private final String tag = "Transfer";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_transfer);

        memail = findViewById(R.id.useremail);
        mstore = findViewById(R.id.store);
        mCoins = findViewById(R.id.coins);

        mstore.setOnClickListener(view -> store());
    }

    private void store() {
        // create a message of the form { ”email”: str1, ”coins”: str2 }
        Map<String, String> bank = new HashMap<>();
        bank.put(email, memail.getText().toString());
        bank.put(coins, mCoins.getText().toString());
        // send the message and listen for success or failure
        firestoreBank.set(bank)
                .addOnSuccessListener(v -> Toast.makeText(getApplicationContext(),

                        "Message sent!",
                        Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    Log.e(tag, e.getMessage());
                });

    }
}
