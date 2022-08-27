package com.eldaas.attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;
import androidx.fragment.app.Fragment;

import android.annotation.SuppressLint;
import android.app.Dialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {
    CardView attendance_Btn,employee_Btn,addEmployee_Btn,extra_Btn;
    Dialog dialog;
    private String OrganizationName;
    private DatabaseReference reference=FirebaseDatabase.getInstance().getReference();
    private FirebaseUser firebaseUser= FirebaseAuth.getInstance().getCurrentUser();

    @SuppressLint("NonConstantResourceId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Initialization();     //Function to initialize the variables

        attendance_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                reference.child("Users").child(firebaseUser.getUid()).child("organization").get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DataSnapshot> task) {
                        if (task.isSuccessful()){
                            String orgName=String.valueOf(task.getResult().getValue());
                            Intent intent= new Intent(MainActivity.this,TodayAttendance.class);
                            intent.putExtra("orgName",orgName);
                            startActivity(intent);
                        }
                    }
                });
            }
        });
        employee_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(MainActivity.this,EmployeesActivity.class);
                startActivity(intent);
            }
        });
        addEmployee_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AddPerson.class);
                startActivity(intent);
            }
        });
        extra_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this,AttendanceActivity.class);
                startActivity(intent);
            }
        });

    }

    //Function to initialize the variables
    private void Initialization() {
        reference = FirebaseDatabase.getInstance().getReference();
        attendance_Btn=findViewById(R.id.todayAttendance_Btn);
        employee_Btn=findViewById(R.id.employee_btn);
        addEmployee_Btn=findViewById(R.id.addEmployee_Btn);
        extra_Btn=findViewById(R.id.extra_Btn);
    }

    @SuppressLint({"UseCompatLoadingForDrawables", "ObsoleteSdkInt", "SetTextI18n"})
    public void onBackPressed() {

        //Pop-up Dialog box when back pressed -> ask the user if they want to exit or not.

        dialog = new Dialog(MainActivity.this);
        dialog.setContentView(R.layout.custom_dialog);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            dialog.getWindow().setBackgroundDrawable(MainActivity.this.getDrawable(R.drawable.custom_dialog_background));
        }
        dialog.getWindow().setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        dialog.setCancelable(false); //Optional
        dialog.getWindow().getAttributes().windowAnimations = R.style.animation; //Setting the animations to dialog

        Button Proceed = dialog.findViewById(R.id.proceed);
        Button Cancel = dialog.findViewById(R.id.cancel);
        TextView title = dialog.findViewById(R.id.dialog_title);
        TextView description = dialog.findViewById(R.id.dialog_description);

        Proceed.setText("Exit");
        Proceed.setBackground(getResources().getDrawable(R.drawable.negative));
        title.setText("Confirm exit");
        description.setText("Do you really want to exit?");

        Proceed.setOnClickListener(v -> {
            dialog.dismiss();
            finishAffinity();
        });

        Cancel.setOnClickListener(v -> dialog.dismiss());
        dialog.show();
    }
}