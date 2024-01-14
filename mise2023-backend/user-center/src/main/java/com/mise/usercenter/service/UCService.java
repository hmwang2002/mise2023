package com.mise.usercenter.service;

import java.util.List;

/**
 * @author whm
 * @date 2023/10/26 21:49
 */
public interface UCService {
    void joinCommunity(Long userId, Long communityId);

    List<Long> getCommunityIdList(Long userId);
}
