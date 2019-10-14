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
import com.cq.hhxk.voice.voiceoperationsystem.pojo.ExhibitionCSHPojo;

import java.util.List;

public class AutoPollExhibitionSchAdapter extends RecyclerView.Adapter<AutoPollExhibitionSchAdapter.BaseViewHolder> {
    private final List<ExhibitionCSHPojo> mData;

    public AutoPollExhibitionSchAdapter(List<ExhibitionCSHPojo> mData) {
        this.mData = mData;
    }

    @Override
    public BaseViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.item_exhibition_rv_sch, parent, false);
        BaseViewHolder holder = new BaseViewHolder(view);
        return holder;
    }

    @Override
    public void onBindViewHolder(BaseViewHolder holder, int position) {
        ExhibitionCSHPojo data = mData.get(position % mData.size());
        if (data.getColor() % 2 != 0) {
            holder.lin.setBackgroundColor(Color.parseColor("#0F1942"));
        } else {
            holder.lin.setBackgroundColor(Color.parseColor("#141F4C"));
        }
        holder.date.setText(mData.get(position % mData.size()).getData());
        holder.info.setText(mData.get(position % mData.size()).getInfo());
    }

    @Override
    public int getItemCount() {
        return Integer.MAX_VALUE;
    }

    class BaseViewHolder extends RecyclerView.ViewHolder{
        RelativeLayout lin;
        TextView date,info;
        public BaseViewHolder(View itemView) {
            super(itemView);
            lin = itemView.findViewById(R.id.lin);
            date = itemView.findViewById(R.id.date);
            info = itemView.findViewById(R.id.info);
        }
    }
}