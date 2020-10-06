package com.example.wifilogger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private WifiManager wifiManager;
    private Button buttonScan;
    private Button buttonRead;
    private Button buttonLogOut;
    private Button buttonDelete;
    private List<ScanResult> results;
    private String unique_id;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        /*  In MainActivity.class after we created all the private variables we set the respective task for when each of the button is clicked */
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        /* Scan Data Button */
        /* on click executes scanWifi method */
        buttonScan = findViewById(R.id.scanBtn);
        buttonScan.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                scanWifi();
            }
        });

        /* Delete Account button */
        /* on click executes deleteUser method */
        buttonDelete = findViewById(R.id.deleteAccount);
        buttonDelete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                deleteUser();
            }
        });

        /* Log out button */
        /* on click Logs the user out of his account and redirects him to the LogInActivity.class (Login activity)*/
        buttonLogOut = findViewById(R.id.logOutBtn);
        buttonLogOut.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i=new Intent(MainActivity.this, LogInActivity.class);
                i.putExtra("finish", true);
                i.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity( i );
                FirebaseAuth.getInstance().signOut();
                finish();
                Toast.makeText(MainActivity.this, "You Logged Out", Toast.LENGTH_SHORT).show();
            }
        });

        /* Read Data button */
        /* on click it redirects the user to the ListActivity.class (List activity) */
        buttonRead = findViewById(R.id.readBtn);
        buttonRead.setOnClickListener( new View.OnClickListener() {
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, ListActivity.class);
                startActivity(intent);
            }

        });
    }

    /* Method that scans for Wifi AP's */
    /* After the Scan Data button will be clicked a new request for a Wifi scan will be started and a toast will be displayed */
    private void scanWifi() {
        wifiManager = (WifiManager) getApplicationContext().getSystemService(Context.WIFI_SERVICE);
        registerReceiver(wifiReceiver, new IntentFilter(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION));
        wifiManager.startScan();
        Toast.makeText(this, "Scanning WiFi ...", Toast.LENGTH_SHORT).show();
    }

    /*
     Method that deletes the user account from the Firebase Database.
     Redirects the user  back to the LogInActivity.class (Log In page).
    */
    public void deleteUser() {
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        user.delete()
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "User account deleted", Toast.LENGTH_LONG).show();
                            Intent intent = new Intent(MainActivity.this,LogInActivity.class);
                            startActivity(intent);
                        }
                    }
                });
    }

    /* After the Wifi scan request is started the app will store the data got from the scan on the respective Firebase Database by getting the phone id */
    BroadcastReceiver wifiReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            results = wifiManager.getScanResults();
            unregisterReceiver(this);
            unique_id = Settings.Secure.getString(getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID);

            /* Here we get the specific instance of the database based on the phone id */
            FirebaseDatabase database = FirebaseDatabase.getInstance();
            DatabaseReference myRef = database.getReference("Phone ID: " + unique_id);

            /* For each Wifi AP scanned the respective BSSID, SSID, frequency and RSSI will be store in the Firebase Database*/
            for (ScanResult scanResult : results) {
                String netSSID = scanResult.SSID;
                int rssi = wifiManager.getConnectionInfo().getRssi();
                if (scanResult.SSID.equalsIgnoreCase(netSSID)) {
                    rssi = scanResult.level;
                }
                myRef.child("BSSID: " + scanResult.BSSID).child("SSID").setValue(scanResult.SSID);
                myRef.child("BSSID: " + scanResult.BSSID).child("FREQUENCY").setValue(scanResult.frequency);
                myRef.child("BSSID: " + scanResult.BSSID).child("RSSI").setValue(rssi);
            }
            Toast.makeText(MainActivity.this, "There are"  +" "+ results.size() +" "+"AP's ", Toast.LENGTH_SHORT).show();
        }
    };
}

