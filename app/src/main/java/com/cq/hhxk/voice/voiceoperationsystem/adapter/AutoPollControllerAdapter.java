package com.cq.hhxk.voice.voiceoperationsystem.adapter;

import android.graphics.Color;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.cq.hhxk.voice.voiceoperationsystem.R;
import com.cq.hhxk.voice.voiceoperationsystem.pojo.AutoPollControllerPojo;
import com.orhanobut.logger.Logger;

import java.util.List;

public class AutoPollControllerAdapter extends RecyclerView.Adapter<AutoPollControllerAdapter.BaseViewHolder> {
    private final List<AutoPollControllerPojo> mData;

    public AutoPollControllerAdapter(List<AutoPollControllerPojo> mData) {
        this.mData = mData;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exhibition_rv_ven, parent, false);
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
        holder.title.setText(mData.get(position % mData.size()).getName());
        holder.person.setText(mData.get(position % mData.size()).getInfo());
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    class BaseViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout lin;
        TextView title,person;
        public BaseViewHolder(View itemView) {
            super(itemView);
            lin = itemView.findViewById(R.id.lin);
            title = itemView.findViewById(R.id.title);
            person = itemView.findViewById(R.id.person);
        }
    }
}