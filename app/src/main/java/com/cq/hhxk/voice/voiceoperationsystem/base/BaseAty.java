package com.cq.hhxk.voice.voiceoperationsystem.base;


import android.content.Context;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.em.baseframe.base.BaseActivity;
import com.em.baseframe.util.AppJsonUtil;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Response;

public abstract class BaseAty extends BaseActivity {

    public boolean isShowOnFailureToast = true;

    @Override
    public boolean setIsInitRequestData() {
        return false;
    }

    @Override
    public void btnClick(View view) {

    }

    public void initToolbar(Toolbar toolbar, String title) {
        TextView tv_title = (TextView) toolbar.getChildAt(toolbar.getChildCount() - 1);

        tv_title.setText(title);

        ImageView iv_return = (ImageView) toolbar.getChildAt(toolbar.getChildCount() - 2);

        setSupportActionBar(toolbar);
        iv_return.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //  setHasAnimiation(false);
                View view = getWindow().peekDecorView();
                if (view != null) {
                    InputMethodManager inputmanger = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    inputmanger.hideSoftInputFromWindow(view.getWindowToken(), 0);
                }
                finish();
            }
        });
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
