package com.naumankhaliq.slposts.utils.extensions

import android.util.Patterns

fun String.isValidEmail(): Boolean {
    return isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(this).matches()
}