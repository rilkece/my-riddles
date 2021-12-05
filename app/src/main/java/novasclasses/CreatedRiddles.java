package novasclasses;

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

import java.util.ArrayList;

/**
 * A simple {@link Fragment} subclass.
 */
public class CreatedRiddles extends Fragment {
    private FloatingActionButton fab_create;
    private RecyclerView mRecyclerMyRiddles;
    private DatabaseReference mReference;
    private MyRiddleAdapter myRiddleAdapter;
    private ArrayList<MyRiddleProfile> myRiddlesList;

    private FirebaseAuth mAuth;
    private FirebaseUser user;

    public CreatedRiddles() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_created_riddles, container, false);
        fab_create = v.findViewById(R.id.fab_create_riddle);
        fab_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MySampleFabFragment dialogFrag = MySampleFabFragment.newInstance();
                dialogFrag.setParentFab(fab_create);
                dialogFrag.show(getFragmentManager(), dialogFrag.getTag());
            }
        });
        //set adapter with my riddles
        mRecyclerMyRiddles  = (RecyclerView) v.findViewById(R.id.myRecyclerCreatedRiddles);
        mRecyclerMyRiddles.setLayoutManager(new LinearLayoutManager(getContext()));

        mReference = FirebaseDatabase.getInstance().getReference().child("games");
        mReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull final DataSnapshot snapshot) {
                myRiddlesList = new ArrayList<MyRiddleProfile>();
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        for (final DataSnapshot dataSnapshot: snapshot.getChildren()){

                            MyRiddleProfile myRiddleProfile = dataSnapshot.getValue(MyRiddleProfile.class);
                            assert myRiddleProfile != null;
                            if (myRiddleProfile.getAuthor()!=null){
                                if (myRiddleProfile.getAuthor().equals(user.getUid())){
                                    myRiddlesList.add(myRiddleProfile);
                                }

                            }
                            myRiddleAdapter = new MyRiddleAdapter(getActivity(), myRiddlesList);
                            mRecyclerMyRiddles.setAdapter(myRiddleAdapter);
                        }
                    }
                }, 3000);


            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });


        return v;
    }
}
