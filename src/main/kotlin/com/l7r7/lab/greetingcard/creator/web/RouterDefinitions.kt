package com.l7r7.lab.greetingcard.creator.web

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.core.io.ClassPathResource
import org.springframework.web.reactive.function.server.router
import java.util.*

@Configuration
class RouterDefinitions(private val handlerFunctions: HandlerFunctions) {
    @Bean
    fun routes() = router {
        GET("/feed", handlerFunctions.feed())
        GET("/count", handlerFunctions.count())
        resources("/**", ClassPathResource("static/"))
    }

    @Bean
    fun createRoutes() = router {
        GET("/created", handlerFunctions.createdList())
        GET("/created/{id}", handlerFunctions.createdSingle())
        POST("/create", handlerFunctions.createNew())
    }

    @Bean
    fun updateRoutes() = router {
        POST("/update", handlerFunctions.update())
        GET("/updated/{id}", handlerFunctions.updatedSingle())
    }

    @Bean
    fun publishRoutes() = router {
        GET("/published", handlerFunctions.publishedList())
        GET("/published/{id}", handlerFunctions.publishedSingle())
        POST("/publish", handlerFunctions.publish())
    }
}

data class NewCard(val title: String, val author: String, val greetingText: String)
data class UpdateCard(val id: UUID, val title: String, val author: String, val greetingText: String)
