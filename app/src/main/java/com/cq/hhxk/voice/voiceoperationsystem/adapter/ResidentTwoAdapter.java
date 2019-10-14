package com.cq.hhxk.voice.voiceoperationsystem.adapter;

import com.cq.hhxk.voice.voiceoperationsystem.R;
import com.cq.hhxk.voice.voiceoperationsystem.pojo.ResidentTwoPojo;
import com.em.baseframe.adapter.recyclerview.BaseQuickAdapter;
import com.em.baseframe.adapter.recyclerview.BaseViewHolder;
import java.util.List;

public class ResidentTwoAdapter extends BaseQuickAdapter<ResidentTwoPojo, BaseViewHolder> {

    public ResidentTwoAdapter(int layoutResId, List<ResidentTwoPojo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ResidentTwoPojo item) {
        helper.setText(R.id.name,item.getName());
    }
}
