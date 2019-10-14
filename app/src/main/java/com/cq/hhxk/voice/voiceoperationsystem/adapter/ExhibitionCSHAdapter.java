package com.cq.hhxk.voice.voiceoperationsystem.adapter;

import android.graphics.Color;

import com.cq.hhxk.voice.voiceoperationsystem.R;
import com.cq.hhxk.voice.voiceoperationsystem.pojo.ExhibitionCSHPojo;
import com.em.baseframe.adapter.recyclerview.BaseQuickAdapter;
import com.em.baseframe.adapter.recyclerview.BaseViewHolder;

import java.util.List;

public class ExhibitionCSHAdapter extends BaseQuickAdapter<ExhibitionCSHPojo, BaseViewHolder> {

    public ExhibitionCSHAdapter(int layoutResId, List<ExhibitionCSHPojo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ExhibitionCSHPojo item) {
        if (item.getColor() % 2 != 0) {
            helper.setBackgroundColor(R.id.lin, Color.parseColor("#0F1942"));
        } else {
            helper.setBackgroundColor(R.id.lin, Color.parseColor("#141F4C"));
        }
    }
}
