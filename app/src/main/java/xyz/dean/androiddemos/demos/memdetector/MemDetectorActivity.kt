package xyz.dean.androiddemos.demos.memdetector

import android.os.Bundle
import android.widget.TextView
import xyz.dean.androiddemos.BaseActivity
import xyz.dean.androiddemos.DemoItem
import xyz.dean.androiddemos.R
import xyz.dean.androiddemos.monitor.memory.MemoryDetector

class MemDetectorActivity : BaseActivity() {
    override fun getDemoItem(): DemoItem = demoItem
    private lateinit var detector: MemoryDetector

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_mem_detector)
        val tv = findViewById<TextView>(R.id.mem_info_tv)
        detector = MemoryDetector(application, 1000)
        detector.registerMemoryInfoCallback {
            tv.post { tv.text = it.toString() }
        }
    }

    override fun onStart() {
        super.onStart()
        detector.prepare()
    }

    override fun onResume() {
        super.onResume()
        detector.start()
    }

    override fun onPause() {
        super.onPause()
        detector.pause()
    }

    override fun onStop() {
        super.onStop()
        detector.stop()
    }

    companion object {
        val demoItem = DemoItem("memdetector",
            R.string.mem_detector_demo_name,
            R.string.mem_detector_describe_text,
            MemDetectorActivity::class.java, R.mipmap.img_practice)
    }
}
