package com.mise.postcenter.service.impl;

import com.mise.postcenter.domain.entity.History;
import com.mise.postcenter.domain.entity.Like;
import com.mise.postcenter.domain.entity.Post;
import com.mise.postcenter.domain.vo.PostVO;
import com.mise.postcenter.repository.CommentRepository;
import com.mise.postcenter.repository.HistoryRepository;
import com.mise.postcenter.repository.LikeRepository;
import com.mise.postcenter.repository.PostRepository;
import com.mise.postcenter.service.PostService;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PostServiceImpl implements PostService {

    @Autowired
    private PostRepository postRepository;

    @Autowired
    private CommentRepository commentRepository;

    @Autowired
    private LikeRepository likeRepository;

    @Autowired
    private HistoryRepository historyRepository;


    static final Long defaultFirstPostId = 0L;
    static final Long defaultFirstLikeId = 0L;


    public List<Post> getAllPosts() {
        return postRepository.findAll();
    }

    public Post getPostById(Long id) {
        return postRepository.findById(id).orElse(null);
    }

    public List<Post> getPostByTitleContaining(String keyword) {
        return postRepository.findByTitleContaining(keyword);
    }

    public List<Post> getPostByTitleAndUserId(String title, Long userId) {
        return postRepository.findByTitleAndUserId(title, userId);
    }

    public Post createPost(PostVO postVO) {
        Post post = new Post();
        post.setTagList(postVO.getTagList());
        post.setContent(postVO.getContent());
        post.setPhoto(postVO.getPhoto());
        post.setCommunityId(Long.valueOf(postVO.getCommunityId()));
        post.setIsPublic(postVO.getIsPublic());
        post.setTitle(postVO.getTitle());
        post.setUserId(Long.valueOf(postVO.getUserId()));
        post.setCommentNum(0);
        post.setLikeNum(0);
        post.setDislikeNum(0);
        post.setPostId(getLastPostId() + 1);
        post.setCreateTime(new Date());
        post.setLastUpdateTime(new Date());
        return postRepository.save(post);
    }

    public void deletePost(Long id) {
        postRepository.deleteById(id);
    }

    @Override
    public void deleteAll() {
        postRepository.deleteAll();
    }

    @Override
    public void updatePost(Post post) {
        post.setLastUpdateTime(new Date());
        postRepository.save(post);
    }

    public Long getLastPostId() {
        Post lastPost = postRepository.findFirstByOrderByCreateTimeDesc();
        if (lastPost != null) {
            return lastPost.getPostId();
        }
        return defaultFirstPostId;
    }

    @Override
    public List<Post> getPostsByCommunityId(Long communityId) {
        return postRepository.findPostByCommunityId(communityId);
    }

    @Override
    public List<Post> getPostsByUserId(String userId) {
        return postRepository.findPostByUserId(Long.parseLong(userId));
    }

    @Override
    public List<Post> getSimilarPost(Post targetPost, List<Post> postList, int topK) {
        if (topK <= postList.size()) {
            return postList;
        }
        List<Integer> scoreList = new ArrayList<>();
        int keywordN = 10;
        TFIDFAnalyzer tfidfAnalyzer = new TFIDFAnalyzer();
        List<Keyword> targetKeywords = tfidfAnalyzer.analyze(targetPost.getContent(), keywordN);
        if (targetKeywords.size() < 10) {
            keywordN = targetKeywords.size();
        }
        for (Post post : postList) {
            List<Keyword> keywords = tfidfAnalyzer.analyze(post.getContent(), keywordN);
            int score = 0;
            for (Keyword keyword : targetKeywords) {
                for (Keyword keyword1 : keywords) {
                    if (keyword.getName().equals(keyword1.getName())) {
                        score += keyword.getTfidfvalue() * keyword1.getTfidfvalue();
                    }
                }
            }
            scoreList.add(score);
        }
        List<Post> similarPostList = new ArrayList<>();
        for (int i = 0; i < topK; i++) {
            int maxIndex = 0;
            int maxScore = 0;
            for (int j = 0; j < scoreList.size(); j++) {
                if (scoreList.get(j) > maxScore) {
                    maxScore = scoreList.get(j);
                    maxIndex = j;
                }
            }
            similarPostList.add(postList.get(maxIndex));
            scoreList.remove(maxIndex);
            postList.remove(maxIndex);
        }
        return similarPostList;
    }

    @Override
    public boolean up(Long userId, Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return false;
        }
        post.setLikeNum(post.getLikeNum() + 1);
        postRepository.save(post);

        Like like = new Like();
        like.setLikeId(getLastLikeId() + 1);
        like.setUserId(userId);
        like.setPostId(postId);
        like.setLikeTime(new Date());
        likeRepository.save(like);
        return true;
    }

    @Override
    public boolean up_back(Long userId, Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return false;
        }
        post.setLikeNum(post.getLikeNum() - 1);
        postRepository.save(post);

//        Like like = new Like();
//        like.setLikeId(getLastLikeId() + 1);
//        like.setUserId(userId);
//        like.setPostId(postId);
//        like.setLikeTime(new Date());
//        likeRepository.save(like);
        return true;
    }


    @Override
    public boolean down(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return false;
        }
        post.setDislikeNum(post.getDislikeNum() + 1);
        postRepository.save(post);
        return true;
    }

    @Override
    public boolean down_back(Long postId) {
        Post post = postRepository.findById(postId).orElse(null);
        if (post == null) {
            return false;
        }
        post.setDislikeNum(post.getDislikeNum() - 1);
        postRepository.save(post);
        return true;
    }

    @Override
    public List<Post> getLikesByUserId(Long userId) {
        List<Like> likes = likeRepository.findAllByUserId(userId);
        ArrayList<Post> posts = new ArrayList<>();
        for (Like like : likes) {
            Long postId = like.getPostId();
            Post post = postRepository.findById(postId).orElse(null);
            posts.add(post);
        }
        return posts;
    }

    @Override
    public List<Post> getHistoriesByUserId(Long userId) {
        List<History> histories = historyRepository.findAllByUserId(userId);
        ArrayList<Post> posts = new ArrayList<>();
        for (History history : histories) {
            Long postId = history.getPostId();
            Post post = postRepository.findById(postId).orElse(null);
            posts.add(post);
        }
        return posts;
    }

    @Override
    public History createHistory(Long userId, Long postId) {
        History history = new History();
        history.setHistoryId(getLastHistoryId() + 1);
        history.setUserId(userId);
        history.setPostId(postId);
        history.setVisitTime(new Date());
        return historyRepository.save(history);
    }

    @Override
    public List<Post> getRecentPosts() {
        return postRepository.findTop20ByOrderByCreateTimeDesc();
    }

    private Long getLastHistoryId() {
        History lastHistory = historyRepository.findFirstByOrderByVisitTimeDesc();
        if (lastHistory != null) {
            return lastHistory.getHistoryId();
        }
        return defaultFirstLikeId;
    }

    public Long getLastLikeId() {
        Like lastLike = likeRepository.findFirstByOrderByLikeTimeDesc();
        if (lastLike != null) {
            return lastLike.getLikeId();
        }
        return defaultFirstLikeId;
    }
}
