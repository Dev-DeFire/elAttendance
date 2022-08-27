package com.eldaas.attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.ArrayList;
import java.util.List;

public class EmployeesActivity extends AppCompatActivity {
    private List<PersonModelClass> userList;
    private UserAdapter userAdapter;
    FirebaseAuth fAuth=FirebaseAuth.getInstance();
    FirebaseUser fUser=fAuth.getCurrentUser();
    DatabaseReference databaseReference;
    RecyclerView recyclerView;
    ProgressDialog progressDialog;
    Dialog dialog;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_employees);
        userList = new ArrayList<>();
        readData();
        recyclerView = findViewById(R.id.employeeList);
//        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
//        layoutManager.setReverseLayout(true);
//        layoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false));
//        if (fUser == null) {
//            fAuth.signOut();
//            startActivity(new Intent(this, MainActivity.class));
//            // User is signed in
//        }
        userAdapter = new UserAdapter(EmployeesActivity.this, userList, new UserAdapter.UserRecyclerViewOnClickListner() {
            @Override
            public void onLongItemClick(String emplUid,String name) {
                longClick(emplUid, name);
            }

            @Override
            public void onIttemClick(String name, String email, String id, String organization, String uid, String phone, String embeddings, String admin, String father, String bloodGrp, String startDate) {
                try {
                    Intent intent=new Intent(EmployeesActivity.this,MonthlyActivity.class);
                    intent.putExtra("eOrganizationId",id);
                    intent.putExtra("ename",name);
                    intent.putExtra("uid",uid);
                    intent.putExtra("efather",father);
                    intent.putExtra("eemail",email);
                    intent.putExtra("econtact",phone);
                    intent.putExtra("sDate",startDate);
                    intent.putExtra("bloodgrp",bloodGrp);
                    startActivity(intent);
                }catch (Exception e){
                    Toast.makeText(EmployeesActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

        recyclerView.setAdapter(userAdapter);


        //PROGRESS DIALOG
        progressDialog = new ProgressDialog(this);
        progressDialog.setCancelable(true);
        progressDialog.setMessage("Fetching Data Please wait !");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        //FirebaseDatabase.getInstance().setPersistenceEnabled(true);
        progressDialog.show();
    }
    private void readData() {
        databaseReference = FirebaseDatabase.getInstance().getReference().child("Users").child(fUser.getUid()).child("Employees");
        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                userList.clear();
                if(snapshot.exists()){
                    try {
                        for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                            PersonModelClass user = dataSnapshot.getValue(PersonModelClass.class);
                            userList.add(user);
                        }
                        userAdapter.notifyDataSetChanged();
                        progressDialog.dismiss();

                        if (userList.isEmpty()){
                            Toast.makeText(EmployeesActivity.this, "No recipients", Toast.LENGTH_SHORT).show();
                            progressDialog.dismiss();
                        }
                    }catch(Exception e){
                        progressDialog.dismiss();
                        Toast.makeText(EmployeesActivity.this, "fuck"+e.getMessage(), Toast.LENGTH_SHORT).show();
                    }
                }else{
                    progressDialog.dismiss();
                    DynamicToast.makeError(EmployeesActivity.this,"Empty Database!").show();
                }


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                progressDialog.dismiss();
            }
        });

    }
    @SuppressLint({"UseCompatLoadingForDrawables", "ObsoleteSdkInt", "SetTextI18n"})
    public void longClick(String emplUid,String emplName) {

        //Pop-up Dialog box when back pressed -> ask the user if they want to exit or not.

        dialog = new Dialog(EmployeesActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(EmployeesActivity.this.getDrawable(R.drawable.custom_dialog_background));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation; //Setting the animations to dialog

        Button Proceed = dialog.findViewById(R.id.proceed);
        Button Cancel = dialog.findViewById(R.id.cancel);
        TextView title = dialog.findViewById(R.id.dialog_title);
        TextView description = dialog.findViewById(R.id.dialog_description);

        Proceed.setText("Proceed");
        Proceed.setBackground(getResources().getDrawable(R.drawable.negative));
        title.setText("Confirm Delete!");
        description.setText("Do you really want to remove "+emplName);

        Proceed.setOnClickListener(v -> {
            databaseReference=FirebaseDatabase.getInstance().getReference()
                    .child("Users").child(fUser.getUid()).child("Employees").child(emplUid);
            databaseReference.removeValue();
            dialog.dismiss();
        });

        Cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }
}