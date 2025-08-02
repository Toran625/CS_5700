import java.util.concurrent.Executors
import java.util.concurrent.ScheduledExecutorService
import java.util.concurrent.ScheduledFuture
import java.util.concurrent.TimeUnit

class CPUScheduler private constructor() {
    private val executor: ScheduledExecutorService = Executors.newScheduledThreadPool(2)
    private var cpuFuture: ScheduledFuture<*>? = null
    private var timerFuture: ScheduledFuture<*>? = null

    companion object {
        @Volatile
        private var INSTANCE: CPUScheduler? = null

        fun getInstance(): CPUScheduler {
            return INSTANCE ?: synchronized(this) {
                INSTANCE ?: CPUScheduler().also { INSTANCE = it }
            }
        }
    }

    fun scheduleExecution(cpu: CPU) {
        cpuFuture = executor.scheduleAtFixedRate(
            { cpu.executeInstruction() },
            0,
            2,
            TimeUnit.MILLISECONDS
        )

        timerFuture = executor.scheduleAtFixedRate(
            { cpu.decrementTimer() },
            0,
            16,
            TimeUnit.MILLISECONDS
        )
    }

    fun shutdown() {
        cpuFuture?.cancel(true)
        timerFuture?.cancel(true)
        executor.shutdown()
        try {
            if (!executor.awaitTermination(1, TimeUnit.SECONDS)) {
                executor.shutdownNow()
            }
        } catch (e: InterruptedException) {
            executor.shutdownNow()
        }
    }
}