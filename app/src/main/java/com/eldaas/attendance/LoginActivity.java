package com.eldaas.attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.pranavpandey.android.dynamic.toasts.DynamicToast;

import java.util.Objects;

public class LoginActivity extends AppCompatActivity {
    TextInputLayout org_email,org_pass;
    TextView gotoregister,forgetPass;
    FirebaseAuth fAuth;
    String control="";
    View login;
    ProgressBar buttonProgress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        org_email = findViewById(R.id.login_email);
        org_pass = findViewById(R.id.login_password);
        gotoregister = findViewById(R.id.reg_btn);
        login = findViewById(R.id.loginButton);
        forgetPass=findViewById(R.id.forgotpass_tv);
        buttonProgress = findViewById(R.id.buttonProgress);
        fAuth = FirebaseAuth.getInstance();
        control=getIntent().getStringExtra("devicecontrol");
        if(Objects.equals(control, "admin")){
            gotoregister.setVisibility(View.VISIBLE);
            forgetPass.setVisibility(View.VISIBLE);
        }else{
            gotoregister.setVisibility(View.GONE);
            forgetPass.setVisibility(View.GONE);
        }
        gotoregister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(LoginActivity.this,RegisterActivity.class);
                startActivity(intent);
            }
        });
        forgetPass.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(LoginActivity.this,ForgetPasswordActivity.class));
            }
        });
        ConstraintLayout cl = findViewById(R.id.progress_button_bg);
        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                cl.setBackground(getResources().getDrawable(R.drawable.positive)); //Change the button drawable to green
                String email = org_email.getEditText().getText().toString().trim();
                String pass = org_pass.getEditText().getText().toString().trim();
                if(TextUtils.isEmpty(email)){
                    org_email.setError("e-mail is required ");
                    return;
                }
                if(TextUtils.isEmpty(pass)){
                    org_pass.setError("password is required");
                    return;
                }
                buttonProgress.setVisibility(View.VISIBLE);
                if (checkInternetConnectivity()){
                    fAuth.signInWithEmailAndPassword(email,pass).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if(task.isSuccessful()){
                                DynamicToast.makeSuccess(LoginActivity.this,"Logged In !").show();
                                if(Objects.equals(control, "admin")){
                                    startActivity(new Intent(LoginActivity.this,MainActivity.class));
                                }
                                else{
                                    startActivity(new Intent(LoginActivity.this,AttendanceActivity.class));
                                }
                                finish();
                            }else{
                                DynamicToast.makeError(LoginActivity.this,""+task.getException().getMessage()).show();
                            }
                            buttonProgress.setVisibility(View.GONE);
                        }
                    });
                }else{
                    DynamicToast.makeError(LoginActivity.this,"Internet connection is not available").show();
                }


            }
        });


    }
    public boolean checkInternetConnectivity() {
        Context context=this;
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        if (activeNetwork != null) {
            if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI||activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                return true;
            }
        } else {
            return false;
        }
        return false;
    }

}