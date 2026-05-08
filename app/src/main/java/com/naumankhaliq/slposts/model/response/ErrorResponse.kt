/*
 * MIT License
 *
 * Copyright (c) 2026 Nauman Khaliq
 * 
 */
package com.naumankhaliq.slposts.model.response

import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

class ErrorResponse {
    var status: String? = null
    var responseCode: Int? = null
    var message: String? = null
    val isStatusSuccess: Boolean get() = responseCode == 200 || responseCode == 201
    val isUnauthorized: Boolean get() = responseCode == 401

    companion object {
        suspend fun<T> Response<T>.parseErrorResponse(): ErrorResponse? {
            this.let { apiResponse ->
                return withContext(Dispatchers.IO) {
                    runCatching {
                        Gson().fromJson(
                            apiResponse.errorBody()?.string(),
                            ErrorResponse::class.java
                        )
                    }.getOrNull()
                }
            }
        }
    }
}

