package com.example.wanjing.coinz;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreSettings;

import java.util.HashMap;
import java.util.Map;

public class Transfer extends AppCompatActivity {


    private EditText memail;
    private Button mstore;
    private EditText mCoins;

    private static final String email = "Username";
    private static final String coins = "Coins";

    private FirebaseFirestore firestore;
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

        firestore = FirebaseFirestore.getInstance();

        // Use com.google.firebase.Timestamp objects instead of java.util.Date objects
        FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder().setTimestampsInSnapshotsEnabled(true)
                .build();
        firestore.setFirestoreSettings(settings);
        firestoreBank =
                firestore.collection("Bank")
                        .document("Transfer");
        // Set a listener for changes to the /Bank/Transfer document
        realtimeUpdateListener();
    }

    private void store() {
        // create a bANK of the form { username: coins }
        Map<String, String> bank = new HashMap<>();
        bank.put(memail.getText().toString(),mCoins.getText().toString());
        // send the coins and listen for success or failure
        firestoreBank.set(bank)
                .addOnSuccessListener(v -> Toast.makeText(getApplicationContext(),

                        "Coins Transferred!",
                        Toast.LENGTH_SHORT).show())
                .addOnFailureListener(e -> {
                    Log.e(tag, e.getMessage());
                });

    }

    private void realtimeUpdateListener() {
        firestoreBank.addSnapshotListener((documentSnapshot, e) -> {
            if (e != null) {
                Log.e(tag, e.getMessage());
            } else if (documentSnapshot != null && documentSnapshot.exists()) {
                String incoming = (documentSnapshot.getData().get(email))
                        + ": "
                        + (documentSnapshot.getData().get(coins));
                mCoins.setText(incoming);
            }
        });
    }

}
