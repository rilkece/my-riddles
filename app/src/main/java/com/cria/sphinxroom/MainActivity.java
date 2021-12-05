package com.cria.sphinxroom;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager.widget.ViewPager;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.View;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.tabs.TabItem;
import com.google.android.material.tabs.TabLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FileDownloadTask;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.mikhaellopez.circularimageview.CircularImageView;

import java.io.File;

import novasclasses.PagerAdapter;
import stream.customalert.CustomAlertDialogue;

public class MainActivity extends AppCompatActivity {
    private TextView nameUser;
    private DatabaseReference mDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageReference mStorageRef;
    private CircularImageView profilePic;
    private ProgressBar profilePicProgressBar;


    public TabLayout tabMain;
    public TabItem tabItemEnigmas;
    public TabItem tabItemCriados;
    public TabItem tabItemConfigs;
    public ViewPager viewPagerMain;


    private InterstitialAd mInterstitialAd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        //ads
        // Sample AdMob app ID: ca-app-pub-3940256099942544~3347511713
        MobileAds.initialize(this, getResources().getString(R.string.admob_appid));


        //iniciar database reference, storage and auth
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("users");
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //elementos do cabeçalho
        nameUser = findViewById(R.id.textViewName);
        profilePic = findViewById(R.id.profilePic);
        profilePicProgressBar = findViewById(R.id.profilePicProgressBar);
        tabMain = findViewById(R.id.tabLayoutMain);
        tabItemEnigmas = findViewById(R.id.tabItemEnigmas);
        tabItemCriados = findViewById(R.id.tabItemCriados);
        tabItemConfigs = findViewById(R.id.tabItemConfigs);
        viewPagerMain = findViewById(R.id.viewPagerMain);

        //setar nome e foto de perfil
        setName();
        setProfilePic();

        //set profilepic update
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                CustomAlertDialogue.Builder alertProfilePic = new CustomAlertDialogue.Builder(MainActivity.this)
                        .setStyle(CustomAlertDialogue.Style.DIALOGUE)
                        .setCancelable(false)
                        .setTitle("Foto de Perfil")
                        .setMessage("Alterar sua foto de perfil?")
                        .setPositiveText("Confirmar")
                        .setPositiveColor(R.color.deep_purple_600)
                        .setPositiveTypeface(Typeface.DEFAULT_BOLD)
                        .setOnPositiveClicked(new CustomAlertDialogue.OnPositiveClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog) {
                                updateProfilePic();
                                dialog.dismiss();
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

        //setar tab adapter

        PagerAdapter pagerAdapter = new PagerAdapter(getSupportFragmentManager(), tabMain.getTabCount());
        viewPagerMain.setAdapter(pagerAdapter);
        tabMain.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPagerMain.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
    }

    private void setProfilePic() {
        StorageReference profileRef = mStorageRef.child("ProfilesPics/"+user.getUid());
        Glide.with(getApplicationContext())
                .load(profileRef)
                .diskCacheStrategy(DiskCacheStrategy.NONE)
                .skipMemoryCache(true)
                .into(profilePic);
        //Toast.makeText(getApplicationContext(), "ProfiliPic Setada", Toast.LENGTH_SHORT).show();
        profilePic.setVisibility(View.VISIBLE);
        profilePicProgressBar.setVisibility(View.GONE);

    }

    private void updateProfilePic() {
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode==1 && resultCode==RESULT_OK && data!=null && data.getData()!=null){
            profilePic.setVisibility(View.INVISIBLE);
            profilePicProgressBar.setVisibility(View.VISIBLE);
            Uri profilePicUri = data.getData();
            StorageReference newProfileReference = mStorageRef.child("ProfilesPics/"+user.getUid());
            newProfileReference.putFile(profilePicUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.img_upload_success), Toast.LENGTH_SHORT).show();

                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    setProfilePic();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getApplicationContext(), getResources().getString(R.string.img_upload_fail), Toast.LENGTH_SHORT).show();

                }
            });
        }
    }

    private void setName() {
       // Read from the database
        mDatabase.child(user.getUid()).child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                nameUser.setText(value);
                Log.d("Database reader", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Database reader", "Failed to read value.", error.toException());
            }
        });

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        
    }
}
