package com.example;

public interface PriorityQueueService extends QueueService {
    public void push(String queueUrl, String messageBody, int priority);
}
