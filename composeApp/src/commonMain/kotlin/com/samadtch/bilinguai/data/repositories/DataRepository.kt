package com.samadtch.bilinguai.data.repositories

import com.samadtch.bilinguai.data.datasources.remote.base.AuthRemoteSource
import com.samadtch.bilinguai.data.datasources.remote.base.ConfigRemoteSource
import com.samadtch.bilinguai.data.datasources.remote.base.DataRemoteSource
import com.samadtch.bilinguai.data.datasources.remote.base.ModelRemoteSource
import com.samadtch.bilinguai.data.repositories.base.DataRepository
import com.samadtch.bilinguai.di.Dispatcher
import com.samadtch.bilinguai.models.Data
import com.samadtch.bilinguai.utilities.exceptions.APIException
import com.samadtch.bilinguai.utilities.exceptions.APIException.Companion.API_ERROR_AUTH
import com.samadtch.bilinguai.utilities.exceptions.APIException.Companion.API_ERROR_OTHER
import com.samadtch.bilinguai.utilities.exceptions.APIException.Companion.API_ERROR_RATE_LIMIT
import com.samadtch.bilinguai.utilities.exceptions.AuthException
import com.samadtch.bilinguai.utilities.exceptions.AuthException.Companion.AUTH_ERROR_USER_LOGGED_OUT
import com.samadtch.bilinguai.utilities.exceptions.DataException
import com.samadtch.bilinguai.utilities.exceptions.DataException.Companion.DATA_ERROR_NETWORK
import com.samadtch.bilinguai.utilities.exceptions.DataException.Companion.DATA_ERROR_NOT_FOUND
import com.samadtch.bilinguai.utilities.exceptions.DataException.Companion.DATA_ERROR_SERVICE
import com.samadtch.bilinguai.utilities.exceptions.DataException.Companion.FIRESTORE_CONCURRENCY_ERRORS
import com.samadtch.bilinguai.utilities.exceptions.DataException.Companion.FIRESTORE_ERROR_DEADLINE
import com.samadtch.bilinguai.utilities.exceptions.DataException.Companion.FIRESTORE_ERROR_NOT_FOUND
import com.samadtch.bilinguai.utilities.exceptions.DataException.Companion.FIRESTORE_NETWORK_ERRORS
import kotlinx.coroutines.withContext
import kotlinx.datetime.Clock

class DataRepository(
    private val configRemoteSource: ConfigRemoteSource,
    private val modelRemoteDataSource: ModelRemoteSource,
    private val authRemoteDataSource: AuthRemoteSource,
    private val dataRemoteDataSource: DataRemoteSource,
    private val dispatcher: Dispatcher
) : DataRepository {

    override suspend fun generateData(inputs: Map<String, Any>, temperature: Float): Result<Data> =
        withContext(dispatcher.io) {
            try {
                val userId = authRemoteDataSource.getUserId()
                if (userId.isFailure) Result.failure(DataException(AUTH_ERROR_USER_LOGGED_OUT))
                else {
                    var prompt = configRemoteSource.getStringConfig("LLM_PROMPT")
                    inputs.forEach {
                        prompt = when (it.value) {
                            is List<*> -> prompt.replace(
                                "{{${it.key}}}",
                                (it.value as List<*>).joinToString(", ")
                            )

                            is Boolean -> prompt.replace(Regex("""\{\{${it.key}::TRUE\((.*?)\)FALSE\((.*?)\)}}""")) { match ->
                                if (it.value as Boolean) match.groups[1]!!.value
                                else match.groups[2]!!.value
                            }

                            else -> prompt.replace("{{${it.key}}}", it.value.toString())
                        }
                    }

                    //Generate Data
                    val response = modelRemoteDataSource.generateData(
                        settings = mapOf(
                            "URL" to configRemoteSource.getStringConfig("LLM_URL"),
                            "model" to configRemoteSource.getStringConfig("LLM_MODEL"),
                            "temperature" to temperature
                        ),
                        prompt = prompt
                    ).getOrThrow()

                    //Init Data
                    val data = Data(
                        null,
                        language = (inputs["language"] as List<*>)[0] as String,
                        topic = inputs["topic"] as String,
                        conversation = response.conversation,
                        vocabulary = response.vocabulary,
                        Clock.System.now().epochSeconds
                    )

                    //Insert on Db
                    val id = dataRemoteDataSource.insertData(userId.getOrNull()!!, data)

                    //Return
                    Result.success(data.copy(dataId = id))
                }
            } catch (e: APIException) {
                when (e.code) {
                    API_ERROR_RATE_LIMIT -> Result.failure(e)
                    API_ERROR_AUTH -> Result.failure(e)
                    else -> Result.failure(APIException(API_ERROR_OTHER))
                }
            } catch (e: DataException) {
                when (e.code) {
                    in FIRESTORE_NETWORK_ERRORS, in FIRESTORE_CONCURRENCY_ERRORS, FIRESTORE_ERROR_DEADLINE ->
                        Result.failure(DataException(DATA_ERROR_NETWORK))

                    FIRESTORE_ERROR_NOT_FOUND -> Result.failure(DataException(DATA_ERROR_NOT_FOUND))
                    else -> Result.failure(DataException(DATA_ERROR_SERVICE))
                }
            }
        }

    override suspend fun getData(): Result<List<Data>> = withContext(dispatcher.io) {
        val userId = authRemoteDataSource.getUserId()
        if (userId.isFailure) Result.failure(DataException(AUTH_ERROR_USER_LOGGED_OUT))
        else {
            try {
                val response = dataRemoteDataSource.getData(userId.getOrNull()!!)
                if (response.isEmpty()) Result.failure(DataException(DATA_ERROR_NOT_FOUND))
                else Result.success(response)
            } catch (e: DataException) {
                when (e.code) {
                    in FIRESTORE_NETWORK_ERRORS,
                    in FIRESTORE_CONCURRENCY_ERRORS,
                    FIRESTORE_ERROR_DEADLINE -> Result.failure(DataException(DATA_ERROR_NETWORK))

                    FIRESTORE_ERROR_NOT_FOUND, DATA_ERROR_NOT_FOUND -> Result.failure(
                        DataException(
                            DATA_ERROR_NOT_FOUND
                        )
                    )

                    else -> Result.failure(DataException(DATA_ERROR_SERVICE))
                }
            }
        }
    }

    override suspend fun deleteData(dataId: String) {
        val userId = authRemoteDataSource.getUserId()
        if (userId.isFailure) throw AuthException(AUTH_ERROR_USER_LOGGED_OUT)
        else {
            try {
                dataRemoteDataSource.deleteData(userId.getOrNull()!!, dataId)
            } catch (e: DataException) {
                when (e.code) {
                    in FIRESTORE_NETWORK_ERRORS,
                    in FIRESTORE_CONCURRENCY_ERRORS,
                    FIRESTORE_ERROR_DEADLINE -> throw DataException(DATA_ERROR_NETWORK)

                    FIRESTORE_ERROR_NOT_FOUND -> throw DataException(DATA_ERROR_NOT_FOUND)
                    else -> throw DataException(DATA_ERROR_SERVICE)
                }
            }
        }
    }

}