package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class DietPlanActivity extends AppCompatActivity {

    ImageView header;
    TextView[]exType = new TextView[3];
    TextView []period = new TextView[3];
    String emailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_diet_plan);

        emailId = getIntent().getStringExtra("Email");
        header = findViewById(R.id.header);
        exType[0] = findViewById(R.id.mealHeaderName1);
        exType[1] = findViewById(R.id.mealHeaderName2);
        exType[2] = findViewById(R.id.mealHeaderName3);
        period[0] = findViewById(R.id.periodHeaderName1);
        period[1] = findViewById(R.id.periodHeaderName2);
        period[2] = findViewById(R.id.periodHeaderName3);
        setUpToolBar();
        getDietPlans();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(DietPlanActivity.this, LoginActivity.class));

        return super.onOptionsItemSelected(item);
    }

    private void setUpToolBar() {
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Fitness App");
        toolbar.setSubtitle("User");
        setSupportActionBar(toolbar);

        toolbar.setNavigationIcon(getResources().getDrawable(R.mipmap.ic_action_back));
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent temp = new Intent(DietPlanActivity.this, UserActivity.class);

                if(getSupportActionBar() != null)
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                temp.putExtra("Email", emailId);
                startActivity(temp);
            }
        });
    }

    private void getDietPlans() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("User").child(emailId.replace(".", "_DOT_"));

        dbRef.child("Diet Plans").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Meal []data = new Meal[3];
                data[0] = dataSnapshot.child("DietPlan1").getValue(Meal.class);
                data[1] = dataSnapshot.child("DietPlan2").getValue(Meal.class);
                data[2] = dataSnapshot.child("DietPlan3").getValue(Meal.class);

                for (int i = 0; i < 3; i++) {
                    if(data[i] != null){
                        exType[i].setText(data[i].getMeal());
                        period[i].setText(data[i].getTimePeriod());
                    }
                    else {
                        exType[i].setText("Not Assigned");
                        period[i].setText("");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}
