package com.design.warehousemanagement.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.design.warehousemanagement.common.Result;
import com.design.warehousemanagement.pojo.Users;
import com.design.warehousemanagement.pojo.dto.ResetPasswordDTO;
import com.design.warehousemanagement.pojo.dto.UserDTO;
import com.design.warehousemanagement.pojo.vo.UserVO;
import com.design.warehousemanagement.service.UsersService;
import com.design.warehousemanagement.mapper.user.UsersMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.function.Function;

/**
* @author Administrator
* @description 针对表【users(学员基本信息表)】的数据库操作Service实现
* @createDate 2025-04-09 13:19:11
*/
@Service
@RequiredArgsConstructor
public class UsersServiceImpl extends ServiceImpl<UsersMapper, Users>
    implements UsersService{

    private final UsersMapper userMapper;

    @Override
    public Result addUser(UserDTO userDTO) {
        List<String> conflictMessages = new ArrayList<>();
        checkFieldConflict(userDTO.getUsername(), "昵称", userMapper::existsByUsername, conflictMessages);
        checkFieldConflict(userDTO.getIdCardNumber(), "身份证号", userMapper::existsByIdCardNumber, conflictMessages);
        checkFieldConflict(userDTO.getPhoneNumber(), "手机号", userMapper::existsByPhoneNumber, conflictMessages);
        if (!conflictMessages.isEmpty()) {
            return Result.error(String.join(", ", conflictMessages));
        }
        if (userDTO.getUserId() == null) {
            userDTO.setUserId(UUID.randomUUID().toString());
        }
        if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
            userDTO.setPassword("123456");
        }
        if(userDTO.getRoleId()==null){
            userDTO.setRoleId(0);
        }
        try {
            userMapper.insertUser(userDTO);
            return Result.success("用户添加成功");
        } catch (Exception e) {
            // 记录异常日志
            log.error("用户添加失败: {}",  e);
            return Result.error("用户添加失败，请稍后重试");
        }
    }

    /**
     * 检查字段是否存在冲突，并将冲突信息添加到消息列表中
     *
     * @param fieldValue 字段值
     * @param fieldName 字段名称（如“姓名”、“身份证号、手机号”）
     * @param existsChecker 检查字段是否存在的方法引用
     * @param conflictMessages 存储冲突信息的列表
     */
    private void checkFieldConflict(String fieldValue, String fieldName,
                                    Function<String, Integer> existsChecker, List<String> conflictMessages) {
        if (fieldValue != null && !fieldValue.isEmpty() && existsChecker.apply(fieldValue) > 0) {
            conflictMessages.add(fieldName + "已存在");
        }
    }

    @Override
    public Result deleteUser(String userId) {
        try {
            int affectedRows = userMapper.deleteUser(userId);
            if (affectedRows > 0) {
                return Result.success("用户删除成功");
            } else {
                return Result.error("用户不存在，删除失败");
            }
        } catch (Exception e) {
            log.error("删除用户失败: {}",  e);
            return Result.error("删除用户失败，请稍后重试");
        }
    }

    @Override
    public Result updateUser(String userId, UserDTO userDTO) {
        try {
            userDTO.setUserId(userId);
            int affectedRows = userMapper.updateUser(userDTO);
            if (affectedRows > 0) {
                return Result.success("用户更新成功");
            } else {
                return Result.error("用户不存在，更新失败");
            }
        } catch (Exception e) {
            log.error("更新用户失败: {}",e);
            return Result.error("更新用户失败，请稍后重试");
        }
    }

    @Override
    public Result getUser(String userId) {
        try {
            UserVO user = userMapper.selectUserById(userId);
            if (user != null) {
                return Result.success(user);
            } else {
                return Result.error("用户不存在");
            }
        } catch (Exception e) {
            // 捕获异常并记录日志
            log.error("查询用户失败: {}", e);
            return Result.error("查询用户失败，请稍后重试");
        }
    }

    @Override
    public Result getAllUsers() {
        List<UserVO> users = userMapper.selectAllUsers();
        return Result.success(users);
    }


    @Override
    public Result getUsersByName(String name, int page, int size,Integer roleId) {
        Page<Users> pagination = new Page<>(page, size);
        IPage<Users> result = userMapper.selectUsersByName(pagination, name,roleId);
        return Result.success(result);
    }

    @Override
    public Users login(String username, String password, Integer roleId) {
        QueryWrapper<Users> queryWrapper = new QueryWrapper<>();
        queryWrapper.eq("username", username)
                .eq("password", password)
                .eq("role_id", roleId);
        return userMapper.selectOne(queryWrapper) ;
    }

    @Override
    public void resetPassword(ResetPasswordDTO dto) {
        Users user = userMapper.selectByResetCriteria(dto);
        if (user == null) {
            throw new IllegalArgumentException("未找到匹配的用户");
        }
        String rawPassword = "123456";
        user.setPassword(rawPassword);
        userMapper.updateById(user);
    }

}




