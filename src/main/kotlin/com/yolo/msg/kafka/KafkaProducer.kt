package com.yolo.msg.kafka

import com.yolo.msg.Person
import com.yolo.msg.market.model.Purchase
import com.yolo.msg.stock.model.StockTickerData
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>
) {

    fun sendMessage(person: Person) {
        kafkaTemplate.send("stream-topic", person)
    }

    fun sendPurchaseInfo(purchase: Purchase) {
        kafkaTemplate.send("transactions", purchase)
    }

    fun sendStockInfo(stockTickerData: StockTickerData) {
        kafkaTemplate.send(STOCK_TICKER_TABLE_TOPIC, stockTickerData)
        kafkaTemplate.send(STOCK_TICKER_STREAM_TOPIC, stockTickerData)
    }

    companion object {
        private const val STOCK_TICKER_TABLE_TOPIC = "stock-ticker-table"
        private const val STOCK_TICKER_STREAM_TOPIC = "stock-ticker-stream"
    }
}
