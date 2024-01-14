package com.mise.usercenter.client;

import com.mise.usercenter.domain.entity.Comment;
import com.mise.usercenter.domain.entity.Post;
import com.mise.usercenter.domain.vo.CommentVO;
import com.mise.usercenter.domain.vo.PostVO;
import com.mise.usercenter.domain.vo.R;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * @author whm
 * @date 2023/11/26 18:22
 */
@FeignClient("post-center")
public interface PostClient {
    @PostMapping("/post/addPost")
    R<Post> addPost(@RequestBody PostVO postVO);

    @PostMapping("/post/addComment")
    R<Comment> addComment(@RequestBody CommentVO commentVO);

    @PostMapping("/post/up")
    R<String> up(@RequestParam String userId, @RequestParam String postId);

    @PostMapping("/post/up_back")
    R<String> up_back(@RequestParam String userId, @RequestParam String postId);

    @PostMapping("/post/down")
    R<String> down(@RequestParam String postId);

    @PostMapping("/post/down_back")
    R<String> down_back(@RequestParam String postId);

    @GetMapping("/post/likes")
    R<List<Post>> likes(@RequestParam String userId);

    @GetMapping("/post/getAllPosts")
    R<List<Post>> getAllPosts(@RequestParam String communityId);

    @GetMapping("/post/getUserPosts")
    R<List<Post>> getUserPosts(@RequestParam("userId") String userId);

    @GetMapping("/post/histories")
    R<List<Post>> histories(@RequestParam String userId);

    @GetMapping("/post/getRecentPosts")
    R<List<Post>> getRecentPosts();

    @PostMapping("/post/getComments")
    R<List<Comment>> getComment(@RequestParam String postId);
}
