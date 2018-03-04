package com.l7r7.lab.greetingcard.creator.card.domain

import org.springframework.data.annotation.Id
import org.springframework.data.mongodb.core.mapping.Document
import java.time.Instant
import java.util.*

@Document(collection = "cards")
data class Card(
        @Id val id: UUID,
        val title: String,
        val greetingText: String,
        val author: String,
        val status: CardStatus,
        val created: Instant,
        val updated: Instant
)