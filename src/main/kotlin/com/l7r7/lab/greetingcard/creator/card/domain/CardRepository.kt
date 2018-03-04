package com.l7r7.lab.greetingcard.creator.card.domain

import org.springframework.data.repository.reactive.ReactiveSortingRepository
import java.util.*

interface CardRepository : ReactiveSortingRepository<Card, UUID>
