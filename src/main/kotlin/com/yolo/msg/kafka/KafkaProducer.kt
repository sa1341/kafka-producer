package com.yolo.msg.kafka

import com.fasterxml.jackson.databind.ObjectMapper
import com.yolo.msg.Person
import org.springframework.kafka.core.KafkaTemplate
import org.springframework.stereotype.Service

@Service
class KafkaProducer(
    private val kafkaTemplate: KafkaTemplate<String, Any>,
    private val mapper: ObjectMapper
) {

    fun sendMessage(person: Person) {
        val event = mapper.writeValueAsString(person)
        kafkaTemplate.send("stream-topic", person)
    }
}
