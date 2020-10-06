package com.example.wifilogger;

import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class LogInActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthStateListener;
    Button login;
    Button register;
    EditText username;
    EditText password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_log );
        /*
         Checking if the WIFI is disable.
         If WIFI is disable, it will be enabled automatically.
        */
        WifiManager wifiManager = (WifiManager) getApplicationContext().getSystemService( Context.WIFI_SERVICE );
        if (!wifiManager.isWifiEnabled()) {
            Toast.makeText(this, "WiFi is disabled ... We need to enable it", Toast.LENGTH_LONG).show();
            wifiManager.setWifiEnabled(true);
        }

        /* Saving views from activity_log in variables */
        username = findViewById( R.id.Username );
        password = findViewById( R.id.userPassword );
        login = findViewById( R.id.BtnLogin );
        register = findViewById( R.id.BtnRegister );

        /* If user is authenticated the MainAtivity.class (Menu) starts */
        mAuth = FirebaseAuth.getInstance();
        mAuthStateListener= new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    startActivity( new Intent( LogInActivity.this, MainActivity.class ) );
                }
            }
        };
        /* Login button */
        /* on click executes mthod singIn */
        login.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIN();
            }
        });
        /* Register button */
        /* on click executes method singUP */
        register.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUP();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener( mAuthStateListener );
    }
    /*
     Method that lets the user Log In if an account is created.
     Checks if the user has complete the necessary fields and Toasts him instructions otherwise
    */
    private void signIN() {
        String mEmail = username.getText().toString();
        String mPassword = password.getText().toString();

        if (TextUtils.isEmpty( mEmail ) || (TextUtils.isEmpty( mPassword ))) {
            Toast.makeText( LogInActivity.this, "Please complete fields", Toast.LENGTH_LONG ).show();
            if ((TextUtils.isEmpty( mPassword ) || mPassword.length() < 6 )){
                password.setError("Your password should have at least 6 characters");
            }

        } else {
            mAuth.signInWithEmailAndPassword( mEmail, mPassword ).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if (!task.isSuccessful()) {
                        Toast.makeText( LogInActivity.this, "Please Register to continue ", Toast.LENGTH_LONG ).show();
                    }
                }
            } );
        }
    }
    /* Method that allows the user to Sign up (register) with an account */
    private void signUP() {
        mAuth.createUserWithEmailAndPassword( username.getText().toString(), password.getText().toString()).addOnCompleteListener( new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    Toast.makeText( LogInActivity.this, "Register succesfully", Toast.LENGTH_LONG).show();
                }else {
                     Toast.makeText( LogInActivity.this, task.getException().getMessage(), Toast.LENGTH_LONG).show();

                }
            }
        });
    }

}
