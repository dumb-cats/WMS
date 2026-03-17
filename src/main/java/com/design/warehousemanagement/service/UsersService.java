package com.design.warehousemanagement.service;

import com.design.warehousemanagement.common.Result;
import com.design.warehousemanagement.pojo.Users;
import com.baomidou.mybatisplus.extension.service.IService;
import com.design.warehousemanagement.pojo.dto.ResetPasswordDTO;
import com.design.warehousemanagement.pojo.dto.UserDTO;

/**
* @author Administrator
* @description 针对表【users(学员基本信息表)】的数据库操作Service
* @createDate 2025-04-09 13:19:11
*/
public interface UsersService extends IService<Users> {

    Result addUser(UserDTO userDTO);

    Result deleteUser(String userId);

    Result updateUser(String userId, UserDTO userDTO);

    Result getUser(String userId);

    Result getAllUsers();

    Result getUsersByName(String name, int page, int size,Integer roleId);

    Users login(String username, String password, Integer roleId);

    void resetPassword(ResetPasswordDTO dto);
}
