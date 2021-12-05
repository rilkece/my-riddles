package novasclasses;

import android.app.Dialog;
import android.content.Intent;
import android.net.Uri;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.allattentionhere.fabulousfilter.AAH_FabulousFragment;
import com.cria.sphinxroom.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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

public class MySampleFabFragment extends AAH_FabulousFragment {
    private FirebaseUser user;
    private DatabaseReference mDatabase;
    private DatabaseReference gDatabase;
    FirebaseDatabase mFirebaseDatabase;

    public String gameName;
    public String gameDesc;
    public String gameID;

    public ProgressBar progressBarCreateGame;
    public EditText editGameName;
    public EditText editGameDesc;


    public static MySampleFabFragment newInstance() {
        MySampleFabFragment f = new MySampleFabFragment();
        return f;
    }

    @Override

    public void setupDialog(Dialog dialog, int style) {
        FirebaseAuth mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mFirebaseDatabase = FirebaseDatabase.getInstance();
        mDatabase = mFirebaseDatabase.getReference("users");
        gDatabase = mFirebaseDatabase.getReference("games");
        View contentView = View.inflate(getContext(), R.layout.create_riddle_layout, null);
        LinearLayout ll_content = (LinearLayout) contentView.findViewById(R.id.ll_content_main);
        editGameName = (EditText) contentView.findViewById(R.id.editTextGameName);
        editGameDesc = (EditText) contentView.findViewById(R.id.editTextGameDesc);
        Button btm_cancel = (Button) contentView.findViewById(R.id.cancel_btn_create_riddle_layout);
        Button btm_confirm = (Button) contentView.findViewById(R.id.confirm_btn_create_riddle_layout);
        progressBarCreateGame = (ProgressBar) contentView.findViewById(R.id.progressBarCreateGame);

        btm_cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                //Toast.makeText(getContext(), Integer.toString((int) System.currentTimeMillis(), 16), Toast.LENGTH_SHORT).show();
                closeFilter("closed");
            }
        });
        btm_confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!editGameName.getText().toString().equals("") && !editGameDesc.getText().toString().equals("")){
                    String nome = editGameName.getText().toString();
                    String desc = editGameDesc.getText().toString();
                    creategame(nome, desc);

                    //Toast.makeText(getContext(), nome + " - " + desc, Toast.LENGTH_SHORT).show();
                }else {
                    Toast.makeText(getContext(), getResources().getString(R.string.fillAll), Toast.LENGTH_SHORT).show();

                }
            }
        });


        //params to set
        setAnimationDuration(600); //optional; default 500ms
        setPeekHeight(500); // optional; default 400dp
        //setCallbacks((Callbacks) getActivity()); //optional; to get back result
        //setAnimationListener((AnimationListener) getActivity()); //optional; to get animation callbacks
        //setViewgroupStatic(ll_buttons); // optional; layout to stick at bottom on slide
        //setViewPager(vp_types); //optional; if you use viewpager that has scrollview
        setViewMain(ll_content); //necessary; main bottomsheet view
        setMainContentView(contentView); // necessary; call at end before super
        super.setupDialog(dialog, style); //call super at last
    }



    private void creategame(String nome, String desc) {
        gameName = nome;
        gameDesc = desc;
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent, 1);

    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        editGameName.setText("");
        editGameDesc.setText("");
        progressBarCreateGame.setVisibility(View.VISIBLE);
        if (requestCode==1 && resultCode==-1 && data!=null && data.getData()!=null){
            Uri riddleCoverPicUri = data.getData();
            gameID = idUnique();
            final StorageReference newRiddleCoverReference = FirebaseStorage.getInstance().getReference().child("Games/"+"Covers/"+gameID);
            newRiddleCoverReference.putFile(riddleCoverPicUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(getContext(), getResources().getString(R.string.img_upload_success), Toast.LENGTH_SHORT).show();
                }
            }).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {


                    HashMap<String, Object> map = new HashMap<>();
                    map.put("name", gameName);
                    map.put("desc", gameDesc);
                    map.put("id", gameID);
                    map.put("added", "0");
                    map.put("completed", "0");
                    map.put("fases", "0");
                    map.put("date", String.valueOf(System.currentTimeMillis()));
                    map.put("author", user.getUid());


                    gDatabase.child(gameID).setValue(map);
                    mDatabase.child(user.getUid()).child("myRiddles").child(gameID).setValue(map);

                    Toast.makeText(getContext(), getResources().getString(R.string.game_created), Toast.LENGTH_SHORT).show();
                    mDatabase.child(user.getUid()).child("totalAdded").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Integer totalAdded = 0;
                            if (snapshot.exists()){
                                totalAdded = Integer.parseInt(Objects.requireNonNull(snapshot.getValue()).toString());
                            }
                            totalAdded = totalAdded+1;
                            mDatabase.child(user.getUid()).child("totalAdded").setValue(String.valueOf(totalAdded));
                            Toast.makeText(getContext(), "total added: "+ String.valueOf(totalAdded), Toast.LENGTH_SHORT).show();
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {
                            Toast.makeText(getContext(), "Falhou", Toast.LENGTH_SHORT).show();
                        }
                    });
                    progressBarCreateGame.setVisibility(View.GONE);
                    closeFilter("closed");



                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(getContext(), getResources().getString(R.string.img_upload_fail), Toast.LENGTH_SHORT).show();
                }
            });

        }else {
            Toast.makeText(getContext(), getResources().getString(R.string.img_upload_fail), Toast.LENGTH_SHORT).show();
        }
    }
    public String idUnique(){
       return Integer.toString((int) System.currentTimeMillis(), 16);

    }

}

/*
RelativeLayout rl_content = (RelativeLayout) contentView.findViewById(R.id.rl_content);
        LinearLayout ll_buttons = (LinearLayout) contentView.findViewById(R.id.ll_buttons);
        contentView.findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                closeFilter("closed");
            }
        });
* */
