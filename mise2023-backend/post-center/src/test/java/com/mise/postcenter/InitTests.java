package com.mise.postcenter;

import com.mise.postcenter.domain.vo.CommentVO;
import com.mise.postcenter.domain.vo.PostVO;
import com.mise.postcenter.service.CommentService;
import com.mise.postcenter.service.PostService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;

/**
 * 批量插入测试数据
 */
@SpringBootTest
public class InitTests {

    @Autowired
    private PostService postService;

    @Autowired
    private CommentService commentService;

    public static final List<String> TAG_LIST = List.of("数据库", "安全");
    public static final String TITLE = "ClickHouse Keeper: 一个用 C++ 编写的 ZooKeeper 替代品";

    public static final String CONTENT = "现代分布式系统需要一个共享且可靠的信息存储库和一致性系统，以协调和同步分布式操作。对于 ClickHouse，最初选择了 ZooKeeper。它广泛被使用，证明是可靠的，并提供了简单而强大的 API，合理的性能。\n" +
            "然而，对于 ClickHouse，不仅性能，而且资源效率和可扩展性一直是首要任务。ZooKeeper 作为一个 Java 生态系统项目，不能很好地优雅地融入我们的 C++ 代码库，而且随着我们以越来越高的规模使用它，我们开始遇到资源使用和运维挑战。为了克服 ZooKeeper 的这些缺点，考虑到我们的项目需要解决的其他要求和目标，我们从零开始构建了 ClickHouse Keeper。\n" +
            "ClickHouse Keeper 是 ZooKeeper 的替代方案，具有完全兼容的客户端协议和相同的数据模型。除此之外，它还提供以下好处：\n" +
            "• 更容易的设置和操作：ClickHouse Keeper 是用 C++ 实现的，而不是 Java，因此可以嵌入到 ClickHouse 中或独立运行\n" +
            "• 由于更好的压缩，快照和日志占用的磁盘空间要少得多\n" +
            "• 默认数据包和节点数据大小没有限制（在 ZooKeeper 中是 1 MB）\n" +
            "• 没有 ZXID 溢出问题（在 ZooKeeper 中，它在每 20 亿次事务时强制重新启动）\n" +
            "• 因为使用了更好的分布式一致性协议，在网络分区后能更快地恢复。\n" +
            "• 额外的一致性保证：ClickHouse Keeper 提供与 ZooKeeper 相同的一致性保证 - 线性可写，以及相同会话内严格的操作顺序。此外，通过设置 quorum_reads，ClickHouse Keeper 还提供线性读取。\n" +
            "• ClickHouse Keeper 对于相同数量的数据更节约资源，使用的内存更少（稍后在本博客中我们将证明这一点）\n" +
            "ClickHouse Keeper 的开发始于 2021 年 2 月，作为 ClickHouse 服务中的嵌入式服务。同一年，引入了独立模式，并添加了 Jepsen 测试 - 每 6 小时，我们运行带有多种不同工作流和失败场景的自动化测试，以验证一致性机制的正确性。\n" +
            "在撰写本博客时，ClickHouse Keeper 已经在生产环境中运行了一年半以上，并且自 2022 年 5 月首次进行私人预览以来，已经在我们自己的 ClickHouse Cloud 中大规模部署。";

    public static final String PHOTO = "https://ucc.alicdn.com/pic/developer-ecology/5lrhca4a7nuco_a7d7469c9cb4427f8ad6a52cbfb175db.png";

    @Test
    void testAddPosts() {
        // 插入文章数据
        for (int i = 0; i < 100; i++) {
            PostVO postVO = new PostVO();
            postVO.setCommunityId("3");
            postVO.setIsPublic(true);
            postVO.setTagList(TAG_LIST);
            postVO.setTitle(TITLE);
            postVO.setContent(CONTENT);
            postVO.setPhoto(PHOTO);
            postVO.setUserId("1");
            postService.createPost(postVO);
        }

        for (int j = 0; j < 4; j++) {
            // 插入评论数据
            for (int i = 0; i < 100; i++) {
                CommentVO commentVO = new CommentVO();
                commentVO.setContent("很棒的文章！");
                commentVO.setPostId(String.valueOf(i + 1));
                commentVO.setUserId("1");
                commentService.createComment(commentVO);
            }
        }
    }
}
