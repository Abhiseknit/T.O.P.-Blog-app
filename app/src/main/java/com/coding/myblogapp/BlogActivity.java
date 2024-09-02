package com.coding.myblogapp;

import android.content.Intent;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.coding.myblogapp.databinding.ActivityBlogBinding;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.HashMap;

public class BlogActivity extends AppCompatActivity {
    ActivityBlogBinding binding;
    String id;
    String title, desc, count;
    Integer n_count;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityBlogBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        // Retrieve the blog ID passed from the Adapter
        id = getIntent().getStringExtra("id");

        Log.d("BlogActivity", "Blog ID: " + id); // Debugging line

        if (id != null) {
            // Fetch and display the blog data
            showdata();
        } else {
            // Handle error: ID is missing
            binding.textView6.setText("Error: Blog ID is missing.");
        }
    }

    private void showdata() {
        FirebaseFirestore.getInstance().collection("Blogs").document(id)
                .addSnapshotListener(new EventListener<DocumentSnapshot>() {
                    @Override
                    public void onEvent(@Nullable DocumentSnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Handle the error
                            binding.textView6.setText("Error fetching data.");
                            return;
                        }
                        if (value != null && value.exists()) {
                            Log.d("BlogActivity", "Fetched Data: " + value.getData()); // Debugging line

                            Glide.with(getApplicationContext())
                                    .load(value.getString("img"))
                                    .into(binding.imageView3);

                            binding.textView5.setText(Html.fromHtml(
                                    "<font color = 'B7B7B7'> </font> <font color = '000000'>" + value.getString("authorname") + "</font>"
                            ));
                            binding.textView6.setText(value.getString("title"));
                            binding.textView7.setText(value.getString("description"));

                            title = value.getString("title");
                            desc = value.getString("description");
                            count = value.getString("share_count");

                            int i_count = Integer.parseInt(count);
                            n_count = i_count + 1;
                        } else {
                            // Handle the case where the document does not exist
                            binding.textView6.setText("Blog not found.");
                        }
                    }
                });

        binding.floatingActionButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_SEND);
                String shareBody = desc;
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_SUBJECT, title);
                intent.putExtra(Intent.EXTRA_TEXT, shareBody);
                startActivity(Intent.createChooser(intent, "Share Using"));

                HashMap<String, Object> map = new HashMap<>();
                map.put("share_count", String.valueOf(n_count));
                FirebaseFirestore.getInstance().collection("Blogs").document(id).update(map);
            }
        });

        binding.imageView4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onBackPressed();
            }
        });
    }
}
