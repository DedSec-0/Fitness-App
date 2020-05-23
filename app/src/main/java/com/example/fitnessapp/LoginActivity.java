package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class LoginActivity extends AppCompatActivity {

    public EditText emailId, password;
    Button btnSignIn;
    TextView txSignUp;
    FirebaseAuth mFireBaseAuth;
    Spinner Category;
    String[] category = new String[]{"Trainer", "Food Specialist", "User", "Admin"};
    private String categorySelected;
    private FirebaseAuth.AuthStateListener mAuthStateListener;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        mFireBaseAuth = FirebaseAuth.getInstance();
        emailId = findViewById(R.id.editText);
        password = findViewById(R.id.editText2);
        txSignUp = findViewById(R.id.textView);
        btnSignIn = findViewById(R.id.button);
        Category = findViewById(R.id.category);
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, category);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        Category.setAdapter(adapter);

        Category.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(8,160,233));
                categorySelected = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TextView errorText = (TextView) Category.getSelectedView();
                errorText.setError("Please Select Category");
                errorText.setTextColor(Color.RED); //just to highlight that this is an error
                errorText.setText("Please Select Category");
            }
        });

        addAuthListener();
        addSignInListener();

        txSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, MainActivity.class));

            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        mFireBaseAuth.addAuthStateListener(mAuthStateListener);
    }

    private void addAuthListener() {
        mAuthStateListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                FirebaseUser mFireBaseUser = mFireBaseAuth.getCurrentUser();
                if(mFireBaseUser != null) {
                    final String email = mFireBaseUser.getEmail();
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference();

                    dbRef.child("User").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(email.replace(".", "_DOT_")))
                            {
                                Intent temp = new Intent(LoginActivity.this, UserActivity.class);
                                temp.putExtra("Email", email);
                                startActivity(temp);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                    dbRef.child("Trainer").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(email.replace(".", "_DOT_")))
                            {
                                Intent temp = new Intent(LoginActivity.this, TrainerAndFoodActivity.class);
                                temp.putExtra("category", "Trainer");
                                startActivity(temp);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                    dbRef.child("Food Specialist").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(email.replace(".", "_DOT_")))
                            {
                                Intent temp = new Intent(LoginActivity.this, TrainerAndFoodActivity.class);
                                temp.putExtra("category", "Food Specialist");
                                startActivity(temp);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });
                    dbRef.child("Admin").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(email.replace(".", "_DOT_")))
                            {
                                Intent temp = new Intent(LoginActivity.this, AdminActivity.class);
                                startActivity(temp);
                            }
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });

                    Toast.makeText(LoginActivity.this, "You are logged in!", Toast.LENGTH_SHORT).show();
                }
                else {
                    Toast.makeText(LoginActivity.this, "Please Log In!", Toast.LENGTH_SHORT).show();
                }
            }
        };
    }

    private void addSignInListener() {
        btnSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String email = emailId.getText().toString();
                final String pass = password.getText().toString();

                if(email.isEmpty()){
                    emailId.setError("Please Enter Email");
                    emailId.requestFocus();
                }
                else if(pass.isEmpty()){
                    password.setError("Please Enter your password");
                    password.requestFocus();
                }
                else if(pass.isEmpty() && email.isEmpty()) {
                    Toast.makeText(LoginActivity.this, "Fields are empty!", Toast.LENGTH_SHORT ).show();
                }
                else if (!(pass.isEmpty() && email.isEmpty())) {
                    DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child(categorySelected);

                    dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                            if (dataSnapshot.hasChild(email.replace(".", "_DOT_")))
                            {
                                mFireBaseAuth.signInWithEmailAndPassword(email, pass).addOnCompleteListener(LoginActivity.this, new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if(!task.isSuccessful()){
                                            Toast.makeText(LoginActivity.this, "Username or password is incorrect", Toast.LENGTH_SHORT ).show();
                                        }
                                        else {
                                            Intent temp;
                                            if(categorySelected == "User") {
                                                temp = new Intent(LoginActivity.this, UserActivity.class);
                                                temp.putExtra("Email", email);
                                            }
                                            else if (categorySelected == "Admin") {
                                                temp = new Intent(LoginActivity.this, AdminActivity.class);
                                            }
                                            else {
                                                temp = new Intent(LoginActivity.this, TrainerAndFoodActivity.class);
                                                temp.putExtra("category", categorySelected);
                                            }
                                            startActivity(temp);
                                        }
                                    }
                                });
                            }
                            else
                                Toast.makeText(LoginActivity.this, "Username or password is incorrect", Toast.LENGTH_SHORT ).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError databaseError) {}
                    });

                }
                else {
                    Toast.makeText(LoginActivity.this, "Error Occurred", Toast.LENGTH_SHORT ).show();
                }
            }
        });
    }
}
