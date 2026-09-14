package sangiorgi.wps.opensource.root

import com.topjohnwu.superuser.Shell
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import sangiorgi.wps.opensource.core.logging.AppLogger
import sangiorgi.wps.opensource.core.logging.LogLevel
import sangiorgi.wps.opensource.core.result.ShellResult
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class SuCommandExecutor @Inject constructor(
    private val logger: AppLogger
) {
    // Detect forbidden injection characters in individual arguments
    private val dangerousCharsRegex = Regex("[;&|`$\n\r<>]")

    suspend fun execute(command: String, timeoutSec: Long = 15): ShellResult = withContext(Dispatchers.IO) {
        val start = System.currentTimeMillis()
        logger.log(
            component = "SuCommandExecutor",
            operation = "execute",
            result = "Exec: $command",
            level = LogLevel.DEBUG
        )

        val job = Shell.cmd(command)
        val result = job.exec()
        val duration = System.currentTimeMillis() - start

        val shellResult = ShellResult(
            command = command,
            isSuccess = result.isSuccess,
            exitCode = result.code,
            stdout = result.out,
            stderr = result.err,
            durationMs = duration
        )

        logger.log(
            component = "SuCommandExecutor",
            operation = "result",
            result = "Exit ${result.code}, success: ${result.isSuccess} for $command",
            durationMs = duration,
            level = if (result.isSuccess) LogLevel.INFO else LogLevel.WARN
        )

        shellResult
    }

    /**
     * Executes safe shell command with sanitized arguments to prevent shell injection.
     */
    suspend fun executeEscaped(vararg args: String): ShellResult {
        for (arg in args) {
            if (dangerousCharsRegex.containsMatchIn(arg)) {
                logger.log(
                    component = "SuCommandExecutor",
                    operation = "security_reject",
                    result = "Rejected command due to metacharacters in argument: $arg",
                    level = LogLevel.ERROR
                )
                return ShellResult(
                    command = args.joinToString(" "),
                    isSuccess = false,
                    exitCode = -1,
                    stdout = emptyList(),
                    stderr = listOf("Security violation: argument contains forbidden shell metacharacters"),
                    durationMs = 0
                )
            }
        }
        val command = args.joinToString(" ")
        return execute(command)
    }
}
