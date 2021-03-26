package com.example.mymed;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
public class SavePasswordActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private FirebaseAuth firebaseAuth;
    private Button newPasswordButton;
    private EditText emailEdit;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_save_password);
        firebaseAuth = FirebaseAuth.getInstance();
        newPasswordButton = (Button)findViewById(R.id.save_password_btn);
        emailEdit = (EditText)findViewById(R.id.editTextTextEmailAddress);

        newPasswordButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if(!emailEdit.getText().toString().equals("")) {
                    FirebaseAuth.getInstance().sendPasswordResetEmail(emailEdit.getText().toString())
                            .addOnCompleteListener(new OnCompleteListener<Void>() {

                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d(TAG, "Email sent.");
                                        Toast.makeText(SavePasswordActivity.this.getApplicationContext(), "E' stata recapitata una mail al tuo indirizzo per reimpostare la password", Toast.LENGTH_SHORT).show();
                                        firebaseAuth.signOut();
                                    }
                                }
                            });
                }
                else{
                    Toast.makeText(SavePasswordActivity.this.getApplicationContext(), "Inserisci e-mail!", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }
}