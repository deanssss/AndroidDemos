package xyz.dean.util.task

import xyz.dean.util.log
import java.lang.Exception
import java.util.concurrent.ExecutorService

private const val TAG = "Task"

sealed class Task : Runnable

class CompleteTask constructor(
    private val onCompleted: () -> Unit = { }
) : Task() {
    override fun run() {
        log.d(TAG, "initialize tasks execute completed.")
        onCompleted.invoke()
    }

    companion object {
        val EMPTY = CompleteTask()
    }
}

class TerminateTask constructor(
    private val errorTask: ExecutableTask,
    private val error: Throwable
) : Task() {
    override fun run() {
        throw Exception("Task execute failed, task: ${errorTask.taskName}", error)
    }
}

class ExecutableTask internal constructor(
    private val taskExecutor: TaskExecutor,
    private val customExecutorService: ExecutorService?,
    taskName: String,
    private val ignoreError: Boolean,
    private val runnable: Runnable,
    internal val dependencies: MutableList<ExecutableTask> = mutableListOf()
) : Task() {
    val taskName = taskName.takeIf { it.isNotEmpty() } ?: runnable.toString()
    val isReady: Boolean get() = dependencies.isEmpty() && !isRunning
    private val next: MutableList<ExecutableTask> = mutableListOf()
    private var isRunning: Boolean = false

    init {
        linkToDependencies()
    }

    fun execute() {
        isRunning = true
        if (customExecutorService != null) {
            customExecutorService.submit(this)
        } else {
            taskExecutor.submit(this)
        }
    }

    override fun run() {
        log.d(TAG, "Exec $taskName")
        try {
            runnable.run()
            taskExecutor.complete(this)
        } catch (e: Throwable) {
            if (!ignoreError) {
                taskExecutor.terminate(this, e)
            } else {
                log.e(TAG, "Execute task $taskName failed.", e)
                taskExecutor.complete(this)
            }
        }
    }

    private fun linkToDependencies() {
        dependencies.forEach { it.next.add(this) }
    }

    fun destroy() {
        isRunning = false
        unlink()
    }

    private fun unlink() {
        next.forEach { it.dependencies.remove(this) }
    }
}

class TaskBuilder internal constructor(
    private val taskExecutor: TaskExecutor,
    private val taskName: String,
    private val ignoreError: Boolean,
    private val runnable: Runnable,
    private val customExecutorService: ExecutorService?
) {
    private val dependencies = mutableListOf<ExecutableTask>()

    fun dependent(vararg task: ExecutableTask): TaskBuilder {
        dependencies.addAll(task)
        return this
    }

    internal fun build(): ExecutableTask {
        return ExecutableTask(
            taskExecutor, customExecutorService,
            taskName, ignoreError, runnable, dependencies
        )
    }
}