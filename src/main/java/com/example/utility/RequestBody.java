package com.example.utility;

public class RequestBody {
    private String queueUrl;
    private String messageBody;
    private String receiptId;

    public String getQueueUrl() {
        return queueUrl;
    }

    public String getReceiptId() {
        return receiptId;
    }

    public void setReceiptId(String receiptId) {
        this.receiptId = receiptId;
    }

    public void setQueueUrl(String queueUrl) {
        this.queueUrl = queueUrl;
    }

    public String getMessageBody() {
        return messageBody;
    }

    public void setMessageBody(String messageBody) {
        this.messageBody = messageBody;
    }
}
