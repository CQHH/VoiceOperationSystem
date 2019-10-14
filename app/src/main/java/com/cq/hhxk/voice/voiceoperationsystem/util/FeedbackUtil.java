package com.cq.hhxk.voice.voiceoperationsystem.util;

import java.util.HashMap;
import java.util.Map;

/**
 * @title  随机反馈模板
 * @date   2019/07/23
 * @author enmaoFu
 */
public class FeedbackUtil {

    /**
     * 普通反馈集合
     */
    private static Map<Integer,String> ordinaryList;

    /**
     * 打开应用反馈集合
     */
    private static Map<Integer,String> openList;

    /**
     * 导航反馈集合
     */
    private static Map<Integer,String> navigationList;

    /**
     * 唤醒反馈集合
     */
    private static Map<Integer,String> waleupList;

    private static void initList(){
        ordinaryList = new HashMap<>();
        ordinaryList.put(1,"我现在理解能力有限，换个方式问我吧。");
        ordinaryList.put(2,"阁下的问题好高深呀，能不能换个简单点的。");
        ordinaryList.put(3,"我不太明白这个问题。");

        openList = new HashMap<>();
        openList.put(1,"没有这个应用，换一个打开吧。");
        openList.put(2,"找不到这个应用，先看看安装了什么应用吧。");
        openList.put(3,"没有这个应用，试试打开日历。");

        navigationList = new HashMap<>();
        navigationList.put(1,"你要去哪儿？我不是很明白。");
        navigationList.put(2,"没有找到这个地点，换一个吧。");
        navigationList.put(3,"你说的地点在地球外吧！");

        waleupList = new HashMap<>();
        waleupList.put(1,"我在，什么事儿？");
        waleupList.put(2,"需要我做什么？");
        waleupList.put(3,"请讲。");
    }

    /**
     * 得到反馈
     * @param type 1：普通反馈，2：打开应用反馈，3：导航反馈，4：唤醒反馈
     * @return
     */
    public static String getFeedbackText(int type){
        initList();
        String feedbackText = "";
        int random = (int)(1+Math.random()*(3));
        switch (type){
            case 1:
                feedbackText = ordinaryList.get(random);
                break;
            case 2:
                feedbackText = openList.get(random);
                break;
            case 3:
                feedbackText = navigationList.get(random);
                break;
            case 4:
                feedbackText = waleupList.get(random);
                break;
        }
        return feedbackText;
    }

}
