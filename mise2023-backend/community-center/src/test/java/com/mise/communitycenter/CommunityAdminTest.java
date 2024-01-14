package com.mise.communitycenter;


//import com.mise.communitycenter.mapper.LocalhostMapper;
import com.mise.communitycenter.domain.vo.CommunityVO;
import com.mise.communitycenter.mapper.CommunityMapper;
import com.mise.communitycenter.service.CommunityService;
import com.mise.communitycenter.util.TimeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class CommunityAdminTest {


    @Autowired
    private CommunityService communityService;

    @Test
    public void init_community_admin() {
        CommunityVO communityVO = new CommunityVO();
//        communityVO.setCommunityID(201);
        communityVO.setName("22222211112222");
        communityVO.setCreateTime(TimeUtil.getCurrentTime());
        communityVO.setPublic(true);
        communityService.createCommunity(18, communityVO);
    }
}
