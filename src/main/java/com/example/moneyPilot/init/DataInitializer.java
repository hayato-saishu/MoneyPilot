package com.example.moneyPilot.init;

import com.example.moneyPilot.domain.Category;
import com.example.moneyPilot.domain.TransactionType;
import com.example.moneyPilot.domain.User;
import com.example.moneyPilot.mapper.CategoryMapper;
import com.example.moneyPilot.mapper.UserMapper;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.time.OffsetDateTime;
import java.util.UUID;

@Component
public class DataInitializer implements ApplicationRunner {
    private final UserMapper userMapper;
    private final CategoryMapper categoryMapper;
    private final PasswordEncoder passwordEncoder;

    public DataInitializer(UserMapper userMapper, CategoryMapper categoryMapper, PasswordEncoder passwordEncoder) {
        this.userMapper = userMapper;
        this.categoryMapper = categoryMapper;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public void run(ApplicationArguments args) {
        seedDemoUser();
        seedSystemCategories();
    }

    private void seedDemoUser() {
        if (userMapper.findByEmail("demo@example.com") != null) {
            return;
        }
        OffsetDateTime now = OffsetDateTime.now();
        userMapper.insert(new User(
                UUID.randomUUID(),
                "Demo User",
                "demo@example.com",
                passwordEncoder.encode("password123"),
                true,
                now,
                now));
    }

    private void seedSystemCategories() {
        if (categoryMapper.countSystemCategories() > 0) {
            return;
        }
        OffsetDateTime now = OffsetDateTime.now();
        insertSystemCategory("給与", TransactionType.INCOME, "#2A9D8F", now);
        insertSystemCategory("副業", TransactionType.INCOME, "#457B9D", now);
        insertSystemCategory("食費", TransactionType.EXPENSE, "#E76F51", now);
        insertSystemCategory("固定費", TransactionType.EXPENSE, "#F4A261", now);
    }

    private void insertSystemCategory(String name, TransactionType type, String color, OffsetDateTime now) {
        categoryMapper.insert(new Category(
                UUID.randomUUID(),
                null,
                name,
                type,
                color,
                true,
                now));
    }
}
