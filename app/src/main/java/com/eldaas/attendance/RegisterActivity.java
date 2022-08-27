package com.eldaas.attendance;

import static android.content.ContentValues.TAG;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    TextInputLayout org_email,org_pass,org_name,org_phone;
    TextView gotologin;
    View CreateAccount_Button;
    private FirebaseAuth mAuth;
    ProgressBar buttonProgress;
    TextView CreateAccount_Text;
    Spinner spinner;
    DatabaseReference databaseRef;
    ValueEventListener listener;
    ArrayList<String> list;
    ArrayAdapter<String> adapter;
    StringBuilder selectedItem = new StringBuilder();
    ImageView registerOrganization;
    boolean isAdmin = true;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n", "UseCompatLoadingForDrawables"})


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        Initialization();     //Function to initialize the variables
        gotologin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                intent.putExtra("devicecontrol", "admin");
                startActivity(intent);
                finish();
            }
        });
        list.add("Organization Name");
        spinner.setAdapter(adapter);

        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                selectedItem.setLength(0);
                selectedItem.append(parent.getItemAtPosition(position).toString());
            }

            // to close the onItemSelected
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        registerOrganization.setOnClickListener(v -> insertOrganization_name());

        fetchOrganization_name();

        CreateAccount_Text.setText("Create account");
        ConstraintLayout cl = findViewById(R.id.progress_button_bg);
        CreateAccount_Button.setOnClickListener(view -> {
            cl.setBackground(getResources().getDrawable(R.drawable.positive)); //Change the button drawable to green
            if (isConnected()) { //Check internet connection

                //Get all the input from the user

                String sPhone = org_phone.getEditText().getText().toString().trim();

                String sEmail = org_email.getEditText().getText().toString().trim();
                String sPass = org_pass.getEditText().getText().toString().trim();
                String sName = org_name.getEditText().getText().toString().trim();

                //Check if the details entered are valid or not
                if (sName.isEmpty()) {
                    org_name.setError("Field can't be empty");
                    org_name.requestFocus();
                    return;
                }
                if (sEmail.isEmpty()) {
                    org_email.setError("Field can't be empty");
                    org_email.requestFocus();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(sEmail).matches()) {
                    org_email.setError("Please enter a valid email address");
                    org_email.requestFocus();
                    return;
                } else if (sPass.isEmpty()) {
                    org_pass.setError("Field can't be empty");
                    org_pass.requestFocus();
                    return;
                } else if (sPass.length() < 8) {
                    org_pass.setError("password must be at least 8 characters");
                    org_pass.requestFocus();
                    return;
                } else if (sPhone.isEmpty()) {
                    org_phone.setError("Field can't be empty");
                    org_phone.requestFocus();
                    return;
                } else if (selectedItem.toString().equals("Organization Name")) {
                    DynamicToast.makeError(RegisterActivity.this, "Select organization name").show();
                    return;
                }

                //Creating a new Account
                buttonProgress.setVisibility(View.VISIBLE);
                CreateAccount_Text.setVisibility(View.GONE);
                mAuth.createUserWithEmailAndPassword(sEmail, sPass)
                        .addOnCompleteListener(RegisterActivity.this, task -> {
                            if (task.isSuccessful()) {
                                //Successfully Created a new account

                                Users users = new Users(sEmail, sEmail, "", selectedItem.toString(), "0", sPhone, "", String.valueOf(isAdmin)); //Creating a User Object with the inputs by user

                                FirebaseDatabase.getInstance().getReference("Users")
                                        .child(Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid())
                                        .setValue(users).addOnCompleteListener(task1 -> {
                                            if (task1.isSuccessful()) {
                                                Log.d(TAG, "createUserWithEmail:success");
                                                FirebaseUser user = mAuth.getCurrentUser();
                                                DynamicToast.makeSuccess(this, "Registered successfully").show();
                                                DynamicToast.makeSuccess(this, "Verification mail sent").show(); //Send a verification link
                                                intentNow();
                                                assert user != null;
                                                user.sendEmailVerification();
                                            } else {
                                                // If sign in fails, display a message to the user.
                                                Log.w(TAG, "createUserWithEmail:failure", task1.getException());
                                                DynamicToast.makeError(this, "Failed").show();
                                            }
                                            buttonProgress.setVisibility(View.GONE);
                                            CreateAccount_Text.setVisibility(View.VISIBLE);
                                        });
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG, "createUserWithEmail:failure", task.getException());
                                DynamicToast.makeError(this, "Authentication Failed").show();
                                buttonProgress.setVisibility(View.GONE);
                                CreateAccount_Text.setVisibility(View.VISIBLE);
                            }
                        });
            }

        });
    }
        

    private void fetchOrganization_name() {
        listener = databaseRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren())
                    list.add(Objects.requireNonNull(snap.getValue()).toString());
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });
    }

    //To check Internet Connectivity
    private boolean isConnected() {
        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED)
            return true;

        DynamicToast.makeError(getApplicationContext(), "You're not connected to Internet!").show();
        return false;
    }

    private void intentNow() {
        startActivity(new Intent(getApplicationContext(), LoginActivity.class));
        finish();
    }
    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    public void insertOrganization_name() {

        Dialog dialog = new Dialog(RegisterActivity.this);
        dialog.setContentView(R.layout.edittext_dialog);
        dialog.getWindow().setBackgroundDrawable(RegisterActivity.this.getDrawable(R.drawable.custom_dialog_background));
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation; //Setting the animations to dialog

        Button Proceed = dialog.findViewById(R.id.proceed);
        Button Cancel = dialog.findViewById(R.id.cancel);
        EditText editText = dialog.findViewById(R.id.edittext_box);
        TextView title = dialog.findViewById(R.id.dialog_title);

        Proceed.setText("Add Organization");
        editText.setHint("Enter Organization name here");
        title.setText("Register your Organization");

        Proceed.setOnClickListener(v -> {

            String inputOrganization = editText.getText().toString().trim();

            if (!inputOrganization.isEmpty()) { //If the user's input is not Empty

                databaseRef.push().setValue(inputOrganization)
                        .addOnCompleteListener(task -> {
                            list.clear();
                            fetchOrganization_name();
                            adapter.notifyDataSetChanged();
                            DynamicToast.makeSuccess(RegisterActivity.this, "Organization registered!").show();
                        });

            } else
                DynamicToast.makeError(getApplicationContext(), "Please enter something").show(); //If the user's input is empty

            dialog.dismiss();
        });

        Cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
    //Function to initialize the variables
    private void Initialization() {
        org_email=findViewById(R.id.org_email);
        org_name=findViewById(R.id.org_name);
        org_phone=findViewById(R.id.org_phoneNo);
        org_pass=findViewById(R.id.org_password);
        gotologin=findViewById(R.id.reg_login_btn);
        mAuth = FirebaseAuth.getInstance();
        CreateAccount_Button = findViewById(R.id.login_button);
        buttonProgress = findViewById(R.id.buttonProgress);
        CreateAccount_Text = findViewById(R.id.buttonText);
        spinner = findViewById(R.id.college_names_spinner);
        databaseRef = FirebaseDatabase.getInstance().getReference("Organization_Names");
        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        registerOrganization = findViewById(R.id.add_organization);
    }

}