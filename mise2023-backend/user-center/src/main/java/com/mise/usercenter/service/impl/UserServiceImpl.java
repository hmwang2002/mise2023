package com.mise.usercenter.service.impl;

import cn.dev33.satoken.secure.BCrypt;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.mise.usercenter.client.CommunityClient;
import com.mise.usercenter.client.PostClient;
import com.mise.usercenter.domain.entity.Comment;
import com.mise.usercenter.domain.entity.Post;
import com.mise.usercenter.domain.entity.User;
import com.mise.usercenter.domain.vo.*;
import com.mise.usercenter.mapper.UserMapper;
import com.mise.usercenter.service.UserService;
import com.mise.usercenter.utils.RedisCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.exceptions.TooManyResultsException;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.*;

/**
 * @author whm
 * @date 2023/10/24 15:54
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UserServiceImpl implements UserService {
    private final UserMapper userMapper;

    private final RedisCache redisCache;

    private final PostClient postClient;

    private final CommunityClient communityClient;

    @Override
    public Long login(String userName, String password) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, userName);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            return null;
        }
        user.setLastLoginTime(new Date());
        userMapper.updateById(user);
        return user.getUserId();
    }

    @Override
    public String register(UserVO userVO) {
        String captcha = redisCache.getCacheObject(userVO.getPhone());
        if (!userVO.getVerifyCode().equals(captcha)) {
            return "验证码错误";
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, userVO.getUserName());
        User user = userMapper.selectOne(queryWrapper);
        if (user != null) {
            return "用户名已存在";
        }

        queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, userVO.getPhone());
        user = userMapper.selectOne(queryWrapper);
        if (user != null) {
            return "该手机号已绑定用户";
        }

        user = new User();
        user.setUserName(userVO.getUserName());
        user.setPassword(BCrypt.hashpw(userVO.getPassword()));
        user.setPhone(userVO.getPhone());
        user.setCreateTime(new Date());
        user.setPhoto("https://kiyotakawang.oss-cn-hangzhou.aliyuncs.com/%E9%BB%98%E8%AE%A4%E5%A4%B4%E5%83%8F.jpg");
        userMapper.insert(user);
        return "注册成功";
    }

    @Override
    public String update(UserVO userVO) {
        String captcha = redisCache.getCacheObject(userVO.getPhone());
        if (!userVO.getVerifyCode().equals(captcha)) {
            return "验证码错误";
        }
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getPhone, userVO.getPhone());
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return "该手机号未绑定用户";
        }
        user.setPassword(BCrypt.hashpw(userVO.getPassword()));
        userMapper.updateById(user);
        return "修改密码成功";
    }

    @Override
    public Long getUserId(String userName) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, userName);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) return null;
        return user.getUserId();
    }

    @Override
    public String getAvatar(Long userId) {
        User user = userMapper.selectById(userId);
        return user.getPhoto();
    }

    @Override
    public String editUserPhoto(String userName, String url) {
        LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User::getUserName, userName);
        User user = userMapper.selectOne(queryWrapper);
        if (user == null) {
            return "用户不存在";
        }
        user.setPhoto(url);
        userMapper.updateById(user);
        return "修改用户头像成功";
    }

    @Override
    public String publish(PostVO postVO) {
        R<Post> r = postClient.addPost(postVO);
        if (r.getCode() == 1) return "发布成功";
        else return "发布失败";
    }

    @Override
    public boolean comment(CommentVO commentVO) {
        R<Comment> r = postClient.addComment(commentVO);
        return r.getCode() == 1;
    }

    @Override
    public boolean up(String userId, String postId) {
        R<String> r = postClient.up(userId, postId);
        return r.getCode() == 1;
    }

    @Override
    public boolean up_back(String userId, String postId) {
        R<String> r = postClient.up_back(userId, postId);
        return r.getCode() == 1;
    }

    @Override
    public boolean down(String postId) {
        R<String> r = postClient.down(postId);
        return r.getCode() == 1;
    }

    @Override
    public boolean down_back(String postId) {
        R<String> r = postClient.down_back(postId);
        return r.getCode() == 1;
    }

    @Override
    public List<PostResponseVO> likes(String userId) {
        R<List<Post>> r = postClient.likes(userId);
        if (r.getCode() == 1) {
            List<Post> posts = r.getData();
            List<PostResponseVO> postResponseVOS = new ArrayList<>();
            for (Post post : posts) {
                if (post == null) {
                    continue;
                }
                PostResponseVO postResponseVO = new PostResponseVO();
                postResponseVO.setPostId(post.getPostId().toString());
                String name = communityClient.getCommunityById(post.getCommunityId()).getData().getName();
                postResponseVO.setCommunityName(name);
                postResponseVO.setIsPublic(post.getIsPublic());
                postResponseVO.setTagList(post.getTagList());
                postResponseVO.setTitle(post.getTitle());
                postResponseVO.setContent(post.getContent());
                postResponseVO.setCommentNum(post.getCommentNum().toString());
                postResponseVO.setLikeNum(post.getLikeNum().toString());
                postResponseVO.setDislikeNum(post.getDislikeNum().toString());
                postResponseVO.setCreateTime(post.getCreateTime());
                postResponseVO.setLastUpdateTime(post.getLastUpdateTime());
                postResponseVO.setPostPicture(post.getPhoto());
                User user = userMapper.selectById(post.getUserId());
                postResponseVO.setPhoto(user.getPhoto());
                postResponseVO.setUserName(user.getUserName());
                postResponseVOS.add(postResponseVO);
            }
            return postResponseVOS;
        }
        return null;
    }

    @Override
    public List<PostResponseVO> posts(String userId) {
        R<List<Post>> r = postClient.getUserPosts(userId);
        if (r.getCode() == 1) {
            List<Post> posts = r.getData();
            List<PostResponseVO> postResponseVOS = new ArrayList<>();
            for (Post post : posts) {
                PostResponseVO postResponseVO = new PostResponseVO();
                postResponseVO.setPostId(post.getPostId().toString());
                String name = communityClient.getCommunityById(post.getCommunityId()).getData().getName();
                postResponseVO.setCommunityName(name);
                postResponseVO.setIsPublic(post.getIsPublic());
                postResponseVO.setTagList(post.getTagList());
                postResponseVO.setTitle(post.getTitle());
                postResponseVO.setContent(post.getContent());
                postResponseVO.setCommentNum(post.getCommentNum().toString());
                postResponseVO.setLikeNum(post.getLikeNum().toString());
                postResponseVO.setDislikeNum(post.getDislikeNum().toString());
                postResponseVO.setCreateTime(post.getCreateTime());
                postResponseVO.setLastUpdateTime(post.getLastUpdateTime());
                postResponseVO.setPostPicture(post.getPhoto());
                User user = userMapper.selectById(post.getUserId());
                postResponseVO.setPhoto(user.getPhoto());
                postResponseVO.setUserName(user.getUserName());
                postResponseVOS.add(postResponseVO);
            }
            return postResponseVOS;
        }
        return null;
    }

    @Override
    public List<Post> history(String userId) {
        R<List<Post>> r = postClient.histories(userId);
        if (r.getCode() == 1) {
            return r.getData();
        }
        return null;
    }

    @Override
    public List<PostResponseVO> getRecentPosts() {
        R<List<Post>> r = postClient.getRecentPosts();
        if (r.getCode() == 1) {
            List<Post> posts = r.getData();
            List<PostResponseVO> postResponseVOS = new ArrayList<>();
            for (Post post : posts) {
                PostResponseVO postResponseVO = new PostResponseVO();
                postResponseVO.setPostId(post.getPostId().toString());
                String name = communityClient.getCommunityById(post.getCommunityId()).getData().getName();
                postResponseVO.setCommunityName(name);
                postResponseVO.setIsPublic(post.getIsPublic());
                postResponseVO.setTagList(post.getTagList());
                postResponseVO.setTitle(post.getTitle());
                postResponseVO.setContent(post.getContent());
                postResponseVO.setCommentNum(post.getCommentNum().toString());
                postResponseVO.setLikeNum(post.getLikeNum().toString());
                postResponseVO.setDislikeNum(post.getDislikeNum().toString());
                postResponseVO.setCreateTime(post.getCreateTime());
                postResponseVO.setLastUpdateTime(post.getLastUpdateTime());
                postResponseVO.setPostPicture(post.getPhoto());
                User user = userMapper.selectById(post.getUserId());
                postResponseVO.setPhoto(user.getPhoto());
                postResponseVO.setUserName(user.getUserName());
                postResponseVOS.add(postResponseVO);
            }
            return postResponseVOS;
        }
        return null;
    }

    @Override
    public List<PostResponseVO> getAllPostsByCommunity(long communityId) {
        R<List<Post>> r = postClient.getAllPosts(String.valueOf(communityId));
        if (r.getCode() == 1) {
            List<Post> posts = r.getData();
            List<PostResponseVO> postResponseVOS = new ArrayList<>();
            for (Post post : posts) {
                PostResponseVO postResponseVO = new PostResponseVO();
                postResponseVO.setPostId(post.getPostId().toString());
                String name = communityClient.getCommunityById(post.getCommunityId()).getData().getName();
                postResponseVO.setCommunityName(name);
                postResponseVO.setIsPublic(post.getIsPublic());
                postResponseVO.setTagList(post.getTagList());
                postResponseVO.setTitle(post.getTitle());
                postResponseVO.setContent(post.getContent());
                postResponseVO.setCommentNum(post.getCommentNum().toString());
                postResponseVO.setLikeNum(post.getLikeNum().toString());
                postResponseVO.setDislikeNum(post.getDislikeNum().toString());
                postResponseVO.setCreateTime(post.getCreateTime());
                postResponseVO.setLastUpdateTime(post.getLastUpdateTime());
                postResponseVO.setPostPicture(post.getPhoto());
                User user = userMapper.selectById(post.getUserId());
                postResponseVO.setPhoto(user.getPhoto());
                postResponseVO.setUserName(user.getUserName());
                postResponseVOS.add(postResponseVO);
            }
            return postResponseVOS;
        }
        return null;
    }

    @Override
    public List<CommentResponseVO> getComment(String postId) {
        R<List<Comment>> r = postClient.getComment(postId);
        if (r.getCode() == 1) {
            List<Comment> comments = r.getData();
            List<CommentResponseVO> commentResponseVOS = new ArrayList<>();
            for (Comment comment : comments) {
                CommentResponseVO commentResponseVO = new CommentResponseVO();
                commentResponseVO.setCommentId(comment.getCommentId().toString());
                commentResponseVO.setPostId(comment.getPostId().toString());
                commentResponseVO.setContent(comment.getContent());
                User user = userMapper.selectById(comment.getUserId());
                commentResponseVO.setUserName(user.getUserName());
                commentResponseVO.setPhoto(user.getPhoto());
                commentResponseVO.setCreateTime(comment.getCreateTime());
                commentResponseVOS.add(commentResponseVO);
            }
            return commentResponseVOS;
        }
        return null;
    }

    @Override
    public Map<CommunityVO, List<User>> getApplicationByAdminId(@RequestParam long adminID) {
        Response<List<CommunityVO>> r = communityClient.getAdminCommunitiesByAdminId(adminID);
        Map<CommunityVO, List<User>> res = new HashMap<>();
        if (r.getCode() == 200) {
            List<CommunityVO> communities = r.getData();
            for (CommunityVO community : communities) {
                long communityID = community.getCommunityID();
                Response<List<Long>> rr = communityClient.getApplicationOfCommunity(communityID);
                if (rr.getCode() == 200) {
                    List<Long> userIdList = rr.getData();
                    List<User> userList = new ArrayList<>();
                    for (Long id : userIdList) { //根据userid查完整的user信息
                        QueryWrapper<User> wrapper = new QueryWrapper<>();
                        wrapper.lambda()
                                .eq(User::getUserId, id)
                                .select(User::getUserId, User::getUserName, User::getPhoto);
                        try {
                            User user = userMapper.selectOne(wrapper);
                            if (user == null) {
                                log.error("No such userId: {}", id);
                            }
                            userList.add(user);
                        } catch (TooManyResultsException e) {
                            log.error("Duplicated userId: {}", id);
                            userList.add(null);
                        }
                    }
                    res.put(community, userList);
                } else {
                    return null;
                }
            }
            return res;
        }
        return null;
    }

    @Override
    public User getCommunityOwner(long communityID) {
        Response<Long> r = communityClient.getCommunityOwner(communityID);
        Long id = r.getData();
        User user = new User();
        QueryWrapper<User> wrapper = new QueryWrapper<>();
        wrapper.lambda()
                .eq(User::getUserId, id)
                .select(User::getUserId, User::getUserName, User::getPhoto);
        try {
            user = userMapper.selectOne(wrapper);
            if (user == null) {
                log.error("No such userId: {}", id);
            }
        } catch (TooManyResultsException e) {
            log.error("Duplicated userId: {}", id);
        }
        return user;
    }

    @Override
    public List<User> getCommunityManagers(long communityID) {
        Response<List<Long>> response = communityClient.getCommunityManagers(communityID);
        List<Long> userIdList = response.getData();
        List<User> userList = new ArrayList<>();
        for (Long id : userIdList) { //根据userid查完整的user信息
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.lambda()
                    .eq(User::getUserId, id)
                    .select(User::getUserId, User::getUserName, User::getPhoto);
            try {
                User user = userMapper.selectOne(wrapper);
                if (user == null) {
                    log.error("No such userId: {}", id);
                }
                userList.add(user);
            } catch (TooManyResultsException e) {
                log.error("Duplicated userId: {}", id);
                userList.add(null);
            }
        }
        return userList;
    }

    @Override
    public List<User> getCommunityMembers(long communityID) {
        Response<List<Long>> response = communityClient.getCommunityMembers(communityID);
        List<Long> userIdList = response.getData();
        List<User> userList = new ArrayList<>();
        for (Long id : userIdList) { //根据userid查完整的user信息
            QueryWrapper<User> wrapper = new QueryWrapper<>();
            wrapper.lambda()
                    .eq(User::getUserId, id)
                    .select(User::getUserId, User::getUserName, User::getPhoto);
            try {
                User user = userMapper.selectOne(wrapper);
                if (user == null) {
                    log.error("No such userId: {}", id);
                }
                userList.add(user);
            } catch (TooManyResultsException e) {
                log.error("Duplicated userId: {}", id);
                userList.add(null);
            }
        }
        return userList;
    }


}
