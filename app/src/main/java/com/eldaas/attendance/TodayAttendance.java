package com.eldaas.attendance;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.facebook.shimmer.ShimmerFrameLayout;
import com.firebase.ui.database.FirebaseRecyclerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class TodayAttendance extends AppCompatActivity {
    RecyclerView recyclerView;
    myAdapter myAdapter;

    private ShimmerFrameLayout ShimmerViewContainer;
    TextView dateDisplay;
    @SuppressLint("SimpleDateFormat")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_today_attendance);
        Initialization(); //Function to initialize the variables
        String OrganizationName= getIntent().getStringExtra("orgName");

        ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE); //Check internet connection


        ShimmerViewContainer.startShimmer(); //start shimmer animation
        ShimmerViewContainer.setVisibility(View.VISIBLE);

        Calendar cal = Calendar.getInstance(); //get calendar instance

        //Getting the values of year, month name, date and full date format in the form of string.
        String year = new SimpleDateFormat("yyyy").format(cal.getTime());
        String month = new SimpleDateFormat("MMM").format(cal.getTime());
        String date = new SimpleDateFormat("dd").format(cal.getTime());
        String fullDate = new SimpleDateFormat("dd-MM-yyyy").format(cal.getTime());

        dateDisplay.setText(fullDate);

        LinearLayout layout = findViewById(R.id.emptyState); //Initializing (Empty state illustration)

        //Firebase data -> RecyclerView
        FirebaseRecyclerOptions<ModelClass> options =
                new FirebaseRecyclerOptions.Builder<ModelClass>()
                        .setQuery(FirebaseDatabase.getInstance().getReference().child("Attendees").child(OrganizationName).child(year).child(month).child(date), ModelClass.class)
                        .build();


        //Setting up the adapter with the Firebase UI variable -> 'options'
        myAdapter = new myAdapter(options);

        if (myAdapter.getItemCount() == 0) {        //If no item is found in the recycler view
            recyclerView.setVisibility(View.GONE);      //Disable recycler view
            ShimmerViewContainer.startShimmer();
            layout.setVisibility(View.INVISIBLE);       //Disable Empty State Illustration
            ShimmerViewContainer.setVisibility(View.VISIBLE);
            new Handler().postDelayed(() -> { //Perform some task after some delay

                ShimmerViewContainer.setVisibility(View.INVISIBLE); //Disable shimmering effect after the delay time

                //If connected to internet
                if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                        connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {

                    if (myAdapter.getItemCount() == 0) //If still the adapter is empty (we give time to the app to fetch data from firebase and check here)
                        layout.setVisibility(View.VISIBLE); //Checking again to handle slow internet or firebase issues, show Empty State Illustration
                } else {
                    //If no internet connection found
                    recyclerView.setVisibility(View.INVISIBLE);
                    TextView error = layout.findViewById(R.id.error);
                    error.setText("Please check Internet!");
                    layout.setVisibility(View.VISIBLE);
                }
            }, 2000); //Delay Time

        } else {
            //If data/item is present in Adapter
            layout.setVisibility(View.INVISIBLE);
            ShimmerViewContainer.setVisibility(View.GONE);
            recyclerView.setAdapter(myAdapter);
        }

        myAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onChanged() {
                super.onChanged();
                checkEmpty();
            }

            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                super.onItemRangeInserted(positionStart, itemCount);
                checkEmpty();
            }

            @Override
            public void onItemRangeRemoved(int positionStart, int itemCount) {
                super.onItemRangeRemoved(positionStart, itemCount);
                checkEmpty();
            }

            //Handle the condition when there's any change in item count of the adapter
            void checkEmpty() {
                if (myAdapter.getItemCount() == 0) { //If no item found in adapter
                    recyclerView.setVisibility(View.GONE); //Disable recyclerView
                    layout.setVisibility(View.INVISIBLE);
                    ShimmerViewContainer.setVisibility(View.VISIBLE); //Show shimmering effect

                    new Handler().postDelayed(() -> {
                        ShimmerViewContainer.setVisibility(View.INVISIBLE); //Disable shimmering effect after the delay time
                        layout.setVisibility(View.VISIBLE); //Show Empty State Illustration
                    }, 2000); //Delay time

                } else { //If item is found in adapter
                    layout.setVisibility(View.INVISIBLE);
                    ShimmerViewContainer.setVisibility(View.GONE);
                    recyclerView.setAdapter(myAdapter);
                }
            }
        });
    }

    //When the activity starts
    @Override
    protected void onStart() {
        super.onStart();
        recyclerView.setVisibility(View.VISIBLE);
        myAdapter.startListening();
    }

    //When the activity is stopped
    @Override
    protected void onStop() {
        super.onStop();
        if (myAdapter != null) { //checking if adapter is not empty
            myAdapter.stopListening();
            recyclerView.setVisibility(View.GONE);
        } else {
            LinearLayout layout = findViewById(R.id.emptyState);
            layout.setVisibility(View.INVISIBLE);
        }
    }

    //Function to initialize the variables
    private void Initialization() {
        recyclerView = findViewById(R.id.recyclerView);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        ShimmerViewContainer = findViewById(R.id.shimmerFrameLayout);
        dateDisplay = findViewById(R.id.date);
    }
    @Override
    public void onBackPressed() {
        Intent intent = new Intent(getApplicationContext(), MainActivity.class);
        startActivity(intent);
        finish();
    }

}