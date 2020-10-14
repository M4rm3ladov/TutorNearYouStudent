package com.example.tutornearyoustudent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.tutornearyoustudent.Model.StudentInfoModel;
import com.example.tutornearyoustudent.Utils.UserUtils;
import com.firebase.ui.auth.AuthMethodPickerLayout;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.Arrays;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static final int LOGIN_REQUEST_CODE = 7171;

    FirebaseAuth firebaseAuth;
    FirebaseDatabase firebaseDatabase;
    FirebaseAuth.AuthStateListener authStateListener;
    DatabaseReference studentInfoRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //startService(new Intent(getBaseContext(), MyService.class));

        firebaseDatabase = FirebaseDatabase.getInstance();
        studentInfoRef = firebaseDatabase.getReference(CommonClass.STUDENT_INFO_REFERENCE);

        firebaseAuth = FirebaseAuth.getInstance();

        authStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser firebaseUser = firebaseAuth.getCurrentUser();
                if (firebaseUser != null) {
                    // update token
                    FirebaseInstanceId.getInstance()
                            .getInstanceId()
                            .addOnFailureListener(new OnFailureListener() {
                                @Override
                                public void onFailure(@NonNull Exception e) {
                                    Toast.makeText(MainActivity.this, e.getMessage(),Toast.LENGTH_SHORT).show();
                                }
                            })
                            .addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
                                @Override
                                public void onSuccess(InstanceIdResult instanceIdResult) {
                                    Log.d("TOKEN", instanceIdResult.getToken());
                                    UserUtils.updateToken(MainActivity.this, instanceIdResult.getToken());
                                }
                            });
                    handleInfoRegister();
                } else {
                    handleLoginRegister();
                }
            }
        };
    }

    @Override
    protected void onStart() {
        super.onStart();
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    @Override
    protected void onStop() {
        if (firebaseAuth != null && authStateListener != null) {
            firebaseAuth.removeAuthStateListener(authStateListener);
        }
        super.onStop();
    }

    private void handleInfoRegister() {
        studentInfoRef.child(firebaseAuth.getCurrentUser().getUid())
                .addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            StudentInfoModel studentInfoModel = snapshot.getValue(StudentInfoModel.class);
                            CommonClass.currentUser = studentInfoModel;
                            startActivity(new Intent(MainActivity.this, StudentHomeActivity.class));

                        } else {
                            //showRegisterLayout();
                            Intent registerIntent = new Intent(MainActivity.this, RegisterActivity.class);
                            startActivity(registerIntent);
                        }
                        finish();
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
    }

    private void handleLoginRegister() {
        AuthMethodPickerLayout authMethodPickerLayout = new AuthMethodPickerLayout
                .Builder(R.layout.activity_main)
                .setPhoneButtonId(R.id.btn_phone_sign_in)
                .setGoogleButtonId(R.id.btn_google_sign_in)
                .build();

        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
        );

        startActivityForResult(AuthUI.getInstance()
                .createSignInIntentBuilder()
                .setAvailableProviders(providers)
                .setIsSmartLockEnabled(false)
                .setAuthMethodPickerLayout(authMethodPickerLayout)
                .setAlwaysShowSignInMethodScreen(true)
                .setTheme(R.style.AppTheme)
                .build(), LOGIN_REQUEST_CODE);
    }

}