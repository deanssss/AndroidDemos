package xyz.dean.util.task

import xyz.dean.util.log
import java.util.concurrent.BlockingQueue
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors
import java.util.concurrent.LinkedBlockingQueue

class TaskExecutor internal constructor(
    private val name: String,
    private val executorService: ExecutorService
) {
    private val tasks: MutableList<ExecutableTask> = mutableListOf()
    internal val queue: BlockingQueue<Task> = LinkedBlockingQueue()

    /**
     * 调用此方法将创建任务，并将其添加到任务图中，以供后续执行。
     */
    fun TaskBuilder.install(): ExecutableTask {
        return this.build().also { tasks.add(it) }
    }

    fun startUp() {
        log.d(TAG, "Executor $name start up")
        loop@ while (true) {
            executeTasks()
            when (val task = queue.take()) {
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

    private fun executeTasks() {
        // 拓扑排序处理任务执行
        val readyTasks = tasks.filter { it.isReady() }

        if (readyTasks.isEmpty() && tasks.isEmpty()) {
            queue.offer(CompleteTask)
            return
        }

        readyTasks.forEach { readyTask ->
            readyTask.isRunning = true
            executorService.submit(readyTask)
        }
    }

    private fun removeTask(task: ExecutableTask) {
        task.unlink()
        task.isRunning = false
        tasks.remove(task)
    }

    // 验证图中是否存在环形依赖
    private fun validateDepsGraph(): Boolean {
        val visited = mutableListOf<ExecutableTask>()

        while (true) {
            val vi = tasks.filter {
                !visited.contains(it) // 过滤掉已访问过的
                        && it.deps.all { d -> visited.contains(d) } // 当前节点所有依赖也被访问过
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
                validateDepsGraph()
            }
        }

        /**
         * 声明一个可执行任务，通过[runnable]提供具体的逻辑；[ignoreErrors]用于设置当任务抛出异常时，
         * 是否需要中断剩余任务的执行；使用[taskName]为任务提供一个易于理解的名字。
         */
        fun TaskExecutor.task(
            runnable: Runnable,
            taskName: String = "",
            ignoreErrors: Boolean = false
        ): TaskBuilder {
            return TaskBuilder(this, taskName, ignoreErrors, runnable)
        }

        private fun defaultExecutorService(): ExecutorService {
            return Executors.newFixedThreadPool(Runtime.getRuntime().availableProcessors())
        }
    }
}