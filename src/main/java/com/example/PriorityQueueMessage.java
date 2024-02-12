package com.example;

public class PriorityQueueMessage extends Message{

    private int priority;

    PriorityQueueMessage(String msgBody, int priority) {
        super(msgBody);
        this.priority = priority;
    }

    PriorityQueueMessage(String msgBody, String receiptId, int priority) {
        super(msgBody, receiptId);
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }
}
