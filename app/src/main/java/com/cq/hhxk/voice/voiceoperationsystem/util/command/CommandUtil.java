package com.cq.hhxk.voice.voiceoperationsystem.util.command;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;

import com.orhanobut.logger.Logger;
import com.tencent.mmkv.MMKV;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @title  服务指挥调度
 * @date   2019/07/03
 * @author enmaoFu
 */
public class CommandUtil {

    /**
     * 类似于SP文件缓存
     */
    private MMKV kv;

    private CommandUtil(){}

    private static final CommandUtil commandUtil = new CommandUtil();

    //静态工厂方法
    public static CommandUtil getInstance() {
        return commandUtil;
    }

    /**
     * 百度导航
     * @param context
     * @param str
     */
    public void navigationBaidu(Context context,String str){
        boolean navigationPredicateA = str.contains("导航");
        boolean navigationPredicateB = str.contains("导航到");
        boolean navigationPredicateC = str.contains("导航去");
        boolean navigationPredicateD = str.contains("我要去");
        boolean navigationPredicateE = str.contains("我要到");
        String address = "";
        if(navigationPredicateA){
            address = str.substring(str.indexOf("导航") + 2,str.length() - 0);
        }
        if(navigationPredicateB){
            address = str.substring(str.indexOf("导航到") + 3,str.length() - 0);
        }
        if(navigationPredicateC){
            address = str.substring(str.indexOf("导航去") + 3,str.length() - 0);
        }
        if(navigationPredicateD){
            address = str.substring(str.indexOf("我要去") + 3,str.length() - 0);
        }
        if(navigationPredicateE){
            address = str.substring(str.indexOf("我要到") + 3,str.length() - 0);
        }
        Logger.v(address + "----");
        //调起百度进行驾车导航
        Intent intent = new Intent();
        intent.setData(Uri.parse("baidumap://map/navi?query=" + address + "&src=andr.baidu.openAPIdemo"));
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(intent);
    }

    /**
     * 打开APP
     * @param context
     * @param packageName
     */
    public void openApp(Context context, String packageName) {
        Intent intent = context.getPackageManager().getLaunchIntentForPackage(packageName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(intent);
    }

    /**
     * 过滤字符得到要打开的App名字
     */
    public String openAppFilter(Context context, String instructionsStr){
        boolean openBlA = instructionsStr.contains("打开");
        boolean openBlB = instructionsStr.contains("进入");
        String appName = "";
        if(openBlA){
            appName = instructionsStr.substring(2,instructionsStr.length() - 0);
        }
        if(openBlB){
            appName = instructionsStr.substring(2,instructionsStr.length() - 0);
        }
        return appName;
    }

    /**
     * 通过名称获取APP包名
     * @param context
     */
    public String getAppPackage(Context context,String appNameStr){
        //初始化本地缓存
        kv = MMKV.defaultMMKV();
        //获取本地缓存的数据
        String appNameToPackageKv = kv.decodeString("appNameToPackage");
        //查找到的包名
        String getPackage = "";
        Logger.v(appNameToPackageKv + "---");
        //获取包管理器
        PackageManager packageManager = context.getPackageManager();
        //获取所有安装的app
        List<PackageInfo> installedPackages = packageManager.getInstalledPackages(0);
        if(null == appNameToPackageKv || "".equals(appNameToPackageKv) || "null".equals(appNameToPackageKv)){
            Logger.v("无数据第一次存储...");
            StringBuilder appNameToPackage = new StringBuilder();
            for(PackageInfo info : installedPackages){
                //app包名
                String packageName = info.packageName;
                ApplicationInfo ai = null;
                try {
                    ai = packageManager.getApplicationInfo(packageName, 0);
                } catch (PackageManager.NameNotFoundException e) {
                    e.printStackTrace();
                }
                //获取应用名称
                String appName = (String) packageManager.getApplicationLabel(ai);
                appNameToPackage.append(appName + "-" + packageName + ",");
            }
            kv.encode("appNameToPackage",appNameToPackage.toString());
            String[] appNameToPackages = kv.decodeString("appNameToPackage").split(",");
            Map<String,String> maps = new HashMap<>();
            for(int i = 0; i < appNameToPackages.length; i++){
                maps.put(appNameToPackages[i].substring(0,appNameToPackages[i].indexOf("-")),
                        appNameToPackages[i].substring(appNameToPackages[i].indexOf("-") + 1,appNameToPackages[i].length() - 0));
            }
            getPackage = maps.get(appNameStr);
        }else{
            Logger.v("有数据开始判断...");
            String[] appNameToPackages = appNameToPackageKv.split(",");
            if(appNameToPackages.length == installedPackages.size()){
                Logger.v("有数据长度一致...");
                Map<String,String> maps = new HashMap<>();
                for(int i = 0; i < appNameToPackages.length; i++){
                    maps.put(appNameToPackages[i].substring(0,appNameToPackages[i].indexOf("-")),
                            appNameToPackages[i].substring(appNameToPackages[i].indexOf("-") + 1,appNameToPackages[i].length() - 0));
                }
                getPackage = maps.get(appNameStr);
            }else{
                Logger.v("有数据长度不一致...");
                StringBuilder appNameToPackage = new StringBuilder();
                for(PackageInfo info : installedPackages){
                    //app包名
                    String packageName = info.packageName;
                    ApplicationInfo ai = null;
                    try {
                        ai = packageManager.getApplicationInfo(packageName, 0);
                    } catch (PackageManager.NameNotFoundException e) {
                        e.printStackTrace();
                    }
                    //获取应用名称
                    String appName = (String) packageManager.getApplicationLabel(ai);
                    appNameToPackage.append(appName + "-" + packageName + ",");
                }
                kv.encode("appNameToPackage",appNameToPackage.toString());
                String[] appNameToPackagesNew = kv.decodeString("appNameToPackage").split(",");
                Map<String,String> maps = new HashMap<>();
                for(int i = 0; i < appNameToPackagesNew.length; i++){
                    maps.put(appNameToPackagesNew[i].substring(0,appNameToPackagesNew[i].indexOf("-")),
                            appNameToPackagesNew[i].substring(appNameToPackagesNew[i].indexOf("-") + 1,appNameToPackagesNew[i].length() - 0));
                }
                getPackage = maps.get(appNameStr);
            }
        }
        return getPackage;
    }

}
