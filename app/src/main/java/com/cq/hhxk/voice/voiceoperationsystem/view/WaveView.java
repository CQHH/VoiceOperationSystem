package com.cq.hhxk.voice.voiceoperationsystem.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.ValueAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.LinearGradient;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;


import com.cq.hhxk.voice.voiceoperationsystem.R;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * @title  语音播放曲线
 * @date   2019/07/31
 * @author enmaoFu
 */
public class WaveView extends View {
    private static final String TAG = WaveView.class.getSimpleName();
    private int mWidth, mHeight;
    private Paint mPaint;
    private int MAX;
    private float amplitude = 0.6f;
    private float wAmplitude = 0.6f;

    private int[] COLORS = {R.color.green, R.color.blue, R.color.pink};
    private Random random;

    private static final float MAX_VOLUME = 30;
    private List<Wave> waves;

    private int[] lineColors = new int[]{0xFF111111, 0xFFFFFFFF, 0xFFFFFFFF, 0xFF111111};
    private float[] linepositions = new float[]{4f, 4f, 4f, 4f};
    private boolean running;
    private long l;

    private int isOpen = 0;

    public WaveView(Context context) {
        super(context);
        init();
    }

    public WaveView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        mHeight = h;
        mWidth = w;

        MAX = h * 2 / 3;
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        mHeight = getMeasuredHeight();
        mWidth = getMeasuredWidth();
    }

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:

                    running = true;
                    waveCount = 10;
                    createWave();
                    setWaveCount(waveCount);
                    postInvalidate();
                    break;
            }
        }
    };

    public int getIsOpen() {
        return isOpen;
    }

    public void setIsOpen(int isOpen) {
        this.isOpen = isOpen;
    }

    public void startAnim() {
        isOpen = 1;
        if (!running)
            handler.sendEmptyMessageDelayed(1, 100);
    }

    private int waveCount = 10;

    private void setWaveCount(int count) {
        int size = waves.size();
        if (count > size) {
            count = size;
        }
        for (int i = 0; i < size; i++) {
            if (i < count) {
                waves.get(i).playing = true;
            } else {
                waves.get(i).playing = false;
            }
        }
    }

    public void setVolume(float volume) {
        if (volume <= 3) {
            amplitude = 0.5f;
            waveCount = 10;
            wAmplitude = 1.2f;
        } else if (volume > 3 && volume < 10) {
            amplitude = 0.7f;
            waveCount = 10;
            wAmplitude = 1;
        } else if (volume > 10 && volume < 20) {
            amplitude = 0.9f;
        } else if (volume > 20) {
            waveCount = 10;
            amplitude = 1.2f;
        }
        Log.e("AA-->", "setVolume: " + amplitude + "--" + volume);

        setWaveCount(waveCount);
    }

    public void stopAnim() {
        running = false;
        waveCount = 0;
        isOpen = 0;
        setWaveCount(waveCount);
    }

    private void init() {
        mPaint = new Paint();
        random = new Random();

        mPaint.setAntiAlias(true);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
    }

    private void createWave() {
        if (waves == null) {
            waves = new ArrayList<>();
        }
        waves.clear();
        for (int i = 0; i < 17; i++) {
            Wave wave = new Wave();
            initAnimator(wave);
            waves.add(wave);
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        // TODO Auto-generated method stub
        super.onDraw(canvas);

        if (!running) {
            return;
        }
        l = System.currentTimeMillis();
        Log.e(TAG, "onDraw: " + l);
        drawLine(canvas);

        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.ADD));
        for (Wave wave : waves) {
            if (wave.playing) {

                wave.draw(canvas, mPaint);
            }
        }
        Log.e(TAG, "onDraw: " + (System.currentTimeMillis() - l));
        postInvalidateDelayed(20);
    }

    private void initAnimator(final Wave waveBean) {
        ValueAnimator animator = ValueAnimator.ofInt(10, waveBean.maxHeight);
        animator.setDuration(waveBean.duration);
        animator.setInterpolator(new AccelerateDecelerateInterpolator());
        animator.addUpdateListener(new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                waveBean.waveHeight = (int) animation.getAnimatedValue();
                if (waveBean.waveHeight > waveBean.maxHeight / 2) {
                    waveBean.waveHeight = waveBean.maxHeight - waveBean.waveHeight;
                }
//                Log.e("AAA-->", "initAnimator: " + waveBean.toString());
            }
        });
        animator.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationEnd(Animator animation) {
                super.onAnimationEnd(animation);
                if (waveBean.playing) {

                    waveBean.respawn();
                    initAnimator(waveBean);
                }
//                waves.remove(waveBean);
            }
        });
        animator.start();
    }

    class Wave {
        boolean playing = false;
        int maxHeight;
        int maxWidth;
        int color;
        float speed = 0.3f;
        double seed, open_class;

        int waveHeight;
        int duration;
        Paint mPaint;

        public Wave() {
            mHeight = getMeasuredHeight();
            mWidth = getMeasuredWidth();
            respawn();
        }

        public void respawn() {
            this.seed = Math.random();  // 位置
            maxWidth = (random.nextInt(mWidth / 16) + mWidth * 3 / 11);
            if (seed <= 0.2) {
                maxHeight = random.nextInt(MAX / 6) + MAX / 5;
                open_class = 2;
            } else if (seed <= 0.3 && seed > 0.2) {
                maxHeight = random.nextInt(MAX / 3) + MAX * 1 / 5;
                open_class = 3;
            } else if (seed > 0.3 && seed <= 0.7) {
                maxHeight = random.nextInt(MAX / 2) + MAX * 2 / 5;
                open_class = 3;
            } else if (seed > 0.7 && seed <= 0.8) {
                maxHeight = random.nextInt(MAX / 3) + MAX * 1 / 5;
                open_class = 3;
            } else if (seed > 0.8) {
                maxHeight = random.nextInt(MAX / 6) + MAX / 5;
                open_class = 2;
            }
            duration = random.nextInt(1000) + 1000;
            color = COLORS[random.nextInt(3)];
        }

        double equation(double i) {
            i = Math.abs(i);
            double y = -1 * amplitude
                    * Math.pow(1 / (1 + Math.pow(open_class * i, 2)), 2);
            return y;
        }

        public void draw(Canvas canvas, Paint mPaint) {
            this.mPaint = mPaint;

            this._draw(1, canvas);
        }

        private void _draw(int m, Canvas canvas) {

            Path path = new Path();
            Path pathN = new Path();
            path.moveTo(mWidth / 2, mHeight / 2);
            pathN.moveTo(mWidth / 2, mHeight / 2);
            double x_base = mWidth / 2  // 波浪位置
                    + (-mWidth / 6 + this.seed
                    * (mWidth / 3));
            double y_base = mHeight / 2;

            double x, y, x_init = 0;
            double i = -1;
            while (i <= 1) {
                x = x_base + i * maxWidth * wAmplitude;
                double function = equation(i) * waveHeight;
                y = y_base + function;
                if (x_init > 0 || x > 0) {
                    x_init = mWidth / 4;
                }
                if (y > 0.1) {
                    path.lineTo((float) x, (float) y);
                    pathN.lineTo((float) x, (float) ((float) y_base - function));
                }
                i += 0.01;
            }
            mPaint.setColor(getResources().getColor(color));
            canvas.drawPath(path, mPaint);
            canvas.drawPath(pathN, mPaint);

        }

        @Override
        public String toString() {
            return "Wave{" +
                    "maxHight=" + maxHeight +
                    ", maxWidth=" + maxWidth +
                    ", color=" + color +
                    ", speed=" + speed +
                    ", amplitude=" + amplitude +
                    ", seed=" + seed +
                    ", open_class=" + open_class +
                    ", mPaint=" + mPaint +
                    '}';
        }
    }

    private void drawLine(Canvas canvas) {
        canvas.save();
        LinearGradient shader = new LinearGradient(
                mWidth, 0,
                mWidth * 39, 0,
                lineColors,
                linepositions,
                Shader.TileMode.MIRROR);
        mPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_OVER));
        mPaint.setShader(shader);
        mPaint.setStrokeWidth(2);
        canvas.drawLine(mWidth, mHeight / 2, mWidth * 39, mHeight / 2, mPaint);
        mPaint.setXfermode(null);
        mPaint.setShader(null);
        mPaint.clearShadowLayer();
        canvas.restore();
    }
}


