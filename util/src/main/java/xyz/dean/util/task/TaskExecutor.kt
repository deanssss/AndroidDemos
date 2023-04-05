package xyz.dean.util.task

import xyz.dean.util.log
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue
import kotlin.concurrent.thread

class TaskExecutor internal constructor(
    private val name: String,
    private val executorService: ExecutorService
) {
    private val tasks: MutableList<ExecutableTask> = mutableListOf()
    private val executedTasks: BlockingQueue<Task> = LinkedBlockingQueue()

    /**
     * 调用此方法将创建任务，并将其添加到任务图中，以供后续执行。
     */
    fun TaskBuilder.install(): ExecutableTask {
        return this.build().also { tasks.add(it) }
    }

    fun startUp() {
        start(CompleteTask.EMPTY)
    }

    fun startUpAsync(onCompleted: () -> Unit = { }) {
        thread {
            val completeTask = CompleteTask(onCompleted)
            start(completeTask)
        }
    }

    private fun start(completeTask: CompleteTask) {
        log.d(TAG, "Executor $name start up")
        loop@ while (true) {
            executeTasks(completeTask)
            // 对执行完毕的任务做收尾工作
            when (val task = executedTasks.take()) {
                is ExecutableTask -> removeTask(task)
                is CompleteTask -> {
                    // 所有任务都执行完毕
                    executorService.shutdown()
                    task.run()
                    break@loop
                }
                is TerminateTask -> {
                    // 任务执行出现异常，抛出终止请求
                    executorService.shutdown()
                    task.run()
                    break@loop
                }
            }
        }
    }

    private fun executeTasks(completeTask: CompleteTask) {
        // 拓扑排序处理任务执行
        val readyTasks = tasks.filter { it.isReady }
        if (readyTasks.isEmpty() && tasks.isEmpty()) {
            // 任务都执行完了
            executedTasks.offer(completeTask)
            return
        }
        readyTasks.forEach { readyTask -> readyTask.execute() }
    }

    private fun removeTask(task: ExecutableTask) {
        task.destroy()
        tasks.remove(task)
    }

    internal fun submit(task: ExecutableTask) {
        executorService.submit(task)
    }

    internal fun terminate(errorTask: ExecutableTask, e: Throwable) {
        executedTasks.clear()
        // 执行终止通知
        executedTasks.offer(TerminateTask(errorTask, e))
    }

    internal fun complete(task: ExecutableTask) {
        // 执行成功通知
        executedTasks.offer(task)
    }

    // 验证图中是否存在环形依赖
    private fun validateDepsGraph(): Boolean {
        val visited = mutableListOf<ExecutableTask>()
        while (true) {
            val vi = tasks.filter {
                !visited.contains(it) // 过滤掉已访问过的
                        && it.dependencies.all { d -> visited.contains(d) } // 当前节点所有依赖也被访问过
            }
            if (vi.isEmpty()) {
                // 依赖图中所有节点都被访问过了，就能保证其中无环。
                return visited.size == tasks.size
            }
            visited.addAll(vi)
        }
    }

    companion object {
        private const val TAG = "TaskExecutor"

        fun build(
            name: String = "",
            executorService: ExecutorService = defaultExecutorService(),
            config: TaskExecutor.() -> Unit
        ): TaskExecutor {
            return TaskExecutor(name, executorService).apply {
                config()
                // 虽然任务当前的定义方式可以保证依赖图中一定无环，保险起见还是验证一下。
                if (!validateDepsGraph()) error("There is a cycle in the task graph.")
            }
        }

        /**
         * 声明一个可执行任务，通过[runnable]提供具体的逻辑；[ignoreErrors]用于设置当任务抛出异常时，
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

        private fun defaultExecutorService(): ExecutorService {
            return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
        }
    }
}