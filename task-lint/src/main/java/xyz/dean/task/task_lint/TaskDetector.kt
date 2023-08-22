package xyz.dean.task.task_lint

import com.android.tools.lint.detector.api.*

class TaskDetector : Detector(), Detector.UastScanner {
    override fun getApplicableAsmNodeTypes(): IntArray? {
        return super.getApplicableAsmNodeTypes()
    }

    companion object {
        val ISSUE = Issue.create("TaskNotUseWarning",
            "任务没有使用",
            "定义了一个task，但是没有调用install来使用它",
            Category.USABILITY,
            5,
            Severity.WARNING,
            Implementation(TaskDetector::class.java, Scope.JAVA_FILE_SCOPE)
        )
    }
}