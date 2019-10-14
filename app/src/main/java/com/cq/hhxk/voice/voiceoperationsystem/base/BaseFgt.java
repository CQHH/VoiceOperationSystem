package com.cq.hhxk.voice.voiceoperationsystem.base;

import android.view.View;

import com.em.baseframe.base.BaseLazyFragment;
import com.em.baseframe.util.AppJsonUtil;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public abstract class BaseFgt extends BaseLazyFragment {

    //设置是否弹出错误信息
    public boolean isShowOnFailureToast = true;

    @Override
    protected boolean setIsInitRequestData() {
        return true;
    }

    @Override
    public void onUserVisible() {

    }

    @Override
    public void onUserInvisible() {
        super.onUserInvisible();

    }

    @Override
    public void btnClick(View view) {

    }

    /**
     * 弹出错误信息
     * @param result
     * @param call
     * @param response
     * @param what
     */
    @Override
    public void onFailure(String result, Call<ResponseBody> call, Response<ResponseBody> response, int what) {
        super.onFailure(result, call, response, what);
        if (isShowOnFailureToast){
            String msg = AppJsonUtil.getString(result, "msg");
            if (msg != null) {
                showErrorToast(msg);
            }
        }
    }

}
