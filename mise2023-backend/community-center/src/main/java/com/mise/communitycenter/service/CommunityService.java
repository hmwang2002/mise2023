package com.mise.communitycenter.service;

import com.mise.communitycenter.domain.vo.CommunityVO;
import com.mise.communitycenter.domain.vo.MemberVO;
import com.mise.communitycenter.domain.vo.PostVO;

import java.util.List;

/**
 * @author whm,wlf
 * @date 2023/10/27 17:04
 */
public interface CommunityService {

    /**
     * 创建社区
     * @param userID 用户id
     * @param communityVO 社区对象
     * @return 创建结果
     */
    boolean createCommunity(long userID, CommunityVO communityVO);

    /**
     * 获取社区成员
     * @param communityID 社区id
     * @return 社区成员id列表
     */
    List<Long> getCommunityMembers(long communityID);

    Long getCommunityOwner(long communityID);

    List<Long> getCommunityManagers(long communityID);

    /**
     * 获取用户创建的社区
     * @param userID
     * @return 创建的社区列表
     */
    List<CommunityVO> getCreatedCommunity(long userID);

    /**
     * 获取用户管理的社区
     * @param userID
     * @return 管理的社区列表
     */
    List<CommunityVO> getManagedCommunity(long userID);

    /**
     * 获取用户加入的社区，普通用户
     * @param userID
     * @return 加入的社区列表
     */
    List<CommunityVO> getJoinedCommunity(long userID);

    /**
     * 获取社区内的帖子
     * @param communityID 社区id
     * @return 帖子列表
     */
    List<PostVO> getPosts(long communityID);

    /**
     * 删除社区中的某位成员
     * @param memberID 要删除的成员的id
     * @return 删除结果
     */
    boolean deleteMember(long communityID, long memberID);


    /**
     * 为社区添加成员
     * @param communityID 社区id
     * @param memberID 成员id
     * @return 添加操作结果
     */
    boolean addMember(long communityID, long memberID, int role);

    /**
     * 展示当前热门社区, 以及用户可能感兴趣的社区
     * @param userID 用户id
     * @return 社区列表
     */
    List<CommunityVO> showHotAndInterestingCommunities(long userID);

    /**
     * 将社区的现有成员设置为管理员
     * @param userId
     * @param community_id
     * @return
     */
    boolean setAdmin(long community_id, long userId);

    boolean cancelAdmin(long community_id, long userId);


    /**
     * 根据社区id查询社区信息
     * @param communityId 社区id
     * @return 社区信息
     */
    CommunityVO getCommunityById(long communityId);

    List<CommunityVO> getAdminCommunitiesByAdminId(long adminId);

    /**
     * 获取人数前十的热门社区
     * @return 社区列表
     */
    List<CommunityVO> getHotCommunities();

    /**
     * 获取人气较高的社区作为推荐社区，且要满足用户未加入
     * @return 社区列表
     */
    List<CommunityVO> getRecommendedCommunities(long userId);

    /**
     * 判断用户是否在该社区中
     */
    Boolean getWhetherIn(long userId, long communityId);
}
