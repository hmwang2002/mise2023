package com.mise.communitycenter.domain.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.List;

/**
 * 查看社区申请情况VO
 */
@Data
@NoArgsConstructor
public class ApplicationCheckVO implements Serializable {

    private CommunityVO communityVO;

    @JsonFormat(with = JsonFormat.Feature.ACCEPT_SINGLE_VALUE_AS_ARRAY)
    private List<Long> userIdList;
}
