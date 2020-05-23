package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.cardview.widget.CardView;

import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Adapter;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.GridLayout;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

public class UserActivity extends AppCompatActivity {

    GridLayout gridView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_user);

        gridView = findViewById(R.id.user_container);
        setSingleEvent();

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Fitness App");
        toolbar.setSubtitle("User");
        setSupportActionBar(toolbar);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(UserActivity.this, LoginActivity.class));

        return super.onOptionsItemSelected(item);
    }

    private void setSingleEvent() {
        final Intent []intent = new Intent[3];
        intent[0] = new Intent(UserActivity.this, RecommendedExActivity.class);
        intent[1] = new Intent(UserActivity.this, DietPlanActivity.class);
        intent[2] = new Intent(UserActivity.this, ProfileActivity.class);

        for (int i = 0; i < gridView.getChildCount(); i++) {
            CardView cardView = (CardView) gridView.getChildAt(i);
            final int index = i;

            cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    intent[index].putExtra("Email", getIntent().getStringExtra("Email"));
                    startActivity(intent[index]);
                }
            });
        }
    }
}
