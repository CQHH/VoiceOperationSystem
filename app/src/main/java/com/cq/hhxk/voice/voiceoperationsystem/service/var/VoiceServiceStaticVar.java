package com.cq.hhxk.voice.voiceoperationsystem.service.var;

/**
 * @title  语音服务相关静态变量
 * @date   2019/07/12
 * @author enmaoFu
 */
public class VoiceServiceStaticVar {

    /**
     * 语音识别结果缓冲字符串
     */
    public static String bufferStr = "";

    /**
     * 播报的完毕的处理状态
     * 0：直接关闭
     * 1：继续启动识别
     * 2：回到提示页，并关闭
     * 3：不作任何操作
     */
    public static int type = 0;

    /**
     * 是否识别完毕
     * 0：是
     * 1：否
     */
    public static int isRecogInt = 0;

    /**
     * 是否播报完毕
     * 0：是
     * 1：否
     */
    public static int isSpeakInt = 0;

    /**
     * 管理者打开综合报表
     * 0：未打开
     * 1：已打开
     */
    public static int startComprehensiveControllerActivity = 0;

    /**
     * 管理者打开的综合报表是否已经有预警
     * 0：否
     * 1：是
     */
    public static int isWaring = 0;

    /**
     * 管理者打开预警详情页（交通详情）
     * 0：未打开
     * 1：已打开
     */
    public static int startMapActivity = 0;

    /**
     * 工作人员打开综合报表（城管）
     * 0：未打开
     * 1：已打开
     */
    public static int startComprehensivePersonnelActivity = 0;

    /**
     * 工作人员打开综合报表（海绵）
     * 0：未打开
     * 1：已打开
     */
    public static int startSpongeActivity = 0;

    /**
     * 工作人员打开综合报表（会展）
     * 0：未打开
     * 1：已打开
     */
    public static int startExhibitionActivity = 0;

    /**
     * 进入导航页面的类型
     * 0：访客直接进入
     * 1：工作人员从综合报表巡查进入
     */
    public static int startNaviType = 0;

    /**
     * 居民进入公交车信息页面
     * 0：未进入
     * 1：已进入
     */
    public static int startResident = 0;

    /**
     * 进入开发者页面
     * 0：未进入
     * 1：已进入
     */
    public static int startConfigure = 0;

    /**
     * 是否从后台以管理者场景打开综合报表
     * 0：否
     * 1：是
     */
    public static int isStartComprehensiveControllerActivity = 0;

    /**
     * 是否从后台以工作人员场景打开综合报表
     * 0：否
     * 1：是
     */
    public static int isStartComprehensivePersonnelActivity = 0;

    /**
     * 是否从后台以工作人员场景打开海绵报表
     * 0：否
     * 1：是
     */
    public static int isStartSpongeActivity = 0;

    /**
     * 是否从后台以工作人员场景打开会展报表
     * 0：否
     * 1：是
     */
    public static int isStartExhibitionActivity = 0;

    /**
     * 是否从后台以访客场景打开导航
     * 0：否
     * 1：是
     */
    public static int isStartNaviVisitor = 0;

    /**
     * 是否从后台以居民场景打开公交出行页面
     * 0：否
     * 1：是
     */
    public static int isStartResident = 0;

    /**
     * 是否从后台打开开发者页面
     * 0：否
     * 1：是
     */
    public static int isStartConfigure = 0;

    /**
     * 权限
     * 1：管理者
     * 2：工作人员（城管）
     * 3：工作人员（海绵）
     * 4：工作人员（会展）
     */
    public static int jurisdiction = 0;

}
