package com.l7r7.lab.greetingcard.creator.web

import com.l7r7.lab.greetingcard.creator.card.domain.CardStatus
import com.l7r7.lab.greetingcard.creator.card.service.CardService
import org.slf4j.LoggerFactory
import org.springframework.stereotype.Controller
import org.springframework.ui.Model
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.ModelAttribute
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.PostMapping
import java.util.*

@Controller
class CardController(private val cardService: CardService) {
    private val log = LoggerFactory.getLogger(CardController::class.java)

    @GetMapping
    fun listCards(model: Model): String {
        model.addAttribute("createdCards", cardService.findAllCreated())
        model.addAttribute("publishedCards", cardService.findAllPublished())
        model.addAttribute("count", cardService.count())
        return "list"
    }

    @GetMapping("edit/{id}")
    fun editPage(@PathVariable("id") id: UUID, model: Model): String {
        cardService.findById(id)
                .subscribe({ card ->
                    if (card.status == CardStatus.CREATED) model.addAttribute("card", card)
                    else model.addAttribute("errorMessage", "Card is not in state 'created'")
                }, { model.addAttribute("errorMessage", "ID not found") })
        return "edit"
    }

    @GetMapping("new")
    fun newPage(model: Model): String {
        model.addAttribute("card", NewCardDto())
        return "new"
    }

    @PostMapping("new")
    fun newCard(model: Model, @ModelAttribute card: NewCardDto): String {
        cardService.create(card.title, card.author, card.greetingText)
                .doOnEach { t -> log.warn(t.toString()) }
                .map { c -> CreatedCardDto(c.id, c.author, c.title, c.greetingText) }
                .subscribe { c -> model.addAttribute("card", c) }
        return "created"
    }

    @PostMapping("edit")
    fun updateCard(model: Model, @ModelAttribute cardAttribute: CreatedCardDto): String {
        cardService.update(cardAttribute.id, cardAttribute.title, cardAttribute.author, cardAttribute.greetingText)
                .subscribe({ c ->
                    model.addAttribute("card", c)
                    model.addAttribute("success", true)
                }, { model.addAttribute("success", false) })
        return "updated"
    }

    @PostMapping("publish")
    fun publish(model: Model, @ModelAttribute cardAttribute: CardToPublish): String {
        cardService.publish(cardAttribute.id)
                .subscribe({
                    model.addAttribute("card", it)
                    model.addAttribute("success", true)
                }, {
                    model.addAttribute("success", false)
                })
        return "published"
    }
}

class NewCardDto(var author: String = "", var title: String = "", var greetingText: String = "")
class CreatedCardDto(var id: UUID, var author: String, var title: String, var greetingText: String)
class CardToPublish(var id: UUID)
