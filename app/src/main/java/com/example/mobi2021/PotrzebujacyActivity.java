package com.example.mobi2021;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.Math;
import java.util.ArrayList;
import java.util.Map;

public class PotrzebujacyActivity extends AppCompatActivity {

    private ListView mListView;

    private ArrayList<String>Imie=new ArrayList<>();
    private ArrayList<String>Telefon=new ArrayList<>();
    //private ArrayList<String>Dystans=new ArrayList<>();
    private ArrayList<Double>Ocena=new ArrayList<>();
    private ArrayList<Long>LiczbaOcen=new ArrayList<>();
    private ArrayList<String>Dlugosc=new ArrayList<>();
    private ArrayList<String>Szerokosc=new ArrayList<>();
    private ArrayList<String>DaneShort=new ArrayList<>();
    private String Latitude;
    private String Longitude;
    double longitude;
    double latitude;
    ArrayAdapter<String> arrayAdapter;
    LocationTrack locationTrack;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_potrzebujacy);
        locationTrack = new LocationTrack(PotrzebujacyActivity.this);

        if (locationTrack.canGetLocation()) {


            longitude = locationTrack.getLongitude();
            latitude = locationTrack.getLatitude();



            //Pobieramy sourceLatidude z urządzenia
            Latitude = Double.toString(latitude);
            //Pobieramy sourceLongitude z urządzenia
            Longitude = Double.toString(longitude);

        }

        mListView=(ListView)findViewById(R.id.listView);

        arrayAdapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, DaneShort);

        mListView.setAdapter(arrayAdapter);
        ustawLokalizacje(Longitude, Latitude);

        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(PotrzebujacyActivity.this, SzczegolyWolActivity.class);
                Bundle b = new Bundle();
                b.putString("imie", Imie.get(position)); //Your id
                b.putDouble("ocena", Ocena.get(position));
                b.putLong("liczbaOcen", LiczbaOcen.get(position));
                b.putString("telefon", Telefon.get(position));
                b.putDouble("dlugosc", longitude);
                b.putDouble("szerokosc", latitude);
                b.putString("dlugosc2", Dlugosc.get(position));
                b.putString("szerokosc2", Szerokosc.get(position));
                intent.putExtras(b); //Put your id to your next Intent
                startActivity(intent);
                finish();
                //String selectedItem = Telefon.get(position);
                //Uri number = Uri.parse("tel:"+selectedItem);
                //Intent callIntent = new Intent(Intent.ACTION_DIAL,number);
                //if(callIntent.resolveActivity(getPackageManager()) !=null){
                //    startActivity(callIntent);
                //}
            }
        });

        DatabaseReference ref = FirebaseDatabase.getInstance().getReference();
        ref.addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        //Get map of users in datasnapshot
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                       // Log.e("e",dataSnapshot.getValue().toString());
                        collectData((Map<String,Object>) dataSnapshot.getValue());
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                        //handle databaseError
                    }
                });
    }

    private void collectData(Map<String,Object> users) {
        for (Map.Entry<String, Object> entry : users.entrySet()){
            Map singleUser = (Map) entry.getValue();
            //jezeli uzytkownik jest gotowy to wyswietlamy go na liscie
            if(singleUser.get("gotowy").toString().compareTo("Tak")==0){
                Imie.add(singleUser.get("imie").toString());
                Telefon.add(singleUser.get("tel").toString());
                //Dystans.add(singleUser.get("dystans").toString());
                Ocena.add(Double.parseDouble(singleUser.get("ocena").toString()));
                LiczbaOcen.add(Long.parseLong(singleUser.get("liczbaOcen").toString()));
                //tutaj zapisuje lokalizacje do arraylistow
                Dlugosc.add(singleUser.get("lokalizacjaDlugosc").toString());
                Szerokosc.add(singleUser.get("lokalizacjaSzerokosc").toString());
                String tmp = singleUser.get("imie").toString() + "     " + singleUser.get("ocena").toString()+"/5     "+ Math.round(Double.parseDouble(countDistance(longitude,
                        Double.parseDouble(singleUser.get("lokalizacjaDlugosc").toString()), latitude,Double.parseDouble(singleUser.get("lokalizacjaSzerokosc").toString())))*10)/10.0f+"km";
                DaneShort.add(tmp);
                arrayAdapter.notifyDataSetChanged();
            }
        }
    }
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
    String countDistance(double longitude1, double longitude2, double latitude1, double latitude2) {

        double earthRadius = 6371;
        double dLat = Math.toRadians(latitude2-latitude1);
        double dLng = Math.toRadians(longitude2-longitude1);
        double a = Math.sin(dLat/2) * Math.sin(dLat/2) +
                Math.cos(Math.toRadians(latitude1)) * Math.cos(Math.toRadians(latitude2)) *
                        Math.sin(dLng/2) * Math.sin(dLng/2);

        double c = 2 * Math.atan2(Math.sqrt(a), Math.sqrt(1-a));
        double dist = earthRadius * c;

        return Double.toString(dist);
    }
}