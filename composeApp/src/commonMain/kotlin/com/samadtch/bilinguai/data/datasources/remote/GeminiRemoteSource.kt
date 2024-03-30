package com.samadtch.bilinguai.data.datasources.remote

import com.samadtch.bilinguai.data.datasources.remote.base.ModelRemoteSource
import com.samadtch.bilinguai.data.datasources.remote.dto.Content
import com.samadtch.bilinguai.data.datasources.remote.dto.GeminiRequest
import com.samadtch.bilinguai.data.datasources.remote.dto.GeminiResponse
import com.samadtch.bilinguai.data.datasources.remote.dto.GenerationConfig
import com.samadtch.bilinguai.data.datasources.remote.dto.Part
import com.samadtch.bilinguai.models.pojo.DataResponse
import com.samadtch.bilinguai.utilities.exceptions.APIException
import com.samadtch.bilinguai.utilities.exceptions.APIException.Companion.API_ERROR_AUTH
import com.samadtch.bilinguai.utilities.exceptions.APIException.Companion.API_ERROR_GENERATION
import com.samadtch.bilinguai.utilities.exceptions.APIException.Companion.API_ERROR_NETWORK
import com.samadtch.bilinguai.utilities.exceptions.APIException.Companion.API_ERROR_OTHER
import com.samadtch.bilinguai.utilities.exceptions.APIException.Companion.API_ERROR_RATE_LIMIT
import com.samadtch.bilinguai.utilities.exceptions.APIException.Companion.API_ERROR_SERVER_OVERLOAD
import com.samadtch.bilinguai.utilities.exceptions.APIException.Companion.API_ERROR_SERVER_REQUEST
import com.samadtch.bilinguai.utilities.exceptions.sendCrashlytics
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json

class GeminiRemoteSource(private val client: HttpClient) : ModelRemoteSource {

    override suspend fun generateData(
        settings: Map<String, Any>,
        prompt: String
    ): Result<DataResponse> {
        return try {
            val response = client.post(settings["URL"]!!.toString()) {
                contentType(ContentType.Application.Json)
                setBody(
                    GeminiRequest(
                        contents = listOf(
                            Content(
                                parts = listOf(
                                    Part(prompt)
                                )
                            )
                        ),
                        generationConfig = GenerationConfig(settings["temperature"] as Float)
                    )
                )
            }.body<GeminiResponse>()

            //Return Result
            Result.success(Json.decodeFromString<DataResponse>(response.candidates.map { p -> p.content.parts.map { it.text } }
                .joinToString(" ")
                .removePrefix("[```json")
                .removeSuffix("```]")
            ))
        } catch (e: SerializationException) {
            Result.failure(APIException(API_ERROR_GENERATION))
        } catch (e: UnresolvedAddressException) {
            Result.failure(APIException(API_ERROR_NETWORK))
        } catch (e: RedirectResponseException) {
            Result.failure(APIException(API_ERROR_OTHER, e.message))
        } catch (e: ClientRequestException) {
            println(e.message)
            when (e.response.status) {
                HttpStatusCode.Unauthorized -> {
                    sendCrashlytics(Exception("API Call: Error Unauthorized ${e.message}"))
                    Result.failure(APIException(API_ERROR_AUTH))
                }

                HttpStatusCode.TooManyRequests -> Result.failure(APIException(API_ERROR_RATE_LIMIT))
                else -> Result.failure(APIException(API_ERROR_OTHER, e.message))
            }
        } catch (e: ServerResponseException) {
            when (e.response.status) {
                HttpStatusCode.InternalServerError -> Result.failure(
                    APIException(API_ERROR_SERVER_REQUEST)
                )

                HttpStatusCode.ServiceUnavailable -> Result.failure(
                    APIException(API_ERROR_SERVER_OVERLOAD)
                )

                else -> Result.failure(APIException(API_ERROR_OTHER, e.message))
            }
        }
    }

}