package com.yolo.msg

import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.runApplication
import org.springframework.cloud.client.discovery.EnableDiscoveryClient

@EnableDiscoveryClient
@SpringBootApplication
class MsgApplication

fun main(args: Array<String>) {
	runApplication<MsgApplication>(*args)
}
