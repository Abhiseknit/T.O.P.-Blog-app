package com.coding.myblogapp;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.CustomTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.ArrayList;
import java.util.HashMap;



public class Adapter extends RecyclerView.Adapter<Adapter.ViewHolder> {

    private ArrayList<ModelClass> list;

    public Adapter(ArrayList<ModelClass> list) {
        this.list = list;
        this.notifyDataSetChanged();
    }


    public void filter_list(ArrayList<ModelClass> filter_list) {
        list = filter_list;
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.row, parent, false);
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        ModelClass modelClass = list.get(position);
        holder.title.setText(modelClass.getTitle());
        holder.authorname.setText(modelClass.getAuthorname());
        holder.date.setText(modelClass.getDate());
        holder.share_count.setText(modelClass.getShare_count());

        // Load image with resizing and limiting bitmap size using Glide
        Glide.with(holder.authorname.getContext())
                .asBitmap()
                .load(modelClass.getImg())
                .override(600, 400) // Resize to a smaller size
                .fitCenter() // Scale the image uniformly
                .into(new CustomTarget<Bitmap>() {
                    @Override
                    public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                        holder.img.setImageBitmap(resource);
                    }

                    @Override
                    public void onLoadCleared(@Nullable Drawable placeholder) {
                        // Handle clearing resources if needed
                    }
                });

        // Item click to open BlogActivity
        holder.itemView.setOnClickListener(view -> {
            Intent intent = new Intent(holder.authorname.getContext(), BlogActivity.class);
            intent.putExtra("id", modelClass.getId());
            holder.authorname.getContext().startActivity(intent);
        });
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        ImageView img;
        TextView date, title, authorname, share_count;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            img = itemView.findViewById(R.id.imageView3);
            date = itemView.findViewById(R.id.hdate);
            authorname = itemView.findViewById(R.id.textView6);
            title = itemView.findViewById(R.id.textView7);
            share_count = itemView.findViewById(R.id.textView8);
        }
    }

    // Method to show the update dialog when an item is swiped
    public void showUpdateDialog(ModelClass modelClass, int position, Context context) {
        final Dialog u_dialog = new Dialog(context);
        u_dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        u_dialog.setCancelable(false);
        u_dialog.getWindow().setBackgroundDrawableResource(android.R.color.transparent);
        u_dialog.setContentView(R.layout.updatedialog);
        u_dialog.show();

        // Initialize UI components
        EditText title = u_dialog.findViewById(R.id.b_title);
        EditText description = u_dialog.findViewById(R.id.b_description);
        EditText authorname = u_dialog.findViewById(R.id.b_authorname);
        TextView dialogbutton = u_dialog.findViewById(R.id.btn_bpublish);

        // Set current values in the EditText fields
        title.setText(modelClass.getTitle());
        description.setText(modelClass.getDescription());
        authorname.setText(modelClass.getAuthorname());

        // Update button click listener
        dialogbutton.setOnClickListener(view -> {
            if (title.getText().toString().isEmpty()) {
                title.setError("This Field is required!!");
            } else if (description.getText().toString().isEmpty()) {
                description.setError("This Field is required!!");
            } else if (authorname.getText().toString().isEmpty()) {
                authorname.setError("This Field is required!!");
            } else {
                HashMap<String, Object> map = new HashMap<>();
                map.put("title", title.getText().toString());
                map.put("description", description.getText().toString());
                map.put("authorname", authorname.getText().toString());

                FirebaseFirestore.getInstance().collection("Blogs").document(modelClass.getId()).update(map)
                        .addOnCompleteListener(task -> {
                            if (task.isSuccessful()) {
                                u_dialog.dismiss();
                                notifyItemChanged(position); // Refresh the item to show updated content
                            } else {
                                Log.e("Adapter", "Update failed: " + task.getException());
                            }
                        });
            }
        });
    }

    // Attach ItemTouchHelper to the RecyclerView

    public void attachItemTouchHelperToRecyclerView(RecyclerView recyclerView) {
        ItemTouchHelper.SimpleCallback simpleCallback = new ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT) {
            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                return false; // No drag & drop functionality required
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                int position = viewHolder.getAdapterPosition();
                ModelClass modelClass = list.get(position);
                Context context = viewHolder.itemView.getContext();

                if (direction == ItemTouchHelper.RIGHT) {
                    // Show update dialog on swipe right
                    showUpdateDialog(modelClass, position, context);
                } else if (direction == ItemTouchHelper.LEFT) {
                    // Handle swipe left for deletion (optional)
                    AlertDialog.Builder deleteDialogBuilder = new AlertDialog.Builder(context);
                    deleteDialogBuilder.setTitle("Are you sure you want to delete this?");
                    deleteDialogBuilder.setPositiveButton("Yes, Delete", (dialogInterface, i) -> {
                        FirebaseFirestore.getInstance().collection("Blogs").document(modelClass.getId()).delete()
                                .addOnCompleteListener(task -> {
                                    if (task.isSuccessful()) {
                                        list.remove(position);
                                        notifyItemRemoved(position);
                                    }
                                });
                    });
                    deleteDialogBuilder.setNegativeButton("Cancel", (dialogInterface, i) -> {
                        notifyItemChanged(position); // Refresh the item to undo the swipe
                    });
                    deleteDialogBuilder.show();
                }
            }
        };

        // Attach the ItemTouchHelper to the RecyclerView
        new ItemTouchHelper(simpleCallback).attachToRecyclerView(recyclerView);
    }
}
