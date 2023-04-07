package xyz.dean.util.task

import xyz.dean.util.log
import java.lang.Exception
import java.util.concurrent.ExecutorService

private const val TAG = "Task"

/**
 * 在[TaskExecutor]声明一个可执行任务，通过[runnable]提供具体的逻辑；[ignoreErrors]用于设置当任务抛出异常时，
 * 是否需要中断剩余任务的执行；使用[taskName]为任务提供一个易于理解的名字。
 */
fun TaskExecutor.task(
    runnable: Runnable,
    taskName: String = "",
    ignoreErrors: Boolean = false,
    executorService: ExecutorService? = null
): TaskBuilder {
    return TaskBuilder(this, taskName, ignoreErrors, runnable, executorService)
}

/**
 * 制造一个屏障Task，该任务会将已经设置的所有任务作为依赖。
 *
 * @see TaskExecutor.task
 */
fun TaskExecutor.barrier(
    runnable: Runnable = Runnable { },
    taskName: String = "",
    ignoreErrors: Boolean = false,
    executorService: ExecutorService? = null
): ExecutableTask {
    return TaskBuilder(this, taskName, ignoreErrors, runnable, executorService)
        .dependent(tasks)
        .install()
}

/**
 * 在[ExecutableTask]所在的[TaskExecutor]上声明一个可执行任务，并将此任务设置为当前任务的依赖。
 *
 * @see TaskExecutor.task
 */
fun ExecutableTask.task(
    runnable: Runnable,
    taskName: String = "",
    ignoreErrors: Boolean = false,
    executorService: ExecutorService? = null
): TaskBuilder {
    return TaskBuilder(this.taskExecutor, taskName, ignoreErrors, runnable, executorService)
        .dependent(this)
}

/**
 * 在[ExecutableTask]所在的[TaskExecutor]上声明一个可执行任务，并将此任务设置为当前任务的依赖。
 * 列表不能为空。
 *
 * @see TaskExecutor.task
 * @see ExecutableTask.task
 */
fun List<ExecutableTask>.task(
    runnable: Runnable,
    taskName: String = "",
    ignoreErrors: Boolean = false,
    executorService: ExecutorService? = null
): TaskBuilder {
    return TaskBuilder(this.first().taskExecutor, taskName, ignoreErrors, runnable, executorService)
        .dependent(this)
}

/**
 * 为[ExecutableTask]提供使用[ExecutableTask.task]的域，这个域中定义的所有task都会依赖此task。
 *
 * @see ExecutableTask.task
 */
inline fun ExecutableTask.andThen(dependBuild: ExecutableTask.() -> Unit) {
    dependBuild()
}

/**
 * 为[ExecutableTask]列表提供使用[ExecutableTask.task]的域，这个域中定义的所有task都会依赖列表中所有的task。
 *
 * @see ExecutableTask.task
 */
inline fun List<ExecutableTask>.andThen(dependBuild: List<ExecutableTask>.() -> Unit) {
    dependBuild()
}

sealed class Task : Runnable

class ExecutableTask internal constructor(
    internal val taskExecutor: TaskExecutor,
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

    fun dependent(vararg tasks: ExecutableTask): TaskBuilder {
        dependencies.addAll(tasks)
        return this
    }

    fun dependent(tasks: List<ExecutableTask>): TaskBuilder {
        dependencies.addAll(tasks)
        return this
    }

    internal fun build(): ExecutableTask {
        return ExecutableTask(
            taskExecutor, customExecutorService,
            taskName, ignoreError, runnable, dependencies
        )
    }
}

internal class CompleteTask constructor(
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

internal class TerminateTask constructor(
    private val errorTask: ExecutableTask,
    private val error: Throwable
) : Task() {
    override fun run() {
        throw Exception("Task execute failed, task: ${errorTask.taskName}", error)
    }
}