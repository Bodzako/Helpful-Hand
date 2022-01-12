package com.example.mobi2021;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.TargetApi;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.ColorFilter;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;

public class MainActivity extends AppCompatActivity {

    private EditText mEmailField;
    private EditText mPasswordField;
    private boolean GrantedPermission = false;

    private Button mLoginBtn;
    private Button mRegisterBtn;
    private Button mHelper;
    private Button mHelp;

    private FirebaseAuth mAuth;

    private ArrayList permissionsToRequest;
    private ArrayList permissionsRejected = new ArrayList();
    private ArrayList permissions = new ArrayList();

    private final static int ALL_PERMISSIONS_RESULT = 101;
    LocationTrack locationTrack;
    boolean czyZalogowano=false;

    //private FirebaseAuth.AuthStateListener mAuthListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //zapytanie o wlaczenie gps
        permissions.add(ACCESS_FINE_LOCATION);
        permissions.add(ACCESS_COARSE_LOCATION);
        permissionsToRequest = findUnAskedPermissions(permissions);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {


            if (permissionsToRequest.size() > 0)
                requestPermissions((String[]) permissionsToRequest.toArray(new String[permissionsToRequest.size()]), ALL_PERMISSIONS_RESULT);
        }

        mAuth = FirebaseAuth.getInstance();

        mEmailField= (EditText)findViewById(R.id.emailField);
        mPasswordField= (EditText)findViewById(R.id.passwordField);

        mLoginBtn = (Button)findViewById(R.id.registerAccountBtn);
        mRegisterBtn = (Button)findViewById(R.id.registerBtn);

        /*mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if(firebaseAuth.getCurrentUser()!=null){
                    startActivity(new Intent(MainActivity.this, WolontariuszActivity.class));
                }
            }
        };*/

        mLoginBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startSignIn();
            }
        });

        mRegisterBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, RegisterActivity.class));
            }
        });
        mHelper = (Button)findViewById(R.id.button2);
        mHelp = (Button)findViewById(R.id.button3);
        mHelp.setBackgroundColor(Color.TRANSPARENT);
        mHelper.setBackgroundColor(Color.TRANSPARENT);
        mHelper.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView image1 = (ImageView)findViewById(R.id.imageView);
                image1.setColorFilter(Color.argb(20,0,0,0));
                ImageView image2 = (ImageView)findViewById(R.id.imageView2);
                image2.setColorFilter(Color.argb(200,255,255,255));
                if(czyZalogowano){
                    startActivity(new Intent(MainActivity.this, WolontariuszActivity.class));
                }
            }
        });
        mHelp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ImageView image1 = (ImageView)findViewById(R.id.imageView);
                image1.setColorFilter(Color.argb(200,255,255,255));
                ImageView image2 = (ImageView)findViewById(R.id.imageView2);
                image2.setColorFilter(Color.argb(20,0,0,0));
                if(czyZalogowano){
                    FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                    String id = user.getUid();
                    FirebaseDatabase database = FirebaseDatabase.getInstance();
                    DatabaseReference myRef = database.getReference(id+"/gotowy");
                    myRef.setValue("Nie");
                    //String tmp = user.getPhotoUrl().toString();
                    startActivity(new Intent(MainActivity.this, PotrzebujacyActivity.class));
                }
            }
        });
    }
    //!--TRACKER--

    private ArrayList findUnAskedPermissions(ArrayList wanted) {
        ArrayList result = new ArrayList();

        for (Object perm : wanted) {
            if (!hasPermission((String) perm)) {
                result.add(perm);
            }
        }

        return result;
    }
    private boolean hasPermission(String permission) {
        if (canMakeSmores()) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                GrantedPermission = true;
                return (checkSelfPermission(permission) == PackageManager.PERMISSION_GRANTED);
            }
        }
        return true;
    }
    private boolean canMakeSmores() {
        return (Build.VERSION.SDK_INT > Build.VERSION_CODES.LOLLIPOP_MR1);
    }
    @TargetApi(Build.VERSION_CODES.M)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {

        switch (requestCode) {

            case ALL_PERMISSIONS_RESULT:
                for (Object perms : permissionsToRequest) {
                    if (!hasPermission((String) perms)) {
                        permissionsRejected.add(perms);
                    }
                }

                if (permissionsRejected.size() > 0) {


                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        if (shouldShowRequestPermissionRationale((String) permissionsRejected.get(0))) {
                            showMessageOKCancel("These permissions are mandatory for the application. Please allow access.",
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                                                requestPermissions((String[]) permissionsRejected.toArray(new String[permissionsRejected.size()]), ALL_PERMISSIONS_RESULT);
                                            }
                                        }
                                    });
                            return;
                        }
                    }

                }

                break;
        }

    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", null)
                .create()
                .show();
    }
    //!---END TREACKER---

    @Override
    protected void onStart() {
        super.onStart();
        //mAuth.addAuthStateListener(mAuthListener);
    }

    private void startSignIn(){
        if(!czyZalogowano){//jezeli nie jestesmy zalogowani to sie logujemy
            String email = mEmailField.getText().toString();
            String password = mPasswordField.getText().toString();

            if(TextUtils.isEmpty(email) || TextUtils.isEmpty(password)) {
                Toast.makeText(MainActivity.this, "Nie podano wszystkich danych!", Toast.LENGTH_LONG).show();
            }else{
                mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (!task.isSuccessful()) {
                            Toast.makeText(MainActivity.this, "Problem z zalogowaniem!", Toast.LENGTH_LONG).show();
                        }
                        else{
                            Toast.makeText(MainActivity.this, "Zalogowano!", Toast.LENGTH_LONG).show();
                            czyZalogowano=true;
                            /*FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                            String tmp = user.getPhotoUrl().toString();
                            if (GrantedPermission)
                                //startTrackerService();
                                if(tmp.compareTo("Wolontariusz0")==0 || tmp.compareTo("Wolontariusz1")==0){
                                    startActivity(new Intent(MainActivity.this, WolontariuszActivity.class));
                                }
                                else if(tmp.compareTo("Potrzebujacy")==0){
                                    startActivity(new Intent(MainActivity.this, PotrzebujacyActivity.class));
                                }*/
                        }
                    }
                });
            }
        }
        ((InputMethodManager)getSystemService(Activity.INPUT_METHOD_SERVICE)).toggleSoftInput(InputMethodManager.SHOW_IMPLICIT,0);
    }
}