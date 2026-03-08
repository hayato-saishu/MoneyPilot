package com.example.moneyPilot.mapper;

import com.example.moneyPilot.domain.RecurringIncome;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;
import java.util.UUID;

@Mapper
public interface RecurringIncomeMapper {
    @Insert("""
            INSERT INTO recurring_incomes (id, user_id, category_id, amount, memo, day_of_month, is_active, created_at, updated_at)
            VALUES (#{id}, #{userId}, #{categoryId}, #{amount}, #{memo}, #{dayOfMonth}, #{active}, #{createdAt}, #{updatedAt})
            """)
    int insert(RecurringIncome recurringIncome);

    @Select("""
            SELECT id, user_id AS userId, category_id AS categoryId, amount, memo, day_of_month AS dayOfMonth,
                   is_active AS active, created_at AS createdAt, updated_at AS updatedAt
            FROM recurring_incomes
            WHERE user_id = #{userId}
            ORDER BY created_at ASC
            """)
    List<RecurringIncome> listByUserId(@Param("userId") UUID userId);

    @Select("""
            SELECT id, user_id AS userId, category_id AS categoryId, amount, memo, day_of_month AS dayOfMonth,
                   is_active AS active, created_at AS createdAt, updated_at AS updatedAt
            FROM recurring_incomes
            WHERE id = #{id}
            """)
    RecurringIncome findById(@Param("id") UUID id);

    @Update("""
            UPDATE recurring_incomes
            SET category_id = #{categoryId}, amount = #{amount}, memo = #{memo},
                day_of_month = #{dayOfMonth}, is_active = #{active}, updated_at = #{updatedAt}
            WHERE id = #{id}
            """)
    int update(RecurringIncome recurringIncome);

    @Delete("DELETE FROM recurring_incomes WHERE id = #{id}")
    int deleteById(@Param("id") UUID id);

    @Select("""
            SELECT id, user_id AS userId, category_id AS categoryId, amount, memo, day_of_month AS dayOfMonth,
                   is_active AS active, created_at AS createdAt, updated_at AS updatedAt
            FROM recurring_incomes
            WHERE is_active = TRUE AND day_of_month = #{dayOfMonth}
            """)
    List<RecurringIncome> listActiveByDayOfMonth(@Param("dayOfMonth") int dayOfMonth);
}
