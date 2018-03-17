package com.l7r7.lab.greetingcard.creator.web

import com.l7r7.lab.greetingcard.creator.card.domain.Card
import com.l7r7.lab.greetingcard.creator.card.domain.CardStatus
import com.l7r7.lab.greetingcard.creator.card.service.CardService
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.reactive.result.view.Rendering
import reactor.core.publisher.Flux
import reactor.core.publisher.Mono
import java.util.*

@Controller
class CardController(private val cardService: CardService) {

    @GetMapping
    fun listCards(): Mono<Rendering> {
        val created: Mono<Pair<String, MutableList<Card>>> = cardService.findAllCreated().collectList().map { Pair("createdCards", it) }
        val published: Mono<Pair<String, MutableList<Card>>> = cardService.findAllPublished().collectList().map { Pair("publishedCards", it) }
        val count: Mono<Pair<String, Long>> = cardService.count().map { Pair("count", it) }

        return Flux.merge(created, published, count)
                .collectMap({ it.first }, { it.second })
                .switchIfEmpty(Mono.just(emptyMap()))
                .zipWith(Mono.just(Rendering.view("list")), { model, rendering -> rendering.model(model) })
                .map { it.build() }
    }

    @GetMapping("edit/{id}")
    fun editPage(@PathVariable("id") id: UUID): Mono<Rendering> =
            cardService.findById(id)
                    .map { card -> if (card.status == CardStatus.CREATED) Pair("card", card) else Pair("errorMessage", "Card is not in state 'created'") }
                    .switchIfEmpty(Mono.just(Pair("errorMessage", "ID not found")))
                    .map { mapOf(it) }
                    .zipWith(Mono.just(Rendering.view("edit")), { model, rendering -> rendering.model(model) })
                    .map { it.build() }

    @GetMapping("new")
    fun newPage(model: Model): Mono<Rendering> =
            Mono.just(Rendering.view("new"))
                    .map { it.modelAttribute("card", NewCardDto()) }
                    .map { it.build() }

    @PostMapping("new")
    fun newCard(@ModelAttribute card: NewCardDto): Mono<Rendering> =
            cardService.create(card.title, card.author, card.greetingText)
                    .map { c -> CreatedCardDto(c.id, c.author, c.title, c.greetingText) }
                    .map { Pair("card", it) }
                    .map { mapOf(it) }
                    .map { Rendering.view("created").model(it) }
                    .switchIfEmpty(Mono.just(Pair("errorMessage", "card not present")).map { mapOf(it) }.map { Rendering.view("created").model(it) })
                    .map { it.build() }

    @PostMapping("edit")
    fun updateCard(@ModelAttribute cardAttribute: CreatedCardDto): Mono<Rendering> =
            cardService.update(cardAttribute.id, cardAttribute.title, cardAttribute.author, cardAttribute.greetingText)
                    .map { card -> mapOf(Pair("card", card), Pair("success", true)) }
                    .switchIfEmpty(Mono.just(mapOf(Pair("success", false))))
                    .zipWith(Mono.just(Rendering.view("updated")), { model, rendering -> rendering.model(model) })
                    .map { it.build() }

    @PostMapping("publish")
    fun publish(@ModelAttribute cardAttribute: CardToPublish): Mono<Rendering> =
            cardService.publish(cardAttribute.id)
                    .map { card -> mapOf(Pair("card", card), Pair("success", true)) }
                    .switchIfEmpty(Mono.just(mapOf(Pair("success", false))))
                    .zipWith(Mono.just(Rendering.view("published")), { model, rendering -> rendering.model(model) })
                    .map { it.build() }
}

class NewCardDto(var author: String = "", var title: String = "", var greetingText: String = "")
class CreatedCardDto(var id: UUID, var author: String, var title: String, var greetingText: String)
class CardToPublish(var id: UUID)
