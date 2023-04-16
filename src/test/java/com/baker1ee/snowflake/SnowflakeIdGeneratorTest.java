package com.baker1ee.snowflake;

import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ConcurrentSkipListSet;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import static org.junit.jupiter.api.Assertions.assertEquals;

class SnowflakeIdGeneratorTest {

    long SERVER_ID = 100;
    SnowflakeIdGenerator idGenerator = new SnowflakeIdGenerator(SERVER_ID);

    @Test
    void nextId_should_generate_id_including_time_serverId_sequence_information() {
        // given
        LocalDate today = LocalDateTime.now().toLocalDate();

        // when
        long id = idGenerator.nextId();

        // then
        assertEquals(today, SnowflakeIdGenerator.getCreatedLocalDateTime(id).toLocalDate());
        assertEquals(SERVER_ID, SnowflakeIdGenerator.getServerId(id));
        assertEquals(0, SnowflakeIdGenerator.getSequence(id));
    }

    @Test
    void nextId_should_generate_unique_id() {
        // given
        int iterations = 1_000_000;

        // when
        Set<Long> idSet = new HashSet<>();
        for (int i = 0; i < iterations; i++) {
            long id = idGenerator.nextId();
            idSet.add(id);
        }

        // then
        assertEquals(iterations, idSet.size());
    }

    @Test
    void nextId_should_generate_unique_id_in_multi_thread_environment() {
        // given
        int threadNum = 100;
        int iterations = 10_000;

        // when
        Set<Long> idSet = new ConcurrentSkipListSet<>();
        ExecutorService executor = Executors.newFixedThreadPool(threadNum);
        CompletableFuture[] futures = new CompletableFuture[threadNum];
        for (int i = 0; i < threadNum; i++) {
            futures[i] = CompletableFuture.runAsync(() -> makeId(iterations, idSet), executor);
        }
        CompletableFuture.allOf(futures).join();

        // then
        assertEquals(threadNum * iterations, idSet.size());
    }

    private void makeId(int iterations, Set<Long> idSet) {
        for (int i = 0; i < iterations; i++) {
            idSet.add(idGenerator.nextId());
        }
    }
}