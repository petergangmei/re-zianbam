package com.zianbam.yourcommunity.Fragments;


import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.zianbam.yourcommunity.Adapter.PostAdapter;
import com.zianbam.yourcommunity.Model.Post;
import com.zianbam.yourcommunity.R;

import java.util.ArrayList;


public class HomeFragment extends Fragment {
    private RecyclerView recyclerView;
    private PostAdapter postAdapter;
    private ArrayList<Post> postList;
    DatabaseReference reference, ref;
    ProgressBar progressBar ,prograssing;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
         View view = inflater.inflate(R.layout.fragment_home, container, false);

        progressBar = view.findViewById(R.id.progress_bar);
         recyclerView = view.findViewById(R.id.recycle_view_post);
        recyclerView.setHasFixedSize(true);
        final LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        linearLayoutManager.setReverseLayout(true);
        linearLayoutManager.setStackFromEnd(true);
        recyclerView.setLayoutManager(linearLayoutManager);
        postList = new ArrayList<>();
        postAdapter= new PostAdapter(getContext(), postList);
        recyclerView.setAdapter(postAdapter);
        readPostG();
         return view;
    }

    private void readPostG() {
        postList.clear();

        reference = FirebaseDatabase.getInstance().getReference("Posts");
        reference.keepSynced(true);
        Query query = reference.limitToLast(35);
        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                if (dataSnapshot.exists()){
                    postList.clear();
                    for (DataSnapshot snapshot:dataSnapshot.getChildren()){
                        Post p =  snapshot.getValue(Post.class);
                        if (p.getDeleted().equals("false")&&p.getReported()<5){
                            postList.add(p);
                        }
                    }

                    postAdapter.notifyDataSetChanged();

                    progressBar.setVisibility(View.GONE);
                }else {
                    Toast.makeText(getContext(), "No post available!", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {
            }
        });
    }



}
