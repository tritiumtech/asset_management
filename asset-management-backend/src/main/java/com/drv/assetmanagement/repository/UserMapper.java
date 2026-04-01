package com.drv.assetmanagement.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.drv.assetmanagement.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

@Mapper
public interface UserMapper extends BaseMapper<User> {

    @Select("SELECT * FROM user WHERE username = #{username} AND deleted = 0")
    User findByUsername(@Param("username") String username);

    @Select("SELECT * FROM user WHERE employee_no = #{employeeNo} AND deleted = 0")
    User findByEmployeeNo(@Param("employeeNo") String employeeNo);
}