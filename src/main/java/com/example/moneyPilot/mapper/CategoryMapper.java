package com.example.moneyPilot.mapper;

import com.example.moneyPilot.domain.Category;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;
import java.util.UUID;

@Mapper
public interface CategoryMapper {
    @Select("""
            SELECT id, user_id AS userId, name, type, color,
                     CASE WHEN user_id IS NULL THEN TRUE ELSE FALSE END AS system,
                     created_at AS createdAt
            FROM categories
            WHERE (user_id IS NULL OR user_id = #{userId})
              AND (#{type} IS NULL OR type = #{type})
            ORDER BY name
            """)
    List<Category> listByUserAndType(@Param("userId") UUID userId, @Param("type") String type);

    @Insert("""
            INSERT INTO categories (id, user_id, name, type, color, created_at)
            VALUES (#{id}, #{userId}, #{name}, #{type}, #{color}, #{createdAt})
            """)
    int insert(Category category);

    @Select("""
            SELECT id, user_id AS userId, name, type, color,
                     CASE WHEN user_id IS NULL THEN TRUE ELSE FALSE END AS system,
                     created_at AS createdAt
            FROM categories
            WHERE id = #{id}
            """)
    Category findById(@Param("id") UUID id);

    @Select("SELECT COUNT(*) FROM categories WHERE user_id IS NULL")
    int countSystemCategories();
}
