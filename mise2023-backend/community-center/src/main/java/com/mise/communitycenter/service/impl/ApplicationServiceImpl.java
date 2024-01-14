package com.mise.communitycenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.mise.communitycenter.domain.entity.Application;
import com.mise.communitycenter.domain.vo.ApplicationCheckVO;
import com.mise.communitycenter.domain.vo.CommunityVO;
import com.mise.communitycenter.enums.ApplicationStatus;
import com.mise.communitycenter.enums.Role;
import com.mise.communitycenter.mapper.ApplicationMapper;
import com.mise.communitycenter.mapper.CommunityMapper;
import com.mise.communitycenter.service.ApplicationService;
import com.mise.communitycenter.service.CommunityService;
import com.mise.communitycenter.util.TimeUtil;
import lombok.extern.slf4j.Slf4j;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
public class ApplicationServiceImpl implements ApplicationService {

    @Autowired
    private ApplicationMapper applicationMapper;

    @Autowired
    private CommunityMapper communityMapper;

    @Autowired
    private CommunityService communityService;

    @Override
    public boolean applyForCommunity(long userID, long communityID) {
        Application application = new Application();
        application.setCommunityID(communityID);
        application.setUserID(userID);
        application.setStatus(ApplicationStatus.PENDING);
        application.setApplyTime(TimeUtil.getCurrentTime());
        int result = applicationMapper.insert(application);
        return result == 1;
    }

    @Override
    public boolean exitCommunity(long userID, long communityID) {
        UpdateWrapper<Application> wrapper = new UpdateWrapper<>();
        wrapper.eq("user_id",userID);
        wrapper.eq("community_id", communityID);
        int result = applicationMapper.delete(wrapper);
        return result == 1;
    }

    @Override
    public boolean accept(long userID, long communityID, long handlerID) {
        UpdateWrapper<Application> wrapper = new UpdateWrapper<>();
        wrapper.eq("user_id",userID);
        wrapper.eq("community_id", communityID);
        wrapper.set("status", ApplicationStatus.APPROVED);
        wrapper.set("handler_id", handlerID);
        wrapper.set("handle_time", TimeUtil.getCurrentTime());
        int result = applicationMapper.update(null, wrapper);
        communityService.addMember(communityID, userID, Role.MEMBER);
        return result == 1;
    }

    @Override
    public boolean refuse(long userID, long communityID, long handlerID) {
        UpdateWrapper<Application> wrapper = new UpdateWrapper<>();
        wrapper.eq("user_id",userID);
        wrapper.eq("community_id", communityID);
        wrapper.set("status", ApplicationStatus.DECLINED);
        wrapper.set("handler_id", handlerID);
        wrapper.set("handle_time", TimeUtil.getCurrentTime());
        int result = applicationMapper.update(null, wrapper);
        return result == 1;
    }

    @Override
    public List<ApplicationCheckVO> getApplicationByAdminId(long adminId) {
        List<ApplicationCheckVO> res = new ArrayList<>();
        // 先查管理员管理的所有社区的id
        try {
            List<Long> communityIds = communityMapper.getCommunitiesByAdminId(adminId);
            for (Long communityId : communityIds) {
                ApplicationCheckVO applicationCheckVO = new ApplicationCheckVO();
                CommunityVO community = communityService.getCommunityById(communityId); // 查社区详情
                applicationCheckVO.setCommunityVO(community);

                List<Long> applyUserIds = applicationMapper.getApplyUserIdsByCommunityId(communityId); //申请加入社区的用户id，且状态为未处理
                applicationCheckVO.setUserIdList(applyUserIds);

                res.add(applicationCheckVO);
            }
        } catch (Exception e) {
            log.error("Get application map failed, related admin id is {}", adminId);
            return null;
        }
        return res;
    }

    @Override
    public List<Long> getApplicationOfCommunity(long communityId) {
        return applicationMapper.getApplyUserIdsByCommunityId(communityId);
    }
}
