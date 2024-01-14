package com.mise.communitycenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mise.communitycenter.domain.entity.Community;
import com.mise.communitycenter.domain.vo.CommunityVO;
import com.mise.communitycenter.domain.vo.MemberVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
 * @author whm,wlf
 * @date 2023/10/27 17:06
 */
@Mapper
public interface CommunityMapper extends BaseMapper<Community> {

//    /**
//     * 根据社区id查询社区成员
//     * @param communityID 社区id
//     * @return 社区成员id列表
//     */
//    @Select("select user_id from community_user " +
//            "where community_id = #{communityID}")

    /**
     * 根据社区id查询群主
     */
    @Select("select user_id from community_user " +
            "where community_id = #{communityID} and role = 0")
    Long findCommunityOwner(long communityID);

    /**
     * 根据社区id查询管理员
     */
    @Select("select user_id from community_user " +
            "where community_id = #{communityID} and role = 1")
    List<Long> findCommunityManagers(long communityID);

    /**
     * 根据社区id查询普通用户
     */
    @Select("select user_id from community_user " +
            "where community_id = #{communityID} and role = 2")
    List<Long> findCommunityMembers(long communityID);

    /**
     * 根据用户id查询用户创建的社区列表
     * @param userId
     * @return 创建的社区列表
     */
    @Select("select community_id from community_user " +
            "where user_id = #{userId} and role = 0")
    List<Long> getCreatedCommunity(long userId);

    /**
     * 根据用户id查询用户创建的社区列表
     * @param userId
     * @return 创建的社区列表
     */
    @Select("select community_id from community_user " +
            "where user_id = #{userId} and role = 1")
    List<Long> getManagedCommunity(long userId);

    /**
     * 根据用户id查询用户创建的社区列表
     * @param userId
     * @return 创建的社区列表
     */
    @Select("select community_id from community_user " +
            "where user_id = #{userId} and role = 2")
    List<Long> getJoinedCommunity(long userId);


    /**
     * 根据社区id查询社区帖子
     * @param communityID 社区id
     * @return 帖子id列表
     */
    @Select("select post_id from community_post " +
            "where community_id = #{communityID}")
    List<Long> findCommunityPosts(long communityID);


    /**
     * 删除社区成员
     * @param communityID 社区id
     * @param memberID 成员id
     * @return 删除结果
     */
    @Delete("delete from community_user " +
            "where community_id = #{communityID} and user_id = #{memberID}")
    boolean deleteMember(long communityID, long memberID);

    /**
     * 添加社区成员
     * @param communityID 社区id
     * @param memberID 成员id
     * @return 添加结果
     */
    @Insert("insert into community_user (community_id, user_id, role) " +
            "values (#{communityID}, #{memberID}, #{role})")
    boolean addMember(long communityID, long memberID, int role);

    /**
     * 根据管理员id查询其管理的所有社区id
     * @param adminId 管理员id
     * @return 社区idList
     */
    @Select("select community_id from community_user " +
            "where user_id = #{adminId} and (role = 0 or role = 1)")
    List<Long> getCommunitiesByAdminId(long adminId);

    @Insert("insert into community_user (community_id, user_id, role) " +
            "values (#{communityId}, #{userId}, 0)")
    boolean addAdmin(long communityId, long userId);

    @Update("update community_user " +
            "set role = 2 " +
            "where community_id = #{communityId} and user_id = #{userId}")
    boolean removeAdmin(long communityId, long userId);

    @Select("select community_id from community where name = #{name}")
    Long findCommunityIdByName(String name);

    @Update("update community_user " +
            "set role = 1 " +
            "where community_id = #{community_id} " +
            "and " +
            "user_id = #{userId}")
    boolean setAdmin(long community_id, long userId);

    @Update("update community_user " +
            "set role = 2 " +
            "where community_id = #{community_id} " +
            "and " +
            "user_id = #{userId}")
    boolean cancelAdmin(long community_id, long userId);

    /**
     * 选取人数排名前十的社区作为热门社区
     * @return 社区id集合
     */
    @Select("SELECT community_id " +
            "FROM community_user " +
            "GROUP BY community_id " +
            "ORDER BY COUNT(user_id) DESC " +
            "LIMIT 10")
    List<Long> getTopTenCommunities();

    /**
     * 选取出用户未加入的十个人数最多的社区作为推荐社区
     *  @return 社区id集合
     */
    @Select("SELECT DISTINCT c.community_id " +
            "FROM community_user c " +
            "LEFT JOIN community_user cu ON c.community_id = cu.community_id AND cu.user_id = #{userId} " +
            "WHERE cu.community_id IS NULL")

    List<Long> getTopTenCommunitiesNotJoined(long userId);

    /**
     * 查找用户是否在某一社区中
     * @return boolean
     */
    @Select("SELECT COUNT(*) > 0 " +
            "FROM community_user " +
            "WHERE user_id = #{userId} AND community_id = #{communityId}")
    Boolean getWhetherIn(long userId, long communityId);


}
