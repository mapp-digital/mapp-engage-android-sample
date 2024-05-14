package com.appoxee.testapp;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;

import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.appoxee.internal.inapp.model.APXInboxMessage;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.resource.drawable.DrawableTransitionOptions;

import java.io.InputStream;
import java.net.URL;
import java.util.List;

/**
 * Created by Varun on 4/3/2018.
 */


public class InboxAdapter extends RecyclerView.Adapter<InboxAdapter.MyViewHolder> {

    private List<APXInboxMessage> inboxList;

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public TextView subject, summary, time, status;
        public ImageView icon;

        public MyViewHolder(View view) {
            super(view);
            subject = (TextView) view.findViewById(R.id.subject);
            summary = (TextView) view.findViewById(R.id.summary);
            icon = (ImageView) view.findViewById(R.id.imageUrl);
            time = (TextView) view.findViewById(R.id.time);
            status=view.findViewById(R.id.status);
        }
    }


    public InboxAdapter(List<APXInboxMessage> inboxList) {
        this.inboxList = inboxList;
    }

    @Override
    public MyViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View itemView = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.row_for_inbox_list, parent, false);

        return new MyViewHolder(itemView);
    }

    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        APXInboxMessage inboxMessage = inboxList.get(position);
        holder.subject.setText(inboxMessage.getSubject());
        holder.summary.setText(inboxMessage.getSummary());
        holder.status.setText(inboxMessage.getStatus()!=null ? inboxMessage.getStatus().toLowerCase() : "");
        if (inboxMessage.getSentDate() != null) {
            holder.time.setText(inboxMessage.getSentDate().toString());
        }
        Glide.with(holder.itemView).load(inboxMessage.getIconUrl()).into(holder.icon);

    }

    @Override
    public int getItemCount() {
        return inboxList.size();
    }


}

