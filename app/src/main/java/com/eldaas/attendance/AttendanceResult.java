package com.eldaas.attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Objects;

public class AttendanceResult extends AppCompatActivity {

    TextView Name;
    private DatabaseReference reference;
    private static int SPLASH_SCREEN=2000;
    private String uniqueId;
    private String name,OrganizationId,OrganizationName;
    private boolean attendance=false,mark=false;
    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();
    private HashMap<String, String> map = new HashMap<>(); //Creating a Hashmap to store the attendance status

    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_attendance_result);
        Initialize(); //Function to initialize the variables
        String userID = uniqueId;
        SimpleDateFormat timeFormat = new SimpleDateFormat("hh:mm a"); //Eg: 12:33 AM & 12:33 PM
        final String Time = timeFormat.format(new Date());      //Storing current time in string of the above mentioned format

        SharedPreferences userDataSP = AttendanceResult.this.getSharedPreferences("userData", 0);
        SharedPreferences.Editor editor = userDataSP.edit(); //Enabling SharedPreference Editor mode
        editor.putString("markTime", Time);      //Replacing the name value with updated name
        editor.apply();



        Calendar cal = Calendar.getInstance(); //Creating a calendar instance
        String year = new SimpleDateFormat("yyyy").format(cal.getTime());   //Storing year as string
        String month = new SimpleDateFormat("MMM").format(cal.getTime());   //Storing month name as string
        String date = new SimpleDateFormat("dd").format(cal.getTime());     //Storing date as string

//        Name.setText(name+OrganizationId+OrganizationName);     //Setting the textView text -> User's Name


        map.put("inTime", Time);
        map.put("status", "Present");
        map.put("outTime","Not Marked");

        reference.child("Users").child(firebaseUser.getUid()).child("Employees").child(userID).child("Attendance").child(year).child(month).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()&&snapshot.hasChild(date)){
                    reference.child("Users").child(firebaseUser.getUid()).child("Employees").child(userID).child("Attendance").child(year).child(month).child(date).child("outTime").setValue(Time).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            addData(userID,year,month,date,Time);
                        }
                    });
                }else
                {
                    //Pushing the data in User's node
                    reference.child("Users").child(firebaseUser.getUid()).child("Employees").child(userID).child("Attendance").child(year).child(month).child(date).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void unused) {
                            addData(userID,year,month,date,Time);
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                intentNow();
            }
        });
//        reference.child("Users").child(firebaseUser.getUid()).child("Employees").child(userID).child("Attendance").child(year).child(month).child(date).child("inTime").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (task.isSuccessful()){
//                    String intime=String.valueOf(task.getResult().getValue());
//                    if (task.){
//                        reference.child("Users").child(firebaseUser.getUid()).child("Employees").child(userID).child("Attendance").child(year).child(month).child(date).child("outTime").setValue(Time);
//                    }
//                    else {
//                        //Pushing the data in User's node
//                        reference.child("Users").child(firebaseUser.getUid()).child("Employees").child(userID).child("Attendance").child(year).child(month).child(date).setValue(map);
//                    }
//                }else {
//                    //Pushing the data in User's node
//                    reference.child("Users").child(firebaseUser.getUid()).child("Employees").child(userID).child("Attendance").child(year).child(month).child(date).setValue(map);
//                }
//
//            }
//        }).addOnFailureListener(new OnFailureListener() {
//            @Override
//            public void onFailure(@NonNull Exception e) {
//                Intent intent= new Intent(AttendanceResult.this,AttendanceActivity.class);
//                startActivity(intent);
//                finish();
//            }
//        });

//        reference.child("Users").child(firebaseUser.getUid()).child("Employees").child(userID).child("name").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//            @Override
//            public void onComplete(@NonNull Task<DataSnapshot> task) {
//                if (task.isSuccessful()) {
//                    name=String.valueOf(task.getResult().getValue());
//                    reference.child("Users").child(firebaseUser.getUid()).child("Employees").child(userID).child("id").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                        @Override
//                        public void onComplete(@NonNull Task<DataSnapshot> task) {
//                            if (task.isSuccessful()) {
//                                OrganizationId=String.valueOf(task.getResult().getValue());
//                                map.put("name", name);
//                                map.put("id", OrganizationId);
//                                reference.child("Users").child(firebaseUser.getUid()).child("Employees").child(userID).child("organization").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                                    @Override
//                                    public void onComplete(@NonNull Task<DataSnapshot> task) {
//                                        if (task.isSuccessful()) {
//                                            OrganizationName=String.valueOf(task.getResult().getValue());
//                                            Name.setText(name+"\n"+OrganizationId);
//                                            reference.child("Attendees").child(OrganizationName).child(year).child(month).child(date).child(userID).child("inTime").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
//                                                @Override
//                                                public void onComplete(@NonNull Task<DataSnapshot> task) {
//                                                    if (task.isSuccessful()){
//                                                        String intime=String.valueOf(task.getResult().getValue());
//                                                        //Pushing the data in Global Attendance Node with extra data like name and Organization ID
//                                                        reference.child("Attendees").child(OrganizationName).child(year).child(month).child(date).child(userID).child("outTime").setValue(Time).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                            @Override
//                                                            public void onSuccess(Void unused) {
//                                                                new Handler().postDelayed(new Runnable() {
//                                                                    @Override
//                                                                    public void run() {
//                                                                        Intent intent= new Intent(AttendanceResult.this,AttendanceActivity.class);
//                                                                        startActivity(intent);
//                                                                        finish();
//                                                                    }
//                                                                },SPLASH_SCREEN);
//                                                            }
//                                                        });
//                                                    }else{
//                                                        //Pushing the data in Global Attendance Node with extra data like name and College ID
//                                                        reference.child("Attendees").child(OrganizationName).child(year).child(month).child(date).child(userID).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
//                                                            @Override
//                                                            public void onSuccess(Void unused) {
//                                                                new Handler().postDelayed(new Runnable() {
//                                                                    @Override
//                                                                    public void run() {
//                                                                        Intent intent= new Intent(AttendanceResult.this,AttendanceActivity.class);
//                                                                        startActivity(intent);
//                                                                        finish();
//                                                                    }
//                                                                },SPLASH_SCREEN);
//                                                            }
//                                                        });
//                                                    }
//                                                }
//                                            }).addOnFailureListener(new OnFailureListener() {
//                                                @Override
//                                                public void onFailure(@NonNull Exception e) {
//                                                    Intent intent= new Intent(AttendanceResult.this,AttendanceActivity.class);
//                                                    startActivity(intent);
//                                                    finish();
//                                                }
//                                            });
//                                        }
//                                    }
//                                });
//                            }
//                        }
//                    });
//                }
//            }
//        });

    }
    private void addData(String userID,String year,String month,String date,String Time){
        reference.child("Users").child(firebaseUser.getUid()).child("Employees").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    name = Objects.requireNonNull(snapshot.child("name").getValue()).toString();
                    OrganizationId=Objects.requireNonNull(snapshot.child("id").getValue()).toString();
                    OrganizationName=Objects.requireNonNull(snapshot.child("organization").getValue()).toString();
                    Name.setText(name+"\n"+OrganizationId);
                    map.put("name", name);
                    map.put("id", OrganizationId);
                    reference.child("Attendees").child(OrganizationName).child(year).child(month).child(date).child(userID).addValueEventListener(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.exists()){
                                reference.child("Attendees").child(OrganizationName).child(year).child(month).child(date).child(userID).child("outTime").setValue(Time).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        intentNow();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }else{
                                //Pushing the data in Global Attendance Node with extra data like name and College ID
                                reference.child("Attendees").child(OrganizationName).child(year).child(month).child(date).child(userID).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                    @Override
                                    public void onSuccess(Void unused) {
                                        intentNow();
                                    }
                                }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {

                                    }
                                });
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            intentNow();
                        }
                    });
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void intentNow() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                Intent intent= new Intent(AttendanceResult.this,AttendanceActivity.class);
                startActivity(intent);
                finish();
            }
        },SPLASH_SCREEN);
    }
    //Function to initialize the variables
    private void Initialize() {
        Name = findViewById(R.id.name_display);
        reference = FirebaseDatabase.getInstance().getReference();
        uniqueId=getIntent().getStringExtra("uid");

    }

}