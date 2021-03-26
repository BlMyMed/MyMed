package com.example.mymed;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.List;

public class HelperAdapterBooking extends RecyclerView.Adapter {

    List<BookingUser> bookingList;

    public HelperAdapterBooking(List<BookingUser> bookingList) {
        this.bookingList = bookingList;
    }

    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_layout_booking,parent,false);
        ViewHolderClassBooking viewHolderClass = new ViewHolderClassBooking(view);

        return viewHolderClass;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {

        ViewHolderClassBooking viewHolderClass=(ViewHolderClassBooking)holder;

        BookingUser booking = bookingList.get(position);

        viewHolderClass.data_prenotazione.setText(booking.getData_prenotazione());
        viewHolderClass.turno_prenotazione.setText(booking.getOra());
        viewHolderClass.paziente_prenotazione.setText(booking.getId_utente());
        viewHolderClass.studio_prenotazione.setText(booking.getStudio_selezionato());
        if(booking.getStato().equals("booked")){
            viewHolderClass.stato_prenotazione.setBackgroundColor(Color.GREEN);
            viewHolderClass.stato_prenotazione.setText("CONFERMATO");
        }
        else if(booking.getStato().equals("pending")){
            viewHolderClass.stato_prenotazione.setBackgroundColor(Color.YELLOW);
            viewHolderClass.stato_prenotazione.setText("IN ATTESA");
        }
        else{
            viewHolderClass.stato_prenotazione.setBackgroundColor(Color.RED);
            viewHolderClass.stato_prenotazione.setText("RIFIUTATO");
        }

    }

    @Override
    public int getItemCount() {
        return bookingList.size();
    }

    public class ViewHolderClassBooking extends RecyclerView.ViewHolder{

        TextView data_prenotazione, stato_prenotazione,turno_prenotazione, paziente_prenotazione,studio_prenotazione;
        public ViewHolderClassBooking(@NonNull View itemView) {
            super(itemView);
            data_prenotazione = itemView.findViewById(R.id.data_prenotazione);
            stato_prenotazione = itemView.findViewById(R.id.stato_prenotazione);
            turno_prenotazione = itemView.findViewById(R.id.ora_prenotazione);
            paziente_prenotazione = itemView.findViewById(R.id.paziente_prenotazione);
            studio_prenotazione = itemView.findViewById(R.id.studio_prenotazione);
        }
    }
}
