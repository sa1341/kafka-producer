package com.yolo.msg.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.yolo.msg.BoardDto
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val mapper: ObjectMapper
){

    fun sendMessage(boardDto: BoardDto) {
        val event = mapper.writeValueAsString(boardDto)
        kafkaTemplate.send("sample-topic", event)
    }
}
