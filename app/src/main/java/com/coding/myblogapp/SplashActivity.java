package com.coding.myblogapp;

import static android.content.ContentValues.TAG;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.coding.myblogapp.databinding.ActivitySplashBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInStatusCodes;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.util.ArrayList;
import android.os.Bundle;
import androidx.recyclerview.widget.RecyclerView;
import java.util.ArrayList;
import com.coding.myblogapp.Adapter;
import com.coding.myblogapp.ModelClass;


public class SplashActivity extends AppCompatActivity {

    private ActivitySplashBinding binding;
    private GoogleSignInOptions signInOptions;
    private GoogleSignInClient signInClient;
    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivitySplashBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        // Initialize your RecyclerView
        RecyclerView recyclerView = findViewById(R.id.recycler_view);

        recyclerView.setLayoutManager(new LinearLayoutManager(this));

        // Assuming you have already populated yourDataList with your data
        ArrayList<ModelClass> yourDataList = new ArrayList<>();

        // Initialize your Adapter with the data list
        Adapter adapter = new Adapter(yourDataList);

        // Set the Adapter to the RecyclerView
        recyclerView.setAdapter(adapter);

        // Attach the ItemTouchHelper to the RecyclerView for swipe actions
        adapter.attachItemTouchHelperToRecyclerView(recyclerView);


        setupSignIn();
    }

    private void setupSignIn() {
        auth = FirebaseAuth.getInstance();
        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(this, signInOptions);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();

        if (currentUser != null) {
            Log.d("SplashActivity", "User is already signed in, starting DrawerActivity.");
            new Handler().postDelayed(() -> {
                startActivity(new Intent(this, DrawerActivity.class));
                finish();
            }, 3000); // 3-second delay for splash screen
        } else {
            Log.d("SplashActivity", "No user signed in, initiating sign-in.");
            signIn();
        }
    }

    private void signIn() {
        Intent intent = signInClient.getSignInIntent();
        startActivityForResult(intent, 100);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 100) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                AuthCredential authCredential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
                auth.signInWithCredential(authCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            Log.d("SplashActivity", "Login Successful, starting DrawerActivity.");
                            Toast.makeText(SplashActivity.this, "Login Successful!", Toast.LENGTH_SHORT).show();
                            new Handler().postDelayed(() -> {
                                startActivity(new Intent(getApplicationContext(), DrawerActivity.class));
                                finish();
                            }, 3000); // 3-second delay for splash screen
                        } else {
                            Log.d("SplashActivity", "Login Failed.");
                            Toast.makeText(SplashActivity.this, "Login Failed!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            } catch (ApiException e) {
                Log.e(TAG, "Google Sign-In failed. Error code: " + e.getStatusCode(), e);
                if (e.getStatusCode() == GoogleSignInStatusCodes.NETWORK_ERROR) {
                    // Handle network error
                    Toast.makeText(this, "Network error. Please try again.", Toast.LENGTH_SHORT).show();
                }
            }
        }
    }
}
