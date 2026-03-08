package com.example.moneyPilot.batch;

import com.example.moneyPilot.service.RecurringIncomeService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.LocalDate;

@Component
public class RecurringIncomeBatchJob {
    private static final Logger logger = LoggerFactory.getLogger(RecurringIncomeBatchJob.class);
    private final RecurringIncomeService recurringIncomeService;

    public RecurringIncomeBatchJob(RecurringIncomeService recurringIncomeService) {
        this.recurringIncomeService = recurringIncomeService;
    }

    @Scheduled(cron = "0 5 0 * * *")
    public void run() {
        RecurringIncomeService.BatchResult result = recurringIncomeService.applyRecurringIncomesForDate(LocalDate.now());
        logger.info("[RecurringIncomeBatch] completed created={} skipped={}", result.createdCount(), result.skippedCount());
    }
}
