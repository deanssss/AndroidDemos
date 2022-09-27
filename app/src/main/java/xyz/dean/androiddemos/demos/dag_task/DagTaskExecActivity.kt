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
            val start = System.currentTimeMillis()
            TaskExecutor.build {
                val logTask = task(taskName = "init log", runnable = InitLogTask())
                    .install()
                val serTask = task(taskName = "init server", runnable = InitServiceTask())
                    .dependent(logTask)
                    .install()
                val moduleTask = task(taskName = "init module", runnable = InitModuleTask())
                    .dependent(logTask)
                    .install()
                val bTask = task(taskName = "init b", runnable = InitBTask())
                    .dependent(serTask)
                    .install()
                val cTask = task(InitCTask())
                    .dependent(moduleTask, bTask)
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

    class InitLogTask : Runnable {
        override fun run() {
            log.d(TAG, "[${Thread.currentThread().name}]init log start")
            Thread.sleep(1000)
            log.d(TAG, "[${Thread.currentThread().name}]init log success")
        }
    }

    class InitServiceTask : Runnable {
        override fun run() {
            log.d(TAG, "[${Thread.currentThread().name}]init service start")
            Thread.sleep(1000)
            log.d(TAG, "[${Thread.currentThread().name}]init service success")
        }
    }

    class InitBTask : Runnable {
        override fun run() {
            log.d(TAG, "[${Thread.currentThread().name}]init B start")
            Thread.sleep(1000)
            log.d(TAG, "[${Thread.currentThread().name}]init B success")
        }
    }

    class InitModuleTask : Runnable {
        override fun run() {
            log.d(TAG, "[${Thread.currentThread().name}]init module start")
            Thread.sleep(2000)
            log.d(TAG, "[${Thread.currentThread().name}]init module success")
        }
    }

    class InitCTask : Runnable {
        override fun run() {
            log.d(TAG, "[${Thread.currentThread().name}]init C start")
            Thread.sleep(1000)
            log.d(TAG, "[${Thread.currentThread().name}]init C success")
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