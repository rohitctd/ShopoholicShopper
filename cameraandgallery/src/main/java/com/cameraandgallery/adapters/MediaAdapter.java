package com.cameraandgallery.adapters;


import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;


import com.bumptech.glide.Glide;
import com.google.android.cameraview.R;

import java.util.List;

import com.cameraandgallery.interfaces.RecyclerCallBack;

public class MediaAdapter extends RecyclerView.Adapter<MediaAdapter.MyViewHolder>{
    private final RecyclerCallBack recyclerCallBack;
    private List<String> bitmapList;
    private List<Boolean> selected;
    private Context context;

    class MyViewHolder extends RecyclerView.ViewHolder {
        private final FrameLayout rootView;
        ImageView thumbnail,check;

        MyViewHolder(View view) {
            super(view);
            thumbnail=(ImageView) view.findViewById(R.id.image);
            check=(ImageView) view.findViewById(R.id.image2);
            rootView=(FrameLayout) view.findViewById(R.id.image_root_view);
        }
    }

    public MediaAdapter(List<String> bitmapList, List<Boolean> selected, Context context, RecyclerCallBack recyclerCallBack) {
        this.bitmapList = bitmapList;
        this.context=context;
        this.selected=selected;
        this.recyclerCallBack=recyclerCallBack;
    }

    @NonNull
    @Override
    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_media_item, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(@NonNull final MyViewHolder holder, int position) {
        Glide.with(context).load("file://"+bitmapList.get(position)).override(100,100).crossFade().centerCrop().dontAnimate().skipMemoryCache(true).into(holder.thumbnail);
        if(selected!=null && selected.get(position).equals(true)){
            holder.check.setVisibility(View.VISIBLE);
            holder.check.setAlpha(0.5f);
        }else{
            holder.check.setVisibility(View.GONE);
        }
        holder.rootView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                recyclerCallBack.onClick(holder.getAdapterPosition(), holder.rootView);
            }
        });

    }

    @Override
   public int getItemCount() {
        return bitmapList.size();
    }
}

