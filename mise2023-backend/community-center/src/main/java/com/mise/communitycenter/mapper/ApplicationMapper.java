package com.mise.communitycenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mise.communitycenter.domain.entity.Application;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface ApplicationMapper extends BaseMapper<Application> {

    /**
     * 根据社区id查询申请加入该社区的所有用户id，且申请未处理
     * @param communityId 社区id
     * @return 用户idList
     */
    @Select("select user_id from application " +
            "where community_id = #{communityId} and status = 'Pending'")
    List<Long> getApplyUserIdsByCommunityId(long communityId);
}
