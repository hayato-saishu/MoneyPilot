package com.example.moneyPilot.mapper;

import com.example.moneyPilot.domain.Transaction;
import org.apache.ibatis.annotations.Delete;
import org.apache.ibatis.annotations.Insert;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@Mapper
public interface TransactionMapper {
    @Insert("""
            INSERT INTO transactions (id, user_id, category_id, type, amount, memo, date, recurring_income_id, created_at, updated_at)
            VALUES (#{id}, #{userId}, #{categoryId}, #{type}, #{amount}, #{memo}, #{date}, #{recurringIncomeId}, #{createdAt}, #{updatedAt})
            """)
    int insert(Transaction transaction);

    @Select("""
            SELECT id, user_id AS userId, category_id AS categoryId, type, amount, memo, date,
                   recurring_income_id AS recurringIncomeId, created_at AS createdAt, updated_at AS updatedAt
            FROM transactions
            WHERE user_id = #{userId}
              AND EXTRACT(YEAR FROM date) = #{year}
              AND EXTRACT(MONTH FROM date) = #{month}
              AND (#{categoryId} IS NULL OR category_id = #{categoryId})
              AND (#{type} IS NULL OR type = #{type})
            ORDER BY date DESC, created_at DESC
            """)
    List<Transaction> listByCondition(@Param("userId") UUID userId,
            @Param("year") int year,
            @Param("month") int month,
            @Param("categoryId") UUID categoryId,
            @Param("type") String type);

    @Select("""
            SELECT id, user_id AS userId, category_id AS categoryId, type, amount, memo, date,
                   recurring_income_id AS recurringIncomeId, created_at AS createdAt, updated_at AS updatedAt
            FROM transactions WHERE id = #{id}
            """)
    Transaction findById(@Param("id") UUID id);

    @Update("""
            UPDATE transactions
            SET category_id = #{categoryId}, amount = #{amount}, memo = #{memo}, date = #{date}, updated_at = #{updatedAt}
            WHERE id = #{id}
            """)
    int update(Transaction transaction);

    @Delete("DELETE FROM transactions WHERE id = #{id}")
    int deleteById(@Param("id") UUID id);

    @Select("""
            SELECT COUNT(*) FROM transactions
            WHERE recurring_income_id = #{recurringIncomeId}
              AND date >= #{startDate}
              AND date < #{endDate}
            """)
    int countByRecurringIncomeInRange(@Param("recurringIncomeId") UUID recurringIncomeId,
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);
}
