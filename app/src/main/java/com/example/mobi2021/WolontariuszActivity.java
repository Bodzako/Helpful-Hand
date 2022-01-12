package com.example.mobi2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.location.LocationRequest;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Map;

public class WolontariuszActivity extends AppCompatActivity {

    private TextView mTextView;
    private Button mButton;
    LocationTrack locationTrack;
    String Latitude;
    String Longitude;
    String statusGotowosci="";
    String statusCzyWybrany="";
    String statusDlugosc="";
    String statusSzerokosc="";
    String info;
    FirebaseUser user;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wolontariusz);
        mTextView=(TextView)findViewById(R.id.textView);
        user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getUid();
        DatabaseReference reff= FirebaseDatabase.getInstance().getReference(id+"/gotowy");
        reff.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                statusGotowosci=snapshot.getValue(String.class);
                //Log.e("e",snapshot.getValue(String.class));
                info="Witaj "+user.getDisplayName().toString()+", twój aktualny status gotowości to: " + statusGotowosci;
                mTextView.setText(info);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }

        });


        final Handler handler = new Handler();
        final int delay = 10000; // 1000 milliseconds == 1 second

        handler.postDelayed(new Runnable() {
            public void run() {
                DatabaseReference reff2= FirebaseDatabase.getInstance().getReference(id+"/czyWybrany");
                reff2.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        statusCzyWybrany=snapshot.getValue(String.class);
                        if(statusCzyWybrany.equals("Tak"))
                        {
                            DatabaseReference reff3 = FirebaseDatabase.getInstance().getReference(id+"/wybranyDlugosc");
                            DatabaseReference reff4 = FirebaseDatabase.getInstance().getReference(id+"/wybranySzerokosc");
                            reff3.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    statusDlugosc = snapshot.getValue(String.class);
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            reff4.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    statusSzerokosc = snapshot.getValue(String.class);
                                    changeIntent();
                                }
                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });
                            reff2.setValue("Nie");
                            Log.e("czy finish zadzialal?", "TAK");
                            finish();
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
                locationTrack = new LocationTrack(WolontariuszActivity.this);
                if (locationTrack.canGetLocation()) {
                    double longitude = locationTrack.getLongitude();
                    double latitude = locationTrack.getLatitude();
                    //Pobieramy sourceLatidude z urządzenia
                    Latitude = Double.toString(latitude);
                    //Pobieramy sourceLongitude z urządzenia
                    Longitude = Double.toString(longitude);
                    ustawLokalizacje(Longitude, Latitude);

                }
                handler.postDelayed(this, delay);
            }
        }, delay);


        mButton=(Button)findViewById(R.id.button);


        mButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String id = user.getUid().toString();
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef = database.getReference(id+"/gotowy");
                String info2="Witaj "+user.getDisplayName().toString()+", twój aktualny status gotowości to: ";
                if(statusGotowosci.equals("Tak")){
                    statusGotowosci="Nie";
                    myRef.setValue("Nie");
                }
                else{
                    statusGotowosci="Tak";
                    myRef.setValue("Tak");
                }
                info2+=statusGotowosci;
                mTextView.setText(info2);
            }
        });
    }
    public void changeIntent() {
        Intent i = new Intent(this, WolontariuszChosenActivity.class);
        i.putExtra("ourLongitude", Longitude);
        i.putExtra("ourLatitude", Latitude);
        i.putExtra("imie", user.getDisplayName().toString());
        i.putExtra("Latitude", statusSzerokosc);
        i.putExtra("Longitude", statusDlugosc);
        startActivity(i);
    }


    //ustawia lokalizacje akualnie zalogowanej osoby
    void ustawLokalizacje(String dlugosc, String szerokosc){

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        String id = user.getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef;
        myRef= database.getReference(id+"/lokalizacjaDlugosc");
        myRef.setValue(dlugosc);
        myRef= database.getReference(id+"/lokalizacjaSzerokosc");
        myRef.setValue(szerokosc);
    }
    private void requestLocationUpdates(){

    }
}