package com.yolo.msg.market.listener

import com.yolo.msg.config.KafkaJacksonConfig.Companion.createPurchaseJsonSerdes
import com.yolo.msg.config.KafkaJacksonConfig.Companion.createPurchasePatternJsonSerdes
import com.yolo.msg.config.KafkaJacksonConfig.Companion.createRewardAccumulatorJsonSerdes
import com.yolo.msg.market.model.Purchase
import com.yolo.msg.market.model.PurchasePattern
import com.yolo.msg.market.model.RewardAccumulator
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Branched
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Produced
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer

@Configuration
class ZMarketStreamListener {

    // TODO: 10/7/23 사용 시 Bean 활성화 필요
    fun purchaseKStream(streamsBuilder: StreamsBuilder): KStream<String, Purchase> {
        val purchaseJsonSerdes = createPurchaseJsonSerdes()
        val purchaseKStream = streamsBuilder.stream(
            "transactions",
            Consumed.with(Serdes.String(), Serdes.serdeFrom(purchaseJsonSerdes.first, purchaseJsonSerdes.second)),
        ).mapValues { p: Purchase ->
            Purchase.from(p)
        }.filter { key, purchase ->
            purchase.price > 5.00
        }

        val purchasePatternSerdes = createPurchasePatternJsonSerdes()
        val patternKStream = purchaseKStream
            .mapValues { p: Purchase -> PurchasePattern.from(p) }
            .to(
                "patterns",
                Produced.with(Serdes.String(), Serdes.serdeFrom(purchasePatternSerdes.first, purchasePatternSerdes.second)),
            )

        val rewardAccumulatorSerdes = createRewardAccumulatorJsonSerdes()
        val rewardsKStream = purchaseKStream
            .mapValues { p: Purchase -> RewardAccumulator.from(p) }
            .to(
                "rewards",
                Produced.with(Serdes.String(), Serdes.serdeFrom(rewardAccumulatorSerdes.first, rewardAccumulatorSerdes.second)),
            )

        purchaseKStream.to(
            "purchases",
            Produced.with(Serdes.String(), Serdes.serdeFrom(purchaseJsonSerdes.first, purchaseJsonSerdes.second)),
        )

        // KStream에서 제공하느 branch를 사용하여 스트림별로 관심사 토픽으로 분기처리 적용
        divideCategoryStream(purchaseKStream, purchaseJsonSerdes)

        return purchaseKStream
    }

    private fun divideCategoryStream(
        purchaseKStream: KStream<String, Purchase>,
        jsonSerdes: Pair<JsonSerializer<Purchase>, JsonDeserializer<Purchase>>,
    ) {
        val isCoffee = { key: String?, p: Purchase -> p.department == "coffee" }
        val isElectronics = { key: String?, p: Purchase -> p.department == "electronics" }

        val kStreamByDept = purchaseKStream
            .split()
            .branch(
                isCoffee,
                Branched.withConsumer { ks ->
                    ks.to(
                        "coffee",
                        Produced.with(Serdes.String(), Serdes.serdeFrom(jsonSerdes.first, jsonSerdes.second)),
                    )
                },
            )
            .branch(
                isElectronics,
                Branched.withConsumer { ks ->
                    ks.to(
                        "electronics",
                        Produced.with(Serdes.String(), Serdes.serdeFrom(jsonSerdes.first, jsonSerdes.second)),
                    )
                },
            )
            .noDefaultBranch()
    }
}
