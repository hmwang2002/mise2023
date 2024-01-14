package com.mise.usercenter.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.mise.usercenter.domain.vo.Response;
import com.mise.usercenter.service.UCService;
import com.mise.usercenter.service.UserService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户和社区互动相关接口
 * @author whm
 * @date 2023/10/27 15:05
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/uc")
public class UCController {
    private final UCService ucService;

    private final UserService userService;

    /**
     * 公开的社区用户点击即可加入，无需审核
     * 需要检验用户是否登录
     * @param communityId
     * @return
     */
    @RequestMapping("/join/public")
    public Response joinPublicCommunity(@RequestParam("communityId") Long communityId) {
        boolean isLogin = StpUtil.isLogin();
        if (!isLogin) {
            log.info("用户未登录，无法加入社区");
            return Response.failed(999, "用户未登录，无法加入社区");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        ucService.joinCommunity(userId, communityId);
        return Response.success(200, "加入社区成功！");
    }

    /**
     * 私有社区当申请被批准后，直接将用户加入社区，无需检验用户是否登录
     * @param communityId
     * @return
     */
    @RequestMapping("/join/private")
    public Response joinPrivateCommunity(@RequestParam("userName") String userName, @RequestParam("communityId") Long communityId) {
        Long userId = userService.getUserId(userName);
        if (userId == null) {
            log.error("用户名不存在，无法加入社区");
            return Response.failed(999, "用户名不存在，无法加入社区");
        }
        ucService.joinCommunity(userId, communityId);
        return Response.success(200, "加入社区成功！");
    }

    /**
     * 获取用户加入的社区id
     */
    @RequestMapping("/getCommunityIdList")
    public Response getCommunityIdList() {
        boolean isLogin = StpUtil.isLogin();
        if (!isLogin) {
            log.info("用户未登录，无法获取社区列表");
            return Response.failed(999, "用户未登录，无法获取社区列表");
        }
        Long userId = StpUtil.getLoginIdAsLong();
        return Response.success(200, "获取社区列表成功！", ucService.getCommunityIdList(userId));
    }
}
