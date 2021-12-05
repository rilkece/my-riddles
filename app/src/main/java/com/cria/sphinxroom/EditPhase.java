package com.cria.sphinxroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import com.google.firebase.storage.UploadTask;

import java.util.HashMap;
import java.util.Objects;

import novasclasses.MyRiddleProfile;

public class EditPhase extends AppCompatActivity {
    private MyRiddleProfile mRiddleProfile;

    private DatabaseReference pDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageReference mStorageRef;

    private TextView txtPhaseNumber;
    private TextInputLayout txtInpLayTitle;
    private TextInputEditText txtInpEdtTitle;
    private TextInputLayout txtInpLayAnswer;
    private TextInputEditText txtInpEdtAnswer;
    private TextInputLayout txtInpLayHint;
    private TextInputEditText txtInpEdtHint;
    private TextInputLayout txtInpLayTip;
    private TextInputEditText txtInpEdtTip;
    private TextInputLayout txtInpLayAlmAns1;
    private TextInputEditText txtInpEdtAlmAns1;
    private TextInputLayout txtInpLayAlmAns2;
    private TextInputEditText txtInpEdtAlmAns2;
    private TextInputLayout txtInpLayAlmAns3;
    private TextInputEditText txtInpEdtAlmAns3;
    private TextInputLayout txtInpLayAlmAnsTip1;
    private TextInputEditText txtInpEdtAlmAnsTip1;
    private TextInputLayout txtInpLayAlmAnsTip2;
    private TextInputEditText txtInpEdtAlmAnsTip2;
    private TextInputLayout txtInpLayAlmAnsTip3;
    private TextInputEditText txtInpEdtAlmAnsTip3;

    private ImageView imgFront;
    private ImageView imgBack;
    private Boolean imgFrontUp;
    private Boolean imgBackUp;

    private Button btnCancel;
    private Button btnConfirm;

    public String verso;

    public Integer phase;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_phase);
        //iniciar firebase
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        pDatabase = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

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


        //declarar elementos
        txtPhaseNumber = findViewById(R.id.txtTitlePhaseEditPhase);
        txtInpLayTitle = findViewById(R.id.outlinedTextFieldTitleEditPhase);
        txtInpEdtTitle = findViewById(R.id.textInputEditTitleEditPhase);
        txtInpLayAnswer = findViewById(R.id.outlinedTextFieldAnswerEditPhase);
        txtInpEdtAnswer = findViewById(R.id.textInputEditAnswerEditPhase);
        txtInpLayHint = findViewById(R.id.outlinedTextFieldHintEditPhase);
        txtInpEdtHint = findViewById(R.id.textInputEditHintEditPhase);
        txtInpLayTip = findViewById(R.id.outlinedTextFieldTipEditPhase);
        txtInpEdtTip = findViewById(R.id.textInputEditTipEditPhase);
        txtInpLayAlmAns1 = findViewById(R.id.outlinedTextFieldAlmostAnswer1EditPhase);
        txtInpEdtAlmAns1 = findViewById(R.id.textInputEditAlmostAnswer1EditPhase);
        txtInpLayAlmAns2 = findViewById(R.id.outlinedTextFieldAlmostAnswer2EditPhase);
        txtInpEdtAlmAns2 = findViewById(R.id.textInputEditAlmostAnswer2EditPhase);
        txtInpLayAlmAns3 = findViewById(R.id.outlinedTextFieldAlmostAnswer3EditPhase);
        txtInpEdtAlmAns3 = findViewById(R.id.textInputEditAlmostAnswer3EditPhase);
        txtInpLayAlmAnsTip1 = findViewById(R.id.outlinedTextFieldAlmostAnswerTip1EditPhase);
        txtInpEdtAlmAnsTip1 = findViewById(R.id.textInputEditAlmostAnswerTip1EditPhase);
        txtInpLayAlmAnsTip2 = findViewById(R.id.outlinedTextFieldAlmostAnswerTip2EditPhase);
        txtInpEdtAlmAnsTip2 = findViewById(R.id.textInputEditAlmostAnswerTip2EditPhase);
        txtInpLayAlmAnsTip3 = findViewById(R.id.outlinedTextFieldAlmostAnswerTip3EditPhase);
        txtInpEdtAlmAnsTip3 = findViewById(R.id.textInputEditAlmostAnswerTip3EditPhase);

        imgFront = findViewById(R.id.imgFrontCreatPhase);
        imgBack = findViewById(R.id.imgBackEditPhase);

        btnCancel = findViewById(R.id.btnCancelEditPhase);
        btnConfirm = findViewById(R.id.btnConfirmEditPhase);

        imgFrontUp = false;
        imgBackUp = false;
        //setar Phase Number
        phase = Integer.parseInt(mRiddleProfile.getFases());
        String phaseName = getResources().getString(R.string.fase)+getResources().getString(R.string.space)+String.valueOf(phase);
        txtPhaseNumber.setText(phaseName);
        //setar elementos
        pDatabase.child("Phases").child(mRiddleProfile.getId()).child(mRiddleProfile.getFases()).child("title").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtInpEdtTitle.setText(Objects.requireNonNull(snapshot.getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        pDatabase.child("Phases").child(mRiddleProfile.getId()).child(mRiddleProfile.getFases()).child("answer").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtInpEdtAnswer.setText(Objects.requireNonNull(snapshot.getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        pDatabase.child("Phases").child(mRiddleProfile.getId()).child(mRiddleProfile.getFases()).child("hint").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtInpEdtHint.setText(Objects.requireNonNull(snapshot.getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        pDatabase.child("Phases").child(mRiddleProfile.getId()).child(mRiddleProfile.getFases()).child("tip").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtInpEdtTip.setText(Objects.requireNonNull(snapshot.getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        pDatabase.child("Phases").child(mRiddleProfile.getId()).child(mRiddleProfile.getFases()).child("almostAnswer1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtInpEdtAlmAns1.setText(Objects.requireNonNull(snapshot.getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        pDatabase.child("Phases").child(mRiddleProfile.getId()).child(mRiddleProfile.getFases()).child("almostAnswer2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtInpEdtAlmAns2.setText(Objects.requireNonNull(snapshot.getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        pDatabase.child("Phases").child(mRiddleProfile.getId()).child(mRiddleProfile.getFases()).child("almostAnswer3").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtInpEdtAlmAns3.setText(Objects.requireNonNull(snapshot.getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        pDatabase.child("Phases").child(mRiddleProfile.getId()).child(mRiddleProfile.getFases()).child("almostAnswerTip1").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtInpEdtAlmAnsTip1.setText(Objects.requireNonNull(snapshot.getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        pDatabase.child("Phases").child(mRiddleProfile.getId()).child(mRiddleProfile.getFases()).child("almostAnswerTip2").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtInpEdtAlmAnsTip2.setText(Objects.requireNonNull(snapshot.getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        pDatabase.child("Phases").child(mRiddleProfile.getId()).child(mRiddleProfile.getFases()).child("almostAnswerTip3").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                txtInpEdtAlmAnsTip3.setText(Objects.requireNonNull(snapshot.getValue()).toString());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



        //setar images
        final StorageReference newPhaseFrontImageReference = mStorageRef.child("Games/"+mRiddleProfile.getId()+"/"+phase+"/Front");
        Glide.with(getApplicationContext())
                .load(newPhaseFrontImageReference)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imgFront);
        final StorageReference newPhaseBackImageReference = mStorageRef.child("Games/"+mRiddleProfile.getId()+"/"+phase+"/Back");
        Glide.with(getApplicationContext())
                .load(newPhaseBackImageReference)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(imgBack);

        imgFront.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgFront.setVisibility(View.GONE);
                imgBack.setVisibility(View.GONE);
                updatePhaseImages("Front");
            }
        });
        imgBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgFront.setVisibility(View.GONE);
                imgBack.setVisibility(View.GONE);
                updatePhaseImages("Back");
            }
        });

        //setar btns
        btnCancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!Objects.equals(Objects.requireNonNull(txtInpEdtTitle.getText()).toString(), "") && !Objects.equals(Objects.requireNonNull(txtInpEdtAnswer.getText()).toString(), "") && !Objects.equals(Objects.requireNonNull(txtInpEdtHint.getText()).toString(), "") && !Objects.equals(Objects.requireNonNull(txtInpEdtTip.getText()).toString(), "")){
                   EditPhase();

                }else {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.fill), Toast.LENGTH_SHORT).show();
                }

            }
        });
    }

    private void EditPhase() {
        String title = Objects.requireNonNull(txtInpEdtTitle.getText()).toString();
        String answer = Objects.requireNonNull(txtInpEdtAnswer.getText()).toString();
        String hint = Objects.requireNonNull(txtInpEdtHint.getText()).toString();
        String tip = Objects.requireNonNull(txtInpEdtTip.getText()).toString();
        String almostAnswer1 = Objects.requireNonNull(txtInpEdtAlmAns1.getText()).toString();
        String almostAnswer2 = Objects.requireNonNull(txtInpEdtAlmAns2.getText()).toString();
        String almostAnswer3 = Objects.requireNonNull(txtInpEdtAlmAns3.getText()).toString();
        String almostAnswerTip1 = Objects.requireNonNull(txtInpEdtAlmAnsTip1.getText()).toString();
        String almostAnswerTip2 = Objects.requireNonNull(txtInpEdtAlmAnsTip2.getText()).toString();
        String almostAnswerTip3 = Objects.requireNonNull(txtInpEdtAlmAnsTip3.getText()).toString();

        HashMap<String, Object> map = new HashMap<>();
        map.put("title", title);
        map.put("answer", answer);
        map.put("hint",hint);
        map.put("tip", tip);
        map.put("almostAnswer1", almostAnswer1);
        map.put("almostAnswer2", almostAnswer2);
        map.put("almostAnswer3", almostAnswer3);
        map.put("almostAnswerTip1", almostAnswerTip1);
        map.put("almostAnswerTip2", almostAnswerTip2);
        map.put("almostAnswerTip3", almostAnswerTip3);


        pDatabase.child("Phases").child(mRiddleProfile.getId()).child(String.valueOf(phase)).setValue(map);
        Toast.makeText(getApplicationContext(), getResources().getString(R.string.phaseCreated), Toast.LENGTH_SHORT).show();
        finish();
    }

    private void updatePhaseImages(String side) {
        verso = side;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            Uri profilePicUri = data.getData();
            final StorageReference newPhaseImageReference = mStorageRef.child("Games/"+mRiddleProfile.getId()+"/"+ phase +"/"+verso);
            newPhaseImageReference.putFile(profilePicUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.img_upload_success), Toast.LENGTH_SHORT).show();

                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    if (verso.equals("Front")){
                        Glide.with(getApplicationContext())
                                .load(newPhaseImageReference)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(imgFront);
                        imgFrontUp = true;
                        imgFront.setVisibility(View.VISIBLE);
                        imgBack.setVisibility(View.VISIBLE);

                    }else {
                        Glide.with(getApplicationContext())
                                .load(newPhaseImageReference)
                                .diskCacheStrategy(DiskCacheStrategy.NONE)
                                .skipMemoryCache(true)
                                .into(imgBack);
                        imgBackUp = true;
                        imgFront.setVisibility(View.VISIBLE);
                        imgBack.setVisibility(View.VISIBLE);
                    }

                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.img_upload_fail), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }
}
