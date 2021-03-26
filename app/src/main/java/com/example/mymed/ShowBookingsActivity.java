package com.example.mymed;

import androidx.annotation.IdRes;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationItemView;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Locale;

public class ShowBookingsActivity extends AppCompatActivity {

    private FirebaseAuth firebaseAuth;
    private RecyclerView recyclerView;
    private Button buttonPrenota;
    private boolean flag;
    private HelperAdapterBooking helperAdapter;
    private DatabaseReference databaseReference;
    List<BookingUser> bookingsList;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_show_bookings);

        firebaseAuth = FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() == null) {
            // User is not logged in
            startActivity(new Intent(ShowBookingsActivity.this, MainActivity.class));
            finish();
        }

        bookingsList= new ArrayList<>();
        recyclerView= (RecyclerView) findViewById(R.id.result_list);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        recyclerView.setAdapter(helperAdapter);

        buttonPrenota = (Button)findViewById(R.id.button_prenota);

        String user_id = firebaseAuth.getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mymed-b094e-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference();
        myRef.child("users").child("patients").child(user_id).child("bookings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot turno:snapshot.getChildren()) {
                    for (DataSnapshot prenotazione : turno.getChildren()) {
                        BookingUser bk = prenotazione.getValue(BookingUser.class);
                        bk.setId_utente("Tu");
                        bookingsList.add(bk);
                    }
                }

                Collections.sort(bookingsList, new Comparator<BookingUser>() {
                    SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
                    SimpleDateFormat sdfAmerica = new SimpleDateFormat("yyyy-mm-dd");
                    @Override
                    public int compare(final BookingUser object1, final BookingUser object2) {
                        LocalDate localDate1;
                        LocalDate localDate2;
                        Locale locale = Locale.US;
                        DateTimeFormatter formatter = DateTimeFormatter.ofPattern ( "dd-MM-yyyy" ).withLocale ( locale );
                        localDate1 = LocalDate.parse ( object1.getData_prenotazione() , formatter );
                        localDate2 = LocalDate.parse ( object2.getData_prenotazione() , formatter );

                        return localDate1.compareTo(localDate2);
                    }
                });

                Collections.reverse(bookingsList);
                helperAdapter = new HelperAdapterBooking(bookingsList);
                recyclerView.setAdapter(helperAdapter);
            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        myRef.child("users").child("patients").child(user_id).get().addOnCompleteListener(new OnCompleteListener<DataSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DataSnapshot> task) {
                if (!task.isSuccessful()) {
                    Log.e("firebase", "Error getting data", task.getException());
                }
                else {
                    if(task.getResult().child("doctor").getValue() == null){
                        startActivity(new Intent(ShowBookingsActivity.this, AddDoctorActivity.class));
                        finish();
                    }
                }
            }
        });

        buttonPrenota.setOnClickListener(new View.OnClickListener() {
            boolean check = false;
            @Override
            public void onClick(View v) {
                myRef.child("users").child("patients").child(user_id).child("bookings").addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        for (DataSnapshot date : snapshot.getChildren()) {
                            for(DataSnapshot prenotazione : date.getChildren()) {
                                BookingUser bk = prenotazione.getValue(BookingUser.class);
                                if (bk.getStato().equals("pending")) {
                                    check = true;
                                    break;
                                }
                            }
                        }

                        if(!check){
                            startActivity(new Intent(ShowBookingsActivity.this, UserActivity.class));
                            finish();
                        }
                        else{
                            Toast.makeText(ShowBookingsActivity.this, "Attenzione, hai già una prenotazione in attesa di conferma.", Toast.LENGTH_LONG).show();
                        }

                    }
                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });


            }
        });

        myRef.child("notifications_user").child(user_id).addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(@NonNull DataSnapshot snapshot, @Nullable String previousChildName) {

                if(flag) {
                    notification(snapshot.getValue().toString());
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(id == R.id.edit_doctor){
            startActivity(new Intent(ShowBookingsActivity.this, AddDoctorActivity.class));
            finish();
        }
        else if(id == R.id.edit_profile){
            startActivity(new Intent(ShowBookingsActivity.this, ChangePasswordActivity.class));
            finish();
        }
        else if(id == R.id.logout){
            firebaseAuth.signOut();
            startActivity(new Intent(ShowBookingsActivity.this, MainActivity.class));
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void notification(String stato_prenotazione){
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            NotificationChannel channel = new NotificationChannel("n","n", NotificationManager.IMPORTANCE_DEFAULT);
            NotificationManager manager = getSystemService(NotificationManager.class);
            manager.createNotificationChannel(channel);
        }

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this,"n")
                .setContentTitle("AGGIORNAMENTO STATO PRENOTAZIONE")
                .setSmallIcon(R.mipmap.ic_launcher)
                .setAutoCancel(true)
                .setContentText("Stato prenotazione: "+stato_prenotazione);

        NotificationManagerCompat managerCompat =  NotificationManagerCompat.from(this);
        managerCompat.notify(999,builder.build());

    }



}