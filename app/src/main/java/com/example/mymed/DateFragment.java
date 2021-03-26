package com.example.mymed;
import android.app.DatePickerDialog;
import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.text.InputType;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Locale;
/**
 * A simple {@link Fragment} subclass.
 * Use the {@link DateFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DateFragment extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private EditText selectedData;
    private DatePickerDialog picker;
    private FirebaseAuth firebaseAuth;
    List<BookingUser> bookingsList;
    private RecyclerView recyclerView;
    private HelperAdapterBooking helperAdapter;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    public DateFragment() {
        // Required empty public constructor
    }
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DateFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static DateFragment newInstance(String param1, String param2) {
        DateFragment fragment = new DateFragment();
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
        View view = inflater.inflate(R.layout.fragment_date, container, false);
        firebaseAuth = FirebaseAuth.getInstance();
        String user_id = firebaseAuth.getCurrentUser().getUid();
        FirebaseDatabase database = FirebaseDatabase.getInstance("https://mymed-b094e-default-rtdb.europe-west1.firebasedatabase.app/");
        DatabaseReference myRef = database.getReference();
        selectedData=(EditText) view.findViewById(R.id.data_prenotazione_doc2);
        selectedData.setInputType(InputType.TYPE_NULL);
        selectedData.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("giornoSettimana", "Giorno selezionato: " );
                final Calendar cldr = Calendar.getInstance();
                int day = cldr.get(Calendar.DAY_OF_MONTH);
                int month = cldr.get(Calendar.MONTH);
                int year = cldr.get(Calendar.YEAR);
                // date picker dialog
                picker = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                SimpleDateFormat simpledateformat = new SimpleDateFormat("dd-MM-yyyy");
                                Calendar newDate = Calendar.getInstance();
                                newDate.set(year, monthOfYear, dayOfMonth);
                                bookingsList= new ArrayList<>();
                                helperAdapter = new HelperAdapterBooking(bookingsList);
                                recyclerView.setAdapter(helperAdapter);
                                selectedData.setText(simpledateformat.format(newDate.getTime()));
                                if(!selectedData.getText().toString().isEmpty()){
                                    Date date1 = null;
                                    try {
                                        date1 = new SimpleDateFormat("dd-MM-yyyy").parse(selectedData.getText().toString());
                                    } catch (ParseException e) {
                                    }
                                }
                                myRef.child("bookings").child(user_id).child(selectedData.getText().toString()).addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                        for (DataSnapshot prenotazione : snapshot.getChildren()) {
                                            BookingUser bk = prenotazione.getValue(BookingUser.class);
                                            if(bk.getStato().equals("booked")) {
                                                DatabaseReference myRef2 = database.getReference();
                                                myRef2.child("users").child("patients").child(bk.getId_utente()).addListenerForSingleValueEvent(new ValueEventListener() {
                                                    @Override
                                                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                                User user = new User(snapshot.child("name").getValue().toString(),snapshot.child("surname").getValue().toString(),snapshot.child("birth_date").getValue().toString(),snapshot.child("doctor").getValue().toString());
                                                                bk.setId_utente(user.getName()+" "+user.getSurname());
                                                                bookingsList.add(bk);
                                                                helperAdapter = new HelperAdapterBooking(bookingsList);
                                                                recyclerView.setAdapter(helperAdapter);
                                                    }
                                                    @Override
                                                    public void onCancelled(@NonNull DatabaseError error) {
                                                    }
                                                });

                                            }
                                        }

                                    }
                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {
                                    }
                                });
                            }
                        }, year, month, day);
                picker.show();
                picker.getDatePicker().setMinDate(System.currentTimeMillis()-1000);
            }
        });
        recyclerView= (RecyclerView) view.findViewById(R.id.result_list_doc_2);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));
        recyclerView.setAdapter(helperAdapter);

        return view;
    }
}