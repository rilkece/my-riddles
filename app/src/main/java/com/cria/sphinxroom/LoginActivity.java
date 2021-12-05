package com.cria.sphinxroom;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.File;

import novasclasses.User;

public class LoginActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN = 01;
    private static final String TAG = "SignInActivity";
    private FirebaseAuth mAuth;
    private GoogleSignInClient mGoogleSignInClient;
    private DatabaseReference mDatabase;
    private StorageReference mStorageRef;
    private  FirebaseDatabase database;
    private DatabaseReference statusReference;
    private ProgressBar progressBar;
    private SignInButton signInButton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        // Configure sign-in to request the user's ID, email address, and basic
        // profile. ID and basic profile are included in DEFAULT_SIGN_IN.
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        // Build a GoogleSignInClient with the options specified by gso.
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        mAuth = FirebaseAuth.getInstance();
        mStorageRef = FirebaseStorage.getInstance().getReference();
        database = FirebaseDatabase.getInstance();
        statusReference = database.getReference("status");
        signInButton = findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });

        //progress bar
        progressBar = findViewById(R.id.progressBarLogin);
    }

    private void signIn() {
        Intent signInIntent = mGoogleSignInClient.getSignInIntent();
        startActivityForResult(signInIntent, RC_SIGN_IN);
        Log.d(TAG, "startActivityForResult works");
    }
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInClient.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            // The Task returned from this call is always completed, no need to attach
            // a listener.
           // Toast.makeText(getApplicationContext(), "Tentando", Toast.LENGTH_SHORT).show();
            loading(true);
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            handleSignInResult(task);

        }
    }

    private void handleSignInResult(Task<GoogleSignInAccount> completedTask) {
        try {
            GoogleSignInAccount account = completedTask.getResult(ApiException.class);
            // Signed in successfully, show authenticated UI.
            firebaseAuthWithGoogle(account.getIdToken());

        } catch (ApiException e) {
            // The ApiException status code indicates the detailed failure reason.
            // Please refer to the GoogleSignInStatusCodes class reference for more information.
            Log.w("SignInActivity", "signInResult:failed code=" + e.getStatusCode());
            Toast.makeText(getApplicationContext(), "fail, try again", Toast.LENGTH_SHORT).show();
            loading(false);
           // updateUI(null);
        }
    }
    private void firebaseAuthWithGoogle(String idToken) {
        AuthCredential credential = GoogleAuthProvider.getCredential(idToken, null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Toast.makeText(getApplicationContext(), user.getDisplayName(), Toast.LENGTH_SHORT).show();
                           // assert user != null;
                            writeNewUser(user.getUid(), user.getDisplayName(), user.getEmail());

                            //updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            Toast.makeText(getApplicationContext(), "fail, try again", Toast.LENGTH_SHORT).show();
                            loading(false);
                            //updateUI(null);
                        }

                        // ...
                    }
                });
    }
    private void writeNewUser(final String userId, final String name, final String email) {
        Log.d(TAG, "Writing new user");

        statusReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.getValue().toString().equals("beta")){
                    mDatabase = database.getReference("users");
                    mDatabase.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            if (snapshot.hasChild(userId)){
                                // Toast.makeText(getApplicationContext(), "usuário existente", Toast.LENGTH_SHORT).show();
                                Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                                startActivity(myIntent);
                                finish();
                            }else {
                                // Toast.makeText(getApplicationContext(), "novo usuário", Toast.LENGTH_SHORT).show();
                                User user = new User(name, email, getResources().getString(R.string.undefined), getResources().getString(R.string.undefined));
                                mDatabase.child(userId).setValue(user);

                                //upload profile pic
                                Uri myURI = Uri.parse("android.resource://com.cria.sphinxroom/" + R.drawable.sphinx_icon);
                                StorageReference profileRef = mStorageRef.child("ProfilesPics/"+userId);

                                profileRef.putFile(myURI).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                                    @Override
                                    public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                        Uri downloadUrl = taskSnapshot.getUploadSessionUri();
                                        //Toast.makeText(getApplicationContext(), downloadUrl.toString(), Toast.LENGTH_SHORT).show();
                                        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                                        startActivity(myIntent);
                                        finish();

                                    }
                                });
                            }

                        }

                        @Override
                        public void onCancelled(@NonNull DatabaseError error) {

                        }
                    });
                }else {
                    Intent myIntent = new Intent(getApplicationContext(), UpdateActivity.class);
                    startActivity(myIntent);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        // Check for existing Google Sign In account, if the user is already signed in
        // the GoogleSignInAccount will be non-null.
        GoogleSignInAccount account = GoogleSignIn.getLastSignedInAccount(this);
        //updateUI(account);
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null){
            //Toast.makeText(getApplicationContext(), "faça login", Toast.LENGTH_SHORT).show();
        }else {
            loading(true);
            FirebaseDatabase statusdatabase = FirebaseDatabase.getInstance();
            DatabaseReference mstatusReference = statusdatabase.getReference("status");
            mstatusReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.getValue().toString().equals("beta")){
                        //Toast.makeText(getApplicationContext(), currentUser.getDisplayName(), Toast.LENGTH_SHORT).show();
                        Intent myIntent = new Intent(getApplicationContext(), MainActivity.class);
                        startActivity(myIntent);
                        finish();
                    }else {
                        Intent myIntent = new Intent(getApplicationContext(), UpdateActivity.class);
                        startActivity(myIntent);
                        finish();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

        }
        //updateUI(currentUser);
    }
    private void loading(Boolean entrando){
        if (entrando){
            signInButton.setVisibility(View.INVISIBLE);
            progressBar.setVisibility(View.VISIBLE);
        }else {
            signInButton.setVisibility(View.VISIBLE);
            progressBar.setVisibility(View.INVISIBLE);
        }


    }

}
