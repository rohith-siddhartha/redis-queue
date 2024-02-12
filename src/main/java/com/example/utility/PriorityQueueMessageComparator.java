package com.example.utility;

import com.example.PriorityQueueMessage;

import java.util.Comparator;

public class PriorityQueueMessageComparator implements Comparator<PriorityQueueMessage> {

    public int compare(PriorityQueueMessage message1, PriorityQueueMessage message2) {

        return Integer.compare(message2.getPriority(), message1.getPriority());

    }

}
