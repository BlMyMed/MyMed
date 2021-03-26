package com.example.mymed;

import android.app.DatePickerDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.DatePicker;
import android.widget.EditText;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link PendingFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class PendingFragment extends Fragment {

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private DatePickerDialog picker;
    private FirebaseAuth firebaseAuth;
    List<BookingUser> bookingsList;
    private RecyclerView recyclerView;
    private HelperAdapterBookingDoctor helperAdapter;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public PendingFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment PendingFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static PendingFragment newInstance(String param1, String param2) {
        PendingFragment fragment = new PendingFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_pending, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        String user_id = firebaseAuth.getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mymed-b094e-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference();

        bookingsList= new ArrayList<>();
        myRef.child("bookings").child(user_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                bookingsList = new ArrayList<>();
                for (DataSnapshot date : snapshot.getChildren()) {
                    for(DataSnapshot prenotazione : date.getChildren()) {
                        Log.d("Log pren: ",prenotazione.getKey());
                        BookingUser bk = prenotazione.getValue(BookingUser.class);
                        if (bk.getStato().equals("pending")) {
                            DatabaseReference myRef2 = database.getReference();
                            myRef2.child("users").child("patients").child(bk.getId_utente())
                                    .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    User user = new User(snapshot.child("name").getValue()
                                            .toString(), snapshot.child("surname").getValue()
                                            .toString(), snapshot.child("birth_date").getValue()
                                            .toString(), snapshot.child("doctor").getValue().toString());

                                    bk.setCodice_utente(bk.getId_utente());
                                    bk.setId_utente(user.getName() + " " + user.getSurname());
                                    bookingsList.add(bk);
                                    Collections.sort(bookingsList, new Comparator<BookingUser>() {
                                        SimpleDateFormat formatter = new SimpleDateFormat("dd-MMM-yyyy");
                                        SimpleDateFormat sdfAmerica = new SimpleDateFormat("yyyy-mm-dd");
                                        @Override
                                        public int compare(final BookingUser object1, final BookingUser object2) {
                                            LocalDate localDate1;
                                            LocalDate localDate2;
                                            Locale locale = Locale.US;
                                            DateTimeFormatter formatter = DateTimeFormatter.ofPattern ( "dd-MM-yyyy" )
                                                    .withLocale ( locale );
                                            localDate1 = LocalDate.parse ( object1.getData_prenotazione() , formatter );
                                            localDate2 = LocalDate.parse ( object2.getData_prenotazione() , formatter );

                                            return localDate1.compareTo(localDate2);
                                        }
                                    });

                                    Collections.reverse(bookingsList);
                                    helperAdapter = new HelperAdapterBookingDoctor(bookingsList);
                                    recyclerView.setAdapter(helperAdapter);
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {
                                }
                            });

                        }else{

                            helperAdapter = new HelperAdapterBookingDoctor(bookingsList);
                            recyclerView.setAdapter(helperAdapter);
                        }
                    }
                }

            }
            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        recyclerView= (RecyclerView) view.findViewById(R.id.result_list_doc_2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(helperAdapter);

        return view;
    }
}