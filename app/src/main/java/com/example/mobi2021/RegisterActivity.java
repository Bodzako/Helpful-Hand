package com.example.mobi2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class RegisterActivity extends AppCompatActivity {

    private EditText mEmailField;
    private EditText mPasswordField;
    private EditText mPasswordField2;
    private EditText mImieField;
    private EditText mPhone;
    
    //private CheckBox mWolontariuszCheckBox;
    //private CheckBox mPotrzebujacyCheckBox;

    private Button mRegisterAccountBtn;
    //private ImageView tlo;

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        mEmailField=(EditText)findViewById(R.id.emailField);
        mPasswordField=(EditText)findViewById(R.id.passwordField);
        mPasswordField2=(EditText)findViewById(R.id.passwordField2);
        mImieField=(EditText)findViewById(R.id.imieField);
        mPhone=(EditText)findViewById(R.id.Phone);
        ImageView tlo = (ImageView)findViewById(R.id.imageView6);
        tlo.setColorFilter(Color.argb(120,255,255,255));

        //mWolontariuszCheckBox=(CheckBox)findViewById(R.id.wolontariuszCheckBox);
        //mPotrzebujacyCheckBox=(CheckBox)findViewById(R.id.potrzebujacyCheckBox);

        mRegisterAccountBtn=(Button)findViewById(R.id.registerAccountBtn);

        mAuth = FirebaseAuth.getInstance();

        mRegisterAccountBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startRegistration();
            }
        });

        /*mWolontariuszCheckBox.setChecked(true);
        mWolontariuszCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mPotrzebujacyCheckBox.setChecked(!mWolontariuszCheckBox.isChecked());
            }
        });

        mPotrzebujacyCheckBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                mWolontariuszCheckBox.setChecked(!mPotrzebujacyCheckBox.isChecked());
            }
        });*/
    }

    private void startRegistration(){
        String email = mEmailField.getText().toString();
        String password = mPasswordField.getText().toString();
        String password2 = mPasswordField2.getText().toString();
        String imie = mImieField.getText().toString();
        String numer = mPhone.getText().toString();
        if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password) || TextUtils.isEmpty(password2) || TextUtils.isEmpty(imie) || TextUtils.isEmpty(numer)){
            Toast.makeText(RegisterActivity.this, "Nie podano wszystkich danych!", Toast.LENGTH_LONG).show();
        }
        else if(password.compareTo(password2)!=0){
            Toast.makeText(RegisterActivity.this, "Hasła nie są takie same!", Toast.LENGTH_LONG).show();
        }
        else{
            mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                @Override
                public void onComplete(@NonNull Task<AuthResult> task) {
                    if(task.isSuccessful()){
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                                .setDisplayName(imie)
                                .build();
                        user.updateProfile(profileUpdates);
                        String id = user.getUid();
                        FirebaseDatabase database = FirebaseDatabase.getInstance();
                        DatabaseReference myRef= database.getReference(id+"/tel");
                        myRef.setValue(numer);
                        myRef= database.getReference(id+"/gotowy");
                        myRef.setValue("Nie");
                        myRef= database.getReference(id+"/imie");
                        myRef.setValue(imie);
                        myRef= database.getReference(id+"/ocena");
                        myRef.setValue(0);
                        myRef= database.getReference(id+"/liczbaOcen");
                        myRef.setValue(0);
                        myRef= database.getReference(id+"/lokalizacjaSzerokosc");
                        myRef.setValue(" ");
                        myRef= database.getReference(id+"/lokalizacjaDlugosc");
                        myRef.setValue(" ");
                        myRef= database.getReference(id+"/czyWybrany");
                        myRef.setValue("Nie");
                        myRef= database.getReference(id+"/wybranyDlugosc");
                        myRef.setValue(" ");
                        myRef= database.getReference(id+"/wybranySzerokosc");
                        myRef.setValue(" ");
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        Toast.makeText(RegisterActivity.this, "Zarejestrowano!", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(RegisterActivity.this, "Nie udało się zarejestrować!", Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }


}

