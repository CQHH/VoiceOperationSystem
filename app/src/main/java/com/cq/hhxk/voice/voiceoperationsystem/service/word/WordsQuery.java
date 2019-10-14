package com.cq.hhxk.voice.voiceoperationsystem.service.word;

import com.cq.hhxk.voice.voiceoperationsystem.service.constant.WordTypeConstant;

/**
 * @title  语音词义遍历判断
 * @date   2019/07/31
 * @author enmaoFu
 */
public class WordsQuery implements WordTypeConstant {

    /**
     * 管理者场景-综合报表-关键词
     */
    private static String[] wordControllerFormList = {"综合情况","综合信息","综合报表","综合数据"};

    /**
     * 管理者场景-查看预警-关键词
     */
    private static String[] wordControllerWarningList = {"预警信息","预警情况","预警详情","异常信息","异常情况","异常详情","警告"};

    /**
     * 管理者场景-预警详情-关键词
     */
    private static String[] wordControllerWarningDetailsList = {"设备信息","设备情况","责任人"};

    /**
     * 访客场景-导航-关键词
     */
    private static String[] wordVisitorNaviList = {"导航","导航到","导航去","我要去","我要到","我想去"};

    /**
     * 访客场景-打开APP-关键词
     */
    private static String[] wordVisitorOpenList = {"打开","进入"};

    /**
     * 工作人员-综合报表-关键词
     */
    private static String[] wordPersonnelFormList = {"城管数据","城管报表","城管信息","城管综合数据","城管综合报表","城管综合信息","城管"};

    /**
     * 工作人员-海绵报表-关键词
     */
    private static String[] wordSpongeFormList = {"海绵数据","海绵报表","海绵"};

    /**
     * 工作人员-会展报表-关键词
     */
    private static String[] wordExhibitionFormList = {"会展数据","会展报表","会展"};

    /**
     * 工作人员-巡检路线（导航）-关键词
     */
    private static String[] wordPersonnelPatrolList = {"巡查","巡检"};

    /**
     * 居民-出行交通情况（公交车）-关键词
     */
    private static String[] wordResidentTripList = {"公交车","公交"};

    /**
     * 居民-出行交通情况（联动打车、网约车）-关键词
     */
    private static String[] wordResidentTaxiList = {"打车","出租车","网约车"};

    /**
     * 返回上一页
     */
    private static String[] wordReturnList = {"返回系统","回到系统"};

    /**
     * 管理者
     */
    private static String[] wordLeaderList = {"1234"};

    /**
     * 工作人员（城管）
     */
    private static String[] wordOperatorList = {"2345"};

    /**
     * 工作人员（海绵）
     */
    private static String[] wordSpongeList = {"4567"};

    /**
     * 工作人员（会展）
     */
    private static String[] wordExhibitionList = {"6789"};

    /**
     * 开发者
     */
    private static String[] wordConfigureList = {"配置","开发"};

    public static boolean query(String str,int type){
        boolean isHave = false;
        switch (type){
            case WORD_CONTROLLER_FORM_LIST:
                for(String word:wordControllerFormList){
                    if(str.contains(word)){
                        isHave = true;
                        break;
                    }else{
                        isHave = false;
                    }
                }
                break;
            case WORD_CONTROLLER_WARNING_LIST:
                for(String word:wordControllerWarningList){
                    if(str.contains(word)){
                        isHave = true;
                        break;
                    }else{
                        isHave = false;
                    }
                }
                break;
            case WORD_CONTROLLER_WARNING_DETAILS_LIST:
                for(String word:wordControllerWarningDetailsList){
                    if(str.contains(word)){
                        isHave = true;
                        break;
                    }else{
                        isHave = false;
                    }
                }
                break;
            case WORD_VISITOR_NAVI_LIST:
                for(String word:wordVisitorNaviList){
                    if(str.contains(word)){
                        isHave = true;
                        break;
                    }else{
                        isHave = false;
                    }
                }
                break;
            case WORD_VISITOR_OPEN_LIST:
                for(String word:wordVisitorOpenList){
                    if(str.contains(word)){
                        isHave = true;
                        break;
                    }else{
                        isHave = false;
                    }
                }
                break;
            case WORD_PERSONNEL_FORM_LIST:
                for(String word:wordPersonnelFormList){
                    if(str.contains(word)){
                        isHave = true;
                        break;
                    }else{
                        isHave = false;
                    }
                }
                break;
            case WORD_PERSONNEL_PATROL_LIST:
                for(String word:wordPersonnelPatrolList){
                    if(str.contains(word)){
                        isHave = true;
                        break;
                    }else{
                        isHave = false;
                    }
                }
                break;
            case WORD_RESIDENT_TRIP_LIST:
                for(String word:wordResidentTripList){
                    if(str.contains(word)){
                        isHave = true;
                        break;
                    }else{
                        isHave = false;
                    }
                }
                break;
            case WORD_RESIDENT_TAXI_LIST:
                for(String word:wordResidentTaxiList){
                    if(str.contains(word)){
                        isHave = true;
                        break;
                    }else{
                        isHave = false;
                    }
                }
                break;
            case WORD_RETURN_LIST:
                for(String word:wordReturnList){
                    if(str.contains(word)){
                        isHave = true;
                        break;
                    }else{
                        isHave = false;
                    }
                }
                break;
            case WORD_LEADER_LIST:
                for(String word:wordLeaderList){
                    if(str.contains(word)){
                        isHave = true;
                        break;
                    }else{
                        isHave = false;
                    }
                }
                break;
            case WORD_OPERATOR_LIST:
                for(String word:wordOperatorList){
                    if(str.contains(word)){
                        isHave = true;
                        break;
                    }else{
                        isHave = false;
                    }
                }
                break;
            case WORD_SPONGE_LIST:
                for(String word:wordSpongeList){
                    if(str.contains(word)){
                        isHave = true;
                        break;
                    }else{
                        isHave = false;
                    }
                }
                break;
            case WORD_SPONGE_FORM_LIST:
                for(String word:wordSpongeFormList){
                    if(str.contains(word)){
                        isHave = true;
                        break;
                    }else{
                        isHave = false;
                    }
                }
                break;
            case WORD_EXHIBITION_LIST:
                for(String word:wordExhibitionList){
                    if(str.contains(word)){
                        isHave = true;
                        break;
                    }else{
                        isHave = false;
                    }
                }
                break;
            case WORD_EXHIBITION_FORM_LIST:
                for(String word:wordExhibitionFormList){
                    if(str.contains(word)){
                        isHave = true;
                        break;
                    }else{
                        isHave = false;
                    }
                }
                break;
            case WORD_CONFIGURE_LIST:
                for(String word:wordConfigureList){
                    if(str.contains(word)){
                        isHave = true;
                        break;
                    }else{
                        isHave = false;
                    }
                }
                break;
        }
        return isHave;
    }

}
