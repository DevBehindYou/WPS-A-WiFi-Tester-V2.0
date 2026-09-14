package com.wpsa.tester.core.logging

import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import javax.inject.Inject
import javax.inject.Singleton

enum class LogLevel {
    DEBUG, INFO, WARN, ERROR
}

data class LogEntry(
    val id: Long = System.nanoTime(),
    val timestamp: Long,
    val component: String,
    val operation: String,
    val result: String,
    val durationMs: Long? = null,
    val level: LogLevel = LogLevel.INFO,
    val error: String? = null
) {
    val formattedTime: String
        get() = SimpleDateFormat("HH:mm:ss.SSS", Locale.US).format(Date(timestamp))
}

@Singleton
class AppLogger @Inject constructor() {
    private val _logs = MutableStateFlow<List<LogEntry>>(emptyList())
    val logs: StateFlow<List<LogEntry>> = _logs.asStateFlow()

    companion object {
        private const val MAX_LOG_ENTRIES = 500
    }

    fun log(
        component: String,
        operation: String,
        result: String,
        durationMs: Long? = null,
        level: LogLevel = LogLevel.INFO,
        error: String? = null
    ) {
        val sanitizedResult = redactSensitiveInfo(result)
        val entry = LogEntry(
            timestamp = System.currentTimeMillis(),
            component = component,
            operation = operation,
            result = sanitizedResult,
            durationMs = durationMs,
            level = level,
            error = error
        )
        _logs.update { list ->
            val updated = list + entry
            if (updated.size > MAX_LOG_ENTRIES) {
                updated.takeLast(MAX_LOG_ENTRIES)
            } else {
                updated
            }
        }
    }

    fun clearLogs() {
        _logs.value = emptyList()
    }

    fun exportLogsText(): String {
        return _logs.value.joinToString("\n") { entry ->
            val dur = entry.durationMs?.let { " (${it}ms)" } ?: ""
            "[${entry.formattedTime}] [${entry.level.name}] [${entry.component}:${entry.operation}] ${entry.result}$dur"
        }
    }

    private fun redactSensitiveInfo(input: String): String {
        // Redact potential 8-digit PINs
        var sanitized = input.replace(Regex("\\b\\d{8}\\b"), "[REDACTED_PIN]")
        // Redact PSK credentials
        sanitized = sanitized.replace(Regex("psk=\\S+"), "psk=[REDACTED_PSK]")
        return sanitized
    }
}
