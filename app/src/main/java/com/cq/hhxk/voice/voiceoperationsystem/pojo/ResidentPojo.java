package com.cq.hhxk.voice.voiceoperationsystem.pojo;

import java.util.List;

public class ResidentPojo {

    private int isSelection = 0;

    private String lineName;

    private String startName;

    private String endName;

    private List<String> roadNames;

    public int getIsSelection() {
        return isSelection;
    }

    public void setIsSelection(int isSelection) {
        this.isSelection = isSelection;
    }

    public String getLineName() {
        return lineName;
    }

    public void setLineName(String lineName) {
        this.lineName = lineName;
    }

    public String getStartName() {
        return startName;
    }

    public void setStartName(String startName) {
        this.startName = startName;
    }

    public String getEndName() {
        return endName;
    }

    public void setEndName(String endName) {
        this.endName = endName;
    }

    public List<String> getRoadNames() {
        return roadNames;
    }

    public void setRoadNames(List<String> roadNames) {
        this.roadNames = roadNames;
    }
}
