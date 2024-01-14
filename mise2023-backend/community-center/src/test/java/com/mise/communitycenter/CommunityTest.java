package com.mise.communitycenter;

import com.mise.communitycenter.domain.entity.Community;
import com.mise.communitycenter.domain.vo.CommunityVO;
import com.mise.communitycenter.mapper.CommunityMapper;
import com.mise.communitycenter.service.CommunityService;
import com.mise.communitycenter.util.TimeUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

@SpringBootTest
class CommunityTest {

    @Autowired
    private CommunityMapper communityMapper;

    @Autowired
    private CommunityService communityService;

    @Test
    public void create_community_test() {
        CommunityVO communityVO = new CommunityVO();
        communityVO.setName("asdasdas");
        communityVO.setCreateTime(TimeUtil.getCurrentTime());
        communityVO.setPublic(true);
        communityService.createCommunity(11, communityVO);
    }

    @Test
    public void set_admin_test() {
        communityService.setAdmin(210, 12);
    }

    @Test
    public void delete_member_test() {
        communityService.deleteMember(206, 20);
    }

    @Test
    public void add_member_test() {
        communityService.addMember(210, 123, 1);
    }

    @Test
    public void mysqlTest() throws ParseException {
        List<Community> communities = init_community();
        communities.forEach(community -> communityMapper.insert(community));
    }

    private List<Community> init_community() throws ParseException {
        String date = "2023-11-";
        List<Community> list = new ArrayList<>();
        for (int day = 1; day <= 30 ; day++) {
            Community community = new Community();
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
            community.setCreateTime(sdf.parse(date + day));
            community.setPublic(true);
            list.add(community);
        }
        return list;
    }

}
