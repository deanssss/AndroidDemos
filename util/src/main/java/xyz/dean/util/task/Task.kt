package xyz.dean.util.task

import xyz.dean.util.log
import java.lang.Exception

private const val TAG = "Task"

sealed class Task : Runnable

object CompleteTask : Task() {
    override fun run() {
        log.d(TAG, "initialize tasks execute completed.")
    }
}

class TerminateTask(
    private val errorTask: ExecutableTask,
    private val error: Throwable
) : Task() {
    override fun run() {
        throw Exception("Task execute failed, task: ${errorTask.getTaskName()}", error)
    }
}

class ExecutableTask internal constructor(
    private val taskExecutor: TaskExecutor,
    private val taskName: String,
    private val ignoreError: Boolean,
    private val runnable: Runnable,
    internal val deps: MutableList<ExecutableTask> = mutableListOf(),
    internal var isRunning: Boolean = false
) : Task() {
    private val next: MutableList<ExecutableTask> = mutableListOf()

    init {
        deps.forEach { it.next.add(this) }
    }

    fun getTaskName(): String = taskName.takeIf { it.isNotEmpty() }
        ?: runnable.toString()

    override fun run() {
        log.d(TAG, "Exec ${getTaskName()}")
        try {
            runnable.run()
        } catch (e: Throwable) {
            if (!ignoreError) {
                taskExecutor.queue.offer(TerminateTask(this, e))
            } else {
                log.e(TAG, "Execute task ${getTaskName()} failed.", e)
            }
        } finally {
            taskExecutor.queue.offer(this)
        }
    }

    fun isReady(): Boolean {
        return deps.isEmpty() && !isRunning
    }

    fun unlink() {
        next.forEach { it.deps.remove(this) }
    }
}

class TaskBuilder internal constructor(
    private val taskExecutor: TaskExecutor,
    private val taskName: String,
    private val ignoreError: Boolean,
    private val runnable: Runnable
) {
    private val deps = mutableListOf<ExecutableTask>()

    fun dependent(vararg task: ExecutableTask): TaskBuilder {
        deps.addAll(task)
        return this
    }

    internal fun build(): ExecutableTask {
        return ExecutableTask(
            taskExecutor,
            taskName, ignoreError, runnable, deps
        )
    }
}