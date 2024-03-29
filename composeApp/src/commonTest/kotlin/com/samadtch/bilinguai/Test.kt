package com.samadtch.bilinguai

import com.samadtch.bilinguai.data.datasources.remote.ModelRemoteSource
import io.ktor.client.HttpClient
import io.ktor.client.engine.cio.CIO
import io.ktor.client.plugins.contentnegotiation.ContentNegotiation
import io.ktor.http.ContentType
import io.ktor.serialization.kotlinx.json.json
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.json.Json
import kotlin.test.BeforeTest
import kotlin.test.Test
import kotlin.test.assertTrue

class ModelTest {

    private lateinit var model: ModelRemoteSource

    @BeforeTest
    fun setupHttpClient() {
        model = ModelRemoteSource(
            HttpClient(CIO) {
                expectSuccess = true//Throw error if failed

                //Content Negotiation
                val json = Json { ignoreUnknownKeys = true }
                install(ContentNegotiation) {
                    json(json, contentType = ContentType.Application.Json)
                }
            }
        )
    }

    @Test
    fun testModelCallSuccess() {
        runBlocking {
            assertTrue {
                val response = model.generateData(
                    mapOf(
                        "URL" to "https://api.openai.com/v1/chat/completions",
                        "model" to "gpt-3.5-turbo",
                        "temperature" to 0.7f,
                    ),
                    """
                        Generate a conversation between 2 people in french, return the data as json that contains a list of all the messages as a strings list (named conversation) and a map of vocabulary from that conversation (only hardest words) which contains the word as key and the definition as value
                    """.trimIndent()
                ).getOrNull()
                response != null && response.vocabulary.isNotEmpty() && response.conversation.isNotEmpty()
            }
        }
    }
}