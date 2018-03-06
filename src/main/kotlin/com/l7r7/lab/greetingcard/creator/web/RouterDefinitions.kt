package com.l7r7.lab.greetingcard.creator.web

import com.l7r7.lab.greetingcard.creator.card.domain.Card
import com.l7r7.lab.greetingcard.creator.card.domain.CardStatus
import com.l7r7.lab.greetingcard.creator.card.service.CardService
import com.l7r7.lab.greetingcard.creator.card.service.ExternalCard
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.web.reactive.function.server.ServerResponse
import org.springframework.web.reactive.function.server.router
import reactor.core.publisher.Mono
import java.util.*

@Configuration
class RouterDefinitions(private val cardService: CardService) {
    @Bean
    fun routes() = router {
        GET("/feed", { ServerResponse.ok().body(cardService.findAllPublishedExternal(), ExternalCard::class.java) })
        GET("/count", { ServerResponse.ok().body(cardService.count(), Long::class.java) })

        GET("/created", { ServerResponse.ok().body(cardService.findAllCreated(), Card::class.java) })
        GET("/created/{id}", { request ->
            ServerResponse.ok().body(Mono.justOrEmpty(request.pathVariable("id"))
                    .map { UUID.fromString(it) }
                    .flatMap { cardService.findById(it) }
                    .filter { it.status == CardStatus.CREATED }, Card::class.java)
        })
        POST("/create", { request ->
            request.bodyToMono(NewCard::class.java)
                    .flatMap { cardService.create(it.title, it.author, it.greetingText) }
                    .flatMap { ServerResponse.created(request.uriBuilder().path("created/{id}").build(it.id)).build() }
        })
        POST("/update", { request ->
            request.bodyToMono(UpdateCard::class.java)
                    .flatMap { cardService.update(it.id, it.title, it.author, it.greetingText) }
                    .flatMap { ServerResponse.created(request.uriBuilder().path("updated/{id}").build(it.id)).build() }
        })
        GET("/updated/{id}", { request ->
            ServerResponse.ok().body(Mono.justOrEmpty(request.pathVariable("id"))
                    .map { UUID.fromString(it) }
                    .flatMap { cardService.findById(it) }
                    .filter { it.status == CardStatus.CREATED }, Card::class.java)
        })
        GET("/published", { ServerResponse.ok().body(cardService.findAllPublished(), Card::class.java) })
        GET("/published/{id}", { request ->
            ServerResponse.ok().body(Mono.justOrEmpty(request.pathVariable("id"))
                    .map { UUID.fromString(it) }
                    .flatMap { cardService.findById(it) }
                    .filter { it.status == CardStatus.PUBLISHED }, Card::class.java)
        })
        POST("/publish", { request ->
            request.bodyToMono(UUID::class.java)
                    .flatMap { cardService.publish(it) }
                    .flatMap { ServerResponse.created(request.uriBuilder().path("published/{id}").build(it.id)).build() }
        })
        resources("/**", ClassPathResource("static/"))
    }
}

data class NewCard(val title: String, val author: String, val greetingText: String)
data class UpdateCard(val id: UUID, val title: String, val author: String, val greetingText: String)
