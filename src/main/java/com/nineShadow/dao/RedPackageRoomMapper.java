package com.nineShadow.dao;

import com.nineShadow.model.RedPackageRoom;

import java.util.List;

public interface RedPackageRoomMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(RedPackageRoom record);

    int insertSelective(RedPackageRoom record);

    RedPackageRoom selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(RedPackageRoom record);

    int updateByPrimaryKey(RedPackageRoom record);

	List<RedPackageRoom> selectAll();
}