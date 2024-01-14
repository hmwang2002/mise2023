package com.mise.communitycenter;

import com.mise.communitycenter.domain.vo.CommunityVO;
import com.mise.communitycenter.service.ApplicationService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;

@SpringBootTest
public class ApplicationTest {

    @Autowired
    private ApplicationService applicationService;

    @Test
    public void getApplicationList_test() {
//        long adminId = 2;
//        Map<CommunityVO, List<Long>> applicationList = applicationService.getApplicationByAdminId(adminId);
//        applicationList.forEach((communityId, userIdList) -> {
//            System.out.print(communityId + ": ");
//            userIdList.forEach(id -> System.out.print(id + " "));
//        });
    }

    @Test
    public void test_apply() {
        applicationService.applyForCommunity(1, 206);
    }
}
