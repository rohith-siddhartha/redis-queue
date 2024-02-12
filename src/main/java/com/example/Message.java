package com.example;

public class Message {
  /** How many times this message has been delivered. */
  private int attempts;

  /** Visible from time */
  private long visibleFrom;

  /** An identifier associated with the act of receiving the message. */
  private String receiptId;

  private String body;

  Message () {}

  Message(String msgBody) {
    this.body = msgBody;
  }

  public void setBody(String body) {
    this.body = body;
  }

  Message(String msgBody, String receiptId) {
    this.body = msgBody;
    this.receiptId = receiptId;
  }

  public String getReceiptId() {
    return this.receiptId;
  }

  public void setAttempts(int attempts) {
    this.attempts = attempts;
  }

  public long getVisibleFrom() {
    return visibleFrom;
  }

  public void setReceiptId(String receiptId) {
    this.receiptId = receiptId;
  }

  public void setVisibleFrom(long visibleFrom) {
    this.visibleFrom = visibleFrom;
  }

  /*
  public boolean isVisible() {
  	return visibleFrom < System.currentTimeMillis();
  }*/

  public boolean isVisibleAt(long instant) {
    return visibleFrom < instant;
  }

  public String getBody() {
    return body;
  }

  public int getAttempts() {
    return attempts;
  }

  public void incrementAttempts() {
    this.attempts++;
  }
}
