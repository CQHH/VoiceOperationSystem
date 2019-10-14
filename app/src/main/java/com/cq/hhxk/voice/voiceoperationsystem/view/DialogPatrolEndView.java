package com.cq.hhxk.voice.voiceoperationsystem.view;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import com.cq.hhxk.voice.voiceoperationsystem.R;

/**
 * @title  工作人员巡检导航结束弹框
 * @date   2019/07/31
 * @author enmaoFu
 */
public class DialogPatrolEndView extends Dialog {

    /**
     * 从下往上滑动动画
     */
    public static final int DIALOG_ANIM_SLID_BOTTOM = com.em.baseframe.R.style.DialogAnimationSlidBottom;
    /**
     * 对话框宽度所占屏幕宽度的比例
     */
    public static final float WIDTHFACTOR = 0.55f;

    /**
     * 对话框
     */
    private Window window;

    private TextView btnView;

    public DialogPatrolEndView(Context context) {
        super(context);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.setCancelable(true);
        setContentView(R.layout.dialog_patrol_end);

        window = this.getWindow();
        //是否系统级弹框
        if (false) {
            window.setType(WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
        }
        // 获取屏幕宽度
        DisplayMetrics metrics = new DisplayMetrics();
        window.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        int screenwidth = metrics.widthPixels;
        int width = 0;
        if (WIDTHFACTOR > 0) {
            width = (int) (screenwidth * WIDTHFACTOR);
        } else {
            width = (int) (screenwidth * WIDTHFACTOR);
        }
        // 设置对话框宽度
        window.getAttributes().width = width;
        window.setBackgroundDrawableResource(R.drawable.dialog_navi_end_shape);
        window.setNavigationBarColor(Color.parseColor("#00ffffff"));

        init();

    }

    public void init(){
        btnView = (TextView)findViewById(R.id.but);
    }

    public void setBtnOnClick(View.OnClickListener clickListener){
        btnView.setOnClickListener(clickListener);
    }

    /**
     * 给对话框设置动画
     */
    public void setDialogAnimation() {
        this.getWindow().setWindowAnimations(DIALOG_ANIM_SLID_BOTTOM);
    }


}
