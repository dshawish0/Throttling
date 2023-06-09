package com.deiaa.throttling.repository;

import org.springframework.stereotype.Repository;

@Repository
public interface HistoryRepository {
    String getHistory() throws InterruptedException;
}
