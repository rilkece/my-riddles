package com.cria.sphinxroom;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Dialog;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import novasclasses.MyRiddleProfile;
import stream.customalert.CustomAlertDialogue;


public class EditRiddle extends AppCompatActivity {
    private MyRiddleProfile mRiddleProfile;
    private TextView txtName;
    private TextView txtCreated;
    private TextView txtAdded;
    private TextView txtCompleted;
    private TextView txtDesc;

    private ImageView imgCover;

    private Button btnFase1;
    private Button btnFase2;
    private Button btnFase3;
    private Button btnFase4;
    private Button btnFase5;
    private Button btnFase6;
    private Button btnFase7;
    private Button btnFase8;
    private Button btnFase9;
    private Button btnFase10;
    private Button btnNewPhase;
    private ImageButton imgBtnOut;

    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_riddle);
        //firebase
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();

        //setar Profile Riddle
        mRiddleProfile = new MyRiddleProfile();
        mRiddleProfile.setName(getIntent().getStringExtra("Name"));
        mRiddleProfile.setId(getIntent().getStringExtra("Id"));
        mRiddleProfile.setFases(getIntent().getStringExtra("Fases"));
        mRiddleProfile.setDesc(getIntent().getStringExtra("Desc"));
        mRiddleProfile.setDate(getIntent().getStringExtra("Date"));
        mRiddleProfile.setCompleted(getIntent().getStringExtra("Completed"));
        mRiddleProfile.setAuthor(getIntent().getStringExtra("Author"));
        mRiddleProfile.setAdded(getIntent().getStringExtra("Added"));


        //declarar e setar elementos

        txtName = findViewById(R.id.txtGameNameEditRiddle);
        txtCreated = findViewById(R.id.txtDateEditRiddles);
        txtAdded = findViewById(R.id.txtAddedEditRiddles);
        txtCompleted = findViewById(R.id.txtCompletedEditRiddles);
        txtDesc = findViewById(R.id.txtDescEditRiddle);
        imgCover = findViewById(R.id.imgCoverEditRiddle);
        btnFase1 = findViewById(R.id.btnFase1EditRiddles);
        btnFase2 = findViewById(R.id.btnFase2EditRiddles);
        btnFase3 = findViewById(R.id.btnFase3EditRiddles);
        btnFase4 = findViewById(R.id.btnFase4EditRiddles);
        btnFase5 = findViewById(R.id.btnFase5EditRiddles);
        btnFase6 = findViewById(R.id.btnFase6EditRiddles);
        btnFase7 = findViewById(R.id.btnFase7EditRiddles);
        btnFase8 = findViewById(R.id.btnFase8EditRiddles);
        btnFase9 = findViewById(R.id.btnFase9EditRiddles);
        btnFase10 = findViewById(R.id.btnFase10EditRiddles);
        btnNewPhase = findViewById(R.id.btnNewPhaseEditRiddle);
        imgBtnOut = findViewById(R.id.imgBtnOutEditRiddle);

        txtName.setText(mRiddleProfile.getName());
        String strCreated = getResources().getString(R.string.criado_em)+" "+ DateFormat.format("dd/MM/yyyy", Long.parseLong(mRiddleProfile.getDate()));
        txtCreated.setText(strCreated);
        String strAdded = getResources().getString(R.string.adicionados)+ getResources().getString(R.string.space)+mRiddleProfile.getAdded();
        txtAdded.setText(strAdded);
        String strCompleted = getResources().getString(R.string.completados)+getResources().getString(R.string.space)+mRiddleProfile.getCompleted();
        txtCompleted.setText(strCompleted);
        StorageReference profileRef = mStorageRef.child("Games/"+"Covers/"+mRiddleProfile.getId());
        txtDesc.setText(mRiddleProfile.getDesc());
        imgBtnOut.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Glide.with(getApplicationContext())
                .load(profileRef)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imgCover);
        btnNewPhase.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAlertDialogue.Builder alertProfilePic = new CustomAlertDialogue.Builder(EditRiddle.this)
                        .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                        .setCancelable(false)
                        .setTitle("Nova Fase")
                        .setMessage("Criar nova fase?")
                        .setPositiveText("Confirmar")
                        .setPositiveColor(R.color.deep_purple_600)
                        .setPositiveTypeface(Typeface.DEFAULT_BOLD)
                        .setOnPositiveClicked(new CustomAlertDialogue.OnPositiveClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog) {
                                Intent intent = new Intent(getApplicationContext(), CreatePhase.class);
                                intent.putExtra("Name", mRiddleProfile.getName());
                                intent.putExtra("Id", mRiddleProfile.getId());
                                intent.putExtra("Fases", mRiddleProfile.getFases());
                                intent.putExtra("Desc", mRiddleProfile.getDesc());
                                intent.putExtra("Date", mRiddleProfile.getDate());
                                intent.putExtra("Completed", mRiddleProfile.getCompleted());
                                intent.putExtra("Author", mRiddleProfile.getAuthor());
                                intent.putExtra("Added", mRiddleProfile.getAdded());
                                startActivity(intent);
                                dialog.dismiss();
                                finish();
                            }
                        })
                        .setNegativeText("Cancelar")
                        .setNegativeColor(R.color.red_600)
                        .setOnNegativeClicked(new CustomAlertDialogue.OnNegativeClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog) {
                                dialog.dismiss();
                            }
                        })
                        .setDecorView(getWindow().getDecorView())
                        .build();
                alertProfilePic.show();
            }
        });

    //set buttons visibility
        setBtnVisibility(Integer.parseInt(mRiddleProfile.getFases()));
    //setar btns
    btnFase1.setOnClickListener(new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            editPhase("1");
        }
    });
        btnFase2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPhase("2");
            }
        });
        btnFase3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPhase("3");
            }
        });
        btnFase4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPhase("4");
            }
        });
        btnFase5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPhase("5");
            }
        });
        btnFase6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPhase("6");
            }
        });
        btnFase7.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPhase("7");
            }
        });
        btnFase8.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPhase("8");
            }
        });
        btnFase9.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPhase("9");
            }
        });
        btnFase10.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editPhase("10");
            }
        });

    }

    private void editPhase(String phaseNumber) {
        Intent intent = new Intent(getApplicationContext(), EditPhase.class);
        intent.putExtra("Name", mRiddleProfile.getName());
        intent.putExtra("Id", mRiddleProfile.getId());
        intent.putExtra("Fases", phaseNumber);
        intent.putExtra("Desc", mRiddleProfile.getDesc());
        intent.putExtra("Date", mRiddleProfile.getDate());
        intent.putExtra("Completed", mRiddleProfile.getCompleted());
        intent.putExtra("Author", mRiddleProfile.getAuthor());
        intent.putExtra("Added", mRiddleProfile.getAdded());
        startActivity(intent);
        finish();
    }

    private void setBtnVisibility(int fases) {
        switch (fases){
            default:
                btnFase1.setVisibility(View.GONE);
                btnFase2.setVisibility(View.GONE);
                btnFase3.setVisibility(View.GONE);
                btnFase4.setVisibility(View.GONE);
                btnFase5.setVisibility(View.GONE);
                btnFase6.setVisibility(View.GONE);
                btnFase7.setVisibility(View.GONE);
                btnFase8.setVisibility(View.GONE);
                btnFase9.setVisibility(View.GONE);
                btnFase10.setVisibility(View.GONE);

                break;
            case 1:
                btnFase2.setVisibility(View.GONE);
                btnFase3.setVisibility(View.GONE);
                btnFase4.setVisibility(View.GONE);
                btnFase5.setVisibility(View.GONE);
                btnFase6.setVisibility(View.GONE);
                btnFase7.setVisibility(View.GONE);
                btnFase8.setVisibility(View.GONE);
                btnFase9.setVisibility(View.GONE);
                btnFase10.setVisibility(View.GONE);
                break;
            case 2:
                btnFase3.setVisibility(View.GONE);
                btnFase4.setVisibility(View.GONE);
                btnFase5.setVisibility(View.GONE);
                btnFase6.setVisibility(View.GONE);
                btnFase7.setVisibility(View.GONE);
                btnFase8.setVisibility(View.GONE);
                btnFase9.setVisibility(View.GONE);
                btnFase10.setVisibility(View.GONE);
                break;
            case 3:
                btnFase4.setVisibility(View.GONE);
                btnFase5.setVisibility(View.GONE);
                btnFase6.setVisibility(View.GONE);
                btnFase7.setVisibility(View.GONE);
                btnFase8.setVisibility(View.GONE);
                btnFase9.setVisibility(View.GONE);
                btnFase10.setVisibility(View.GONE);
                break;
            case 4:
                btnFase5.setVisibility(View.GONE);
                btnFase6.setVisibility(View.GONE);
                btnFase7.setVisibility(View.GONE);
                btnFase8.setVisibility(View.GONE);
                btnFase9.setVisibility(View.GONE);
                btnFase10.setVisibility(View.GONE);
                break;
            case 5:
                btnFase6.setVisibility(View.GONE);
                btnFase7.setVisibility(View.GONE);
                btnFase8.setVisibility(View.GONE);
                btnFase9.setVisibility(View.GONE);
                btnFase10.setVisibility(View.GONE);
                break;
            case 6:
                btnFase7.setVisibility(View.GONE);
                btnFase8.setVisibility(View.GONE);
                btnFase9.setVisibility(View.GONE);
                btnFase10.setVisibility(View.GONE);
                break;
            case 7:
                btnFase8.setVisibility(View.GONE);
                btnFase9.setVisibility(View.GONE);
                btnFase10.setVisibility(View.GONE);
                break;
            case 8:
                btnFase9.setVisibility(View.GONE);
                btnFase10.setVisibility(View.GONE);
                break;
            case 9:
                btnFase10.setVisibility(View.GONE);
                break;
            case 10:
                btnNewPhase.setVisibility(View.GONE);
                break;


        }

    }
}
