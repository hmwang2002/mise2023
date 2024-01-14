package com.mise.usercenter.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.mise.usercenter.domain.entity.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * @author whm
 * @date 2023/10/24 15:46
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

}
