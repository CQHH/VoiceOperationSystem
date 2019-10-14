package com.cq.hhxk.voice.voiceoperationsystem.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.cq.hhxk.voice.voiceoperationsystem.R;
import com.cq.hhxk.voice.voiceoperationsystem.pojo.AutoPollPersonPojo;

import java.util.List;

public class AutoPollPersonAdapter extends RecyclerView.Adapter<AutoPollPersonAdapter.BaseViewHolder> {
    private final List<AutoPollPersonPojo> mData;

    public AutoPollPersonAdapter(List<AutoPollPersonPojo> mData) {
        this.mData = mData;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_comprehensive_personnel, parent, false);
        BaseViewHolder holder = new BaseViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        AutoPollPersonPojo data = mData.get(position % mData.size());
        if (data.getColor() % 2 != 0) {
            holder.lin.setBackgroundColor(Color.parseColor("#0F1942"));
        } else {
            holder.lin.setBackgroundColor(Color.parseColor("#141F4C"));
        }
        holder.name.setText(mData.get(position % mData.size()).getName());
        holder.info.setText(mData.get(position % mData.size()).getInfo());
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    class BaseViewHolder extends RecyclerView.ViewHolder{
        LinearLayout lin;
        TextView name,info;
        public BaseViewHolder(View itemView) {
            super(itemView);
            lin = itemView.findViewById(R.id.lin);
            name = itemView.findViewById(R.id.name);
            info = itemView.findViewById(R.id.info);
        }
    }
}