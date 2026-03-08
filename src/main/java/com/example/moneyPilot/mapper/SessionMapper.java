package com.example.moneyPilot.mapper;

import com.example.moneyPilot.domain.SessionToken;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.UUID;

@Mapper
public interface SessionMapper {
    @Insert("""
            INSERT INTO sessions (id, user_id, refresh_token, expires_at, created_at)
            VALUES (#{id}, #{userId}, #{refreshToken}, #{expiresAt}, #{createdAt})
            """)
    int insert(SessionToken token);

    @Select("SELECT id, user_id AS userId, refresh_token AS refreshToken, expires_at AS expiresAt, created_at AS createdAt FROM sessions WHERE refresh_token = #{refreshToken}")
    SessionToken findByRefreshToken(@Param("refreshToken") String refreshToken);

    @Delete("DELETE FROM sessions WHERE refresh_token = #{refreshToken}")
    int deleteByRefreshToken(@Param("refreshToken") String refreshToken);

    @Delete("DELETE FROM sessions WHERE user_id = #{userId}")
    int deleteByUserId(@Param("userId") UUID userId);
}
