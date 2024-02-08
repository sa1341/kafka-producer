package com.yolo.msg.market.listener

import com.yolo.msg.config.KafkaJacksonConfig.Companion.createPurchaseJsonSerdes
import com.yolo.msg.config.KafkaJacksonConfig.Companion.createPurchasePatternJsonSerdes
import com.yolo.msg.config.KafkaJacksonConfig.Companion.createRewardAccumulatorJsonSerdes
import com.yolo.msg.market.model.Purchase
import com.yolo.msg.market.model.PurchasePattern
import com.yolo.msg.market.model.RewardAccumulator
import com.yolo.msg.market.partitioner.RewardStreamPartitioner
import com.yolo.msg.market.transformer.PurchaseRewardTransformer
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Printed
import org.apache.kafka.streams.kstream.Produced
import org.apache.kafka.streams.kstream.ValueTransformerSupplier
import org.apache.kafka.streams.state.Stores
import org.springframework.context.annotation.Configuration

@Configuration
class ZMarketStreamStateStoreListener {

    // TODO: 10/14 사용 시 Bean 활성화 필요
    fun rewardingProcessor(streamsBuilder: StreamsBuilder): KStream<String, Purchase> {
        val purchaseJsonSerdes = createPurchaseJsonSerdes()

        val purchaseKStream = streamsBuilder.stream(
            "transactions",
            Consumed.with(Serdes.String(), Serdes.serdeFrom(purchaseJsonSerdes.first, purchaseJsonSerdes.second)),
        )

        val purchasePatternSerdes = createPurchasePatternJsonSerdes()
        val patternKStream = purchaseKStream
            .mapValues { p: Purchase -> PurchasePattern.from(p) }

        patternKStream.print(Printed.toSysOut<String?, PurchasePattern?>().withLabel("patterns"))
        patternKStream.to(
            "patterns",
            Produced.with(Serdes.String(), Serdes.serdeFrom(purchasePatternSerdes.first, purchasePatternSerdes.second)),
        )

        val rewardsStateStoreName = "rewardsPointsStore"
        val streamPartitioner = RewardStreamPartitioner()

        val storeSupplier = Stores.inMemoryKeyValueStore(rewardsStateStoreName)
        val storeBuilder = Stores.keyValueStoreBuilder(storeSupplier, Serdes.String(), Serdes.Integer())
        streamsBuilder.addStateStore(storeBuilder)

        val transByCustomerStream = purchaseKStream.through(
            "customer_transactions",
            Produced.with(
                Serdes.String(),
                Serdes.serdeFrom(purchaseJsonSerdes.first, purchaseJsonSerdes.second),
                streamPartitioner,
            ),
        )

        val statefulRewardAccumulator = transByCustomerStream.transformValues(
            ValueTransformerSupplier { PurchaseRewardTransformer(rewardsStateStoreName) },
            rewardsStateStoreName,
        )

        val rewardAccumulatorSerdes = createRewardAccumulatorJsonSerdes()
        statefulRewardAccumulator.print(
            Printed.toSysOut<String?, RewardAccumulator?>().withLabel("rewards"),
        )
        statefulRewardAccumulator.to(
            "rewards",
            Produced.with(Serdes.String(), Serdes.serdeFrom(rewardAccumulatorSerdes.first, rewardAccumulatorSerdes.second)),
        )

        return purchaseKStream
    }
}
