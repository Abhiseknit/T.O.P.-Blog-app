package com.coding.myblogapp.Fragments;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.bumptech.glide.Glide;
import com.coding.myblogapp.R;
import com.coding.myblogapp.SplashActivity;
import com.coding.myblogapp.databinding.FragmentProfileBinding;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;


public class Profile extends Fragment {

    FragmentProfileBinding binding;
    GoogleSignInAccount account;
    GoogleSignInOptions signInOptions;
    GoogleSignInClient signInClient;

    public Profile() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
       binding = FragmentProfileBinding.inflate(inflater,container,false);
        // Inflate the layout for this fragment
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        intevar();
        super.onViewCreated(view, savedInstanceState);
    }

    private void intevar() {
        account= GoogleSignIn.getLastSignedInAccount(getContext());
        binding.uName.setText(account.getDisplayName());
        binding.uEmail.setText(account.getEmail());
        Glide.with(getContext()).load(account.getPhotoUrl()).into(binding.uImage);

        logoutuser();
    }

    private void logoutuser() {

        binding.btnBlogout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

        signInOptions = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        signInClient = GoogleSignIn.getClient(getContext(), signInOptions);
        new AlertDialog.Builder(getActivity())
                .setTitle("Log Out?")
                .setMessage("Are You Sure to Log Out From App?")
                .setCancelable(false)
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                }).setPositiveButton("Yes!", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        FirebaseAuth.getInstance().signOut();
                        signInClient.signOut().addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                dialog.dismiss();
                                startActivity(new Intent(getActivity().getApplicationContext(), SplashActivity.class));
                                getActivity().finish();
                            }
                        });

                    }
                }).show();
            }
        });
    }
    @Override
    public void onDestroy() {
        super.onDestroy();
        binding= null;
    }
}