package com.nineShadow.dao;

import java.util.List;

import com.nineShadow.model.NiuniuRoom;


public interface NiuniuRoomMapper {
    int deleteByPrimaryKey(Integer roomno);

    int insert(NiuniuRoom record);

    int insertSelective(NiuniuRoom record);

    NiuniuRoom selectByPrimaryKey(Integer roomno);

    int updateByPrimaryKeySelective(NiuniuRoom record);

    int updateByPrimaryKey(NiuniuRoom record);
    
    List<NiuniuRoom> selectAll();
}