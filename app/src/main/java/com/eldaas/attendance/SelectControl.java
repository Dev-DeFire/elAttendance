package com.eldaas.attendance;

import androidx.appcompat.app.AppCompatActivity;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;

public class SelectControl extends AppCompatActivity {

    CardView admin_Btn,device_Btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_control);
        admin_Btn=findViewById(R.id.admin);
        device_Btn=findViewById(R.id.device);

        admin_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(SelectControl.this,LoginActivity.class);
                intent.putExtra("devicecontrol","admin");
                startActivity(intent);

            }
        });
        device_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(FirebaseAuth.getInstance().getCurrentUser()!=null){
                    Intent intent= new Intent(SelectControl.this,AttendanceActivity.class);
                    startActivity(intent);
                    finish();
                }else{
                    Intent intent= new Intent(SelectControl.this,LoginActivity.class);
                    intent.putExtra("devicecontrol","device");
                    startActivity(intent);
                }



            }
        });
    }
}