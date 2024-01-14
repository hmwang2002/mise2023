package com.mise.usercenter.client;


import com.mise.usercenter.config.FeignConfig;
import com.mise.usercenter.domain.vo.CommunityVO;
import com.mise.usercenter.domain.vo.Response;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Import;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;


@FeignClient("community-center")
@Import(FeignConfig.class)
public interface CommunityClient {

    @GetMapping("/community/getAdminCommunitiesByAdminId")
    Response<List<CommunityVO>> getAdminCommunitiesByAdminId(@RequestParam long adminId);

    @GetMapping("/application/getApplicationOfCommunity")
    Response<List<Long>> getApplicationOfCommunity(@RequestParam long communityId);

    @GetMapping("/community/getCommunityById")
    Response<CommunityVO> getCommunityById(@RequestParam long communityID);

    @GetMapping("/community/getCommunityOwner")
    Response<Long> getCommunityOwner(@RequestParam long communityID);

    @GetMapping("/community/getCommunityManagers")
    Response<List<Long>> getCommunityManagers(@RequestParam long communityID);

    @GetMapping("/community/getCommunityMembers")
    Response<List<Long>> getCommunityMembers(@RequestParam long communityID);

}