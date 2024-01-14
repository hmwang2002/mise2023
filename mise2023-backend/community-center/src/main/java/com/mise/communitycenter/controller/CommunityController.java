package com.mise.communitycenter.controller;


import com.mise.communitycenter.domain.vo.CommunityVO;
import com.mise.communitycenter.domain.vo.MemberVO;
import com.mise.communitycenter.domain.vo.PostVO;
import com.mise.communitycenter.domain.vo.Response;
import com.mise.communitycenter.service.CommunityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/community")
public class CommunityController {

    @Autowired
    private CommunityService communityService;

    @PostMapping("/createCommunity")
    public Response createCommunity(@RequestParam long userID, @RequestBody CommunityVO communityVO) {
        boolean result = communityService.createCommunity(userID, communityVO);
        if(!result) {
            return Response.failed();
        }
        return Response.success();
    }

    @GetMapping("/getCommunityMembers")
    public Response getCommunityMembers(@RequestParam long communityID) {
        List<Long> membersIds = communityService.getCommunityMembers(communityID);
        if(membersIds == null) {
            return Response.failed();
        }
        return Response.success(membersIds);
    }

    @GetMapping("/getCommunityOwner")
    public Response getCommunityOwner(@RequestParam long communityID) {
        Long membersIds = communityService.getCommunityOwner(communityID);
        if(membersIds == null) {
            return Response.failed();
        }
        return Response.success(membersIds);
    }

    @GetMapping("/getCommunityManagers")
    public Response getCommunityManagers(@RequestParam long communityID) {
        List<Long> membersIds = communityService.getCommunityManagers(communityID);
        if(membersIds == null) {
            return Response.failed();
        }
        return Response.success(membersIds);
    }


    @GetMapping("/getCreatedCommunity")
    public Response getCreatedCommunity(@RequestParam long userID) {
        List<CommunityVO> communityVOs = communityService.getCreatedCommunity(userID);
        if(communityVOs == null){
            return Response.failed();
        }
        return Response.success(communityVOs);
    }


    @GetMapping("/getManagedCommunity")
    public Response getManagedCommunity(@RequestParam long userID) {
        List<CommunityVO> communityVOs = communityService.getManagedCommunity(userID);
        if(communityVOs == null){
            return Response.failed();
        }
        return Response.success(communityVOs);
    }

    @GetMapping("/getJoinedCommunity")
    public Response getJoinedCommunity(@RequestParam long userID) {
        List<CommunityVO> communityVOs = communityService.getJoinedCommunity(userID);
        if(communityVOs == null){
            return Response.failed();
        }
        return Response.success(communityVOs);
    }

    @GetMapping("/getCommunityPosts")
    public Response getCommunityPosts(@RequestParam long communityID) {
        List<PostVO> posts = communityService.getPosts(communityID);
        if(posts == null) {
            return Response.failed();
        }
        return Response.success(posts);
    }

    @GetMapping("/deleteMember")
    public Response deleteMember(@RequestParam long communityID, @RequestParam long memberID) {
        boolean result = communityService.deleteMember(communityID, memberID);
        if(!result) {
            return Response.failed("删除成员失败");
        }
        return Response.success();
    }

    @GetMapping("/addMember")
    public Response addMember(@RequestParam long communityID,
                              @RequestParam long memberID, @RequestParam int role) {
        boolean result = communityService.addMember(communityID, memberID, role);
        if(!result) {
            return Response.failed("添加成员失败");
        }
        return Response.success();
    }

    @GetMapping("/getCommunityById")
    public Response getCommunityById(@RequestParam long communityID) {
        CommunityVO community = communityService.getCommunityById(communityID);
        if(community == null) {
            return Response.failed("查询社区信息失败");
        }
        return Response.success(community);
    }

    @GetMapping("/setAdmin")
    public Response setAdmin(@RequestParam long communityId, @RequestParam long userId) {
        boolean res = communityService.setAdmin(communityId, userId);
        if(!res) {
            return Response.failed("设置管理员失败");
        }
        return Response.success();
    }

    @GetMapping("/cancelAdmin")
    public Response cancelAdmin(@RequestParam long communityId, @RequestParam long userId) {
        boolean res = communityService.cancelAdmin(communityId, userId);
        if(!res) {
            return Response.failed("移除管理员失败");
        }
        return Response.success();
    }

    @GetMapping("/getAdminCommunitiesByAdminId")
    public Response getAdminCommunitiesByAdminId(@RequestParam long adminId) {
        List<CommunityVO> adminCommunitiesByAdminId = communityService.getAdminCommunitiesByAdminId(adminId);
        if(adminCommunitiesByAdminId == null) {
            return Response.failed();
        }
        return Response.success(adminCommunitiesByAdminId);
    }

    @GetMapping("/getHotCommunities")
    public Response getHotCommunities() {
        List<CommunityVO> hotCommunities = communityService.getHotCommunities();
        if (hotCommunities == null) {
            return Response.failed();
        }
        return Response.success(hotCommunities);
    }

    @GetMapping("/getRecommendedCommunities")
    public Response getRecommendedCommunities(@RequestParam long userId) {
        List<CommunityVO> recommendedCommunities = communityService.getRecommendedCommunities(userId);
        if (recommendedCommunities == null) {
            return Response.failed();
        }
        return Response.success(recommendedCommunities);
    }

    @GetMapping("/getInCommunities")
    public Response getInCommunities(@RequestParam long userId){
        List<CommunityVO> communityVOs = communityService.getManagedCommunity(userId);
        List<CommunityVO> communityVOs1 = communityService.getJoinedCommunity(userId);
        if (communityVOs == null){
            return Response.success(communityVOs1);
        }
        communityVOs.addAll(communityVOs1);
        return Response.success(communityVOs);
    }

    @GetMapping("/getWhetherIn")
    public Response getWhetherIn(@RequestParam long userId, @RequestParam long communityId){
        Boolean whetherIn = communityService.getWhetherIn(userId, communityId);
        if(whetherIn == null){
            return Response.failed();
        }
        if(whetherIn){
            return Response.success(true);
        }else {
            return Response.success(false);
        }
    }
}
