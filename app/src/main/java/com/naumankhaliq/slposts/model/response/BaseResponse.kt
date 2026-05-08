/*
 * MIT License
 *
 * Copyright (c) 2026 Nauman Khaliq
 */
package com.naumankhaliq.slposts.model.response

data class BaseResponse<T>(
    val resultCount: Int,
    val results: T?
)