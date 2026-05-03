package com.lucknow.waterbowl.data.api

import com.google.gson.Gson
import com.google.gson.JsonArray
import com.google.gson.JsonObject
import com.google.gson.JsonPrimitive
import com.google.gson.JsonElement
import retrofit2.HttpException
import java.io.IOException
import java.net.SocketTimeoutException
import java.net.UnknownHostException

/**
 * Maps network / HTTP failures to short user-facing messages.
 * FastAPI errors often look like: {"detail":"..."} or {"detail":[{"msg":"..."}]}.
 */
object ApiExceptionMapper {

    private val gson = Gson()

    fun userMessage(throwable: Throwable): String = when (throwable) {
        is HttpException -> parseHttpException(throwable)
        is SocketTimeoutException -> "Request timed out. Try again."
        is UnknownHostException -> "Cannot reach server. Check internet or server address."
        is IOException -> "Network error. Check your connection."
        else -> throwable.message?.takeIf { it.isNotBlank() } ?: "Something went wrong"
    }

    private fun parseHttpException(e: HttpException): String {
        val raw = try {
            e.response()?.errorBody()?.string()
        } catch (_: Exception) {
            null
        } ?: return httpCodeFallback(e.code())

        return try {
            val root = gson.fromJson(raw, JsonObject::class.java)
            if (!root.has("detail")) return httpCodeFallback(e.code())
            formatDetail(root.get("detail")) ?: httpCodeFallback(e.code())
        } catch (_: Exception) {
            httpCodeFallback(e.code())
        }
    }

    private fun formatDetail(element: JsonElement): String? = when {
        element.isJsonPrimitive -> (element as JsonPrimitive).asString
        element.isJsonArray -> {
            val arr = element as JsonArray
            val lines = arr.mapNotNull { item ->
                when {
                    item.isJsonPrimitive -> item.asString
                    item.isJsonObject -> {
                        val o = item.asJsonObject
                        when {
                            o.has("msg") -> o.get("msg")?.asString
                            o.has("message") -> o.get("message")?.asString
                            else -> o.toString()
                        }
                    }
                    else -> item.toString()
                }
            }
            lines.joinToString("\n").ifBlank { null }
        }
        else -> element.toString()
    }

    private fun httpCodeFallback(code: Int): String = when (code) {
        400 -> "Invalid request"
        401 -> "Invalid email or password"
        403 -> "Access denied"
        404 -> "Not found"
        409 -> "Already exists"
        422 -> "Validation error"
        503 -> "Server temporarily unavailable"
        in 500..599 -> "Server error. Try again later."
        else -> "Request failed ($code)"
    }
}
