package com.example.wifilogger;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.provider.Settings;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Date;

public class ListActivity extends AppCompatActivity {

    private String unique_id;
    DatabaseReference reff;
    TextView phoneId;
    TextView date;
    TextView location;
    ArrayAdapter<String> adapter;
    ListView listViewArray;
    ArrayList<String> displayList;
    private Button buttonDelete;
    private String currentDateTimeString;
    private double lat;
    private double lon;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate( savedInstanceState );
        setContentView( R.layout.activity_list );

        /* First get the section ID for the respective data */
        phoneId = (TextView) findViewById( R.id.phoneId );
        date = (TextView) findViewById( R.id.Date );
        location = (TextView) findViewById( R.id.location );
        listViewArray = (ListView) findViewById( R.id.listView );
        displayList = new ArrayList<String>();

        /* Call to the GPSGetter.class in order to get the latitude and the longitude */
        GPSGetter g = new GPSGetter(getApplicationContext());
        Location l = g.getLocation();
        lat = l.getLatitude();
        lon = l.getLongitude();

        /* Getting  the current date and android ID */
        currentDateTimeString = DateFormat.getDateTimeInstance().format(new Date());
        unique_id = Settings.Secure.getString( getApplicationContext().getContentResolver(), Settings.Secure.ANDROID_ID );

        /* Connecting  to the Firebase database and getting the data based on the phone ID */
        reff = FirebaseDatabase.getInstance().getReference().child( "Phone ID: " + unique_id );
        reff.addListenerForSingleValueEvent( new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                /* start setting some data on the respective sections */
                phoneId.setVisibility( View.VISIBLE );
                date.setVisibility( View.VISIBLE );
                location.setVisibility( View.VISIBLE );
                listViewArray.setVisibility( View.VISIBLE );
                phoneId.setText( "Phone ID:" + " " + unique_id );
                date.setText( "Date:" + " " + currentDateTimeString );
                location.setText( "(GPS)" + " " + "Latitude: " + lat + " Longitude: " + lon );

                /* using this for loop to go thro the database data and display each Wifi data using an arraylist */
                for (DataSnapshot dss : dataSnapshot.getChildren()) {
                    String BSSID = dss.getKey();
                    Integer frequency = dss.child( "FREQUENCY" ).getValue( Integer.class );
                    String frequencySt = String.valueOf( frequency );
                    Integer RSSI = dss.child( "RSSI" ).getValue( Integer.class );
                    String RSSISt = String.valueOf( RSSI );
                    String SSID = dss.child( "SSID" ).getValue( String.class );
                    String fullResult = BSSID + "\n" + "FREQUENCY:" + " " + frequencySt + "\n" + "RSSI:" + " " + RSSISt + "\n" + "SSID:" + " " + SSID;
                    displayList.add( fullResult  );
                }
                adapter = new ArrayAdapter<String>( ListActivity.this, android.R.layout.simple_list_item_1, displayList );
                listViewArray.setAdapter( adapter );
            }


            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }

        });

        /* Delete button */
        /* On click deletes the data saved under the respective phone ID from the Database */
        /* Redirects the user to the MainActivity.class (Menu Activity) */
        buttonDelete = findViewById( R.id.deleteBtn );
        buttonDelete.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reff.removeValue();
                location.setText( "" );
                date.setText( "" );
                phoneId.setText( "" );
                adapter.clear();
                Toast.makeText(ListActivity.this, "Data deleted,returning to main screen", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(ListActivity.this, MainActivity.class);
                startActivity(intent);
            }
        });
    }

}



