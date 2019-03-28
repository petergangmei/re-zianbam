package com.zianbam.yourcommunity.Adapter;
import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.DataSource;
import com.bumptech.glide.load.engine.GlideException;
import com.bumptech.glide.request.RequestListener;
import com.bumptech.glide.request.target.Target;
import com.github.chrisbanes.photoview.PhotoView;
import com.zianbam.yourcommunity.CommentsActivity;
import com.zianbam.yourcommunity.Model.Post;
import com.zianbam.yourcommunity.R;

import java.util.List;


public class GridAdapter extends RecyclerView.Adapter<GridAdapter.ViewHolder> {


    private Context context;
    private List<Post> posts;

    public GridAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }


    @NonNull
    @Override
    public GridAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        View view = LayoutInflater.from(context).inflate(R.layout.gridview_item,viewGroup,false);
        return new GridAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final GridAdapter.ViewHolder viewHolder, int i) {
        final Post post = posts.get(i);
        Glide.with(context).load(post.getImageUrl()).listener(new RequestListener<Drawable>() {
            @Override
            public boolean onLoadFailed(@Nullable GlideException e, Object model, Target<Drawable> target, boolean isFirstResource) {
               viewHolder.progressBar.setVisibility(View.GONE);
                return false;
            }

            @Override
            public boolean onResourceReady(Drawable resource, Object model, Target<Drawable> target, DataSource dataSource, boolean isFirstResource) {
                viewHolder.progressBar.setVisibility(View.GONE);
                return false;
            }
        }).into(viewHolder.imageView);

        //get POST Detail
        viewHolder.imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, CommentsActivity.class);
                intent.putExtra("postid", post.getPostid());
                intent.putExtra("publisherid", post.getPublisher());
                context.startActivity(intent);
            }
        });

        viewHolder.imageView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                Toast.makeText(context, "hey", Toast.LENGTH_SHORT).show();
                Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.dialog_custom_layout);
                PhotoView photoView = dialog.findViewById(R.id.imageView);
                Glide.with(context).load(post.getImageUrl()).placeholder(R.drawable.image_placeholder).into(photoView);
                dialog.show();
                return false;
            }
        });


    }

    @Override
    public int getItemCount() {
        return posts.size();
    }

    public class ViewHolder extends  RecyclerView.ViewHolder {
        ImageView imageView;
        ProgressBar progressBar;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.progress_bar);
            imageView = itemView.findViewById(R.id.image_view);
        }

    }
}
