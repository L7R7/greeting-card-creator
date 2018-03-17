package com.l7r7.lab.greetingcard.creator.web

import com.l7r7.lab.greetingcard.creator.card.domain.Card
import com.l7r7.lab.greetingcard.creator.card.domain.CardStatus.CREATED
import com.l7r7.lab.greetingcard.creator.card.domain.CardStatus.PUBLISHED
import com.l7r7.lab.greetingcard.creator.card.service.CardService
import com.l7r7.lab.greetingcard.creator.card.service.ExternalCard
import org.springframework.stereotype.Component
import org.springframework.web.reactive.function.server.ServerRequest
import org.springframework.web.reactive.function.server.ServerResponse
import reactor.core.publisher.Mono
import java.util.*

@Component
class HandlerFunctions(private val cardService: CardService) {
    fun count(): (ServerRequest) -> Mono<ServerResponse> = { ServerResponse.ok().body(cardService.count(), Long::class.java) }

    fun feed(): (ServerRequest) -> Mono<ServerResponse> = { ServerResponse.ok().body(cardService.findAllPublishedExternal(), ExternalCard::class.java) }

    fun createNew(): (ServerRequest) -> Mono<ServerResponse> =
            { request ->
                request.bodyToMono(NewCard::class.java)
                        .flatMap { cardService.create(it.title, it.author, it.greetingText) }
                        .flatMap { ServerResponse.created(request.uriBuilder().path("createdList/{id}").build(it.id)).build() }
            }

    fun createdSingle(): (ServerRequest) -> Mono<ServerResponse> =
            { request ->
                ServerResponse.ok().body(Mono.justOrEmpty(request.pathVariable("id"))
                        .map { UUID.fromString(it) }
                        .flatMap { cardService.findById(it) }
                        .filter { it.status == CREATED }, Card::class.java)
            }

    fun createdList(): (ServerRequest) -> Mono<ServerResponse> = { ServerResponse.ok().body(cardService.findAllCreated(), Card::class.java) }

    fun updatedSingle(): (ServerRequest) -> Mono<ServerResponse> =
            { request ->
                ServerResponse.ok().body(Mono.justOrEmpty(request.pathVariable("id"))
                        .map { UUID.fromString(it) }
                        .flatMap { cardService.findById(it) }
                        .filter { it.status == CREATED }, Card::class.java)
            }

    fun update(): (ServerRequest) -> Mono<ServerResponse> =
            { request ->
                request.bodyToMono(UpdateCard::class.java)
                        .flatMap { cardService.update(it.id, it.title, it.author, it.greetingText) }
                        .flatMap { ServerResponse.created(request.uriBuilder().path("updated/{id}").build(it.id)).build() }
            }

    fun publish(): (ServerRequest) -> Mono<ServerResponse> =
            { request ->
                request.bodyToMono(UUID::class.java)
                        .flatMap { cardService.publish(it) }
                        .flatMap { ServerResponse.created(request.uriBuilder().path("published/{id}").build(it.id)).build() }
            }

    fun publishedSingle(): (ServerRequest) -> Mono<ServerResponse> =
            { request ->
                ServerResponse.ok().body(Mono.justOrEmpty(request.pathVariable("id"))
                        .map { UUID.fromString(it) }
                        .flatMap { cardService.findById(it) }
                        .filter { it.status == PUBLISHED }, Card::class.java)
            }

    fun publishedList(): (ServerRequest) -> Mono<ServerResponse> = { ServerResponse.ok().body(cardService.findAllPublished(), Card::class.java) }
}