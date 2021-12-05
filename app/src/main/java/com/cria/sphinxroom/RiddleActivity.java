package com.cria.sphinxroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import novasclasses.MyRiddleProfile;

public class RiddleActivity extends AppCompatActivity {
    private TextView gameName;
    private TextView gameAuthor;
    private TextView gameAdded;
    private TextView gameCompleted;
    private TextView gameDesc;
    private TextView gameFinished;
    private TextView gameNoPhases;

    private Button btnPhase1;
    private Button btnPhase2;
    private Button btnPhase3;
    private Button btnPhase4;
    private Button btnPhase5;
    private Button btnPhase6;
    private Button btnPhase7;
    private Button btnPhase8;
    private Button btnPhase9;
    private Button btnPhase10;
    private ImageButton imgBtnCloseMainRiddle;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;

    private MyRiddleProfile myRiddleProfile;

    private String status;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riddle);
        //Firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //Declarar elementos
        gameName = findViewById(R.id.txtGameNameMainRiddle);
        gameAuthor = findViewById(R.id.txtDateMainRiddles);
        gameAdded = findViewById(R.id.txtAddedMainRiddles);
        gameCompleted = findViewById(R.id.txtCompletedMainRiddles);
        gameDesc = findViewById(R.id.txtDescMainRiddle);
        gameFinished = findViewById(R.id.txtFinishedMainRiddle);
        gameNoPhases = findViewById(R.id.txtNoPhasesMainRiddle);

        imgBtnCloseMainRiddle = findViewById(R.id.imgBtnOutMainRiddle);
        btnPhase1 = findViewById(R.id.btnFase1MainRiddles);
        btnPhase2 = findViewById(R.id.btnFase2MainRiddles);
        btnPhase3 = findViewById(R.id.btnFase3MainRiddles);
        btnPhase4 = findViewById(R.id.btnFase4MainRiddles);
        btnPhase5 = findViewById(R.id.btnFase5MainRiddles);
        btnPhase6 = findViewById(R.id.btnFase6MainRiddles);
        btnPhase7 = findViewById(R.id.btnFase7MainRiddles);
        btnPhase8 = findViewById(R.id.btnFase8MainRiddles);
        btnPhase9 = findViewById(R.id.btnFase9MainRiddles);
        btnPhase10 = findViewById(R.id.btnFase10MainRiddles);

        //set user Riddle Profile
        myRiddleProfile = new MyRiddleProfile();
        myRiddleProfile.setName(getIntent().getStringExtra("Name"));
        myRiddleProfile.setAuthor(getIntent().getStringExtra("Author"));
        myRiddleProfile.setAdded(getIntent().getStringExtra("Added"));
        myRiddleProfile.setCompleted(getIntent().getStringExtra("Completed"));
        myRiddleProfile.setDate(getIntent().getStringExtra("Date"));
        myRiddleProfile.setDesc(getIntent().getStringExtra("Desc"));
        myRiddleProfile.setFases(getIntent().getStringExtra("Fases"));
        myRiddleProfile.setId(getIntent().getStringExtra("Id"));
        status = getIntent().getStringExtra("Finalized");

        //set texts

        if (myRiddleProfile.getFases().equals(myRiddleProfile.getCompleted())){
            gameFinished.setVisibility(View.VISIBLE);
        }
        gameName.setText(myRiddleProfile.getName());
        gameDesc.setText(myRiddleProfile.getDesc());
        mDatabase.child("users").child(myRiddleProfile.getAuthor()).child("username")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String strGameAuthor = getResources().getString(R.string.autor)+getResources().getString(R.string.space)+snapshot.getValue();
                gameAuthor.setText(strGameAuthor);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mDatabase.child("games").child(myRiddleProfile.getId()).child("added")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String strGameAdded = getResources().getString(R.string.adicionados)+getResources().getString(R.string.space)+snapshot.getValue();
                gameAdded.setText(strGameAdded);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        mDatabase.child("games").child(myRiddleProfile.getId()).child("completed")
                .addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String strGameCompleted = getResources().getString(R.string.completados)+getResources().getString(R.string.space)+snapshot.getValue();
                gameCompleted.setText(strGameCompleted);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //set Buttons Visibility
        Integer phasesCompleted = Integer.parseInt(myRiddleProfile.getCompleted());
        for (int i = 0; i<=phasesCompleted; i++){
            setButtonVisivility(i);
        }

        //set Buttons Clicks
            imgBtnCloseMainRiddle.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    finish();
                }
            });
            btnPhase1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goPhase(myRiddleProfile.getId(), "1");
                }
            });
            btnPhase2.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goPhase(myRiddleProfile.getId(), "2");

                }
            });
            btnPhase3.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goPhase(myRiddleProfile.getId(), "3");
                }
            });
            btnPhase4.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goPhase(myRiddleProfile.getId(), "4");
                }
            });
            btnPhase5.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goPhase(myRiddleProfile.getId(), "5");
                }
            });
            btnPhase6.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goPhase(myRiddleProfile.getId(), "6");
                }
            });
            btnPhase7.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goPhase(myRiddleProfile.getId(), "7");
                }
            });
            btnPhase8.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goPhase(myRiddleProfile.getId(), "8");
                }
            });
            btnPhase9.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    goPhase(myRiddleProfile.getId(), "9");
                }
            });
            btnPhase10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                goPhase(myRiddleProfile.getId(), "10");
            }
            });


    }

    private void goPhase(String id, String phase) {
        Intent intent = new Intent(getApplicationContext(), RiddlePhase.class);
        intent.putExtra("Phase", phase);
        intent.putExtra("Id", id);
        intent.putExtra("Total", myRiddleProfile.getFases());
        intent.putExtra("Finalized", status);
        startActivity(intent);
        finish();
    }

    private void setButtonVisivility(int i) {
        switch (i){
            case 0:
                gameNoPhases.setVisibility(View.GONE);
                btnPhase1.setVisibility(View.VISIBLE);
                break;
            case 1:
                if (Integer.parseInt(myRiddleProfile.getFases())>1){
                    btnPhase2.setVisibility(View.VISIBLE);
                }

                break;
            case 2:
                if (Integer.parseInt(myRiddleProfile.getFases())>2){
                    btnPhase3.setVisibility(View.VISIBLE);
                }
                break;
            case 3:
                if (Integer.parseInt(myRiddleProfile.getFases())>3){
                    btnPhase4.setVisibility(View.VISIBLE);
                }
                break;
            case 4:
                if (Integer.parseInt(myRiddleProfile.getFases())>4){
                    btnPhase5.setVisibility(View.VISIBLE);
                }
                break;
            case 5:
                if (Integer.parseInt(myRiddleProfile.getFases())>5){
                    btnPhase6.setVisibility(View.VISIBLE);
                }
                break;
            case 6:
                if (Integer.parseInt(myRiddleProfile.getFases())>6){
                    btnPhase7.setVisibility(View.VISIBLE);
                }
                break;
            case 7:
                if (Integer.parseInt(myRiddleProfile.getFases())>7){
                    btnPhase8.setVisibility(View.VISIBLE);
                }
                break;
            case 8:
                if (Integer.parseInt(myRiddleProfile.getFases())>8){
                    btnPhase9.setVisibility(View.VISIBLE);
                }
                break;
            case 9:
                if (Integer.parseInt(myRiddleProfile.getFases())>9){
                    btnPhase10.setVisibility(View.VISIBLE);
                }
                break;

        }
    }
}
