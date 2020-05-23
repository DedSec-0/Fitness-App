package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class TrainerDetailActivity extends AppCompatActivity {

    String emailId;
    Button btnAdd;
    Spinner[] exType = new Spinner[3], setsType = new Spinner[3], timeType = new Spinner[3];
    String[] exercises = new String[3], timePeriod = new String[3];
    Integer[] sets = new Integer[3];
    String[] Exercises = new String[]{"None", "Push-Ups", "Squats", "Dips", "Lunge", "DeadLifts", "Crunches"};
    Integer[] Sets = new Integer[]{0, 1, 2, 3};
    String[] Days = new String[]{"None", "3 Days", "5 Days", "7 Days"};
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_detail);

        emailId = getIntent().getStringExtra("Email");
        btnAdd = findViewById(R.id.addExBtn);

        setUpToolBar();
        addListenersOnSpinners();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addExercise();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(TrainerDetailActivity.this, LoginActivity.class));

        return super.onOptionsItemSelected(item);
    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Fitness App");
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent temp = new Intent(TrainerDetailActivity.this, TrainerAndFoodActivity.class);

                if(getSupportActionBar() != null)
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                temp.putExtra("category", "Trainer");
                startActivity(temp);
            }
        });
    }

    private void addListenersOnSpinners() {
        //Exercise 1
        exType[0] = findViewById(R.id.Ex1_dropDown1);
        setsType[0] = findViewById(R.id.Ex1_dropDown2);
        timeType[0] = findViewById(R.id.Ex1_dropDown3);

        ArrayAdapter <String> adapterEx1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Exercises);
        adapterEx1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exType[0].setAdapter(adapterEx1);
        exType[0].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(8, 160, 233));
                exercises[0] = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TextView errorText = (TextView) exType[0].getSelectedView();
                errorText.setError("Please Select Exercise");
                errorText.setTextColor(Color.RED); //just to highlight that this is an error
                errorText.setText("Please Select Exercise");
            }
        });

        ArrayAdapter <Integer> adapterSet1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Sets);
        adapterSet1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setsType[0].setAdapter(adapterSet1);
        setsType[0].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(8, 160, 233));
                sets[0] = (Integer) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TextView errorText = (TextView) setsType[0].getSelectedView();
                errorText.setError("Please Select Set");
                errorText.setTextColor(Color.RED); //just to highlight that this is an error
                errorText.setText("Please Select Set");
            }
        });

        ArrayAdapter <String> adapterTime1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Days);
        adapterTime1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeType[0].setAdapter(adapterTime1);
        timeType[0].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(8, 160, 233));
                timePeriod[0] = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TextView errorText = (TextView) timeType[0].getSelectedView();
                errorText.setError("Please Select Time Period");
                errorText.setTextColor(Color.RED); //just to highlight that this is an error
                errorText.setText("Please Select Time Period");
            }
        });

        //Exercise 2
        exType[1] = findViewById(R.id.Ex2_dropDown1);
        setsType[1] = findViewById(R.id.Ex2_dropDown2);
        timeType[1] = findViewById(R.id.Ex2_dropDown3);

        ArrayAdapter <String> adapterEx2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Exercises);
        adapterEx2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exType[1].setAdapter(adapterEx2);
        exType[1].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(8, 160, 233));
                exercises[1] = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TextView errorText = (TextView) exType[1].getSelectedView();
                errorText.setError("Please Select Exercise");
                errorText.setTextColor(Color.RED); //just to highlight that this is an error
                errorText.setText("Please Select Exercise");
            }
        });

        ArrayAdapter <Integer> adapterSet2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Sets);
        adapterSet2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setsType[1].setAdapter(adapterSet2);
        setsType[1].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(8, 160, 233));
                sets[1] = (Integer) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TextView errorText = (TextView) setsType[1].getSelectedView();
                errorText.setError("Please Select Set");
                errorText.setTextColor(Color.RED); //just to highlight that this is an error
                errorText.setText("Please Select Set");
            }
        });

        ArrayAdapter <String> adapterTime2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Days);
        adapterTime2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeType[1].setAdapter(adapterTime2);
        timeType[1].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(8, 160, 233));
                timePeriod[1] = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TextView errorText = (TextView) timeType[1].getSelectedView();
                errorText.setError("Please Select Time Period");
                errorText.setTextColor(Color.RED); //just to highlight that this is an error
                errorText.setText("Please Select Time Period");
            }
        });

        //Exercise 3
        exType[2] = findViewById(R.id.Ex3_dropDown1);
        setsType[2] = findViewById(R.id.Ex3_dropDown2);
        timeType[2] = findViewById(R.id.Ex3_dropDown3);

        ArrayAdapter <String> adapterEx3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Exercises);
        adapterEx3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        exType[2].setAdapter(adapterEx3);
        exType[2].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(8, 160, 233));
                exercises[2] = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TextView errorText = (TextView) exType[2].getSelectedView();
                errorText.setError("Please Select Exercise");
                errorText.setTextColor(Color.RED); //just to highlight that this is an error
                errorText.setText("Please Select Exercise");
            }
        });

        ArrayAdapter <Integer> adapterSet3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Sets);
        adapterSet3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        setsType[2].setAdapter(adapterSet3);
        setsType[2].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(8, 160, 233));
                sets[2] = (Integer) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TextView errorText = (TextView) setsType[2].getSelectedView();
                errorText.setError("Please Select Set");
                errorText.setTextColor(Color.RED); //just to highlight that this is an error
                errorText.setText("Please Select Set");
            }
        });

        ArrayAdapter <String> adapterTime3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Days);
        adapterTime3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        timeType[2].setAdapter(adapterTime3);
        timeType[2].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(8, 160, 233));
                timePeriod[2] = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TextView errorText = (TextView) timeType[2].getSelectedView();
                errorText.setError("Please Select Time Period");
                errorText.setTextColor(Color.RED); //just to highlight that this is an error
                errorText.setText("Please Select Time Period");
            }
        });
    }

    private void addExercise() {
        dbRef = FirebaseDatabase.getInstance().getReference().child("User").child(emailId.replace(".", "_DOT_")).child("Exercises");

        if(!exercises[0].equals("None") && sets[0] != 0 && !timePeriod[0].equals("None")) {
            Exercise user = new Exercise(exercises[0], sets[0], timePeriod[0]);
            dbRef.child("Exercise1").setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                Toast.makeText(TrainerDetailActivity.this, "Exercises 1 is added successfully!", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(TrainerDetailActivity.this, "Exercises 1 is not added successfully!!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(TrainerDetailActivity.this, "Exercises 1 is not added successfully!!",  Toast.LENGTH_SHORT).show();
                    Log.v("Error in data insertion", "" + e);
                }
            });
        }
        if(!exercises[1].equals("None") && sets[1] != 0 && !timePeriod[1].equals("None")) {
            Exercise user = new Exercise(Exercises[1], sets[1], timePeriod[1]);
            dbRef.child("Exercise2").setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                Toast.makeText(TrainerDetailActivity.this, "Exercises 2 is added successfully!", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(TrainerDetailActivity.this, "Exercises 2 is not added successfully!!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(TrainerDetailActivity.this, "Exercises 2 is not added successfully!!",  Toast.LENGTH_SHORT).show();
                    Log.v("Error in data insertion", "" + e);
                }
            });
        }
        if(!exercises[2].equals("None") && sets[2] != 0 && !timePeriod[2].equals("None")) {
            Exercise user = new Exercise(Exercises[2], sets[2], timePeriod[2]);
            dbRef.child("Exercise3").setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                Toast.makeText(TrainerDetailActivity.this, "Exercises 3 is added successfully!", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(TrainerDetailActivity.this, "Exercises 3 is not added successfully!!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(TrainerDetailActivity.this, "Exercises 3 is not added successfully!!",  Toast.LENGTH_SHORT).show();
                    Log.v("Error in data insertion", "" + e);
                }
            });
        }
    }
}
