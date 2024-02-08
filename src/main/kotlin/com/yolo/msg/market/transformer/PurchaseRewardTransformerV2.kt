package com.yolo.msg.market.transformer

import com.yolo.msg.market.model.Purchase
import com.yolo.msg.market.model.RewardAccumulator
import org.apache.kafka.streams.processor.api.FixedKeyProcessor
import org.apache.kafka.streams.processor.api.FixedKeyProcessorContext
import org.apache.kafka.streams.processor.api.FixedKeyRecord
import org.apache.kafka.streams.state.KeyValueStore

class PurchaseRewardTransformerV2(
    val storeName: String
) : FixedKeyProcessor<String, Purchase, RewardAccumulator> {

    private var stateStore: KeyValueStore<String, Int>? = null
    private var context: FixedKeyProcessorContext<String, RewardAccumulator>? = null

    override fun init(context: FixedKeyProcessorContext<String, RewardAccumulator>?) {
        this.context = context
        this.stateStore = this.context?.getStateStore(storeName) as KeyValueStore<String, Int>?
    }

    override fun process(record: FixedKeyRecord<String, Purchase>?) {
        val rewardAccumulator = RewardAccumulator.from(record!!.value())
        val accumulatedSoFar = stateStore?.get(rewardAccumulator.customerId)

        accumulatedSoFar?.let {
            rewardAccumulator.addRewardPoints(accumulatedSoFar)
        }
        stateStore?.put(rewardAccumulator.customerId, rewardAccumulator.totalRewardPoints)
    }

    override fun close() {
        super.close()
    }
}
