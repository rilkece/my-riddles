package novasclasses;

import android.app.ActivityManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.cria.sphinxroom.LoginActivity;
import com.cria.sphinxroom.MainActivity;
import com.cria.sphinxroom.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
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

import java.util.ArrayList;
import java.util.Objects;

import stream.customalert.CustomAlertDialogue;

/**
 * A simple {@link Fragment} subclass.
 */
public class Settings extends Fragment {
    private TextView userName;
    private TextView userEmail;
    private TextView userGender;
    private TextView userBirth;

    private ImageButton editNameBtn;
    private ImageButton editGenderBtn;
    private ImageButton editBirthBtn;
    private Button logOutButton;

    private StorageReference mStorageRef;
    private FirebaseAuth mAuth;
    private FirebaseUser user;
    private DatabaseReference mDatabase;

    private GoogleSignInClient mGoogleSignInClient;


    public Settings() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(getContext(), gso);

        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        userName = v.findViewById(R.id.nameUserSetting);
        userEmail = v.findViewById(R.id.nameEmailSetting);
        userGender = v.findViewById(R.id.nameGenderSetting);
        userBirth = v.findViewById(R.id.nameBirthSetting);
        editNameBtn = v.findViewById(R.id.editNameImgSetting);
        editGenderBtn = v.findViewById(R.id.editGenderImgSetting);
        editBirthBtn = v.findViewById(R.id.editBirthImgSetting);
        logOutButton = v.findViewById(R.id.logOutButton);



        FirebaseDatabase database = FirebaseDatabase.getInstance();
        mDatabase = database.getReference("users");
        mStorageRef = FirebaseStorage.getInstance().getReference();
        mAuth = FirebaseAuth.getInstance();
        user = mAuth.getCurrentUser();
        setNames();
        setEditButtons();
        setLogOutButton();
        // Inflate the layout for this fragment
        return v;
    }

    private void setLogOutButton() {
        logOutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FirebaseAuth.getInstance().signOut();
                Intent myIntent = new Intent(getContext(), LoginActivity.class);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                myIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(myIntent);
                clearCache();

            }
        });
    }

    private void clearCache() {
        mGoogleSignInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                //Toast.makeText(getContext(), "saiu", Toast.LENGTH_SHORT).show();
            }
        });
        getActivity().finish();
    }

    private void setEditButtons() {
        editNameBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> lineHint = new ArrayList<>();
                lineHint.add(userName.getText().toString());
                CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(getContext())
                        .setStyle(CustomAlertDialogue.Style.INPUT)
                        .setTitle("Nome de Usuário")
                        .setMessage("Edite seu nome de usuário")
                        .setPositiveText(getResources().getString(R.string.update))
                        .setPositiveColor(R.color.positive)
                        .setPositiveTypeface(Typeface.DEFAULT_BOLD)
                        .setOnInputClicked(new CustomAlertDialogue.OnInputClicked() {
                            @Override
                            public void OnClick(View view, Dialog dialog, ArrayList<String> inputList) {
                                Toast.makeText(getContext(), "Sent", Toast.LENGTH_SHORT).show();
                                for (String input : inputList)
                                {
                                   // Toast.makeText(getContext(), input, Toast.LENGTH_SHORT).show();
                                    mDatabase.child(user.getUid()).child("username").setValue(input);
                                    Log.d("Input", input);
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
        });
        editGenderBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ArrayList<String> destructive = new ArrayList<>();
                destructive.add( getResources().getString(R.string.undefined));

                ArrayList<String> other = new ArrayList<>();
                other.add(getResources().getString(R.string.masc));
                other.add(getResources().getString(R.string.fem));

                CustomAlertDialogue.Builder alert = new CustomAlertDialogue.Builder(getContext())
                        .setStyle(CustomAlertDialogue.Style.SELECTOR)
                        .setDestructive(destructive)
                        .setOthers(other)
                        .setOnItemClickListener(new AdapterView.OnItemClickListener() {
                            @Override
                            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                                CustomAlertDialogue.getInstance().dismiss();
                                String gender = getResources().getString(R.string.undefined);
                                if (i==1){
                                    gender=getResources().getString(R.string.masc);
                                }else if(i==2){
                                    gender=getResources().getString(R.string.fem);
                                }
                                mDatabase.child(user.getUid()).child("gender").setValue(gender);
                            }
                        })
                        .setDecorView(getActivity().getWindow().getDecorView())
                        .build();
                alert.show();
            }
        });
        editBirthBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                final DatePicker picker = new DatePicker(getContext());
                picker.setCalendarViewShown(false);

                builder.setTitle(getResources().getString(R.string.data_de_nascimento));
                builder.setView(picker);
                builder.setNegativeButton(getResources().getString(R.string.cancel), null);
                builder.setPositiveButton(getResources().getString(R.string.update), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                       String year = String.valueOf(picker.getYear());
                       String month = String.valueOf(picker.getMonth());
                       String day = String.valueOf(picker.getDayOfMonth());
                       String birth = day+"/"+month+"/"+year;
                        mDatabase.child(user.getUid()).child("birth").setValue(birth);
                    }
                });

                builder.show();
            }
        });
    }


    private void setNames() {
        // Read from the database
        mDatabase.child(user.getUid()).child("username").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                userName.setText(value);
                Log.d("Database reader", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Database reader", "Failed to read value.", error.toException());
            }
        });
        mDatabase.child(user.getUid()).child("email").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                userEmail.setText(value);
                Log.d("Database reader", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Database reader", "Failed to read value.", error.toException());
            }
        });
        mDatabase.child(user.getUid()).child("gender").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                userGender.setText(value);
                Log.d("Database reader", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Database reader", "Failed to read value.", error.toException());
            }
        });
        mDatabase.child(user.getUid()).child("birth").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                // This method is called once with the initial value and again
                // whenever data at this location is updated.
                String value = dataSnapshot.getValue(String.class);
                userBirth.setText(value);
                Log.d("Database reader", "Value is: " + value);
            }

            @Override
            public void onCancelled(DatabaseError error) {
                // Failed to read value
                Log.w("Database reader", "Failed to read value.", error.toException());
            }
        });
    }
}
