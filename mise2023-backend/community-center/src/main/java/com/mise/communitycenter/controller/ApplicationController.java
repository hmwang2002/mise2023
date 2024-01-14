package com.mise.communitycenter.controller;

import com.alibaba.fastjson.JSON;
import com.mise.communitycenter.domain.vo.ApplicationCheckVO;
import com.mise.communitycenter.domain.vo.CommunityVO;
import com.mise.communitycenter.domain.vo.R;
import com.mise.communitycenter.domain.vo.Response;
import com.mise.communitycenter.service.ApplicationService;
import com.mise.communitycenter.service.CommunityService;
import org.apache.catalina.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/application")
public class ApplicationController {

    @Autowired
    private ApplicationService applicationService;
    @Autowired
    private CommunityService communityService;

    @GetMapping("/applyForCommunity")
    public Response applyForCommunity(@RequestParam long userID, @RequestParam long communityID) {
        CommunityVO community = communityService.getCommunityById(communityID);
        if(community.isPublic){
            boolean re = communityService.addMember(communityID, userID, 2);
            if(!re){
                return Response.failed("申请加入社区失败");
            }
            return Response.success();
        }
        boolean result = applicationService.applyForCommunity(userID, communityID);
        if(!result) {
            return Response.failed("申请加入社区失败");
        }
        return Response.success();
    }

    @GetMapping("/exitCommunity")
    public Response exitCommunity(@RequestParam long userID, @RequestParam long communityID) {
        boolean result = applicationService.exitCommunity(userID, communityID);
        if(!result) {
            return Response.failed("退出社区失败");
        }
        return Response.success();
    }

    @GetMapping("/accept")
    public Response accept(@RequestParam long userID, @RequestParam long communityID,
                           @RequestParam long handlerID) {
        boolean result = applicationService.accept(userID, communityID, handlerID);
        if(!result) {
            return Response.failed("处理申请失败");
        }
        return Response.success();
    }

    @GetMapping("/refuse")
    public Response refuse(@RequestParam long userID, @RequestParam long communityID,
                           @RequestParam long handlerID) {
        boolean result = applicationService.refuse(userID, communityID, handlerID);
        if(!result) {
            return Response.failed("处理申请失败");
        }
        return Response.success();
    }

    @GetMapping("/getApplicationOfCommunity")
    public Response getApplicationOfCommunity(@RequestParam long communityId) {
        List<Long> applicationOfCommunity = applicationService.getApplicationOfCommunity(communityId);
        if(applicationOfCommunity == null) {
            return Response.failed("查询申请列表失败");
        }
        return Response.success(applicationOfCommunity);
    }
}
