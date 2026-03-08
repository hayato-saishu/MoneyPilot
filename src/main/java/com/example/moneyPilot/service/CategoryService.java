package com.example.moneyPilot.service;

import com.example.moneyPilot.domain.Category;
import com.example.moneyPilot.domain.TransactionType;
import com.example.moneyPilot.domain.User;
import com.example.moneyPilot.mapper.CategoryMapper;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.OffsetDateTime;
import java.util.Comparator;
import java.util.List;
import java.util.UUID;

@Service
public class CategoryService {
    private final CategoryMapper categoryMapper;

    public CategoryService(CategoryMapper categoryMapper) {
        this.categoryMapper = categoryMapper;
    }

    public List<Category> list(User user, String type) {
        String normalizedType = type == null ? null : TransactionType.fromApiValue(type).name();
        return categoryMapper.listByUserAndType(user.id(), normalizedType).stream()
                .sorted(Comparator.comparing(Category::name))
                .toList();
    }

    @Transactional
    public Category create(User user, String name, String type, String color) {
        TransactionType txType = TransactionType.fromApiValue(type);
        UUID id = UUID.randomUUID();
        Category category = new Category(
                id,
                user.id(),
                name,
                txType,
                color == null ? "#6C757D" : color,
                false,
                OffsetDateTime.now());
        categoryMapper.insert(category);
        return category;
    }

    public Category getById(UUID id) {
        return categoryMapper.findById(id);
    }
}
