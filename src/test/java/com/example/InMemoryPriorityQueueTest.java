package com.example;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

public class InMemoryPriorityQueueTest {

    private PriorityQueueService qs;
    private String queueUrl = "https://sqs.ap-1.amazonaws.com/007/MyQueue";

    @Before
    public void setup() {
        qs = new InMemoryPriorityQueueService();
    }


    @Test
    public void testSendMessage(){
        qs.push(queueUrl, "Good message!", 1);
        PriorityQueueMessage msg = (PriorityQueueMessage) qs.pull(queueUrl);

        assertNotNull(msg);
        assertEquals(1, msg.getPriority());
        assertEquals("Good message!", msg.getBody());
    }

    @Test
    public void testSendMessageToNonExistentQueue(){
        String newQueueUrl = "new-queue-url";
        qs.push(newQueueUrl, "Good message!", 1);
        PriorityQueueMessage msg = (PriorityQueueMessage) qs.pull(newQueueUrl);

        assertNotNull(msg);
        assertEquals(1, msg.getPriority());
        assertEquals("Good message!", msg.getBody());
    }

    @Test
    public void testPullMessage(){
        String msgBody = "{ \"name\":\"John\", \"age\":30, \"car\":null }";

        qs.push(queueUrl, msgBody, 1);
        PriorityQueueMessage msg = (PriorityQueueMessage) qs.pull(queueUrl);

        assertEquals(msgBody, msg.getBody());
        assertEquals(1, msg.getPriority());
        assertTrue(msg.getReceiptId() != null && msg.getReceiptId().length() > 0);
    }

    @Test
    public void testPullEmptyQueue(){
        PriorityQueueMessage msg = (PriorityQueueMessage) qs.pull(queueUrl);
        assertNull(msg);
    }

    @Test
    public void testPullFromNonExistentQueue(){
        String newQueueUrl = "new-queue-url-pull";
        PriorityQueueMessage msg = (PriorityQueueMessage) qs.pull(newQueueUrl);
        assertNull(msg);
    }

    @Test
    public void testDoublePull(){
        qs.push(queueUrl, "Message A.", 1);
        qs.pull(queueUrl);
        PriorityQueueMessage msg = (PriorityQueueMessage) qs.pull(queueUrl);
        assertNull(msg);
    }

    @Test
    public void testDeleteMessage(){
        String msgBody = "{ \"name\":\"John\", \"age\":30, \"car\":null }";

        qs.push(queueUrl, msgBody, 1);
        PriorityQueueMessage msg = (PriorityQueueMessage) qs.pull(queueUrl);

        qs.delete(queueUrl, msg.getReceiptId());
        msg = (PriorityQueueMessage) qs.pull(queueUrl);

        assertNull(msg);
    }

    @Test
    public void testPriorityOrderOfMsgs(){
        String [] msgStrs = {"TEst msg 1", "test msg 2", "Test msg 3",
                "{\n" + 								// test with multi-line message.
                        "    \"name\":\"John\",\n" +
                        "    \"age\":30,\n" +
                        "    \"cars\": {\n" +
                        "        \"car1\":\"Ford\",\n" +
                        "        \"car2\":\"BMW\",\n" +
                        "        \"car3\":\"Fiat\"\n" +
                        "    }\n" +
                        " }"};
        qs.push(queueUrl, msgStrs[0], 2);
        qs.push(queueUrl, msgStrs[1], 1);
        qs.push(queueUrl, msgStrs[2], 2);
        qs.push(queueUrl, msgStrs[3], 3);
        PriorityQueueMessage msg1 = (PriorityQueueMessage) qs.pull(queueUrl);
        PriorityQueueMessage msg2 = (PriorityQueueMessage) qs.pull(queueUrl);
        PriorityQueueMessage msg3 = (PriorityQueueMessage) qs.pull(queueUrl);
        PriorityQueueMessage msg4 = (PriorityQueueMessage) qs.pull(queueUrl);

        org.junit.Assert.assertTrue(msgStrs[3].equals(msg1.getBody())
                && msgStrs[0].equals(msg2.getBody()) && msgStrs[2].equals(msg3.getBody())
                && msgStrs[1].equals(msg4.getBody()));
    }

    @Test
    public void testAckTimeout(){
        InMemoryPriorityQueueService queueService = new InMemoryPriorityQueueService() {
            long now() {
                return System.currentTimeMillis() + 1000 * 30 + 1;
            }
        };

        queueService.push(queueUrl, "Message A.", 1);
        queueService.pull(queueUrl);
        PriorityQueueMessage msg = (PriorityQueueMessage) queueService.pull(queueUrl);
        assertTrue(msg != null && msg.getBody().equals("Message A."));
    }

}
