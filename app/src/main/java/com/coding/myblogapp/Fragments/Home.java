package com.coding.myblogapp.Fragments;

import android.os.Bundle;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SearchView;

import com.coding.myblogapp.Adapter;
import com.coding.myblogapp.ModelClass;
import com.coding.myblogapp.databinding.FragmentHomeBinding;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.DocumentSnapshot;

import java.util.ArrayList;

public class Home extends Fragment {

    private FragmentHomeBinding binding;
    private Adapter adapter;
    private ArrayList<ModelClass> list;

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentHomeBinding.inflate(inflater, container, false);
        setupRv();
        return binding.getRoot();
    }

    private void setupRv() {
        list = new ArrayList<>();
        adapter = new Adapter(list);

        binding.rvBlogs.setLayoutManager(new LinearLayoutManager(getContext()));
        binding.rvBlogs.setAdapter(adapter);

        FirebaseFirestore.getInstance().collection("Blogs").orderBy("timestamp")
                .addSnapshotListener(new EventListener<QuerySnapshot>() {
                    @Override
                    public void onEvent(@Nullable QuerySnapshot value, @Nullable FirebaseFirestoreException error) {
                        if (error != null) {
                            // Handle error
                            return;
                        }

                        list.clear();
                        if (value != null) {
                            for (DocumentSnapshot snapshot : value.getDocuments()) {
                                ModelClass modelClass = snapshot.toObject(ModelClass.class);
                                if (modelClass != null) {
                                    modelClass.setId(snapshot.getId());  // Ensure the ID is set here
                                    list.add(modelClass);
                                }
                            }
                        }
                        adapter.notifyDataSetChanged();
                    }
                });

        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true);
        linearLayoutManager.setReverseLayout(true);
        binding.rvBlogs.setLayoutManager(linearLayoutManager);
        binding.rvBlogs.setAdapter(adapter);

        binding.searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                searchData(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchData(newText);
                return false;
            }
        });
    }

    private void searchData(String query) {
        ArrayList<ModelClass> filteredList = new ArrayList<>();
        for (ModelClass item : list) {
            if (item.getTitle().toLowerCase().contains(query.toLowerCase()) ||
                    item.getAuthorname().toLowerCase().contains(query.toLowerCase())) {
                filteredList.add(item);
            }
        }
        adapter.filter_list(filteredList);
    }
}
