package com.yolo.msg.api

import com.yolo.msg.kafka.KafkaProducer
import com.yolo.msg.market.model.Purchase
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping(value = ["/v1/kafka"])
@RestController
class ZMarketPurchaseApi(
    private val kafkaProducer: KafkaProducer
) {
    @PostMapping("/purchase")
    fun sendMessage(@RequestBody purchase: Purchase) {
        println("purchase: $purchase")
        kafkaProducer.sendPurchaseInfo(purchase)
    }
}
