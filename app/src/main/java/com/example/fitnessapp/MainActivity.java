package com.example.fitnessapp;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.net.Uri;
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
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

public class MainActivity extends AppCompatActivity {

    public EditText emailId, password, name, bloodGroup;
    private Spinner type;
    private String categorySelected;
    String[] category = new String[]{"Trainer", "Food Specialist", "User"};
    Button btnSignUp;
    TextView txSignIn, fileUpload;
    private FirebaseAuth mFireBaseAuth;
    private Uri fileUri;                                      //Uri are actually the Urls that are meant for local storage
    private Member member;
    private ProgressDialog progressDialog;
    DatabaseReference dbRef;
    boolean registerOnly;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Initialization of variables
        registerOnly = getIntent().getBooleanExtra("register", false);
        mFireBaseAuth = FirebaseAuth.getInstance();
        member = new Member();

        emailId = findViewById(R.id.email);
        password = findViewById(R.id.password);
        name = findViewById(R.id.name);
        bloodGroup = findViewById(R.id.bloodGroup);
        txSignIn = findViewById(R.id.textView);
        fileUpload = findViewById(R.id.selectFile);
        btnSignUp = findViewById(R.id.button);

        type = findViewById(R.id.dropDown);
        ArrayAdapter <String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_spinner_dropdown_item, category);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        type.setAdapter(adapter);

        type.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor(Color.rgb(8, 160 , 233));
                categorySelected = (String) parent.getItemAtPosition(position);
                if(categorySelected == "User") {
                    fileUpload.setVisibility(View.VISIBLE);
                    bloodGroup.setVisibility(View.VISIBLE);
                }
                else {
                    fileUpload.setVisibility(View.INVISIBLE);
                    bloodGroup.setVisibility(View.INVISIBLE);
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                TextView errorText = (TextView) type.getSelectedView();
                errorText.setError("Please Select Category");
                errorText.setTextColor(Color.RED); //just to highlight that this is an error
                errorText.setText("Please Select Category");
                fileUpload.setVisibility(View.INVISIBLE);
            }
        });

        btnSignUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                formValidation();
            }
        });

        txSignIn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, LoginActivity.class));
            }
        });

        fileUpload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED)
                    selectFile();
                else
                    ActivityCompat.requestPermissions(MainActivity.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 9);

            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        //check whether user has granted permission or not
        if(requestCode == 9 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            selectFile();
        }
        else
            Toast.makeText(MainActivity.this, "Please provide permission to upload the file!", Toast.LENGTH_SHORT).show();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Check whether user has selected the file or not
        if(requestCode == 86 && resultCode == RESULT_OK && data != null) {
            fileUri = data.getData();
            fileUpload.setText(data.getData().getLastPathSegment());
        }
        else
            Toast.makeText(MainActivity.this, "Please Select a file!", Toast.LENGTH_SHORT).show();
    }

    private void selectFile() {
        Intent i = new Intent();
        i.setType("image/jpeg");
        i.setAction(Intent.ACTION_GET_CONTENT);

        startActivityForResult(i, 86);
    }

    private void uploadFile() {
        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progressDialog.setTitle("Uploading File");
        progressDialog.setProgress(0);
        progressDialog.show();
        dbRef = FirebaseDatabase.getInstance().getReference().child("User");

        final String fileName = System.currentTimeMillis() + "";
        final StorageReference stRef = FirebaseStorage.getInstance().getReference().child("Reports").child(fileName);

        stRef.putFile(fileUri)
                .addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onSuccess(UploadTask.TaskSnapshot task) {
                        stRef.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                            @Override
                            public void onSuccess(Uri uri) {
                                String url = uri.toString();              //Medical Report Path

                                member.setMedicalReport(url);
                                member.setBloodGroup(bloodGroup.getText().toString().trim());
                                member.setName(name.getText().toString().trim());
                                member.setEmail(emailId.getText().toString().trim());
                                member.setCategory(categorySelected);
                                dbRef.child(emailId.getText().toString().replace(".", "_DOT_")).setValue(member)    //replacing '.' with _DOT_, because firebase does'nt support '.'
                                        .addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if(task.isSuccessful()) {
                                                    Toast.makeText(MainActivity.this, "User is added successfully!", Toast.LENGTH_SHORT).show();
                                                    if(!registerOnly) {
                                                        Intent temp = new Intent(MainActivity.this, UserActivity.class);
                                                        temp.putExtra("category", categorySelected);
                                                        startActivity(temp);
                                                    }
                                                    else
                                                        startActivity(new Intent(MainActivity.this, AdminActivity.class));
                                                }
                                                else
                                                    Toast.makeText(MainActivity.this, "File is uploaded but data is not inserted!", Toast.LENGTH_SHORT).show();
                                            }
                                        }).addOnFailureListener(new OnFailureListener() {
                                    @Override
                                    public void onFailure(@NonNull Exception e) {
                                        Toast.makeText(MainActivity.this, "File is uploaded but data is not inserted! ",  Toast.LENGTH_SHORT).show();
                                        Log.v("Error in data insertion", "" + e);
                                    }
                                });
                            }
                        });
                    }

                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        Toast.makeText(MainActivity.this, "File is not uploaded successfully! ",  Toast.LENGTH_SHORT).show();
                        Log.v("Error in file uploading", "" + e);
                    }
                }).addOnProgressListener(new OnProgressListener<UploadTask.TaskSnapshot>() {
                    @Override
                    public void onProgress(@NonNull UploadTask.TaskSnapshot taskSnapshot) {
                        //Track the progress
                        int currentProgress = (int) (100*taskSnapshot.getBytesTransferred()/taskSnapshot.getTotalByteCount());
                        progressDialog.setProgress(currentProgress);
                    }
                });
    }

    private void checkCategory() {
        if(categorySelected == "User") {
            if(fileUri != null)
                uploadFile();
            else
            {
                fileUpload.setError("Please Select a File");
                fileUpload.requestFocus();
            }
        }
        else if (categorySelected == "Trainer"){
            dbRef = FirebaseDatabase.getInstance().getReference().child("Trainer");
            //member.setBloodGroup(bloodGroup.getText().toString().trim());
            member.setName(name.getText().toString().trim());
            member.setEmail(emailId.getText().toString().trim());
            member.setCategory(categorySelected);
            dbRef.child(emailId.getText().toString().replace(".", "_DOT_")).setValue(member)    //replacing '.' with _DOT_, because firebase does'nt support '.'
                    .addOnCompleteListener(new OnCompleteListener <Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Trainer is added successfully!", Toast.LENGTH_SHORT).show();
                                if(!registerOnly) {
                                    Intent temp = new Intent(MainActivity.this, TrainerAndFoodActivity.class);
                                    temp.putExtra("category", categorySelected);
                                    startActivity(temp);
                                }
                                else
                                    startActivity(new Intent(MainActivity.this, AdminActivity.class));
                            }
                            else
                                Toast.makeText(MainActivity.this, "Trainer is not added successfully!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Trainer is not added successfully! ",  Toast.LENGTH_SHORT).show();
                    Log.v("Error in data insertion", "" + e);
                }
            });
        }
        else if (categorySelected == "Food Specialist"){
            dbRef = FirebaseDatabase.getInstance().getReference().child("Food Specialist");
            //member.setBloodGroup(bloodGroup.getText().toString().trim());
            member.setName(name.getText().toString().trim());
            member.setEmail(emailId.getText().toString().trim());
            member.setCategory(categorySelected);
            dbRef.child(emailId.getText().toString().replace(".", "_DOT_")).setValue(member)    //replacing '.' with _DOT_, because firebase does'nt support '.'
                    .addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            if(task.isSuccessful()) {
                                Toast.makeText(MainActivity.this, "Food Specialist is added successfully!", Toast.LENGTH_SHORT).show();
                                if(!registerOnly) {
                                    Intent temp = new Intent(MainActivity.this, TrainerAndFoodActivity.class);
                                    temp.putExtra("category", categorySelected);
                                    startActivity(temp);
                                }
                                else
                                    startActivity(new Intent(MainActivity.this, AdminActivity.class));
                            }
                            else
                                Toast.makeText(MainActivity.this, "Food Specialist is not added successfully!", Toast.LENGTH_SHORT).show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(MainActivity.this, "Food Specialist is not added successfully!",  Toast.LENGTH_SHORT).show();
                    Log.v("Error in data insertion", "" + e);
                }
            });
        }
        else {
            Toast.makeText(MainActivity.this, "Wrong Category Selected!", Toast.LENGTH_SHORT).show();
        }
    }

    private void formValidation() {
        String email = emailId.getText().toString();
        String pass = password.getText().toString();
        String userName = name.getText().toString();
        String blood = bloodGroup.getText().toString();

        if (email.isEmpty()) {
            emailId.setError("Please Enter Email");
            emailId.requestFocus();
        }
        else if (pass.isEmpty()) {
            password.setError("Please Enter your password");
            password.requestFocus();
        }
        if (userName.isEmpty()) {
            name.setError("Please Enter your Name");
            name.requestFocus();
        }
        else if (blood.isEmpty() && categorySelected == "User") {
            bloodGroup.setError("Please Enter your blood group");
            bloodGroup.requestFocus();
        }
        else if(fileUri == null && categorySelected == "User") {
            fileUpload.setError("Please Select a File");
            fileUpload.requestFocus();
        }
        else if (!(pass.isEmpty() && email.isEmpty())) {
            addUser(email, pass);
        }
        else
            Toast.makeText(MainActivity.this, "Error Occurred", Toast.LENGTH_SHORT).show();

    }

    private void addUser(String email, String pass) {
        mFireBaseAuth.createUserWithEmailAndPassword(email, pass).addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()){
                    checkCategory();

                    return;
                }
                Log.w("SignIn", "createUserWithEmail:failure", task.getException());
                Toast.makeText(MainActivity.this, "Sign Up Unsuccessful, Please Try Again!", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
