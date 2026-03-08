package com.example.moneyPilot.mapper;

import com.example.moneyPilot.domain.User;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.UUID;

@Mapper
public interface UserMapper {
    @Insert("""
            INSERT INTO users (id, email, password_hash, name, is_active, created_at, updated_at)
            VALUES (#{id}, #{email}, #{password}, #{name}, #{active}, #{createdAt}, #{updatedAt})
            """)
    int insert(User user);

    @Select("SELECT id, name, email, password_hash AS password, is_active AS active, created_at AS createdAt, updated_at AS updatedAt FROM users WHERE email = #{email}")
    User findByEmail(@Param("email") String email);

    @Select("SELECT id, name, email, password_hash AS password, is_active AS active, created_at AS createdAt, updated_at AS updatedAt FROM users WHERE id = #{id}")
    User findById(@Param("id") UUID id);

    @Select("SELECT COUNT(*) FROM users")
    int countAll();
}
