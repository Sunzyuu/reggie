package com.sunzy.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.sunzy.reggie.domain.User;
import com.sunzy.reggie.mapper.UserMapper;
import com.sunzy.reggie.service.UserService;
import org.springframework.stereotype.Service;

@Service
public class UserSerivceImpl extends ServiceImpl<UserMapper, User> implements UserService {
}
