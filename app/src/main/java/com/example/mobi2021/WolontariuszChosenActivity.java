package com.example.mobi2021;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class WolontariuszChosenActivity extends AppCompatActivity {
    private Button button1;
    private Button button2;
    private TextView textview1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_wolontariusz_chosen);
        String name = getIntent().getStringExtra("imie");
        String ourLongitude = getIntent().getStringExtra("ourLongitude");
        String ourLatitude = getIntent().getStringExtra("ourLatitude");
        String Longitude = getIntent().getStringExtra("Longitude");
        String Latitude = getIntent().getStringExtra("Latitude");

        button1 = (Button)findViewById(R.id.button4);
        button2 = (Button)findViewById(R.id.button5);
        textview1 = (TextView)findViewById(R.id.textView2);
        textview1.setText(name+"\nPoproszono cię o pomoc.\nJeżeli ustaliłeś/aś warunki pomocy, dotknij przycisk poniżej. Zostaniesz przekierowany/na do aplikacji Google Maps");
        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String uri = "https://www.google.com/maps/dir/?api=1&origin=" + ourLatitude + "," + ourLongitude +
                        "&destination=" + Latitude + "," + Longitude +
                        "&travelmode=walking";
                Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
                startActivity(intent);
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(WolontariuszChosenActivity.this, WolontariuszActivity.class));
            }
        });
    }
}