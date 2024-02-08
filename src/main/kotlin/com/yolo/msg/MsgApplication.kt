package com.yolo.msg

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.kafka.annotation.EnableKafkaStreams

@EnableKafkaStreams
@SpringBootApplication
class MsgApplication

fun main(args: Array<String>) {
    runApplication<MsgApplication>(*args)
}
