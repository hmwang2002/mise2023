package com.mise.postcenter.controller;

import com.mise.postcenter.common.R;
import com.mise.postcenter.domain.entity.Comment;
import com.mise.postcenter.domain.entity.Post;
import com.mise.postcenter.domain.vo.CommentVO;
import com.mise.postcenter.domain.vo.PostVO;
import com.mise.postcenter.repository.HistoryRepository;
import com.mise.postcenter.service.CommentService;
import com.mise.postcenter.service.CommonService;
import com.mise.postcenter.service.PostService;
import jakarta.servlet.ServletOutputStream;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RestController
@RequestMapping("/post")
public class PostController {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    @Autowired
    private CommonService commonService;

    @Autowired
    private HistoryRepository historyRepository;


    @Value("${obs.prefix}")
    private String prefix;


    /**
     * 获取最新帖子
     *
     * @return 最新帖子列表
     */
    @GetMapping("/getRecentPosts")
    public R<List<Post>> getRecentPosts() {
        List<Post> posts = postService.getRecentPosts();
        if (posts.isEmpty()) {
            return R.error("没有最新帖子！");
        }
        return R.success(posts);
    }

    /**
     * 发布帖子
     *
     * @param postVO 帖子信息
     * @return 发布结果
     */
    @PostMapping("/addPost")
    public R<Post> addPost(@RequestBody PostVO postVO) {
        Post post = postService.createPost(postVO);
        if (post == null) {
            return R.error("帖子发布失败！");
        }
        return R.success(post);
    }


    /**
     * 删除帖子
     *
     * @param postId 帖子id
     * @return 删除结果
     */
    @PostMapping("/deletePost")
    public R<String> deletePost(@RequestParam String postId) {
        try {
            postService.deletePost(Long.valueOf(postId));
        } catch (Exception e) {
            return R.error("帖子删除失败！");
        }
        return R.success("帖子删除成功！");
    }

    /**
     * 获取帖子
     *
     * @param postId 帖子id
     * @return 帖子
     */
    @GetMapping("/getPost")
    public R<Post> getPost(@RequestParam String userId, @RequestParam String postId) {
        Post post = postService.getPostById(Long.valueOf(postId));
        if (post == null) {
            return R.error("该帖子不存在！");
        }
        historyRepository.save(postService.createHistory(Long.valueOf(userId), Long.valueOf(postId)));
        return R.success(post);
    }

    /**
     * 根据communityId查找其中的所有帖子
     *
     * @param communityId 社区id
     * @return 帖子列表
     */
    @GetMapping("/getAllPosts")
    public R<List<Post>> getAllPosts(@RequestParam String communityId) {
        List<Post> posts = postService.getPostsByCommunityId(Long.valueOf(communityId));
        if (posts.isEmpty()) {
            return R.error("该社区中没有帖子！");
        }
        return R.success(posts);
    }

    /**
     * 根据userId查找该用户发布的所有帖子
     *
     * @param userId 用户id
     * @return 帖子列表
     */
    @GetMapping("getUserPosts")
    public R<List<Post>> getUserPosts(@RequestParam("userId") String userId) {
        List<Post> posts = postService.getPostsByUserId(userId);
        if (posts.isEmpty()) {
            return R.error("该用户没有发布过帖子");
        }
        return R.success(posts);
    }


    /**
     * 发布评论
     *
     * @param commentVO 评论信息
     * @return 发布结果
     */
    @PostMapping("/addComment")
    public R<Comment> addComment(@RequestBody CommentVO commentVO) {
        Comment comment = commentService.createComment(commentVO);
        if (comment == null) {
            return R.error("评论发布失败！");
        }
        return R.success(comment);
    }

    /**
     * 删除评论
     *
     * @param commentId 评论id
     * @return 删除结果
     */
    @PostMapping("/deleteComment")
    public R<String> deleteComment(@RequestParam String commentId) {
        try {
            commentService.deleteComment(Long.valueOf(commentId));
        } catch (Exception e) {
            return R.error("评论删除失败！");
        }
        return R.success("评论删除成功！");
    }

    /**
     * 根据postId查询这条帖子中的评论
     *
     * @param postId 帖子id
     * @return 评论列表
     */
    @PostMapping("/getComments")
    public R<List<Comment>> getComments(@RequestParam String postId) {
        List<Comment> comments = commentService.getCommentsByPostId(Long.valueOf(postId));
        if (comments.isEmpty()) {
            return R.error("该帖子没有评论！");
        }
        return R.success(comments);
    }

    /**
     * 点赞帖子
     *
     * @param userId 用户id
     * @param postId 帖子id
     * @return 点赞结果
     */
    @PostMapping("/up")
    public R<String> up(@RequestParam String userId, @RequestParam String postId) {
        boolean up = postService.up(Long.valueOf(userId), Long.valueOf(postId));
        if (up) {
            return R.success("点赞成功");
        }
        return R.error("点赞失败");
    }

    /**
     * 取消点赞帖子
     *
     * @param userId 用户id
     * @param postId 帖子id
     * @return 取消点赞结果
     */
    @PostMapping("/up_back")
    public R<String> up_back(@RequestParam String userId, @RequestParam String postId) {
        boolean up = postService.up_back(Long.valueOf(userId), Long.valueOf(postId));
        if (up) {
            return R.success("点赞成功");
        }
        return R.error("点赞失败");
    }

    /**
     * 点踩帖子
     *
     * @param postId 帖子id
     * @return 点踩结果
     */
    @PostMapping("/down")
    public R<String> down(@RequestParam String postId) {
        boolean down = postService.down(Long.valueOf(postId));
        if (down) {
            return R.success("点踩成功");
        }
        return R.error("点踩失败");
    }

    /**
     * 取消点踩帖子
     *
     * @param postId 帖子id
     * @return 取消点踩结果
     */
    @PostMapping("/down_back")
    public R<String> down_back(@RequestParam String postId) {
        boolean down = postService.down_back(Long.valueOf(postId));
        if (down) {
            return R.success("点踩成功");
        }
        return R.error("点踩失败");
    }


    /**
     * 查看该用户的所有点赞的帖子
     *
     * @param userId 用户id
     * @return 帖子列表
     */
    @GetMapping("/likes")
    public R<List<Post>> likes(@RequestParam String userId) {
        List<Post> likes = postService.getLikesByUserId(Long.valueOf(userId));
        if (likes.isEmpty()) {
            return R.error("该用户没有喜欢的帖子！");
        }
        return R.success(likes);
    }

    /**
     * 查看该用户的所有浏览过的帖子
     *
     * @param userId 用户id
     * @return 帖子列表
     */
    @GetMapping("/histories")
    public R<List<Post>> histories(@RequestParam String userId) {
        List<Post> histories = postService.getHistoriesByUserId(Long.valueOf(userId));
        if (histories.isEmpty()) {
            return R.error("该用户没有浏览过的帖子！");
        }
        return R.success(histories);
    }


    /**
     * 文件上传
     * 函数参数名"file"需要与前端对应
     *
     * @param file xx
     * @return xx
     */
    @PostMapping("/uploadPicture")
    public R<String> upload(MultipartFile file) throws IOException {
        String fileName = commonService.upload(file);
        return R.success(prefix + fileName);
    }

    /**
     * 文件下载
     *
     * @param name     xx
     * @param response xx
     */
    @GetMapping("/downloadPicture")
    public void download(String name, HttpServletResponse response) throws IOException {

        // 去除前缀
        name = name.substring(prefix.length());

        // 输出流将文件写回浏览器
        ServletOutputStream outputStream = response.getOutputStream();

        // 设置响应类型
        response.setContentType("image/jpeg");

        InputStream inputStream = commonService.download(name);

        byte[] buffer = new byte[1024];
        int bytesRead;
        while ((bytesRead = inputStream.read(buffer)) != -1) {
            outputStream.write(buffer, 0, bytesRead);
        }

        // 关闭资源
        outputStream.close();
        inputStream.close();
    }

}
