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

    private static final String TAG = "firebaseLoin";
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;

    private EditText mEmail,mPassword;
    private Button bSignIn,bSignOut;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_firebase_login);

        mEmail = (EditText) findViewById(R.id.email);
        mPassword = (EditText) findViewById(R.id.pass);
        bSignIn = (Button) findViewById(R.id.SignIn);
        bSignOut = (Button) findViewById(R.id.SignOut);

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
                }else{
                    Log.d(TAG,"onAuthStateChanged:Signed_out");
                    Toast.makeText(getApplicationContext(),

                            "Not logged in yet",
                            Toast.LENGTH_SHORT).show();
                }
            }
        };

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

    public void Setting(View view){
        Intent startNewActivity = new Intent(this,Setting.class);
        startActivity(startNewActivity);
    }

    public void MainActivity(View view){
        Intent startNewActivity = new Intent(this,MainActivity.class);
        startActivity(startNewActivity);
    }
}
