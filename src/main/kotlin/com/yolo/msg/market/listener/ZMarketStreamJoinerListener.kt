/*
package com.kakaopaysec.happyending.consumer.market.listener

import com.kakaopaysec.happyending.consumer.config.KafkaJacksonConfig
import com.yolo.msg.market.joiner.PurchaseJoiner
import com.yolo.msg.market.model.CorrelatedPurchase
import com.yolo.msg.market.model.Purchase
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.KeyValue
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.JoinWindows
import org.apache.kafka.streams.kstream.KStream
import org.apache.kafka.streams.kstream.Printed
import org.apache.kafka.streams.kstream.StreamJoined
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.kafka.support.serializer.JsonDeserializer
import org.springframework.kafka.support.serializer.JsonSerializer
import java.time.Duration

@Configuration class ZMarketStreamJoinerListener {

    @Bean
    fun joinerPurchaseKStream(streamsBuilder: StreamsBuilder): KStream<String, Purchase> {
        val purchaseJsonSerdes = KafkaJacksonConfig.createPurchaseJsonSerdes()

        val customerIdCreditCardMasking: (String?, Purchase) -> KeyValue<String, Purchase> = { key: String?, value: Purchase ->
            val masked = Purchase.from(value)
            KeyValue(masked.customerId, masked)
        }

        val purchaseKStream = streamsBuilder.stream(
            "transactions",
            Consumed.with(Serdes.String(), Serdes.serdeFrom(purchaseJsonSerdes.first, purchaseJsonSerdes.second))
        ).map(customerIdCreditCardMasking)

        divideCategoryStream(purchaseKStream, purchaseJsonSerdes)
        return purchaseKStream
    }

    private fun divideCategoryStream(
        purchaseKStream: KStream<String, Purchase>,
        purchaseJsonSerdes: Pair<JsonSerializer<Purchase>, JsonDeserializer<Purchase>>
    ) {
        val COFFEE_PURCHASE = 0
        val ELECTRONICS_PURCHASE = 1

        val isCoffee = { key: String?, p: Purchase -> p.department == "coffee" }
        val isElectronics = { key: String?, p: Purchase -> p.department == "electronics" }

        val branchesStream = purchaseKStream
            .selectKey { key, value -> value.customerId }
            .branch(isCoffee, isElectronics)

        val coffeeStream = branchesStream[COFFEE_PURCHASE]
        val electronicsStream = branchesStream[ELECTRONICS_PURCHASE]

        val purchaseJoiner = PurchaseJoiner()
        val twentyMinuteWindow = JoinWindows.ofTimeDifferenceWithNoGrace(Duration.ofMillis(60 * 1000 * 20))

        val joinedKStream = coffeeStream.join(
            electronicsStream,
            purchaseJoiner,
            twentyMinuteWindow,
            StreamJoined.with(
                Serdes.String(),
                Serdes.serdeFrom(purchaseJsonSerdes.first, purchaseJsonSerdes.second),
                Serdes.serdeFrom(purchaseJsonSerdes.first, purchaseJsonSerdes.second)
            )
        )

        joinedKStream.print(Printed.toSysOut<String?, CorrelatedPurchase?>().withLabel("joined KStream"))
    }
}
*/
