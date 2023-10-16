package com.yolo.msg.stock.api

import com.yolo.msg.kafka.KafkaProducer
import com.yolo.msg.stock.model.StockTickerData
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping(value = ["/v1/kafka"])
@RestController class StockApi(
    private val kafkaProducer: KafkaProducer
) {
    @PostMapping("/stock")
    fun sendMessage(@RequestBody stockTickerData: StockTickerData) {
        println("stockTickerData: $stockTickerData")
        kafkaProducer.sendStockInfo(stockTickerData)
    }
}
