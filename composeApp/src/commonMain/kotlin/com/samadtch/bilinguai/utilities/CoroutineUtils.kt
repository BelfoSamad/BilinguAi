package com.samadtch.bilinguai.utilities

import com.samadtch.bilinguai.utilities.exceptions.DataException
import com.samadtch.bilinguai.utilities.exceptions.DataException.Companion.DATA_ERROR_NOT_FOUND
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.map

@Suppress("UNCHECKED_CAST")
fun <T, R> Flow<T>.asResult(mapper: ((t: T) -> R)? = null): Flow<Result<R>> {
    return this.map {
            if ((it is List<*> && it.isEmpty()) || it == null)
                Result.failure(DataException(DATA_ERROR_NOT_FOUND))
            else if (mapper != null) Result.success(mapper(it))
            else Result.success(it as R)
        }.catch { emit(Result.failure(it)) }
}

//Simpler version of the above code for Non-Flow data
@Suppress("UNCHECKED_CAST")
fun <T, R> asResult(t: T, mapper: ((t: T) -> R)? = null): Result<R> {
    return try {
        if ((t is List<*> && t.isEmpty()) || t == null)
            Result.failure(DataException(DATA_ERROR_NOT_FOUND))
        else if (mapper != null) Result.success(mapper(t))
        else Result.success(t as R)
    } catch (e: DataException) {
        Result.failure(e)
    }
}