package com.coding.myblogapp.Fragments;

import android.Manifest;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.provider.Settings;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.coding.myblogapp.databinding.FragmentPublishBinding;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.karumi.dexter.Dexter;
import com.karumi.dexter.MultiplePermissionsReport;
import com.karumi.dexter.PermissionToken;
import com.karumi.dexter.listener.PermissionRequest;
import com.karumi.dexter.listener.multi.MultiplePermissionsListener;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class Publish extends Fragment {

    FragmentPublishBinding binding;
    Uri filepath;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = FragmentPublishBinding.inflate(inflater, container, false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        selectImage();
    }

    private void selectImage() {
        binding.bselectimage.setOnClickListener(view -> {
            Intent intent = new Intent();
            intent.setType("image/*");
            intent.setAction(Intent.ACTION_GET_CONTENT);
            startActivityForResult(Intent.createChooser(intent, "Select Your Image!"), 101);
        });
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 101 && resultCode == Activity.RESULT_OK && data != null && data.getData() != null) {
            filepath = data.getData();
            binding.imgThumbnail.setVisibility(View.VISIBLE);
            binding.imgThumbnail.setImageURI(filepath);
            binding.bselectimage.setVisibility(View.INVISIBLE);
            binding.textView4.setVisibility(View.INVISIBLE);
            uploaddata(filepath);
        }
    }

    private void uploaddata(Uri filepath) {
        binding.btnBpublish.setOnClickListener(view -> {
            Dexter.withActivity(getActivity())
                    .withPermissions(
                            Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE
                    )
                    .withListener(new MultiplePermissionsListener() {
                        @Override
                        public void onPermissionsChecked(MultiplePermissionsReport report) {
                            if (report.areAllPermissionsGranted()) {
                                uploadDataWithProgress(filepath);
                            }

                            if (report.isAnyPermissionPermanentlyDenied()) {
                                showSettingDialog();  // Method implemented below
                            }
                        }

                        @Override
                        public void onPermissionRationaleShouldBeShown(List<PermissionRequest> permissions, PermissionToken token) {
                            token.continuePermissionRequest();
                        }
                    })
                    .withErrorListener(dexterError -> getActivity().finish())
                    .check();
        });
    }

    private void uploadDataWithProgress(Uri filepath) {
        if (binding.bTitle.getText().toString().isEmpty()) {
            binding.bTitle.setError("This Field is required!!");
        } else if (binding.bDescription.getText().toString().isEmpty()) {
            binding.bDescription.setError("This Field is required!!");
        } else if (binding.bAuthorname.getText().toString().isEmpty()) {
            binding.bAuthorname.setError("This Field is required!!");
        } else {
            ProgressDialog pd = new ProgressDialog(getContext());
            pd.setTitle("Uploading...");
            pd.setMessage("Please, wait for a while.");
            pd.setCancelable(false);
            pd.show();

            String title = binding.bTitle.getText().toString();
            String description = binding.bDescription.getText().toString();
            String authorname = binding.bAuthorname.getText().toString();

            if (filepath != null) {
                FirebaseStorage storage = FirebaseStorage.getInstance();
                StorageReference reference = storage.getReference().child("image/" + filepath.getLastPathSegment() + ".jpg");
                reference.putFile(filepath).addOnSuccessListener(taskSnapshot -> {
                    reference.getDownloadUrl().addOnCompleteListener(task -> {
                        String file_url = task.getResult().toString();
                        SimpleDateFormat sdf = new SimpleDateFormat("dd MMM", Locale.getDefault());
                        String final_date = sdf.format(new Date());

                        HashMap<String, String> map = new HashMap<>();
                        map.put("title", title);
                        map.put("description", description);
                        map.put("authorname", authorname);
                        map.put("date", final_date);
                        map.put("timestamp", String.valueOf(System.currentTimeMillis()));
                        map.put("img", file_url);
                        map.put("share_count", "0");

                        FirebaseFirestore.getInstance().collection("Blogs").document().set(map).addOnCompleteListener(task1 -> {
                            if (task1.isSuccessful()) {
                                pd.dismiss();
                                Toast.makeText(getContext(), "Post Uploaded!", Toast.LENGTH_SHORT).show();
                                resetUI();
                            } else {
                                pd.dismiss();
                                Toast.makeText(getContext(), "Upload Failed!", Toast.LENGTH_SHORT).show();
                            }
                        });
                    });
                }).addOnFailureListener(e -> {
                    pd.dismiss();
                    Toast.makeText(getContext(), "Upload Failed!", Toast.LENGTH_SHORT).show();
                });
            } else {
                pd.dismiss();
                Toast.makeText(getContext(), "No Image Selected!", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void showSettingDialog() {
        new androidx.appcompat.app.AlertDialog.Builder(getContext())
                .setTitle("Permission Required")
                .setMessage("Please enable the required permissions in Settings to continue.")
                .setPositiveButton("Go to Settings", (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                            Uri.fromParts("package", getActivity().getPackageName(), null));
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                })
                .setNegativeButton("Cancel", (dialog, which) -> dialog.dismiss())
                .show();
    }

    private void resetUI() {
        binding.imgThumbnail.setVisibility(View.INVISIBLE);
        binding.bselectimage.setVisibility(View.VISIBLE);
        binding.textView4.setVisibility(View.VISIBLE);
        binding.bTitle.setText("");
        binding.bDescription.setText("");
        binding.bAuthorname.setText("");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        binding = null;
    }
}
