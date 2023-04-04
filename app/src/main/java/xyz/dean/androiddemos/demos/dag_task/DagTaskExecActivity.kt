package xyz.dean.androiddemos.demos.dag_task

import android.os.Bundle
import android.widget.Toast
import io.reactivex.Completable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.schedulers.Schedulers
import xyz.dean.androiddemos.BaseActivity
import xyz.dean.androiddemos.DemoItem
import xyz.dean.androiddemos.R
import xyz.dean.androiddemos.common.log
import xyz.dean.util.reactivex.BindLife
import xyz.dean.util.task.TaskExecutor
import xyz.dean.util.task.TaskExecutor.Companion.task
import java.util.concurrent.Executors

class DagTaskExecActivity : BaseActivity(), BindLife {
    override val compositeDisposable = CompositeDisposable()
    override fun getDemoItem() = demoItem

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_dag_task_exec)
        execTasks()
    }

    private fun execTasks() {
        Toast.makeText(this, "Start initialize.", Toast.LENGTH_SHORT).show()
        Completable.fromAction {
            val customExecutor = Executors.newSingleThreadExecutor {
                return@newSingleThreadExecutor Thread(it, "SingleThread")
            }
            val start = System.currentTimeMillis()
            TaskExecutor.build {
                val ATask = task(taskName = "A", runnable = InitATask())
                    .install()
                val BTask = task(taskName = "B", runnable = InitBTask(), executorService = customExecutor)
                    .dependent(ATask)
                    .install()
                val CTask = task(taskName = "C", runnable = InitCTask(), executorService = customExecutor)
                    .dependent(ATask)
                    .install()
                val DTask = task(taskName = "D", runnable = InitDTask(), executorService = customExecutor)
                    .dependent(BTask)
                    .install()
                val ETask = task(taskName = "E", runnable = InitETask())
                    .dependent(CTask, DTask)
                    .install()
            }.startUp()
            val end = System.currentTimeMillis()
            log.d(TAG, "Time spent: ${end - start}ms")
        }.subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .doOnComplete {
                Toast.makeText(this, "Initialize finished.", Toast.LENGTH_SHORT).show()
            }
            .bindLife()
    }

    override fun onDestroy() {
        super.onDestroy()
        destroyDisposables()
    }

    class InitATask : Runnable {
        override fun run() {
            log.d(TAG, "[${Thread.currentThread().name}]A start")
            Thread.sleep(1000)
            log.d(TAG, "[${Thread.currentThread().name}]A success")
        }
    }

    class InitBTask : Runnable {
        override fun run() {
            log.d(TAG, "[${Thread.currentThread().name}]B start")
            Thread.sleep(1000)
            log.d(TAG, "[${Thread.currentThread().name}]B success")
        }
    }

    class InitCTask : Runnable {
        override fun run() {
            log.d(TAG, "[${Thread.currentThread().name}]C start")
            Thread.sleep(1000)
            log.d(TAG, "[${Thread.currentThread().name}]C success")
        }
    }

    class InitDTask : Runnable {
        override fun run() {
            log.d(TAG, "[${Thread.currentThread().name}]D start")
            Thread.sleep(1000)
            log.d(TAG, "[${Thread.currentThread().name}]D success")
        }
    }

    class InitETask : Runnable {
        override fun run() {
            log.d(TAG, "[${Thread.currentThread().name}]E start")
            Thread.sleep(2000)
            log.d(TAG, "[${Thread.currentThread().name}]E success")
        }
    }

    companion object {
        private const val TAG = "DagTaskExecActivity"

        val demoItem = DemoItem("dag-task",
            R.string.dag_task_demo_name,
            R.string.dag_task_describe,
            DagTaskExecActivity::class.java, R.mipmap.img_practice)
    }
}