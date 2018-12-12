package com.example.wanjing.coinz;

import android.content.Context;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class firebaseMethods {
    private final String TAG = "firebaseMethods";
    private FirebaseAuth mAuth;
    private Context mContext;
    private String userID;

    public firebaseMethods(Context context){
        mAuth = FirebaseAuth.getInstance();
        mContext = context;

        if(mAuth.getCurrentUser() != null){
            userID = mAuth.getCurrentUser().getUid();
        }
    }


    public void registerNewEmail(final String email,String password){

        mAuth.createUserWithEmailAndPassword(email,password).addOnCompleteListener(new OnCompleteListener<AuthResult>(){
            @Override
            public void onComplete(@NonNull Task<AuthResult> task){
                Log.d(TAG,"onComplete" + task.isSuccessful());
                if(!task.isSuccessful()){
                    Toast.makeText(mContext,
                            "failed",
                            Toast.LENGTH_SHORT).show();
                }else if(task.isSuccessful()){
                    userID = mAuth.getCurrentUser().getUid();
                    Log.d(TAG,"changed" + userID);
                    Toast.makeText(mContext,
                            "Success!",
                            Toast.LENGTH_LONG).show();
                    enterMain();
                }
            }
        });
    }

    // when user wants to enter main activity and they click the button:
    private void enterMain() {
        Intent startNewActivity = new Intent(mContext,MainActivity.class);
        mContext.startActivity(startNewActivity);
    }
}

