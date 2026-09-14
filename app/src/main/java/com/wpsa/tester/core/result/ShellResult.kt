package com.wpsa.tester.core.result

data class ShellResult(
    val command: String,
    val isSuccess: Boolean,
    val exitCode: Int,
    val stdout: List<String>,
    val stderr: List<String>,
    val durationMs: Long
) {
    val outString: String get() = stdout.joinToString("\n")
    val errString: String get() = stderr.joinToString("\n")
}
