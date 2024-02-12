package com.example;

import com.example.utility.JSONMapper;
import redis.clients.jedis.Jedis;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

public class RedisQueueService implements QueueService{

    private final Jedis jedisClient;

    private final long visibilityTimeout;

    public RedisQueueService() {

        String propFileName = "config.properties";
        Properties confInfo = new Properties();

        try (InputStream inStream = getClass().getClassLoader().getResourceAsStream(propFileName)) {
            confInfo.load(inStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

        this.visibilityTimeout = Integer.parseInt(confInfo.getProperty("visibilityTimeout", "30"));
        this.jedisClient = new Jedis(confInfo.getProperty("redisHost"), Integer.parseInt(confInfo.getProperty("redisPort")));
        jedisClient.auth(confInfo.getProperty("redisAuth"));

    }

    public boolean validateQueueURL(String queueUrl) {
        return jedisClient.exists(queueUrl) && jedisClient.type(queueUrl).equals("list");
    }

    @Override
    public void push(String queueUrl, String messageBody) {
        String message = JSONMapper.objectToString(new Message(messageBody));
        jedisClient.rpush(queueUrl, message);
    }

    @Override
    public Message pull(String queueUrl) {

        if(!validateQueueURL(queueUrl)){
            return null;
        }

        long nowTime = now();
        long queueSize = jedisClient.llen(queueUrl);

        for(int i=0;i<queueSize;i++){
            Message msg = JSONMapper.stringToObject(jedisClient.lindex(queueUrl, i),Message.class);
            if(msg.isVisibleAt(nowTime)){
                msg.setReceiptId(UUID.randomUUID().toString());
                msg.incrementAttempts();
                msg.setVisibleFrom(System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(visibilityTimeout));
                jedisClient.lset(queueUrl, i, JSONMapper.objectToString(msg));
                return JSONMapper.stringToObject(jedisClient.lindex(queueUrl, i),Message.class);
            }
        }

        return null;

    }

    @Override
    public void delete(String queueUrl, String receiptId) {

        long nowTime = now();
        long queueSize = jedisClient.llen(queueUrl);

        for(int i=0;i<queueSize;i++){
            Message msg = JSONMapper.stringToObject(jedisClient.lindex(queueUrl, i),Message.class);
            if(!msg.isVisibleAt(nowTime) && msg.getReceiptId().equals(receiptId)){
                jedisClient.lrem(queueUrl, i, JSONMapper.objectToString(msg));
                return;
            }
        }

    }

    public void emptyQueue(String queueUrl) {
        jedisClient.del(queueUrl);
    }


    long now() {
        return System.currentTimeMillis();
    }

}
