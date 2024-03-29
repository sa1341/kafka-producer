package com.yolo.msg.api

import com.yolo.msg.Person
import com.yolo.msg.kafka.KafkaProducer
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RequestMapping(value = ["/v1/kafka"])
@RestController
class MessageApi(
    private val kafkaProducer: KafkaProducer
) {

    @PostMapping("/message")
    fun sendMessage(@RequestBody person: Person) {
        println("person: $person")
        kafkaProducer.sendMessage(person)
    }

    @GetMapping("/default-message")
    fun sendDefaultMessage(): String {
        return "테스트 메시지 입니다."
    }
}
