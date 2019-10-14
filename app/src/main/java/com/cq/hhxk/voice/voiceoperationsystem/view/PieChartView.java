package com.cq.hhxk.voice.voiceoperationsystem.view;

import android.graphics.Color;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.data.PieEntry;
import com.github.mikephil.charting.formatter.PercentFormatter;
import java.util.List;

/**
 * @title  饼状统计图封装
 * @date   2019/07/31
 * @author enmaoFu
 */
public class PieChartView {

    public void init(PieChart pieChart,boolean isItem,boolean isAttribute,boolean isText,boolean isLegend,List<PieEntry> pieEntryList,List<Integer> colors,String nText){

        /**
         * 设置pieChart图表基本属性
         */
        //设置是否使用百分比
        pieChart.setUsePercentValues(true);
        //设置pieChart图表的描述
        pieChart.getDescription().setEnabled(false);
        //设置pieChart图表背景色
        pieChart.setBackgroundColor(Color.parseColor("#00ffffff"));
        //设置pieChart图表上下左右的偏移，类似于外边距
        pieChart.setExtraOffsets(0, 0, 0, 0);
        //设置pieChart图表转动阻力摩擦系数[0,1]
        pieChart.setDragDecelerationFrictionCoef(0.95f);
        //设置pieChart图表起始角度
        pieChart.setRotationAngle(0);
        //设置pieChart图表是否可以手动旋转
        pieChart.setRotationEnabled(true);
        //设置piecahrt图表点击Item高亮是否可用
        pieChart.setHighlightPerTapEnabled(false);

        /**
         * 设置pieChart图表Item文本属性
         */
        //设置pieChart是否只显示饼图上百分比不显示文字（true：下面属性才有效果）
        pieChart.setDrawEntryLabels(isItem);
        //设置pieChart图表文本字体颜色
        pieChart.setEntryLabelColor(Color.WHITE);
        //设置pieChart图表文本字体大小
        pieChart.setEntryLabelTextSize(8f);

        /**
         * 设置pieChart内部圆环属性
         */
        //是否显示PieChart内部圆环(true:下面属性才有意义)
        pieChart.setDrawHoleEnabled(isAttribute);
        //设置PieChart内部圆的半径(这里设置28.0f)
        pieChart.setHoleRadius(58.0f);
        //设置PieChart内部透明圆的半径(这里设置31.0f)
        pieChart.setTransparentCircleRadius(38.0f);
        //设置PieChart内部透明圆与内部圆间距(31f-28f)填充颜色
        pieChart.setTransparentCircleColor(Color.parseColor("#ffffff"));
        //设置PieChart内部透明圆与内部圆间距(31f-28f)透明度[0~255]数值越小越透明
        pieChart.setTransparentCircleAlpha(255);
        //设置PieChart内部圆的颜色
        pieChart.setHoleColor(Color.parseColor("#ffffff"));
        //是否绘制PieChart内部中心文本（true：下面属性才有意义）
        pieChart.setDrawCenterText(isText);
        //设置PieChart内部圆文字的内容
        pieChart.setCenterText(nText);
        //设置PieChart内部圆文字的大小
        pieChart.setCenterTextSize(10f);
        //设置PieChart内部圆文字的颜色
        pieChart.setCenterTextColor(Color.parseColor("#4E5861"));

        /**
         * 设置pieCahrt图例
         */
        //获取图例
        Legend l = pieChart.getLegend();
        //是否启用图列（true：下面属性才有意义）
        l.setEnabled(isLegend);
        //设置垂直位置
        l.setVerticalAlignment(Legend.LegendVerticalAlignment.BOTTOM);
        //设置水平位置
        l.setHorizontalAlignment(Legend.LegendHorizontalAlignment.CENTER);
        //设置整体位置
        l.setOrientation(Legend.LegendOrientation.HORIZONTAL);
        //设置图例的形状
        l.setForm(Legend.LegendForm.DEFAULT);
        //设置图例的大小
        l.setFormSize(4);
        //设置每个图例实体中标签和形状之间的间距
        l.setFormToTextSpace(10f);
        //设置图列换行(注意使用影响性能,仅适用legend位于图表下面)
        l.setWordWrapEnabled(true);
        //设置图例实体之间延X轴的间距（setOrientation = HORIZONTAL有效）
        l.setXEntrySpace(10f);
        //设置图例实体之间延Y轴的间距（setOrientation = VERTICAL 有效）
        l.setYEntrySpace(8f);
        //设置比例块Y轴偏移量
        l.setYOffset(0f);
        //设置图例标签文本的大小
        l.setTextSize(8f);
        //设置图例标签文本的颜色
        l.setTextColor(Color.parseColor("#000000"));

        //pieChart添加数据
        setData(pieChart,pieEntryList,colors);
    }

    /**
     * 设置饼图的数据
     */
    public void setData(PieChart pieChart, List<PieEntry> pieEntryList,List<Integer> colors) {
        //饼状图数据集 PieDataSet
        PieDataSet pieDataSet = new PieDataSet(pieEntryList, "交通态势one");
        //设置饼状Item之间的间隙
        pieDataSet.setSliceSpace(3f);
        //设置饼状Item被选中时变化的距离
        pieDataSet.setSelectionShift(10f);
        //为DataSet中的数据匹配上颜色集(饼图Item颜色)
        pieDataSet.setColors(colors);
        //最终数据 PieData
        PieData pieData = new PieData(pieDataSet);
        //设置是否显示数据实体(百分比，true:以下属性才有意义)
        pieData.setDrawValues(true);
        //设置所有DataSet内数据实体（百分比）的文本颜色
        pieData.setValueTextColor(Color.WHITE);
        //设置所有DataSet内数据实体（百分比）的文本字体大小
        pieData.setValueTextSize(12f);
        //设置所有DataSet内数据实体（百分比）的文本字体格式
        pieData.setValueFormatter(new PercentFormatter());
        pieChart.setData(pieData);
        pieChart.highlightValues(null);
        //将图表重绘以显示设置的属性和数据
        pieChart.invalidate();
    }

}
