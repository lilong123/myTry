package com.nineShadow.model;

import java.util.ArrayList;
import java.util.List;

public class RedPackageRoom {
    private Integer id;

    private String roomName;

    private String packageNums;
    
    private List<String> packageNumList;

    private String multiples;
    
    private List<String> multipleList;

    private String bombNums;

    private List<String> bombNumList=new ArrayList<>();

    private Integer minMoney;

    private Integer maxMoney;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName == null ? null : roomName.trim();
    }

    public String getPackageNums() {
        return packageNums;
    }

    public void setPackageNums(String packageNums) {
        this.packageNums = packageNums == null ? null : packageNums.trim();
    }

    public String getMultiples() {
        return multiples;
    }

    public void setMultiples(String multiples) {
        this.multiples = multiples == null ? null : multiples.trim();
    }

    public String getBombNums() {
        return bombNums;
    }

    public void setBombNums(String bombNums) {
        this.bombNums = bombNums == null ? null : bombNums.trim();
    }

    public Integer getMinMoney() {
        return minMoney;
    }

    public void setMinMoney(Integer minMoney) {
        this.minMoney = minMoney;
    }

    public Integer getMaxMoney() {
        return maxMoney;
    }

    public void setMaxMoney(Integer maxMoney) {
        this.maxMoney = maxMoney;
    }

	public List<String> getPackageNumList() {
		return packageNumList;
	}

	public void setPackageNumList(List<String> packageNumList) {
		this.packageNumList = packageNumList;
	}

	public List<String> getMultipleList() {
		return multipleList;
	}

	public void setMultipleList(List<String> multipleList) {
		this.multipleList = multipleList;
	}

	public List<String> getBombNumList() {
		return bombNumList;
	}

	public void setBombNumList(List<String> bombNumList) {
		this.bombNumList = bombNumList;
	}

    @Override
    public String toString() {
        return "RedPackageRoom{" +
                "id=" + id +
                ", roomName='" + roomName + '\'' +
                ", packageNums='" + packageNums + '\'' +
                ", packageNumList=" + packageNumList +
                ", multiples='" + multiples + '\'' +
                ", multipleList=" + multipleList +
                ", bombNums='" + bombNums + '\'' +
                ", bombNumList=" + bombNumList +
                ", minMoney=" + minMoney +
                ", maxMoney=" + maxMoney +
                '}';
    }
}