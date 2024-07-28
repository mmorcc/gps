package com.lianxin.location.demo.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.lianxin.location.demo.R;
import com.lianxin.location.demo.bean.Device;
import com.lianxin.location.demo.utils.CallBack;
import com.lianxin.location.demo.utils.Util;

import java.util.List;

public class GoodAdapter extends RecyclerView.Adapter<GoodAdapter.ViewHolder> {
    private final Context conext;
    private List<Device> myData;
    private CallBack myCallback;
    private int selectIndex=-1;
    static class ViewHolder extends RecyclerView.ViewHolder{

        ImageView img,star;
        TextView name,dis;
        TextView des;
        View parent;

        public ViewHolder(@NonNull View view) {
            super(view);
//            img = view.findViewById(R.id.img);
            name = view.findViewById(R.id.name);
            dis = view.findViewById(R.id.dis);
            des = view.findViewById(R.id.des);
            star = view.findViewById(R.id.star);
            parent=view;
        }
    }

    public GoodAdapter(Context conext, List<Device> myData, CallBack cb) {
        this.myData = myData;
        this.conext=conext;
        this.myCallback=cb;
    }
    public void setData(List<Device> myData){
        this.myData = myData;;
        selectIndex=-1;
        this.notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_good_item,parent,false);
        final ViewHolder viewHolder = new ViewHolder(view);
        viewHolder.star.setOnClickListener(v->{
            int position = viewHolder.getAdapterPosition();
            Device good = myData.get(position);
            if(Util.IM.equals(good.IM)){
                Util.IM="";
            }else {
                Util.IM=good.IM;
            }
            notifyDataSetChanged();
            if(myCallback!=null){
                myCallback.onDo(good.IM);
            }
        });

        return viewHolder;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Device mDevice = myData.get(position);
//        holder.name.setText(mDevice.getName());
        holder.des.setText(mDevice.IM);
        if(mDevice.distance>=0){
            String formatted = String.format("%.3f", mDevice.distance);
            holder.dis.setText(formatted+" KM");
        }else {
            holder.dis.setText("");
        }
//        Glide.with(conext).load(mDevice.getImgs()).into(holder.img);
        if(Util.IM.equals(mDevice.IM)){
            holder.star.setImageResource(R.drawable.stars);
        }else {
            holder.star.setImageResource(R.drawable.star);
        }
        if(position==selectIndex){
            holder.parent.setBackground(conext.getDrawable(R.drawable.good_select));
        }else {
            holder.parent.setBackground(conext.getDrawable(R.drawable.good_nomarl));
        }
    }
    @Override
    public int getItemCount(){
        return myData.size();
    }
}
