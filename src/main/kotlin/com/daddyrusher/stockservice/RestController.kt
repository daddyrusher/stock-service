package com.daddyrusher.stockservice

import org.springframework.http.MediaType
import org.springframework.messaging.handler.annotation.MessageMapping
import org.springframework.stereotype.Controller
import org.springframework.stereotype.Service
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RestController
import reactor.core.publisher.Flux
import java.time.Duration
import java.time.LocalDateTime.now
import java.util.concurrent.ThreadLocalRandom

@RestController
class RestController(private val service: PriceService) {
    @GetMapping("/stocks/{symbol}", produces = [MediaType.TEXT_EVENT_STREAM_VALUE])
    fun prices(@PathVariable symbol: String): Flux<StockPrice> = service.generatePrices(symbol)
}

@Controller
class RSocketController(private val service: PriceService) {
    @MessageMapping("stockPrices")
    fun prices(symbol: String): Flux<StockPrice> = service.generatePrices(symbol)
}

@Service
class PriceService {
    fun generatePrices(symbol: String): Flux<StockPrice> = Flux
            .interval(Duration.ofSeconds(1))
            .map { StockPrice(symbol, randomStockPrice(), now()) }

    private fun randomStockPrice(): Double = ThreadLocalRandom.current().nextDouble(100.0)
}
