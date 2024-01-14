package com.mise.usercenter.controller;

import cn.dev33.satoken.stp.SaTokenInfo;
import cn.dev33.satoken.stp.StpUtil;
import com.mise.usercenter.domain.vo.CommunityVO;
import com.mise.usercenter.domain.entity.Post;
import com.mise.usercenter.domain.entity.User;
import com.mise.usercenter.domain.vo.*;
import com.mise.usercenter.service.OssService;
import com.mise.usercenter.service.UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

/**
 * @author whm
 * @date 2023/10/24 12:33
 */
@RestController
@RequestMapping("/user")
@RequiredArgsConstructor
public class UserController {
    private final UserService userService;

    private final OssService ossService;

    @RequestMapping("/login")
    public Response login(@RequestParam("userName") String userName, @RequestParam("password") String password) {
        Long id = userService.login(userName, password);
        if (id != null) {
            StpUtil.login(id);
            SaTokenInfo saTokenInfo = StpUtil.getTokenInfo();
            return Response.success(200, "登录成功！", saTokenInfo);
        } else {
            return Response.failed(999, "用户名或密码错误！");
        }
    }

    @RequestMapping("/logout")
    public Response logout() {
        StpUtil.logout();
        return Response.success(200, "退出成功！");
    }

    @PostMapping("/register")
    public Response register(@RequestBody UserVO userVO) {
        String res = userService.register(userVO);
        if (res.equals("注册成功")) {
            return Response.success(200, "注册成功！");
        } else {
            return Response.failed(999, res);
        }
    }

    /**
     * 修改密码
     *
     * @param userVO
     * @return
     */
    @PostMapping("/update")
    public Response update(@RequestBody UserVO userVO) {
        String res = userService.update(userVO);
        if (res.equals("修改密码成功")) {
            return Response.success(200, "修改密码成功！");
        } else {
            return Response.failed(999, res);
        }
    }

    /**
     * 获取用户头像
     */
    @GetMapping("/getAvatar")
    public Response getAvatar() {
        if (StpUtil.isLogin()) {
            Long userId = StpUtil.getLoginIdAsLong();
            String url = userService.getAvatar(userId);
            return Response.success(200, "获取用户头像成功！", url);
        } else {
            return Response.failed(999, "用户未登录！");
        }
    }

    /**
     * 更新用户头像
     */
    @PostMapping("/edit")
    public Response edit(@RequestParam("userName") String userName, @RequestParam("newPhoto") MultipartFile file) {
        String url = ossService.uploadFile(file);
        String res = userService.editUserPhoto(userName, url);
        if (res.equals("用户不存在")) {
            return Response.failed(999, res);
        } else {
            return Response.success(200, res, url);
        }
    }

    /**
     * 用户发布帖子
     */
    @PostMapping("/publish")
    public Response publish(@RequestBody PostVO postVO) {
        if (StpUtil.isLogin()) {
            String res = userService.publish(postVO);
            if (res.equals("发布成功")) {
                return Response.success(200, res);
            } else {
                return Response.failed(999, res);
            }
        }
        return Response.failed(999, "用户未登录，无法发布帖子！");
    }

    /**
     * 用户发布评论
     */
    @PostMapping("/comment")
    public Response comment(@RequestBody CommentVO commentVO) {
        if (StpUtil.isLogin()) {
            String userId = StpUtil.getLoginIdAsString();
            commentVO.setUserId(userId);
            boolean res = userService.comment(commentVO);
            return res ? Response.success(200, "评论成功！") : Response.failed(999, "评论失败！");
        }
        return Response.failed(999, "用户未登录，无法评论！");
    }

    /**
     * 用户点赞帖子
     */
    @PostMapping("/like")
    public Response like(@RequestParam("postId") String postId) {
        if (StpUtil.isLogin()) {
            String userId = StpUtil.getLoginIdAsString();
            boolean res = userService.up(userId, postId);
            return res ? Response.success(200, "点赞成功！") : Response.failed(999, "点赞失败！");
        }
        return Response.failed(999, "用户未登录，无法点赞！");
    }

    /**
     * 用户取消点赞帖子
     */
    @PostMapping("/like_back")
    public Response like_back(@RequestParam("postId") String postId) {
        if (StpUtil.isLogin()) {
            String userId = StpUtil.getLoginIdAsString();
            boolean res = userService.up_back(userId, postId);
            return res ? Response.success(200, "取消点赞成功！") : Response.failed(999, "取消点赞失败！");
        }
        return Response.failed(999, "用户未登录，无法取消点赞！");
    }

    /**
     * 用户点踩帖子
     */
    @PostMapping("/dislike")
    public Response dislike(@RequestParam("postId") String postId) {
        if (StpUtil.isLogin()) {
            boolean res = userService.down(postId);
            return res ? Response.success(200, "点踩成功！") : Response.failed(999, "点踩失败！");
        }
        return Response.failed(999, "用户未登录，无法点踩！");
    }

    /**
     * 用户取消点踩帖子
     */
    @PostMapping("/dislike_back")
    public Response dislike_back(@RequestParam("postId") String postId) {
        if (StpUtil.isLogin()) {
            boolean res = userService.down_back(postId);
            return res ? Response.success(200, "取消点踩成功！") : Response.failed(999, "取消点踩失败！");
        }
        return Response.failed(999, "用户未登录，无法取消点踩！");
    }

    /**
     * 获取用户点赞过的帖子
     */
    @GetMapping("/likes")
    public Response getLikes() {
        if (StpUtil.isLogin()) {
            String userId = StpUtil.getLoginIdAsString();
            List<PostResponseVO> posts = userService.likes(userId);
            if (posts == null) return Response.success(200, "该用户没有喜欢的帖子！");
            return Response.success(200, "获取用户点赞过的帖子成功！", posts);
        }
        return Response.failed(999, "用户未登录");
    }

    /**
     * 获取用户发布的帖子
     */
    @GetMapping("/posts")
    public Response getPosts() {
        if (StpUtil.isLogin()) {
            String userId = StpUtil.getLoginIdAsString();
            List<PostResponseVO> posts = userService.posts(userId);
            if (posts == null) return Response.success(200, "用户未发布过帖子");
            return Response.success(200, "获取用户发布的帖子成功", posts);
        }
        return Response.failed(999, "用户未登录");
    }

    /**
     * 获取社区中的帖子
     */
    @GetMapping("/getAllPosts")
    public Response getAllPosts(@RequestParam("communityId") long communityId){
        if (StpUtil.isLogin()) {
            List<PostResponseVO> posts = userService.getAllPostsByCommunity(communityId);
            if (posts == null) return Response.success(200, "社区还未发布过帖子");
            return Response.success(200, "获取社区中的帖子成功", posts);
        }
        return Response.failed(999, "用户未登录");
    }

    /**
     * 获取用户的浏览记录
     */
    @GetMapping("/history")
    public Response getHistory() {
        if (StpUtil.isLogin()) {
            String userId = StpUtil.getLoginIdAsString();
            List<Post> history = userService.history(userId);
            if (history == null) return Response.success(200, "用户没有浏览过任何帖子");
            return Response.success(200, "获取用户浏览记录成功", history);
        }
        return Response.failed(999, "用户未登录");
    }

    /**
     * 获取最新的帖子
     */
    @GetMapping("/recent")
    public Response getRecent() {
        if (StpUtil.isLogin()) {
            List<PostResponseVO> recentPosts = userService.getRecentPosts();
            if (recentPosts == null) {
                return Response.failed(999, "获取最新帖子失败");
            }
            return Response.success(200, "获取最新帖子成功", recentPosts);
        }
        return Response.failed(999, "用户未登录");
    }

    /**
     * 获取评论列表
     *
     * @param postId 帖子id
     * @return 评论列表
     */
    @PostMapping("/getComments")
    public Response getComment(@RequestParam("postId") String postId) {
        if (StpUtil.isLogin()) {
            List<CommentResponseVO> commentResponseVOS = userService.getComment(postId);
            if (commentResponseVOS == null) {
                return Response.success(200, "获取评论失败");
            }
            return Response.success(200, "获取评论成功", commentResponseVOS);
        }
        return Response.failed(999, "用户未登录");
    }

    @GetMapping("/getApplicationByAdminId")
    public Response<Map<CommunityVO, List<User>>> getApplicationByAdminId(@RequestParam long adminID) {
        Map<CommunityVO, List<User>> applicationByAdminId = userService.getApplicationByAdminId(adminID);
        return Response.success(applicationByAdminId);
    }


    /**
     * 获取社区拥有者
     * @param communityID 社区id
     * @return Response User类型
     */
    @GetMapping("/getCommunityOwner")
    public Response getCommunityOwner(@RequestParam long communityID) {
        User user = userService.getCommunityOwner(communityID);
        return Response.success(user);
    }

    /**
     * 获取社区管理员
     * @param communityID 社区id
     * @return Response List<User>类型
     */
    @GetMapping("/getCommunityManagers")
    public Response getCommunityManagers(@RequestParam long communityID) {
        List<User> userList = userService.getCommunityManagers(communityID);
        return Response.success(userList);
    }

    /**
     * 获取社区普通用户
     * @param communityID 社区id
     * @return Response List<User>
     */
    @GetMapping("/getCommunityMembers")
    public Response getCommunityMembers(@RequestParam long communityID) {
        List<User> userList = userService.getCommunityMembers(communityID);
        return Response.success(userList);
    }

}
