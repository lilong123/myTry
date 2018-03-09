package com.nineShadow.dao;

import com.nineShadow.model.PlayerBase;
import org.apache.ibatis.annotations.Param;

import java.math.BigDecimal;
import java.util.List;

public interface PlayerBaseMapper {
    int deleteByPrimaryKey(Integer id);

    int insert(PlayerBase record);

    int insertSelective(PlayerBase record);

    PlayerBase selectByPrimaryKey(Integer id);

    int updateByPrimaryKeySelective(PlayerBase record);

    int updateByPrimaryKey(PlayerBase record);
    
	List<PlayerBase> selectAll();

	List<PlayerBase> selectByParam(@Param("param") String param);

	PlayerBase selectByOpenid(@Param("openid") String openID);

	List<PlayerBase> selectExceptProxy();

	List<PlayerBase> selectExceptByParam(@Param("param") String param);

	List<PlayerBase> selectByExtendId(@Param("extendId") String spreadid);

	List<PlayerBase> selectByExtendIdParam(@Param("extendId") String spreadid, @Param("param") String param);

    int updateMoneyById(@Param("point") BigDecimal money, @Param("id") Integer userId);

	PlayerBase selectByPhone(@Param("phone") String phoneNum);

	Integer countByParentId(@Param("parentId") String playerid);



	List<PlayerBase> selectByParentId(@Param("parentId") String parentId);
	
	List<PlayerBase> getRobotsByIds(@Param("maxid")int maxid,@Param("minid")int id);
}