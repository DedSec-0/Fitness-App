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
import com.squareup.picasso.Picasso;

public class RecommendedExActivity extends AppCompatActivity {

    ImageView header;
    TextView []exType = new TextView[3];
    TextView []sets = new TextView[3];
    TextView []period = new TextView[3];
    String emailId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_recommended_ex);

        emailId = getIntent().getStringExtra("Email");
        header = findViewById(R.id.header);
        exType[0] = findViewById(R.id.exHeaderName1);
        exType[1] = findViewById(R.id.exHeaderName2);
        exType[2] = findViewById(R.id.exHeaderName3);
        sets[0] = findViewById(R.id.setsHeaderName1);
        sets[1] = findViewById(R.id.setsHeaderName2);
        sets[2] = findViewById(R.id.setsHeaderName3);
        period[0] = findViewById(R.id.daysHeaderName1);
        period[1] = findViewById(R.id.daysHeaderName2);
        period[2] = findViewById(R.id.daysHeaderName3);
        setUpToolBar();
        getExercises();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(RecommendedExActivity.this, LoginActivity.class));

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

                Intent temp = new Intent(RecommendedExActivity.this, UserActivity.class);

                if(getSupportActionBar() != null)
                    getSupportActionBar().setDisplayHomeAsUpEnabled(true);
                temp.putExtra("Email", emailId);
                startActivity(temp);
            }
        });
    }

    private void getExercises() {
        DatabaseReference dbRef = FirebaseDatabase.getInstance().getReference().child("User").child(emailId.replace(".", "_DOT_"));

        dbRef.child("Exercises").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                Exercise []data = new Exercise[3];
                data[0] = dataSnapshot.child("Exercise1").getValue(Exercise.class);
                data[1] = dataSnapshot.child("Exercise2").getValue(Exercise.class);
                data[2] = dataSnapshot.child("Exercise3").getValue(Exercise.class);

                for (int i = 0; i < 3; i++) {
                    if(data[i] != null){
                        exType[i].setText(data[i].getExercise());
                        sets[i].setText(data[i].getSets() + " Sets");
                        period[i].setText(data[i].getTimePeriod());
                    }
                    else {
                        exType[i].setText("Not Assigned");
                        sets[i].setText("");
                        period[i].setText("");
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {}
        });
    }
}
