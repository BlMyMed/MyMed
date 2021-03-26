package com.example.mymed;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class BookingDoctorActivity extends AppCompatActivity {
    private FirebaseAuth firebaseAuth;
    private DatabaseReference myRef;
    private static int count;
    private boolean flag;
    private BottomNavigationView bottomNavigation;
    private static Context context;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_doctor);
        context = this;
        bottomNavigation = findViewById(R.id.bottom_navigation);
        firebaseAuth = FirebaseAuth.getInstance();
        String user_id = firebaseAuth.getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mymed-b094e-default-rtdb.europe-west1.firebasedatabase.app/");
        myRef = database.getReference();
        if (firebaseAuth.getCurrentUser() == null) {
            // User is not logged in
            startActivity(new Intent(BookingDoctorActivity.this, MainActivity.class));
            finish();
        }
        count = 0;
        myRef.child("bookings").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                count = 0;
                for (DataSnapshot date : snapshot.getChildren()) {
                    for(DataSnapshot prenotazione : date.getChildren()) {
                        BookingUser bk = prenotazione.getValue(BookingUser.class);
                        if (bk.getStato().equals("pending")) {
                                    count++;
                        }
                        BottomMenuHelper.showBadge(context, bottomNavigation, R.id.navigation_pending, count+"");
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

            myRef.child("notifications").child(user_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                    Log.d("Valore_Ora",snapshot.child("ora").getValue().toString());
                    if(flag) {
                        notification(snapshot.child("ora").getValue().toString(),
                                snapshot.child("studio_selezionato").getValue().toString(),
                                snapshot.child("data_prenotazione").getValue().toString());
                        snapshot.getRef().setValue(null);
                    }


                }

            @Override
            public void onChildChanged(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onChildRemoved(@NonNull DataSnapshot snapshot) {

            }

            @Override
            public void onChildMoved(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        bottomNavigation.setOnNavigationItemSelectedListener(navigationItemSelectedListener);
        openFragment(PendingFragment.newInstance("", ""));

    }

    @Override
    public void onResume(){
        super.onResume();
        flag=true;
    }
    @Override
    public void onPause(){
        super.onPause();
        flag=false;
    }

    BottomNavigationView.OnNavigationItemSelectedListener navigationItemSelectedListener =
            new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.navigation_date:
                            openFragment(DateFragment.newInstance("", ""));
                            return true;
                        case R.id.navigation_pending:
                            item.setIconTintList(ColorStateList.valueOf(Color.BLUE));
                            openFragment(PendingFragment.newInstance("", ""));
                            return true;
                    }
                    return false;
                }
            };
    public void openFragment(Fragment fragment) {
        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.addToBackStack(null);
        transaction.commit();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_doctor, menu);
        return true;
    }
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.profilo_menu_doc){
            BookingDoctorActivity.this.startActivity(new Intent(BookingDoctorActivity.this, ChangePasswordActivityDoctor.class));
            BookingDoctorActivity.this.finish();
        }
        if(id == R.id.studio_menu_doc){
            BookingDoctorActivity.this.startActivity(new Intent(BookingDoctorActivity.this, ScheduleActivity.class));
            BookingDoctorActivity.this.finish();
        }
        if(id == R.id.logout_menu_doc){
            firebaseAuth.signOut();
            BookingDoctorActivity.this.startActivity(new Intent(BookingDoctorActivity.this, MainActivity.class));
            BookingDoctorActivity.this.finish();
        }
        if(id == R.id.prenotazioni_menu_doc){
            BookingDoctorActivity.this.startActivity(new Intent(BookingDoctorActivity.this, BookingDoctorActivity.class));

            BookingDoctorActivity.this.finish();
        }


        return super.onOptionsItemSelected(item);
    }
    private void notification(String ora, String studio_selezionato,String data_selezionata){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("n","n", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"n")
                .setContentTitle("NUOVA PRENOTAZIONE " + data_selezionata)
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentText(ora + "- "+studio_selezionato);

        NotificationManagerCompat managerCompat =  NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());

    }


}