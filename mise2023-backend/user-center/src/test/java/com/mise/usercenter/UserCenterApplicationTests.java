package com.mise.usercenter;

import com.mise.usercenter.service.SmsService;
import com.mise.usercenter.service.UCService;
import com.mise.usercenter.service.UserService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.util.List;
import java.util.Map;


@SpringBootTest
class UserCenterApplicationTests {
    @Autowired
    private SmsService smsService;

    @Autowired
    private UCService ucService;

    @Autowired
    private UserService userService;

    @Test
    void contextLoads() {
    }


//    @Test
//    void test_user_community() {
//        Map<CommunityVO, List<User>> applicationByAdminId = userService.getApplicationByAdminId(20L);
//        for (CommunityVO vo : applicationByAdminId.keySet()) {
//            List<User> users = applicationByAdminId.get(vo);
//            System.out.print(vo.getCommunityID() + ": ");
//            for (User user : users) {
//                System.out.print(user.getUserId() + " ");
//            }
//            System.out.println();
//        }
//    }

}
