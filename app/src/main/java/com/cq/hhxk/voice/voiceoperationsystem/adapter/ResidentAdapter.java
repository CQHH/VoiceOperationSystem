package com.cq.hhxk.voice.voiceoperationsystem.adapter;

import com.cq.hhxk.voice.voiceoperationsystem.R;
import com.cq.hhxk.voice.voiceoperationsystem.pojo.ResidentPojo;
import com.em.baseframe.adapter.recyclerview.BaseQuickAdapter;
import com.em.baseframe.adapter.recyclerview.BaseViewHolder;
import com.orhanobut.logger.Logger;

import java.util.List;

public class ResidentAdapter extends BaseQuickAdapter<ResidentPojo, BaseViewHolder> {

    public ResidentAdapter(int layoutResId, List<ResidentPojo> data) {
        super(layoutResId, data);
    }

    @Override
    protected void convert(BaseViewHolder helper, ResidentPojo item) {
        switch (item.getIsSelection()){
            case 0:
                Logger.v("00000000000");
                helper.setVisible(R.id.image,false);
                break;
            case 1:
                Logger.v("11111111111");
                helper.setVisible(R.id.image,true);
                break;
        }
        helper.setText(R.id.number,item.getLineName());
        helper.setText(R.id.start,item.getStartName());
        helper.setText(R.id.end_text,item.getEndName());
    }
}
