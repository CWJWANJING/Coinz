package com.example.wanjing.coinz;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class firebaseLogin extends AppCompatActivity {

    // a general tag for firebaseLogin
    private static final String TAG = "firebaseLoin";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    // set up for the Button and the EditText
    private EditText mEmail,mPassword;
    private Button bSignIn,bSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_login);

        mEmail = findViewById(R.id.email);
        mPassword = findViewById(R.id.pass);
        bSignIn = findViewById(R.id.SignIn);
        bSignOut = findViewById(R.id.SignOut);

        mAuth = FirebaseAuth.getInstance();

        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser user = firebaseAuth.getCurrentUser();
                if(user != null){
                    Log.d(TAG,"onAuthStateChanged:Signed_in" + user.getUid());
                    Toast.makeText(getApplicationContext(),

                            "Successfully signed with " + user.getEmail(),
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

        // when sign in button is clicked:
        bSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String email = mEmail.getText().toString();
                String password = mPassword.getText().toString();
                if(!email.equals("") && !password.equals("")){
                    mAuth.signInWithEmailAndPassword(email,password);
                }else{
                    Toast.makeText(getApplicationContext(),

                            "You didn't fill in all the balnks",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });

        // when sign out button is clicked:
        bSignOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                mAuth.signOut();
                Toast.makeText(getApplicationContext(),

                        "Signed out",
                        Toast.LENGTH_SHORT).show();
            }
        });
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
    }

    // when user wants to go to setting page:
    public void Setting(View view){
        Intent startNewActivity = new Intent(this,Setting.class);
        startActivity(startNewActivity);
    }

    // when user wants to go to main activity:
    public void MainActivity(View view){
        Intent startNewActivity = new Intent(this,MainActivity.class);
        startActivity(startNewActivity);
    }
}
