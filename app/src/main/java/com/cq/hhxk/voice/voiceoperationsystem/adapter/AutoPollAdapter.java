package com.cq.hhxk.voice.voiceoperationsystem.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import com.cq.hhxk.voice.voiceoperationsystem.R;
import com.cq.hhxk.voice.voiceoperationsystem.pojo.AutoPollControllerPojo;

import java.util.List;

public class AutoPollAdapter extends RecyclerView.Adapter<AutoPollAdapter.BaseViewHolder> {
    private final List<AutoPollControllerPojo> mData;
    private int resource;

    public AutoPollAdapter(List<AutoPollControllerPojo> mData, int resource) {
        this.mData = mData;
        this.resource = resource;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(this.resource, parent, false);
        BaseViewHolder holder = new BaseViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        AutoPollControllerPojo data = mData.get(position % mData.size());
        if (data.getColor() % 2 != 0) {
            holder.lin.setBackgroundColor(Color.parseColor("#0F1942"));
        } else {
            holder.lin.setBackgroundColor(Color.parseColor("#141F4C"));
        }
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    class BaseViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout lin;
        public BaseViewHolder(View itemView) {
            super(itemView);
            lin = itemView.findViewById(R.id.lin);
        }
    }
}