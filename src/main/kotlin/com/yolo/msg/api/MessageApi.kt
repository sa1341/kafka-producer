package com.yolo.msg.api

import com.yolo.msg.BoardDto
import com.yolo.msg.kafka.KafkaProducer
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping(value = ["/api/kafka"])
@RestController
class MessageApi(
    private val kafkaProducer: KafkaProducer,
) {

    @PostMapping("/send-event")
    fun sendMessage(@RequestBody boardDto: BoardDto) {
        println("boardDto: $boardDto")
        kafkaProducer.sendMessage(boardDto)
    }
}
