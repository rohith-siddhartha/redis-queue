package com.example;

import com.example.utility.PriorityQueueMessageComparator;

import java.io.IOException;
import java.io.InputStream;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

public class InMemoryPriorityQueueService implements PriorityQueueService{

    private final Map<String, Queue<PriorityQueueMessage>> queues;

    private long visibilityTimeout;

    InMemoryPriorityQueueService() {
        this.queues = new ConcurrentHashMap<>();
        String propFileName = "config.properties";
        Properties confInfo = new Properties();

        try (InputStream inStream = getClass().getClassLoader().getResourceAsStream(propFileName)) {
            confInfo.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.visibilityTimeout = Integer.parseInt(confInfo.getProperty("visibilityTimeout", "30"));
    }

    @Override
    public void push(String queueUrl, String messageBody, int priority) {
        Queue<PriorityQueueMessage> queue = queues.get(queueUrl);
        if (queue == null) {
            queue = new PriorityBlockingQueue<>(1, new PriorityQueueMessageComparator());
            queues.put(queueUrl, queue);
        }
        queue.add(new PriorityQueueMessage(messageBody, priority));
    }

    @Override
    public void push(String queueUrl, String messageBody) {
        // can be implemented if there is a default priority
        return;
    }

    @Override
    public Message pull(String queueUrl) {
        Queue<PriorityQueueMessage> queue = queues.get(queueUrl);
        if (queue == null) {
            return null;
        }

        long nowTime = now();

        PriorityQueueMessage msg = null;

        ArrayList<PriorityQueueMessage> messagesHolder = new ArrayList<>();
        while(!queue.isEmpty()){
            if(queue.peek().isVisibleAt(nowTime)) {
                msg = queue.peek();
                break;
            }else{
                messagesHolder.add(queue.poll());
            }
        }
        queue.addAll(messagesHolder);


        if (msg == null) {
            return null;
        } else {
            msg.setReceiptId(UUID.randomUUID().toString());
            msg.incrementAttempts();
            msg.setVisibleFrom(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(visibilityTimeout));

            return new PriorityQueueMessage(msg.getBody(), msg.getReceiptId(), msg.getPriority());
        }
    }

    @Override
    public void delete(String queueUrl, String receiptId) {
        Queue<PriorityQueueMessage> queue = queues.get(queueUrl);
        if (queue != null) {
            long nowTime = now();

            for (Iterator<PriorityQueueMessage> it = queue.iterator(); it.hasNext(); ) {
                Message msg = it.next();
                if (!msg.isVisibleAt(nowTime) && msg.getReceiptId().equals(receiptId)) {
                    it.remove();
                    break;
                }
            }
        }
    }

    long now() {
        return System.currentTimeMillis();
    }
}
