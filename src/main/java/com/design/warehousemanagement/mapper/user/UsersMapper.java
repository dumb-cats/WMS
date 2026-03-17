package com.design.warehousemanagement.mapper.user;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.design.warehousemanagement.pojo.Users;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.design.warehousemanagement.pojo.dto.ResetPasswordDTO;
import com.design.warehousemanagement.pojo.dto.UserDTO;
import com.design.warehousemanagement.pojo.dto.UserInfoDTO;
import com.design.warehousemanagement.pojo.vo.UserVO;
import org.apache.ibatis.annotations.*;

import java.util.List;

/**
* @author Administrator
* @description 针对表【users(学员基本信息表)】的数据库操作Mapper
* @createDate 2025-04-09 13:19:11
* @Entity com.wwp.drivingschool.pojo.Users
*/
@Mapper
public interface UsersMapper extends BaseMapper<Users> {

    @Insert("INSERT INTO users (user_id, username, password, name, gender, id_card_number, phone_number, email, address, date_of_birth,role_id) " +
            "VALUES (#{userId}, #{username}, #{password}, #{name}, #{gender}, #{idCardNumber}, #{phoneNumber}, #{email}, #{address}, #{dateOfBirth},#{roleId})")
    void insertUser(UserDTO userDTO);

    @Delete("DELETE FROM users WHERE user_id = #{userId}")
    int deleteUser(String userId);

    @Update("UPDATE users SET username = #{username}, password = #{password}, name = #{name}, gender = #{gender}, " +
            "id_card_number = #{idCardNumber}, phone_number = #{phoneNumber}, email = #{email}, address = #{address}, date_of_birth = #{dateOfBirth} " +
            "WHERE user_id = #{userId}")
    int updateUser(UserDTO userDTO);

    @Select("SELECT * FROM users WHERE user_id = #{userId}")
    UserVO selectUserById(String userId);

    @Select("SELECT * FROM users")
    List<UserVO> selectAllUsers();

    @Select("SELECT COUNT(*) FROM users WHERE id_card_number = #{idCardNumber}")
    int existsByIdCardNumber(String idCardNumber);

    @Select("SELECT COUNT(*) FROM users WHERE phone_number = #{phoneNumber}")
    int existsByPhoneNumber(String phoneNumber);

    IPage<Users> selectUsersByName(IPage<Users> page, @Param("name") String name ,@Param("roleId")Integer roleId);

    Integer existsByUsername(String s);

    Page<UserInfoDTO> selectUsersByInstructorIdWithKeyword(
            @Param("instructorId") String instructorId,
            @Param("keyword") String keyword,
            Page<UserInfoDTO> page);

    @Select("SELECT * FROM users WHERE username = #{dto.username} AND phone_number = #{dto.phoneNumber} AND email = #{dto.email}")
    Users selectByResetCriteria(@Param("dto") ResetPasswordDTO dto);

    @Select( "SELECT * FROM users WHERE user_id = #{userId}")
    Users findById(@Param("userId") String userId);
}




