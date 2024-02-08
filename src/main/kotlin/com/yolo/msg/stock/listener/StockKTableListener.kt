package com.yolo.msg.stock.listener

import com.yolo.msg.config.KafkaJacksonConfig
import com.yolo.msg.stock.model.StockTickerData
import org.apache.kafka.common.serialization.Serdes
import org.apache.kafka.streams.StreamsBuilder
import org.apache.kafka.streams.kstream.Consumed
import org.apache.kafka.streams.kstream.KTable
import org.apache.kafka.streams.kstream.Printed
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration

@Configuration class StockKTableListener {

    @Bean
    fun stockKTable(streamsBuilder: StreamsBuilder): KTable<String, StockTickerData> {
        val stockSerdes = KafkaJacksonConfig.createStockTickerJsonSerdes()
        val stockTickerTable = streamsBuilder.table(
            STOCK_TICKER_TABLE_TOPIC,
            Consumed.with(
                Serdes.String(),
                Serdes.serdeFrom(stockSerdes.first, stockSerdes.second),
            ),
        )
        val stockTickerStream = streamsBuilder.stream(
            STOCK_TICKER_STREAM_TOPIC,
            Consumed.with(
                Serdes.String(),
                Serdes.serdeFrom(stockSerdes.first, stockSerdes.second),
            ),
        )

        stockTickerTable.toStream().print(Printed.toSysOut<String?, StockTickerData?>().withLabel("Stocks-KTable"))
        stockTickerStream.print(Printed.toSysOut<String?, StockTickerData?>().withLabel("Stocks-KStream"))

        return stockTickerTable
    }

    companion object {
        private const val STOCK_TICKER_TABLE_TOPIC = "stock-ticker-table"
        private const val STOCK_TICKER_STREAM_TOPIC = "stock-ticker-stream"
    }
}
