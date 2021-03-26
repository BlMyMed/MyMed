package com.example.mymed;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class HelperAdapterBookingDoctor extends  HelperAdapterBooking {
    private FirebaseAuth firebaseAuth;
    public HelperAdapterBookingDoctor(List<BookingUser> bookingList) {
        super(bookingList);
    }
    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
        ViewHolderClassBooking viewHolderClass=(ViewHolderClassBooking)holder;

        BookingUser booking = bookingList.get(position);

        viewHolderClass.data_prenotazione.setText(booking.getData_prenotazione());
        viewHolderClass.stato_prenotazione.setText("IN ATTESA");
        viewHolderClass.turno_prenotazione.setText(booking.getOra());
        viewHolderClass.paziente_prenotazione.setText(booking.getId_utente());
        viewHolderClass.studio_prenotazione.setText(booking.getStudio_selezionato());
        viewHolderClass.stato_prenotazione.setBackgroundColor(Color.YELLOW);

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                builder.setPositiveButton("Sì",new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        firebaseAuth = FirebaseAuth.getInstance();
                        String user_id = firebaseAuth.getCurrentUser().getUid();
                        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mymed-b094e-default-rtdb.europe-west1.firebasedatabase.app/");
                        DatabaseReference myRef = database.getReference();
                        Date date1 = null;
                        try {
                            date1 = new SimpleDateFormat("dd-MM-yyyy").parse(bookingList.get(position).getData_prenotazione());
                        } catch (ParseException e) {
                            Log.d("Ok",e.getMessage());
                        }

                        String giornoSettimana = UserActivity.getDayStringOld(date1, Locale.ITALIAN);
                        // Codice turno

                        DatabaseReference myRef2 = database.getReference();

                        myRef2.child("bookings").child(user_id).child(bookingList.get(position).getData_prenotazione())
                                .addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot prenotazione : snapshot.getChildren()) {

                                    if (prenotazione.child("ora").getValue().toString().equals(bookingList.get(position).getOra())) {


                                        myRef.child("users").child("doctors").child(user_id)
                                                .child("offices").child(bookingList.get(position).getStudio_selezionato()).child("timetables")
                                                .child(giornoSettimana).child(prenotazione.getKey()).child("bookings")
                                                .child(bookingList.get(position).
                                                getId_prenotazione()).child("stato").setValue("booked");

                                        myRef.child("bookings").child(user_id)
                                                .child(bookingList.get(position).getData_prenotazione()).child(prenotazione.getKey())
                                                .child("stato").setValue("booked");

                                        myRef.child("users").child("patients").child(bookingList.get(position).getCodice_utente()).
                                                child("bookings").child(prenotazione.getKey()).
                                                child(bookingList.get(position).getId_prenotazione()).child("stato").setValue("booked");

                                        myRef.child("notifications_user").child(bookingList.get(position).getCodice_utente())
                                                .child(bookingList.get(position).getId_prenotazione()).setValue("booked");

                                        Toast.makeText(view.getContext(), "Prenotazione confermata", Toast.LENGTH_LONG).show();

                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }

                        });
                    }
                });

                builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        firebaseAuth = FirebaseAuth.getInstance();
                        String user_id = firebaseAuth.getCurrentUser().getUid();
                        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mymed-b094e-default-rtdb.europe-west1.firebasedatabase.app/");
                        DatabaseReference myRef = database.getReference();
                        Date date1 = null;
                        try {
                            date1 = new SimpleDateFormat("dd-MM-yyyy").parse(bookingList.get(position).getData_prenotazione());
                        } catch (ParseException e) {
                            Log.d("Ok",e.getMessage());
                        }

                        String giornoSettimana = UserActivity.getDayStringOld(date1, Locale.ITALIAN);
                        // Codice turno

                        DatabaseReference myRef2 = database.getReference();

                        myRef2.child("bookings").child(user_id).child(bookingList.get(position).getData_prenotazione()).addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                for (DataSnapshot prenotazione : snapshot.getChildren()) {

                                    if (prenotazione.child("ora").getValue().toString().equals(bookingList.get(position).getOra())) {


                                        myRef.child("users").child("doctors").child(user_id)
                                                .child("offices").child(bookingList.get(position).getStudio_selezionato()).child("timetables")
                                                .child(giornoSettimana).child(prenotazione.getKey()).child("bookings").child(bookingList.get(position).
                                                getId_prenotazione()).child("stato").setValue("refused");

                                        myRef.child("bookings").child(user_id)
                                                .child(bookingList.get(position).getData_prenotazione()).child(prenotazione.getKey())
                                                .child("stato").setValue("refused");

                                        myRef.child("users").child("patients").child(bookingList.get(position).getCodice_utente()).
                                                child("bookings").child(prenotazione.getKey()).
                                                child(bookingList.get(position).getId_prenotazione()).child("stato").setValue("refused");

                                        myRef.child("notifications_user").child(bookingList.get(position).getCodice_utente())
                                                .child(bookingList.get(position).getId_prenotazione()).setValue("refused");

                                        Toast.makeText(view.getContext(), "Prenotazione rifiutata", Toast.LENGTH_LONG).show();


                                    }
                                }

                            }

                            @Override
                            public void onCancelled(@NonNull DatabaseError error) {
                            }

                        });
                    }
                });

                builder.setMessage("Vuoi confermare la prenotazione selezionata?")
                            .setTitle("Conferma prenotazione");
                AlertDialog dialog = builder.create();

                dialog.show();

            }
        });

        }
    }



