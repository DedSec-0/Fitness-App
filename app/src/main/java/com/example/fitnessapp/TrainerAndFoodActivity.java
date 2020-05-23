package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Point;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import androidx.appcompat.widget.Toolbar;

import com.firebase.ui.database.FirebaseListOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.firebase.ui.database.FirebaseListAdapter;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class TrainerAndFoodActivity extends AppCompatActivity {

    ListView listView;
    DatabaseReference dbRef;
    FirebaseListOptions <User> list;
    FirebaseListAdapter adapter;
    TextView name, email, bloodGroup;
    ImageView medicalReport;
    ImageView enlargedMedicalReport;
    String categorySelected;
    ArrayList <User> user = new ArrayList<>();
    private Animator currentAnimator;
    private int shortAnimationDuration;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_trainer_food);

        dbRef = FirebaseDatabase.getInstance().getReference().child("User");
        categorySelected = getIntent().getStringExtra("category");

        Toolbar toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle("Fitness App");
        toolbar.setSubtitle(categorySelected);
        setSupportActionBar(toolbar);
        list = new FirebaseListOptions.Builder<User>()
                .setLayout(R.layout.user_info)
                .setQuery(dbRef, User.class)
                .build();

        adapter = new FirebaseListAdapter(list) {
            @Override
            protected void populateView(@NonNull View v, @NonNull Object model, final int position) {
                name = v.findViewById(R.id.nameId);
                email = v.findViewById(R.id.emailId);
                bloodGroup = v.findViewById(R.id.bloodGroupId);
                medicalReport = v.findViewById(R.id.imageReport);
                enlargedMedicalReport = v.findViewById(R.id.enlargedImageReport);

                user.add(new User());
                user.set(position,(User) model);
                name.setText("Name: " + user.get(position).getName());
                email.setText("Email: " + user.get(position).getEmail());
                bloodGroup.setText("Blood Group: " + user.get(position).getBloodGroup());
                Picasso.get().load(user.get(position).getMedicalReport()).resize(480, 500).into(medicalReport);

                medicalReport.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        zoomImageFromThumb(medicalReport, user.get(position).getMedicalReport());
                    }
                });
            }
        };

        listView = findViewById(R.id.main_container);
        listView.setAdapter(adapter);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent temp;

                if(categorySelected.equals("Trainer"))
                    temp = new Intent(TrainerAndFoodActivity.this, TrainerDetailActivity.class);
                else
                    temp = new Intent(TrainerAndFoodActivity.this, FoodDetailActivity.class);
                temp.putExtra("Email", user.get(position).getEmail());
                startActivity(temp);
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        adapter.startListening();
    }

    @Override
    protected void onStop() {
        super.onStop();
        adapter.stopListening();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        FirebaseAuth.getInstance().signOut();
        startActivity(new Intent(TrainerAndFoodActivity.this, LoginActivity.class));

        return super.onOptionsItemSelected(item);
    }

    private void zoomImageFromThumb(final View thumbView, String imageUrl) {
        // If there's an animation in progress, cancel it
        // immediately and proceed with this one.
        if (currentAnimator != null) {
            currentAnimator.cancel();
        }

        // Load the high-resolution "zoomed-in" image.
//        final ImageView expandedImageView = (ImageView) findViewById(
//                R.id.expanded_image);
        Picasso.get().load(imageUrl).resize(1500, 2300).into(enlargedMedicalReport);

        // Calculate the starting and ending bounds for the zoomed-in image.
        // This step involves lots of math. Yay, math.
        final Rect startBounds = new Rect();
        final Rect finalBounds = new Rect();
        final Point globalOffset = new Point();

        // The start bounds are the global visible rectangle of the thumbnail,
        // and the final bounds are the global visible rectangle of the container
        // view. Also set the container view's offset as the origin for the
        // bounds, since that's the origin for the positioning animation
        // properties (X, Y).
        thumbView.getGlobalVisibleRect(startBounds);
        findViewById(R.id.layout_main)
                .getGlobalVisibleRect(finalBounds, globalOffset);
        startBounds.offset(-globalOffset.x, -globalOffset.y);
        finalBounds.offset(-globalOffset.x, -globalOffset.y);

        // Adjust the start bounds to be the same aspect ratio as the final
        // bounds using the "center crop" technique. This prevents undesirable
        // stretching during the animation. Also calculate the start scaling
        // factor (the end scaling factor is always 1.0).
        float startScale;
        if ((float) finalBounds.width() / finalBounds.height()
                > (float) startBounds.width() / startBounds.height()) {
            // Extend start bounds horizontally
            startScale = (float) startBounds.height() / finalBounds.height();
            float startWidth = startScale * finalBounds.width();
            float deltaWidth = (startWidth - startBounds.width()) / 2;
            startBounds.left -= deltaWidth;
            startBounds.right += deltaWidth;
        } else {
            // Extend start bounds vertically
            startScale = (float) startBounds.width() / finalBounds.width();
            float startHeight = startScale * finalBounds.height();
            float deltaHeight = (startHeight - startBounds.height()) / 2;
            startBounds.top -= deltaHeight;
            startBounds.bottom += deltaHeight;
        }

        // Hide the thumbnail and show the zoomed-in view. When the animation
        // begins, it will position the zoomed-in view in the place of the
        // thumbnail.
        thumbView.setAlpha(0f);
        enlargedMedicalReport.setVisibility(View.VISIBLE);

        // Set the pivot point for SCALE_X and SCALE_Y transformations
        // to the top-left corner of the zoomed-in view (the default
        // is the center of the view).
        enlargedMedicalReport.setPivotX(0f);
        enlargedMedicalReport.setPivotY(0f);

        // Construct and run the parallel animation of the four translation and
        // scale properties (X, Y, SCALE_X, and SCALE_Y).
        AnimatorSet set = new AnimatorSet();
        set
                .play(ObjectAnimator.ofFloat(enlargedMedicalReport, View.X,
                        startBounds.left, finalBounds.left))
                .with(ObjectAnimator.ofFloat(enlargedMedicalReport, View.Y,
                        startBounds.top, finalBounds.top))
                .with(ObjectAnimator.ofFloat(enlargedMedicalReport, View.SCALE_X,
                        startScale, 1f))
                .with(ObjectAnimator.ofFloat(enlargedMedicalReport,
                        View.SCALE_Y, startScale, 1f));
        set.setDuration(shortAnimationDuration);
        set.setInterpolator(new DecelerateInterpolator());
        set.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                currentAnimator = null;
            }

            @Override
            public void onAnimationCancel(Animator animation) {
                currentAnimator = null;
            }
        });
        set.start();
        currentAnimator = set;

        // Upon clicking the zoomed-in image, it should zoom back down
        // to the original bounds and show the thumbnail instead of
        // the expanded image.
        final float startScaleFinal = startScale;
        enlargedMedicalReport.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (currentAnimator != null) {
                    currentAnimator.cancel();
                }

                // Animate the four positioning/sizing properties in parallel,
                // back to their original values.
                AnimatorSet set = new AnimatorSet();
                set.play(ObjectAnimator
                        .ofFloat(enlargedMedicalReport, View.X, startBounds.left))
                        .with(ObjectAnimator
                                .ofFloat(enlargedMedicalReport,
                                        View.Y,startBounds.top))
                        .with(ObjectAnimator
                                .ofFloat(enlargedMedicalReport,
                                        View.SCALE_X, startScaleFinal))
                        .with(ObjectAnimator
                                .ofFloat(enlargedMedicalReport,
                                        View.SCALE_Y, startScaleFinal));
                set.setDuration(shortAnimationDuration);
                set.setInterpolator(new DecelerateInterpolator());
                set.addListener(new AnimatorListenerAdapter() {
                    @Override
                    public void onAnimationEnd(Animator animation) {
                        thumbView.setAlpha(1f);
                        enlargedMedicalReport.setVisibility(View.GONE);
                        currentAnimator = null;
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                        thumbView.setAlpha(1f);
                        enlargedMedicalReport.setVisibility(View.GONE);
                        currentAnimator = null;
                    }
                });
                set.start();
                currentAnimator = set;
            }
        });
    }
}
