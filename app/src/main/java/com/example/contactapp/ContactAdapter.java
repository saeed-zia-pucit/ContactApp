package com.example.contactapp;

import android.annotation.SuppressLint;
import android.text.style.ClickableSpan;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.RecyclerView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;


public class ContactAdapter extends RecyclerView.Adapter<ContactAdapter.ViewHolder>{
    private ArrayList<ContactEntity> listdata;
    private ClickListener clickListener;
    // RecyclerView recyclerView;
    public ContactAdapter(ArrayList<ContactEntity> listdata,ClickListener clickListener) {
        this.listdata = listdata;
        this.clickListener = clickListener;
    }
    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater layoutInflater = LayoutInflater.from(parent.getContext());
        View listItem= layoutInflater.inflate(R.layout.contact_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(listItem);
        return viewHolder;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, @SuppressLint("RecyclerView") int position) {
        final ContactEntity contact = listdata.get(position);
        holder.number.setText(listdata.get(position).getNumber());
        holder.name.setText(listdata.get(position).getName());
        holder.busNumber.setText(listdata.get(position).getBusNumber());

        holder.time.setText(listdata.get(position).getTime());
        if(listdata.get(position).getCallType() !=null && listdata.get(position).getCallType().equals("OUTGOING")){
         holder.arrow.setImageResource(R.drawable.ic_baseline_arrow_upward_24);
        }else {
            holder.arrow.setImageResource(R.drawable.ic_baseline_arrow_downward_24);

        }
        holder.call.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
               clickListener.onCall(position);
            }
        });
        holder.msg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            clickListener.onMsg(position);
            }
        });
       holder.itemView.setOnClickListener(new View.OnClickListener() {
           @Override
           public void onClick(View v) {
               clickListener.onItemClick(position);
           }
       });
    }


    @Override
    public int getItemCount() {
        return listdata.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        public TextView name,number,busNumber,time;
        public ImageView call,msg,arrow;

        public ViewHolder(View itemView) {
            super(itemView);

            this.name = (TextView) itemView.findViewById(R.id.person_name);
            this.number = (TextView) itemView.findViewById(R.id.number);
            this.busNumber = (TextView) itemView.findViewById(R.id.busNumber);
            this.call = (ImageView) itemView.findViewById(R.id.call_btn);
            this.msg = (ImageView) itemView.findViewById(R.id.msg_btn);
             this.time = itemView.findViewById(R.id.call_time);
             this.arrow = itemView.findViewById(R.id.call_arrow);

        }
    }

}
