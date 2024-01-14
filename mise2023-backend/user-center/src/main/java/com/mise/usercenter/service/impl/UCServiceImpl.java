package com.mise.usercenter.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.mise.usercenter.domain.entity.User_Community;
import com.mise.usercenter.mapper.UCMapper;
import com.mise.usercenter.service.UCService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * @author whm
 * @date 2023/10/26 21:49
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class UCServiceImpl implements UCService {
    private final UCMapper ucMapper;


    @Override
    public void joinCommunity(Long userId, Long communityId) {
        LambdaQueryWrapper<User_Community> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User_Community::getUserId, userId);
        User_Community uc = ucMapper.selectOne(queryWrapper);
        if (uc == null) {
            uc = new User_Community();
            uc.setUserId(userId);
            uc.setCommunityList(communityId.toString());
            ucMapper.insert(uc);
            log.info("新增用户社区关联：{}", uc);
        } else {
            String communityList = uc.getCommunityList();
            List<Long> communityIdList = new ArrayList<>(Arrays.stream(communityList.split(",")).map(Long::parseLong).toList());
            if (communityIdList.contains(communityId)) {
                // 应该不会碰到这个情况，前端加入过的社区直接没有申请加入按钮
                log.info("用户已加入该社区");
                return;
            }
            communityIdList.add(communityId);
            communityIdList.sort(Long::compareTo);
            communityList = String.join(",", communityIdList.stream().map(String::valueOf).toList());
            uc.setCommunityList(communityList);
            ucMapper.updateById(uc);
            log.info("用户成功加入社区");
        }
    }

    @Override
    public List<Long> getCommunityIdList(Long userId) {
        LambdaQueryWrapper<User_Community> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(User_Community::getUserId, userId);
        User_Community uc = ucMapper.selectOne(queryWrapper);
        if (uc == null) {
            return null;
        }
        String communityList = uc.getCommunityList();
        List<Long> communityIdList = new ArrayList<>(Arrays.stream(communityList.split(",")).map(Long::parseLong).toList());
        if (!communityIdList.isEmpty()) {
            return communityIdList;
        }
        return null;
    }
}
