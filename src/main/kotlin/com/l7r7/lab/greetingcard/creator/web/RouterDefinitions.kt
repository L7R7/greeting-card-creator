package com.l7r7.lab.greetingcard.creator.web

import com.l7r7.lab.greetingcard.creator.card.service.CardService
import com.l7r7.lab.greetingcard.creator.card.service.ExternalCard
import org.slf4j.LoggerFactory
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class RouterDefinitions(private val cardService: CardService) {
    private val log = LoggerFactory.getLogger(RouterDefinitions::class.java)

    @Bean
    fun routes() = router {
        GET("/feed", { request ->
            log.info("feed is called with host ${request.headers()}")
            ServerResponse.ok().body(cardService.findAllPublishedExternal(), ExternalCard::class.java)
        })
    }
}
