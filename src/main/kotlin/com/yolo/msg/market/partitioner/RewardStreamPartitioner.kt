package com.yolo.msg.market.partitioner

import com.yolo.msg.market.model.Purchase
import org.apache.kafka.streams.processor.StreamPartitioner

class RewardStreamPartitioner : StreamPartitioner<String, Purchase> {

    override fun partition(topic: String, key: String?, value: Purchase, numPartitions: Int): Int {
        return value.customerId.hashCode() % numPartitions
    }
}

