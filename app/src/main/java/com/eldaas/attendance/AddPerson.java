package com.eldaas.attendance;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;
import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
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

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Objects;

public class AddPerson extends AppCompatActivity {
    Spinner bloodGroup;
    TextInputLayout eName,eID,eEmail,eFatherName,eContact;
    String startDate;
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
    ImageView registerCollege;
    String uniqueKey;
    private  FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private FirebaseUser fUser= firebaseAuth.getCurrentUser();
    @SuppressLint("UseSwitchCompatOrMaterialCode")
    boolean isAdmin = false;

    @SuppressLint({"ClickableViewAccessibility", "SetTextI18n", "UseCompatLoadingForDrawables"})
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_person);
        Initialization();     //Function to initialize the variables
        String[] bloodGroupElements= {"O(+ve)","A(+ve)", "B(+ve)", "AB(+ve)", "O(-ve)","A(-ve)", "B(-ve)", "AB(-ve)"};
        ArrayAdapter ad = new ArrayAdapter<String>(this, R.layout.color_spinner, bloodGroupElements);
        bloodGroup.setAdapter(ad);
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
        fetchOrganization_name();

        CreateAccount_Text.setText("Next");
        Calendar cal = Calendar.getInstance(); //get calendar instance
        @SuppressLint("SimpleDateFormat") String fullDate = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());
        ConstraintLayout cl = findViewById(R.id.progress_button_bg);
        cl.setBackground(getResources().getDrawable(R.drawable.positive)); //Change the button drawable to green
        CreateAccount_Button.setOnClickListener(view -> {

            if (isConnected()) { //Check internet connection
                //Get all the input from the user
                String name=eName.getEditText().getText().toString().trim();
                String id = eID.getEditText().getText().toString().trim();
                String email= eEmail.getEditText().getText().toString().trim();
                String fName= eFatherName.getEditText().getText().toString();
                String contact=eContact.getEditText().getText().toString().trim();
                String blood=bloodGroup.getSelectedItem().toString();
                
                //Check if the details entered are valid or not
                if (name.isEmpty()) {
                    eName.setError("Field can't be empty");
                    eName.requestFocus();
                    return;
                }
                if (email.isEmpty()) {
                    eEmail.setError("Field can't be empty");
                    eEmail.requestFocus();
                    return;
                } else if (!Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
                    eEmail.setError("Please enter a valid email address");
                    eEmail.requestFocus();
                    return;
                } else if (id.isEmpty()) {
                    eID.setError("Field can't be empty");
                    eID.requestFocus();
                    return;
                } else if (contact.isEmpty()) {
                    eContact.setError("Field can't be empty");
                    eContact.requestFocus();
                    return;
                } else if (selectedItem.toString().equals("Organization Name")) {
                    DynamicToast.makeError(AddPerson.this, "Select organization name").show();
                    return;
                }else if (fName.isEmpty()) {
                    eContact.setError("Field can't be empty");
                    eContact.requestFocus();
                    return;
                }

                //Creating a new Account
                startDate=String.valueOf(android.text.format.DateFormat.format("dd-MM-yyyy", new java.util.Date()));
                buttonProgress.setVisibility(View.VISIBLE);
                CreateAccount_Text.setVisibility(View.GONE);
                uniqueKey=databaseRef.child("Users").child(fUser.getUid()).child("Employees").push().getKey();
                assert uniqueKey != null;
                //Creating a User Object with the inputs by user
                PersonModelClass person=new PersonModelClass(name,email,id,selectedItem.toString(),uniqueKey,contact,"added", String.valueOf(isAdmin),fName,blood,fullDate);
                    databaseRef.child("Users").child(fUser.getUid()).child("Employees").child(uniqueKey).setValue(person).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            intentNow(AddFaceActivity.class,uniqueKey,true);
                            buttonProgress.setVisibility(View.GONE);
                            CreateAccount_Text.setVisibility(View.VISIBLE);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            DynamicToast.makeError(AddPerson.this, "Server Error! employee not added").show();
                            buttonProgress.setVisibility(View.GONE);
                            CreateAccount_Text.setVisibility(View.VISIBLE);
                        }
                    });


            }

        });
    }
    private void fetchOrganization_name() {
        listener = databaseRef.child("Organization_Names").addValueEventListener(new ValueEventListener() {
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

    private void intentNow(Class targetActivity,String uid, boolean b) {
        Intent intent = new Intent(getApplicationContext(), targetActivity);
        if(b){
            intent.putExtra("uniqueId",uid);
        }
        startActivity(intent);
    }
    @SuppressLint({"UseCompatLoadingForDrawables", "SetTextI18n"})
    public void insertOrganization_name() {

        Dialog dialog = new Dialog(AddPerson.this);
        dialog.setContentView(R.layout.edittext_dialog);
        dialog.getWindow().setBackgroundDrawable(AddPerson.this.getDrawable(R.drawable.custom_dialog_background));
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
                            DynamicToast.makeSuccess(AddPerson.this, "Organization registered!").show();
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
        eName=findViewById(R.id.emp_name);
        eID= findViewById(R.id.emp_Id);
        eEmail=findViewById(R.id.emp_email);
        eFatherName=findViewById(R.id.emp_fathersName);
        eContact=findViewById(R.id.emp_contact);
        bloodGroup= findViewById(R.id.spl);
        mAuth = FirebaseAuth.getInstance();
        CreateAccount_Button = findViewById(R.id.register_button);
        buttonProgress = findViewById(R.id.buttonProgress);
        CreateAccount_Text = findViewById(R.id.buttonText);
        spinner = findViewById(R.id.college_names_spinner);
        databaseRef = FirebaseDatabase.getInstance().getReference();
        list = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, list);
        registerCollege = findViewById(R.id.add_organization_add);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}