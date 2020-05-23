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

public class FoodDetailActivity extends AppCompatActivity {

    String emailId;
    Button btnAdd;
    Spinner[] mlType = new Spinner[3], timeType = new Spinner[3];
    String[] meal = new String[3], timePeriod = new String[3];
    String[] Meals = new String[]{"None", "Vegetables", "Boiled Potatoes", "Eggs", "Yogurt", "Beans", "Meat"};
    String[] Period = new String[]{"None", "Weekly", "Monthly"};
    DatabaseReference dbRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_food_detail);

        emailId = getIntent().getStringExtra("Email");
        btnAdd = findViewById(R.id.addMLBtn);

        setUpToolBar();
        addListenersOnSpinners();
        btnAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                addFoodPlan();
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
        startActivity(new Intent(FoodDetailActivity.this, LoginActivity.class));

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
                Intent temp = new Intent(FoodDetailActivity.this, TrainerAndFoodActivity.class);

                if(getSupportActionBar() != null)
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                temp.putExtra("category", "Food Specialist");
                startActivity(temp);
            }
        });
    }

    private void addListenersOnSpinners() {
        //Meal 1
        mlType[0] = findViewById(R.id.ML1_dropDown1);
        timeType[0] = findViewById(R.id.ML1_dropDown2);

        ArrayAdapter<String> adapterML1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Meals);
        adapterML1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mlType[0].setAdapter(adapterML1);
        mlType[0].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(8, 160, 233));
                meal[0] = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TextView errorText = (TextView) mlType[0].getSelectedView();
                errorText.setError("Please Select Exercise");
                errorText.setTextColor(Color.RED); //just to highlight that this is an error
                errorText.setText("Please Select Exercise");
            }
        });

        ArrayAdapter <String> adapterTime1 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Period);
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

        //Meal 2
        mlType[1] = findViewById(R.id.ML2_dropDown1);
        timeType[1] = findViewById(R.id.ML2_dropDown2);

        ArrayAdapter <String> adapterML2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Meals);
        adapterML2.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mlType[1].setAdapter(adapterML2);
        mlType[1].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(8, 160, 233));
                meal[1] = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TextView errorText = (TextView) mlType[1].getSelectedView();
                errorText.setError("Please Select Exercise");
                errorText.setTextColor(Color.RED); //just to highlight that this is an error
                errorText.setText("Please Select Exercise");
            }
        });

        ArrayAdapter <String> adapterTime2 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Period);
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

        //Meal 3
        mlType[2] = findViewById(R.id.ML3_dropDown1);
        timeType[2] = findViewById(R.id.ML3_dropDown2);

        ArrayAdapter <String> adapterEx3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Meals);
        adapterEx3.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        mlType[2].setAdapter(adapterEx3);
        mlType[2].setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(8, 160, 233));
                meal[2] = (String) parent.getItemAtPosition(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TextView errorText = (TextView) mlType[2].getSelectedView();
                errorText.setError("Please Select Exercise");
                errorText.setTextColor(Color.RED); //just to highlight that this is an error
                errorText.setText("Please Select Exercise");
            }
        });

        ArrayAdapter <String> adapterTime3 = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, Period);
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

    private void addFoodPlan() {
        dbRef = FirebaseDatabase.getInstance().getReference().child("User").child(emailId.replace(".", "_DOT_")).child("Diet Plans");

        if(!meal[0].equals("None") && !timePeriod[0].equals("None")) {
            Meal user = new Meal(meal[0], timePeriod[0]);
            dbRef.child("DietPlan1").setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                Toast.makeText(FoodDetailActivity.this, "DietPlan 1 is added successfully!", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(FoodDetailActivity.this, "DietPlan 1 is not added successfully!!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FoodDetailActivity.this, "DietPlan 1 is not added successfully!!",  Toast.LENGTH_SHORT).show();
                    Log.v("Error in data insertion", "" + e);
                }
            });
        }
        if(!meal[1].equals("None") && !timePeriod[1].equals("None")) {
            Meal user = new Meal(meal[1], timePeriod[1]);
            dbRef.child("DietPlan2").setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                Toast.makeText(FoodDetailActivity.this, "DietPlan 2 is added successfully!", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(FoodDetailActivity.this, "DietPlan 2 is not added successfully!!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FoodDetailActivity.this, "DietPlan 2 is not added successfully!!",  Toast.LENGTH_SHORT).show();
                    Log.v("Error in data insertion", "" + e);
                }
            });
        }
        if(!meal[2].equals("None") && !timePeriod[2].equals("None")) {
            Meal user = new Meal(meal[2], timePeriod[2]);
            dbRef.child("DietPlan3").setValue(user)
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful())
                                Toast.makeText(FoodDetailActivity.this, "DietPlan 3 is added successfully!", Toast.LENGTH_SHORT).show();
                            else
                                Toast.makeText(FoodDetailActivity.this, "DietPlan 3 is not added successfully!!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(FoodDetailActivity.this, "DietPlan 3 is not added successfully!!",  Toast.LENGTH_SHORT).show();
                    Log.v("Error in data insertion", "" + e);
                }
            });
        }
    }
}
