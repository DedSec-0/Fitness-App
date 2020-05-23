package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class AdminActivity extends AppCompatActivity {

    ListView listView;
    ArrayList <User> trainerList = new ArrayList<>();
    ArrayList <User> foodList = new ArrayList<>();
    ArrayList <User> userList = new ArrayList<>();
    ArrayList <Object> list = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin);

        final FirebaseDatabase ref = FirebaseDatabase.getInstance();
        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Fitness App");
        toolbar.setSubtitle("Admin Panel");
        setSupportActionBar(toolbar);
        listView = findViewById(R.id.main_container);

        getUsers(ref);
        listView.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
            @Override
            public boolean onItemLongClick(final AdapterView<?> parent, View view, final int position, long id) {
                new AlertDialog.Builder(view.getContext())
                        .setTitle("Delete entry")
                        .setMessage("Are you sure you want to delete this entry?")
                        .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int which) {
                                final User user = (User) parent.getAdapter().getItem(position);
                                final DatabaseReference dbref = ref.getReference().child(user.getCategory());

                                dbref.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                                        dbref.child(user.getEmail().replace(".", "_DOT_")).removeValue();
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError databaseError) {

                                    }
                                });
                            }
                        })
                        .setNegativeButton(android.R.string.no, null)
                        .setIcon(android.R.drawable.ic_dialog_alert)
                        .show();
                return true;
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
        startActivity(new Intent(this, LoginActivity.class));

        return super.onOptionsItemSelected(item);
    }

    private void getUsers(final FirebaseDatabase ref) {
        DatabaseReference dbRef = ref.getReference().child("User");
        final User[] user = {new User()};

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                userList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    user[0] = ds.getValue(User.class);
                    userList.add(user[0]);
                }

                getTrainers(ref);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }

    private void getTrainers(final FirebaseDatabase ref) {
        DatabaseReference dbRef = ref.getReference().child("Trainer");
        final User[] user = {new User()};

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                trainerList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    user[0] = ds.getValue(User.class);
                    trainerList.add(user[0]);
                }

                getFoodSpecialist(ref);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }

    private void getFoodSpecialist(final FirebaseDatabase ref) {
        DatabaseReference dbRef = ref.getReference().child("Food Specialist");
        final User[] user = {new User()};

        dbRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                foodList.clear();
                for (DataSnapshot ds: dataSnapshot.getChildren()) {
                    user[0] = ds.getValue(User.class);
                    foodList.add(user[0]);
                }

                list.clear();
                list.add("Trainer");
                list.addAll(trainerList);
                list.add("Food Specialist");
                list.addAll(foodList);
                list.add("User");
                list.addAll(userList);

                listView.setAdapter(new AdminAdapter(AdminActivity.this, list));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
