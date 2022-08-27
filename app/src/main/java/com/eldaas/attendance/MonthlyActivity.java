package com.eldaas.attendance;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import org.eazegraph.lib.charts.ValueLineChart;
import org.eazegraph.lib.models.ValueLinePoint;
import org.eazegraph.lib.models.ValueLineSeries;

import java.text.SimpleDateFormat;
import java.time.YearMonth;
import java.util.Calendar;
import java.util.Objects;

public class MonthlyActivity extends AppCompatActivity {
    String userID="",empCollegeId="",empName="",empFather="",empEmail="",empContact="",empStartDate="",empBloodGrp="";
    TextView uidTv,nameTv,fatherTv,emailTv,contactTv,startTv,bloodTv;
    TextView totalDays, presentDays, absentDays, requireDays;
    private DatabaseReference reference;
    long c = 0; //count number of children (days present) in the user's Attendance node
    ValueLineChart mCubicValueLineChart;
    ValueLineSeries series = new ValueLineSeries();
    private FirebaseAuth firebaseAuth=FirebaseAuth.getInstance();
    private FirebaseUser firebaseUser=firebaseAuth.getCurrentUser();

    @RequiresApi(api = Build.VERSION_CODES.O)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_monthly);
        Initialization(); //Function to initialize the variables
        try{
            userID= getIntent().getStringExtra("uid");
            empCollegeId= getIntent().getStringExtra("eOrganizaionId");
            empName= getIntent().getStringExtra("ename");
            empFather= getIntent().getStringExtra("efather");
            empEmail= getIntent().getStringExtra("eemail");
            empContact= getIntent().getStringExtra("econtact");
            empStartDate=getIntent().getStringExtra("sDate");
            empBloodGrp= getIntent().getStringExtra("bloodgrp");
        }catch(Exception e){
            Intent intent = new Intent(MonthlyActivity.this,MainActivity.class);
            startActivity(intent);
            finish();
            Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
        }
        uidTv.setText(empCollegeId);
        nameTv.setText(empName);
        fatherTv.setText(empFather);
        emailTv.setText(empEmail);
        contactTv.setText(empContact);
        startTv.setText(empStartDate);
        bloodTv.setText(empBloodGrp);
        Calendar cal = Calendar.getInstance();
        String year = new SimpleDateFormat("yyyy").format(cal.getTime());
        String monthName = new SimpleDateFormat("MMM").format(cal.getTime());
        String month = new SimpleDateFormat("MM").format(cal.getTime());
        String date = new SimpleDateFormat("dd").format(cal.getTime());
        YearMonth yearMonthObject = YearMonth.of(Integer.parseInt(year), Integer.parseInt(month));
        int daysInMonth = yearMonthObject.lengthOfMonth();
        float todayDate = Float.parseFloat(date);

        reference.child("Users").child(firebaseUser.getUid()).child("Employees").child(userID).child("Attendance").child(year).child(monthName).addListenerForSingleValueEvent(new ValueEventListener() {
            @SuppressLint({"SetTextI18n", "DefaultLocale"})
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                c = snapshot.getChildrenCount();

                float count = c;
                float absent = todayDate - count;
                float percent = (count / todayDate) * 100;
                float expected = ((c + (daysInMonth - todayDate)) / daysInMonth) * 100;

                //Set calculated data in TextView
                totalDays.setText("Total days in this month: "+daysInMonth+" days");
                presentDays.setText("Present: "+(int)count+" days");
                absentDays.setText("Absent: "+(int)absent+" days");
                requireDays.setText("Attendance if daily attended: "+String.format("%.2f", expected)+"%");

                //Set color of the Progress Bar and Graph According to the Attendance Percentage %
                if (percent >= 0 && percent < 30) {
//                    setThis(getResources().getColor(R.color.red_desat), getResources().getColor(R.color.shallow_red));
                    series.setColor(0xFFFF3F3F);

                } else if (percent >= 30 && percent < 50) {
//                    setThis(getResources().getColor(R.color.orange), getResources().getColor(R.color.shallow_orange_yellow));
                    series.setColor(0xFFFF5100);

                } else if (percent >= 50 && percent < 65) {
//                    setThis(getResources().getColor(R.color.orange_yellow), getResources().getColor(R.color.shallow_orange_yellow));
                    series.setColor(0xFFFF9900);

                } else if (percent >= 65 && percent < 75) {
//                    setThis(getResources().getColor(R.color.yellow), getResources().getColor(R.color.shallow_orange_yellow));
                    series.setColor(0xFFFFDD00);

                } else {
//                    setThis(getResources().getColor(R.color.green_desat), getResources().getColor(R.color.shallow_green));
                    series.setColor(0xFF4CA456);
                }

                float[] graphArray = new float[daysInMonth]; //Store the days present in a new array of no. of days in that month. Eg: 31 for January

                for (DataSnapshot ds : snapshot.getChildren()) {
                    int i = Integer.parseInt(Objects.requireNonNull(ds.getKey()));
                    graphArray[i] = 1.4f; //Storing the value 1 on the index (date) the user was present
                }

                for (int i=0; i<graphArray.length; i++) {
                    series.addPoint(new ValueLinePoint(String.valueOf(i), graphArray[i])); //adding the point in the graph on that index
                }



//                mCubicValueLineChart.setShowDecimal(true);
                mCubicValueLineChart.addSeries(series);
                mCubicValueLineChart.startAnimation();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {}
        });

    }
    //Function to initialize the variables
    private void Initialization() {
        uidTv=findViewById(R.id.uid);
        nameTv=findViewById(R.id.name);
        fatherTv= findViewById(R.id.fathername);
        emailTv= findViewById(R.id.email);
        contactTv=findViewById(R.id.contact);
        startTv=findViewById(R.id.joinDate);
        bloodTv=findViewById(R.id.blood);
        reference = FirebaseDatabase.getInstance().getReference();
        totalDays = findViewById(R.id.totalDays);
        presentDays = findViewById(R.id.presentDays);
        absentDays = findViewById(R.id.absentDays);
        requireDays = findViewById(R.id.requireDays);
        mCubicValueLineChart = findViewById(R.id.lineChart);
    }
    //Function to set colors for the Progress Bar
//    private void setThis(int first, int second) {
//        mChart.setProgressColor(first);
//        mChart.setBackgroundBarColor(second);
//        mChart.setTextColor(first);
//    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent intent= new Intent(MonthlyActivity.this,EmployeesActivity.class);
        startActivity(intent);
        finish();
    }
}