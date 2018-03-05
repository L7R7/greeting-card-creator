package com.l7r7.lab.greetingcard.creator.web

import com.l7r7.lab.greetingcard.creator.card.domain.Card
import com.l7r7.lab.greetingcard.creator.card.service.CardService
import com.l7r7.lab.greetingcard.creator.card.service.ExternalCard
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router

@Configuration
class RouterDefinitions(private val cardService: CardService) {
    @Bean
    fun routes() = router {
        GET("/feed", { ServerResponse.ok().body(cardService.findAllPublishedExternal(), ExternalCard::class.java) })
        GET("/count", { ServerResponse.ok().body(cardService.count(), Long::class.java) })
        GET("/created", { ServerResponse.ok().body(cardService.findAllCreated(), Card::class.java) })
        GET("/published", { ServerResponse.ok().body(cardService.findAllPublished(), Card::class.java) })
        resources("/**", ClassPathResource("static/"))
    }
}
