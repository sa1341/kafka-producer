package com.yolo.msg.market.transformer

import com.yolo.msg.market.model.Purchase
import com.yolo.msg.market.model.RewardAccumulator
import org.apache.kafka.streams.processor.api.FixedKeyProcessor
import org.apache.kafka.streams.processor.api.FixedKeyProcessorSupplier
import org.apache.kafka.streams.state.KeyValueStore
import org.apache.kafka.streams.state.StoreBuilder

class PurchaseProcessorSupplier(
    private val storeName: String,
    private val storeBuilder: StoreBuilder<KeyValueStore<String, Int>>
) : FixedKeyProcessorSupplier<String, Purchase, RewardAccumulator> {

    override fun stores(): MutableSet<StoreBuilder<*>> {
        return mutableSetOf(storeBuilder)
    }

    override fun get(): FixedKeyProcessor<String, Purchase, RewardAccumulator> {
        return PurchaseRewardTransformerV2(storeName)
    }
}

