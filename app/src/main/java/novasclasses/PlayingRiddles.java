package novasclasses;

import android.app.Dialog;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.cria.sphinxroom.R;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Objects;

import stream.customalert.CustomAlertDialogue;

/**
 * A simple {@link Fragment} subclass.
 */
public class PlayingRiddles extends Fragment {
    private FloatingActionButton fabAddGame;

    private DatabaseReference mDatabase;
    private DatabaseReference gDatabase;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private StorageReference mStorageRef;

    private RecyclerView mRecyclerGames;
    private GameAdapter myGameAdapter;
    private ArrayList<MyRiddleProfile> myGamesList;

    public PlayingRiddles() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_playing_riddles, container, false);
        //firebase
        //iniciar firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference();
        gDatabase = database.getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        mStorageRef = FirebaseStorage.getInstance().getReference();

        //declarar elementos
        fabAddGame = v.findViewById(R.id.fab_add_riddle);
        //set adapter with my riddles
        mRecyclerGames  = (RecyclerView) v.findViewById(R.id.myRecyclerAddedRiddles);
        mRecyclerGames.setLayoutManager(new LinearLayoutManager(getContext()));


        //setar fab
        fabAddGame.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                checkNewGame();
            }
        });

        gDatabase = FirebaseDatabase.getInstance().getReference().child("users").child(user.getUid()).child("addedRiddles");
        gDatabase.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                myGamesList = new ArrayList<MyRiddleProfile>();
                            for (final DataSnapshot dataSnapshot: snapshot.getChildren()){
                                final MyRiddleProfile myNewRiddleProfile = dataSnapshot.getValue(MyRiddleProfile.class);
                                mDatabase.child("games").child(myNewRiddleProfile.getId()).child("fases").addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(@NonNull DataSnapshot nSnapshot) {
                                        myNewRiddleProfile.setFases(Objects.requireNonNull(nSnapshot.getValue()).toString());
                                        myGamesList.add(myNewRiddleProfile);

                                        myGameAdapter = new GameAdapter(getActivity(), myGamesList);
                                        mRecyclerGames.setAdapter(myGameAdapter);
                                    }

                                    @Override
                                    public void onCancelled(@NonNull DatabaseError error) {

                                    }
                                });




                            }



            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        return v;

    }



    private void checkNewGame() {
        ArrayList<String> lineHint = new ArrayList<>();
        lineHint.add("Código");
        CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(getContext())
                .setStyle(CustomAlertDialogue.Style.INPUT)
                .setTitle("Novo jogo")
                .setMessage("Digite o código do jogo.")
                .setPositiveText(getResources().getString(R.string.confirm))
                .setPositiveColor(R.color.positive)
                .setPositiveTypeface(Typeface.DEFAULT_BOLD)
                .setOnInputClicked(new CustomAlertDialogue.OnInputClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog, ArrayList<String> inputList) {
                        for (final String input : inputList)
                        {
                            mDatabase.child("users").child(user.getUid()).child("addedRiddles").child(input).addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(@NonNull DataSnapshot snapshot) {
                                    if (snapshot.hasChildren()){
                                        Toast.makeText(getContext(), "Jogo já adicionado", Toast.LENGTH_SHORT).show();
                                    }else {
                                        Toast.makeText(getContext(), "Vamos adicionar", Toast.LENGTH_SHORT).show();
                                        mDatabase.child("games").child(input).addListenerForSingleValueEvent(new ValueEventListener() {
                                            @Override
                                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                                if (snapshot.hasChildren()){
                                                    //Toast.makeText(getContext(), getResources().getString(R.string.jogos_adicionado), Toast.LENGTH_SHORT).show();
                                                    MyRiddleProfile sRiddleProfile = snapshot.getValue(MyRiddleProfile.class);
                                                    addGame(sRiddleProfile);
                                                }else {
                                                    Toast.makeText(getContext(), getResources().getString(R.string.jogos_nao_encontrado), Toast.LENGTH_SHORT).show();
                                                    Toast.makeText(getContext(), input+ " - "+snapshot.getValue(), Toast.LENGTH_SHORT).show();
                                                }
                                            }

                                            @Override
                                            public void onCancelled(@NonNull DatabaseError error) {

                                            }
                                        });
                                        Log.d("Input", input);
                                    }
                                }

                                @Override
                                public void onCancelled(@NonNull DatabaseError error) {

                                }
                            });

                        }
                        dialog.dismiss();
                    }
                })
                .setNegativeText(getResources().getString(R.string.cancel))
                .setNegativeColor(R.color.negative)
                .setOnNegativeClicked(new CustomAlertDialogue.OnNegativeClicked() {
                    @Override
                    public void OnClick(View view, Dialog dialog) {
                        dialog.dismiss();
                    }
                })
                .setLineInputHint(lineHint)
                .setDecorView(getActivity().getWindow().getDecorView())
                .build();
        alert.show();
    }

    private void addGame(final MyRiddleProfile mRiddleProfile) {
        mDatabase.child("games").child(mRiddleProfile.getId()).child("author").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                String author = Objects.requireNonNull(snapshot.getValue()).toString();
                String uid = user.getUid();
                if (author.equals(uid)){
                    Toast.makeText(getContext(), getResources().getString(R.string.jogos_criado_pelo_user), Toast.LENGTH_SHORT).show();
                }else {
                    HashMap<String, Object> map = new HashMap<>();
                    map.put("name", mRiddleProfile.getName());
                    map.put("desc", mRiddleProfile.getDesc());
                    map.put("id", mRiddleProfile.getId());
                    map.put("added", "0");
                    map.put("completed","0");
                    map.put("fases", mRiddleProfile.getFases());
                    map.put("date", String.valueOf(System.currentTimeMillis()));
                    map.put("author", mRiddleProfile.getAuthor());


                    mDatabase.child("users").child(user.getUid()).child("addedRiddles").child(mRiddleProfile.getId()).setValue(map);
                    mDatabase.child("games").child(mRiddleProfile.getId()).child("added").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Integer add = Integer.parseInt(Objects.requireNonNull(snapshot.getValue()).toString())+1;
                            mDatabase.child("games").child(mRiddleProfile.getId()).child("added").setValue(String.valueOf(add));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                    mDatabase.child("users").child(mRiddleProfile.getAuthor()).child("myRiddles").child(mRiddleProfile.getId()).child("added").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            Integer add = Integer.parseInt(Objects.requireNonNull(snapshot.getValue()).toString())+1;
                            mDatabase.child("users").child(mRiddleProfile.getAuthor()).child("myRiddles").child(mRiddleProfile.getId()).child("added").setValue(String.valueOf(add));
                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });

                    Toast.makeText(getContext(), getResources().getString(R.string.jogos_adicionado), Toast.LENGTH_SHORT).show();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }

}
