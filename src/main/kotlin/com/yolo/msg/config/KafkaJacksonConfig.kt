package com.yolo.msg.config

import com.fasterxml.jackson.annotation.JsonInclude
import com.fasterxml.jackson.core.JsonGenerator
import com.fasterxml.jackson.databind.DeserializationFeature
import com.fasterxml.jackson.databind.JsonSerializer
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.databind.PropertyNamingStrategies
import com.fasterxml.jackson.databind.SerializerProvider
import com.fasterxml.jackson.datatype.jdk8.Jdk8Module
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateDeserializer
import com.fasterxml.jackson.datatype.jsr310.deser.LocalDateTimeDeserializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateSerializer
import com.fasterxml.jackson.datatype.jsr310.ser.LocalDateTimeSerializer
import com.fasterxml.jackson.module.kotlin.jacksonObjectMapper
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.context.annotation.Primary
import java.math.BigDecimal
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

@Configuration
class KafkaJacksonConfig {

    @Bean
    @Primary
    fun kafkaJacksonMapper(): ObjectMapper {
        return jacksonObjectMapper()
            .registerModule(Jdk8Module())
            .registerModule(customModule())
            .configure(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES, false)
            .configure(DeserializationFeature.READ_DATE_TIMESTAMPS_AS_NANOSECONDS, false)
            .setDefaultPropertyInclusion(JsonInclude.Include.NON_NULL)
            .setPropertyNamingStrategy(PropertyNamingStrategies.SNAKE_CASE)
    }

    private fun customModule(): JavaTimeModule = JavaTimeModule().apply {
        addSerializer(
            LocalDate::class.java,
            LocalDateSerializer(datePattern)
        )
        addDeserializer(
            LocalDate::class.java,
            LocalDateDeserializer(datePattern)
        )
        addSerializer(
            LocalDateTime::class.java,
            LocalDateTimeSerializer(dateTimePattern)
        )
        addDeserializer(
            LocalDateTime::class.java,
            LocalDateTimeDeserializer(dateTimePattern)
        )
        addSerializer(
            BigDecimal::class.java,
            CustomBigDecimalSerializer()
        )
    }

    class CustomBigDecimalSerializer : JsonSerializer<BigDecimal>() {
        override fun serialize(value: BigDecimal, generator: JsonGenerator, serializerProvider: SerializerProvider) {
            generator.writeString(value.stripTrailingZeros().toPlainString())
        }
    }

    companion object {
        val datePattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd")
        val dateTimePattern: DateTimeFormatter = DateTimeFormatter.ofPattern("yyyyMMdd HH:mm:ss")
    }
}
