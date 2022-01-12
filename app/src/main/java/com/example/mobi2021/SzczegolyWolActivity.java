package com.example.mobi2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class SzczegolyWolActivity extends AppCompatActivity {
    private TextView imieText;
    private TextView dystansText;
    private Button WybierzBtn;
    private String dystans;
    private ArrayList<ImageView> szareGwiazdki=new ArrayList<>();
    private ArrayList<ImageView> zloteGwiazdki=new ArrayList<>();
    private Button callBtn;
    boolean wybrany = false;
    Double Dlugosc;
    Double Szerokosc;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_szczegoly_wol);

        Bundle b = getIntent().getExtras();
        String imie;
        String dlugosc;
        String szerokosc;
        double ocena;
        long liczbaOcen;
        String telefon;

        if(b !=null){
            imie=b.getString("imie");
            ocena=b.getDouble("ocena");
            liczbaOcen=b.getLong("liczbaOcen");
            telefon=b.getString("telefon");
            dlugosc=b.getString("dlugosc2");
            szerokosc=b.getString("szerokosc2");
            Dlugosc = b.getDouble("dlugosc");
            Szerokosc = b.getDouble("szerokosc");
            Log.e("dlugosc", szerokosc);
        } else{
            imie="blad";
            ocena=0;
            liczbaOcen=0;
            telefon="000000000";
            dlugosc = "0";
            szerokosc = "0";
            Dlugosc = 0.d;
            Szerokosc = 0.d;
        }
        Double dlugosc1 = new Double(dlugosc);
        Double szerokosc1 = new Double(szerokosc);
        dystans=Math.round(Double.parseDouble(countDistance(Dlugosc, dlugosc1,Szerokosc, szerokosc1))*10)/10.0f+"km";
        //System.out.print("obliczono dystans");
        WybierzBtn = (Button) findViewById(R.id.wybierzBtn);
        WybierzBtn.setAlpha(0.5f);
        WybierzBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(wybrany)
                wybierz(telefon);
            }
        });
        imieText=(TextView)findViewById(R.id.imie);
        imieText.setText(imie);
        dystansText=(TextView)findViewById(R.id.dystans);
        dystansText.setText(dystans);
        szareGwiazdki.add((ImageView)findViewById((R.id.SzaraGwiazdka1)));
        szareGwiazdki.add((ImageView)findViewById((R.id.SzaraGwiazdka2)));
        szareGwiazdki.add((ImageView)findViewById((R.id.SzaraGwiazdka3)));
        szareGwiazdki.add((ImageView)findViewById((R.id.SzaraGwiazdka4)));
        szareGwiazdki.add((ImageView)findViewById((R.id.SzaraGwiazdka5)));
        zloteGwiazdki.add((ImageView)findViewById(R.id.zlotaGwiazdka1));
        zloteGwiazdki.add((ImageView)findViewById(R.id.zlotaGwiazdka2));
        zloteGwiazdki.add((ImageView)findViewById(R.id.zlotaGwiazdka3));
        zloteGwiazdki.add((ImageView)findViewById(R.id.zlotaGwiazdka4));
        zloteGwiazdki.add((ImageView)findViewById(R.id.zlotaGwiazdka5));
        callBtn=(Button)findViewById((R.id.callButton));
        callBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Uri number = Uri.parse("tel:"+telefon);
                Intent callIntent = new Intent(Intent.ACTION_DIAL,number);
                if(callIntent.resolveActivity(getPackageManager()) !=null){
                    startActivity(callIntent);
                    WybierzBtn.setAlpha(1.0f);
                    wybrany = true;
                }
            }
        });
        szareGwiazdki.get(0).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocen(1,ocena,liczbaOcen,telefon);
            }
        });
        szareGwiazdki.get(1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocen(2,ocena,liczbaOcen,telefon);
            }
        });
        szareGwiazdki.get(2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocen(3,ocena,liczbaOcen,telefon);
            }
        });
        szareGwiazdki.get(3).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocen(4,ocena,liczbaOcen,telefon);
            }
        });
        szareGwiazdki.get(4).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ocen(5,ocena,liczbaOcen,telefon);
            }
        });
        wyswietlGwiazdki(ocena);
    }

    void ocen(int nrGwiazdki,double ocena,long liczbaOcen,String telefon){
        ocena=(ocena*liczbaOcen+nrGwiazdki)/(liczbaOcen+1);

        DatabaseReference ref= FirebaseDatabase.getInstance().getReference();
        double finalOcena = ocena;
        ref.orderByChild("tel").equalTo(telefon).addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String klucz = null;
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    klucz=datas.getKey();
                    //Log.e("e",klucz);
                }
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef= database.getReference(klucz+"/ocena");
                myRef.setValue(finalOcena);
                myRef= database.getReference(klucz+"/liczbaOcen");
                myRef.setValue(liczbaOcen+1);
                wyswietlGwiazdki(finalOcena);

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            };
    });



    }
    void wyswietlGwiazdki(double ocena){
        if(ocena>0.5){
            zloteGwiazdki.get(0).setVisibility(View.VISIBLE);
            for(int i=1;i<5;i++){
                zloteGwiazdki.get(i).setVisibility(View.INVISIBLE);
            }
        }
        if(ocena>1.5){
            zloteGwiazdki.get(1).setVisibility(View.VISIBLE);
            for(int i=2;i<5;i++){
                zloteGwiazdki.get(i).setVisibility(View.INVISIBLE);
            }
        }
        if(ocena>2.5){
            zloteGwiazdki.get(2).setVisibility(View.VISIBLE);
            for(int i=3;i<5;i++){
                zloteGwiazdki.get(i).setVisibility(View.INVISIBLE);
            }
        }
        if(ocena>3.5){
            zloteGwiazdki.get(3).setVisibility(View.VISIBLE);
            for(int i=4;i<5;i++){
                zloteGwiazdki.get(i).setVisibility(View.INVISIBLE);
            }
        }
        if(ocena>4.5){
            zloteGwiazdki.get(4).setVisibility(View.VISIBLE);
            
        }
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

    void wybierz(String telefon){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference();
        ref.orderByChild("tel").equalTo(telefon).addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String klucz = null;
                for(DataSnapshot datas: dataSnapshot.getChildren()){
                    klucz=datas.getKey();
                    //Log.e("e",klucz);
                }
                FirebaseDatabase database = FirebaseDatabase.getInstance();
                DatabaseReference myRef= database.getReference(klucz+"/gotowy");
                myRef.setValue("Nie");
                myRef= database.getReference(klucz+"/czyWybrany");
                myRef.setValue("Tak");
                myRef= database.getReference(klucz+"/wybranyDlugosc");
                myRef.setValue(Dlugosc.toString());
                Log.e("d",Dlugosc.toString());
                myRef= database.getReference(klucz+"/wybranySzerokosc");
                myRef.setValue(Szerokosc.toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
            });
    }
}