package com.example.tutornearyoustudent;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.Toast;

import com.example.tutornearyoustudent.Model.StudentInfoModel;
import com.firebase.ui.auth.AuthUI;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputEditText;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.i18n.phonenumbers.NumberParseException;
import com.google.i18n.phonenumbers.PhoneNumberUtil;
import com.google.i18n.phonenumbers.Phonenumber;

public class RegisterActivity extends AppCompatActivity {
    DatabaseReference studentInfoRef;
    FirebaseDatabase firebaseDatabase;
    PhoneNumberUtil phoneNumberUtil;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        phoneNumberUtil =  PhoneNumberUtil.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();
        studentInfoRef = firebaseDatabase.getReference(CommonClass.STUDENT_INFO_REFERENCE);

        showRegisterLayout();
    }

    private void showRegisterLayout() {
        //AlertDialog.Builder builder = new AlertDialog.Builder(this,R.style.AppTheme);
        //View itemView = View.inflate(this, R.layout.layout_register, null);

        TextInputEditText edt_first_name = findViewById(R.id.edt_first_name);//itemView.findViewById(R.id.edt_first_name);
        TextInputEditText edt_last_name = findViewById(R.id.edt_last_name);//itemView.findViewById(R.id.edt_last_name);
        TextInputEditText edt_phone_number = findViewById(R.id.edt_phone_number);//itemView.findViewById(R.id.edt_phone_number);

        Button btn_continue = findViewById(R.id.btn_register);//itemView.findViewById(R.id.btn_register);

        // set data
        if (FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber() != null &&
                (!TextUtils.isEmpty(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber()))) {

            edt_phone_number.setText(FirebaseAuth.getInstance().getCurrentUser().getPhoneNumber());
            edt_phone_number.setEnabled(false);

        }

        // set View
        /*builder.setView(itemView);
        final AlertDialog dialog = builder.create();
        dialog.show(); */

        btn_continue.setOnClickListener(view -> {
            if (TextUtils.isEmpty(edt_first_name.getText().toString().trim())) {
                Toast.makeText(RegisterActivity.this, "Please fill in first name field", Toast.LENGTH_SHORT).show();
                return;

            } else if (TextUtils.isEmpty(edt_last_name.getText().toString().trim())) {
                Toast.makeText(RegisterActivity.this, "Please fill in last name field", Toast.LENGTH_SHORT).show();
                return;

            } else if (TextUtils.isEmpty(edt_phone_number.getText().toString().trim())) {
                Toast.makeText(RegisterActivity.this, "Please fill in phone number field", Toast.LENGTH_SHORT).show();
                return;

            } else if (!isPhoneNumberValid(edt_phone_number.getText().toString(), "+63")) {
                Toast.makeText(RegisterActivity.this, "Please enter a valid phone number", Toast.LENGTH_SHORT).show();
                return;

            } else {
                StudentInfoModel model = new StudentInfoModel();
                model.setFirstName(edt_first_name.getText().toString());
                model.setLastName(edt_last_name.getText().toString());
                model.setPhoneNumber(edt_phone_number.getText().toString());
                model.setRating(0.0);

                studentInfoRef.child(FirebaseAuth.getInstance().getCurrentUser().getUid())
                        .setValue(model)
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                                //dialog.dismiss();
                            }
                        })
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(RegisterActivity.this, "Registered Successfully!", Toast.LENGTH_SHORT).show();
                                //dialog.dismiss();
                                //goToStudentHomeActivity(model);
                                CommonClass.currentUser = model;
                                startActivity(new Intent(RegisterActivity.this, StudentHomeActivity.class));
                            }
                        });
            }
        });
    }

    private boolean isPhoneNumberValid(String phoneNumber, String countryCode) {
        try{
            Phonenumber.PhoneNumber numberProto = phoneNumberUtil.parse(phoneNumber, countryCode);
            return phoneNumberUtil.isValidNumber(numberProto);

        } catch (NumberParseException e){
            System.err.println("NumberParseException was thrown: " + e.toString());
        }
        return false;
    }

    @Override
    public void onBackPressed() {
        AuthUI.getInstance()
                .signOut(this)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        Intent loginIntent = new Intent(RegisterActivity.this, MainActivity.class);
                        startActivity(loginIntent);
                        finish();
                    }
                });
        super.onBackPressed();
    }
}