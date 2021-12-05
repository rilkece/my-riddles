package com.cria.sphinxroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.wajahatkarim3.easyflipview.EasyFlipView;

import java.util.Objects;
import java.util.Random;

import novasclasses.MyPhaseProfile;
import stream.customalert.CustomAlertDialogue;

public class RiddlePhase extends AppCompatActivity {
    private TextView phaseName;
    private ImageButton imgBtnClosePhase;
    private EasyFlipView easyFlipView;
    private Button btnTip1;
    private Button btnTip2;
    private Button btnTip3;
    private TextInputLayout txtInpLayAnswer;
    private TextInputEditText txtInpEdtTxtAnswer;
    private Button btnCheckAnswer;

    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageReference mStorageRef;

    private Integer intTip;
    private String tip;
    private MyPhaseProfile myPhaseProfile;

    private String riddleId;
    private String phase;
    private String total;
    private String status;

    private ImageView imageFront;
    private ImageView imageBack;

    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_riddle_phase);
        //firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //declarar elementos
        phaseName = findViewById(R.id.txtGameNamePhase);
        imgBtnClosePhase = findViewById(R.id.imgBtnOutPhase);
        easyFlipView = findViewById(R.id.imgEasyFlipPhase);
        btnTip1 = findViewById(R.id.btnTip1);
        btnTip2 = findViewById(R.id.btnTip2);
        btnTip3 = findViewById(R.id.btnTip3);
        txtInpLayAnswer = findViewById(R.id.textFieldRiddlePhase);
        txtInpEdtTxtAnswer = findViewById(R.id.textInputEditPhase);
        btnCheckAnswer = findViewById(R.id.btnCheckPhaseAnswer);
        imageFront = findViewById(R.id.imgPhaseFront);
        imageBack = findViewById(R.id.imgPhaseBack);


        //teste.setImageResource(R.drawable.sphinx_icon);

        //declarar variáveis
        riddleId = getIntent().getStringExtra("Id");
        phase = getIntent().getStringExtra("Phase");
        total = getIntent().getStringExtra("Total");
        status = getIntent().getStringExtra("Finalized");
        //Toast.makeText(getApplicationContext(), status, Toast.LENGTH_SHORT).show();
        myPhaseProfile = new MyPhaseProfile();



        //setar random int
        intTip = new Random().nextInt(3)+1;
        //Toast.makeText(getApplicationContext(), String.valueOf(intTip), Toast.LENGTH_SHORT).show();

        //setar tip buttons visibility
        tipButtonsVisibility();

        //setar Phase Profile
        mDatabase.child("Phases").child(riddleId).child(phase).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                myPhaseProfile = snapshot.getValue(MyPhaseProfile.class);
                setPhaseProfile();

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //setar close button
        imgBtnClosePhase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        //set flip Image
        StorageReference frontRef = mStorageRef.child("Games/"+riddleId+"/"+phase+"/"+"Front");
        Glide.with(getApplicationContext())
                .load(frontRef)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageFront);
        StorageReference backRef = mStorageRef.child("Games/"+riddleId+"/"+phase+"/"+"Back");
        Glide.with(getApplicationContext())
                .load(backRef)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imageBack);


        //initialize admob
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        mInterstitialAd = new InterstitialAd(this);
        mInterstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");


    }

    private void tipButtonsVisibility() {
        mDatabase.child("users").child(user.getUid()).child("tipsSaw").child(riddleId).child(phase).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Boolean saw = snapshot.getValue(Boolean.class);
                if (saw==null){
                    btnTip1.setVisibility(View.VISIBLE);
                    btnTip2.setVisibility(View.VISIBLE);
                    btnTip3.setVisibility(View.VISIBLE);
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void setPhaseProfile() {
        //set Title
        phaseName.setText(myPhaseProfile.getTitle());
        //set tip
        tip = myPhaseProfile.getTip();
        //set hint
        txtInpLayAnswer.setHint(myPhaseProfile.getHint());

        //setar buttons tips click
        btnTip1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intTip==1){
                    CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(RiddlePhase.this)
                            .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                            .setTitle("Dica: ")
                            .setMessage(tip)
                            .setNegativeText("OK")
                            .setOnNegativeClicked(new CustomAlertDialogue.OnNegativeClicked() {
                                @Override
                                public void OnClick(View view, Dialog dialog) {
                                    dialog.dismiss();
                                }
                            })
                            .setDecorView(getWindow().getDecorView())
                            .build();
                    alert.show();
                }
                tipClose();
            }

        });
        btnTip2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intTip==2){
                    CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(RiddlePhase.this)
                            .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                            .setTitle("Dica: ")
                            .setMessage(tip)
                            .setNegativeText("OK")
                            .setOnNegativeClicked(new CustomAlertDialogue.OnNegativeClicked() {
                                @Override
                                public void OnClick(View view, Dialog dialog) {
                                    dialog.dismiss();
                                }
                            })
                            .setDecorView(getWindow().getDecorView())
                            .build();
                    alert.show();
                }
                tipClose();
            }

        });
        btnTip3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (intTip==3){
                    CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(RiddlePhase.this)
                            .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                            .setTitle("Dica: ")
                            .setMessage(tip)
                            .setNegativeText("OK")
                            .setOnNegativeClicked(new CustomAlertDialogue.OnNegativeClicked() {
                                @Override
                                public void OnClick(View view, Dialog dialog) {
                                    dialog.dismiss();
                                }
                            })
                            .setDecorView(getWindow().getDecorView())
                            .build();
                    alert.show();
                }
                tipClose();
            }

        });

        //set answer click
        btnCheckAnswer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String anwerOnInput = Objects.requireNonNull(txtInpEdtTxtAnswer.getText()).toString();
                //Toast.makeText(getApplicationContext(), anwerOnInput.toLowerCase()+" = "+myPhaseProfile.getAnswer(), Toast.LENGTH_SHORT).show();
                if (anwerOnInput.equals("")){
                    Toast.makeText(getApplicationContext(), "Nenhuma resposta adicionada", Toast.LENGTH_SHORT).show();
                }else {
                    if (anwerOnInput.toLowerCase().equals(myPhaseProfile.getAnswer().toLowerCase())){
                        correctAnswer();
                    }else {
                        checkAlmostAnswers(anwerOnInput);
                    }
                }


            }
        });


    }

    private void checkAlmostAnswers(String answerAlmost) {
        if(answerAlmost.equals(myPhaseProfile.getAlmostAnswer1())){
            CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(RiddlePhase.this)
                    .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                    .setTitle("Dica: ")
                    .setMessage(myPhaseProfile.getAlmostAnswerTip1())
                    .setNegativeText("OK")
                    .setOnNegativeClicked(new CustomAlertDialogue.OnNegativeClicked() {
                        @Override
                        public void OnClick(View view, Dialog dialog) {
                            dialog.dismiss();
                        }
                    })
                    .setDecorView(getWindow().getDecorView())
                    .build();
            alert.show();

        }else if(answerAlmost.equals(myPhaseProfile.getAlmostAnswer2())){
            CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(RiddlePhase.this)
                    .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                    .setTitle("Dica: ")
                    .setMessage(myPhaseProfile.getAlmostAnswerTip2())
                    .setNegativeText("OK")
                    .setOnNegativeClicked(new CustomAlertDialogue.OnNegativeClicked() {
                        @Override
                        public void OnClick(View view, Dialog dialog) {
                            dialog.dismiss();
                        }
                    })
                    .setDecorView(getWindow().getDecorView())
                    .build();
            alert.show();

        }else if(answerAlmost.equals(myPhaseProfile.getAlmostAnswer3())){
            CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(RiddlePhase.this)
                    .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                    .setTitle("Dica: ")
                    .setMessage(myPhaseProfile.getAlmostAnswerTip3())
                    .setNegativeText("OK")
                    .setOnNegativeClicked(new CustomAlertDialogue.OnNegativeClicked() {
                        @Override
                        public void OnClick(View view, Dialog dialog) {
                            dialog.dismiss();
                        }
                    })
                    .setDecorView(getWindow().getDecorView())
                    .build();
            alert.show();

        }else {
            CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(RiddlePhase.this)
                    .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                    .setTitle("Resposta Incorreta")
                    .setMessage("Não foi dessa vez. Tente novamente.")
                    .setNegativeText("OK")
                    .setOnNegativeClicked(new CustomAlertDialogue.OnNegativeClicked() {
                        @Override
                        public void OnClick(View view, Dialog dialog) {
                            dialog.dismiss();
                        }
                    })
                    .setDecorView(getWindow().getDecorView())
                    .build();
            alert.show();
        }
    }

    private void correctAnswer() {
        mDatabase.child("users").child(user.getUid()).child("addedRiddles").child(riddleId).child("completed").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {

                Integer comp = Integer.parseInt(Objects.requireNonNull(snapshot.getValue()).toString());
                if (comp<Integer.parseInt(phase)){
                    //Toast.makeText(getApplicationContext(), comp+" >= "+Integer.parseInt(phase), Toast.LENGTH_SHORT).show();
                    if (comp<Integer.parseInt(total)){
                        comp = comp+1;
                    }
                    if (status.equals("not")){
                        mDatabase.child("users").child(user.getUid()).child("addedRiddles").child(riddleId).child("completed").setValue(String.valueOf(comp));
                        if (String.valueOf(comp).equals(total)){
                            mDatabase.child("games").child(riddleId).child("completed").addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {

                                    final Integer completados = Integer.parseInt(Objects.requireNonNull(snapshot.getValue()).toString())+1;
                                    mDatabase.child("games").child(riddleId).child("completed").setValue(String.valueOf(completados));
                                    mDatabase.child("games").child(riddleId).child("author").addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                                            mDatabase.child("users").child(Objects.requireNonNull(snapshot.getValue()).toString()).child("myRiddles").child(riddleId).child("completed").setValue(String.valueOf(completados));
                                        }

                                        @Override
                                        public void onCancelled(@NonNull DatabaseError error) {

                                        }
                                    });
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });
                        }
                    }
                }


                if (String.valueOf(comp).equals(total)){
                    //Toast.makeText(getApplicationContext(), "completou: "+"comp: "+comp+", total: "+total, Toast.LENGTH_SHORT).show();
                    nextLevelAds(true);
                }else {
                    //Toast.makeText(getApplicationContext(), "próxima fase"+"comp: "+comp+", total: "+total, Toast.LENGTH_SHORT).show();
                    nextLevelAds(false);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Toast.makeText(getApplicationContext(), "RESPOSTA CORRETA", Toast.LENGTH_SHORT).show();
    }

    public void nextLevelAds(final Boolean isLast){
        mInterstitialAd.loadAd(new AdRequest.Builder().build());
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdLoaded() {
                // Code to be executed when an ad finishes loading.
                if (mInterstitialAd.isLoaded()) {
                    mInterstitialAd.show();
                } else {
                    // Toast.makeText(getApplicationContext(), "The interstitial wasn't loaded yet.", Toast.LENGTH_SHORT).show();
                    Log.d("TAG", "The interstitial wasn't loaded yet.");
                }
            }


            @Override
            public void onAdOpened() {
                // Code to be executed when the ad is displayed.
            }

            @Override
            public void onAdClicked() {
                // Code to be executed when the user clicks on an ad.
            }

            @Override
            public void onAdLeftApplication() {
                // Code to be executed when the user has left the app.
            }

            @Override
            public void onAdClosed() {
                // Code to be executed when the interstitial ad is closed.
                if (isLast) {
                    completou();
                }else {
                    nextPhase();
                }

            }
        });

    }

    private void nextPhase() {
        Integer nextPhase = Integer.parseInt(phase)+1;
        Intent intent = new Intent(getApplicationContext(), RiddlePhase.class);
        intent.putExtra("Phase", String.valueOf(nextPhase));
        intent.putExtra("Id", riddleId);
        intent.putExtra("Total", total);
        intent.putExtra("Finalized", status);
        startActivity(intent);
        finish();
    }

    private void completou() {

        Intent intent = new Intent(getApplicationContext(), GameCompleted.class);
        intent.putExtra("Id", riddleId);
        startActivity(intent);
        finish();
    }

    private void tipClose() {
        btnTip1.setVisibility(View.INVISIBLE);
        btnTip2.setVisibility(View.INVISIBLE);
        btnTip3.setVisibility(View.INVISIBLE);
        mDatabase.child("users").child(user.getUid()).child("tipsSaw").child(riddleId).child(phase).setValue(true);
    }
}
