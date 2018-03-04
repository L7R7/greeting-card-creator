package com.l7r7.lab.greetingcard.creator.web

import com.l7r7.lab.greetingcard.creator.card.domain.Card
import com.l7r7.lab.greetingcard.creator.card.service.CardService
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class RouterDefinitions(private val cardService: CardService) {
    @Bean
    fun routes() = router {
        GET("/feed", { ServerResponse.ok().body(cardService.findAllPublished(), Card::class.java) })
    }
}
