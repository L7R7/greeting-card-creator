package com.l7r7.lab.greetingcard.creator.card.service

import com.l7r7.lab.greetingcard.creator.card.domain.Card
import com.l7r7.lab.greetingcard.creator.card.domain.CardRepository
import com.l7r7.lab.greetingcard.creator.card.domain.CardStatus.CREATED
import com.l7r7.lab.greetingcard.creator.card.domain.CardStatus.PUBLISHED
import org.springframework.stereotype.Service
import reactor.core.publisher.Mono
import java.time.Instant
import java.util.*

@Service
class CardService(private val cardRepository: CardRepository) {
    fun create(title: String, author: String, greetingText: String): Mono<Card> {
        val card = Card(UUID.randomUUID(), title, greetingText, author, CREATED, Instant.now(), Instant.now())
        return cardRepository.save(card)
    }

    fun update(id: UUID, title: String, author: String, greetingText: String): Mono<Card> = cardRepository.findById(id)
            .filter { c -> c.status == CREATED }
            .map { c -> Card(id, title, greetingText, author, c.status, c.created, Instant.now()) }
            .flatMap { c -> cardRepository.save(c) }

    fun publish(cardId: UUID): Mono<Card> = cardRepository.findById(cardId)
            .map { card -> card.copy(status = PUBLISHED) }
            .flatMap { card -> cardRepository.save(card) }


    fun findById(id: UUID) = cardRepository.findById(id)

    fun findAll() = cardRepository.findAll()

    fun findAllCreated() = cardRepository.findAll().filter { card -> card.status == CREATED }

    fun findAllPublished() = cardRepository.findAll().filter { card -> card.status == PUBLISHED }

    fun count() = cardRepository.count()
}