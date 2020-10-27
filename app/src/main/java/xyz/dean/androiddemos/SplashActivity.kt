package xyz.dean.androiddemos

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import xyz.dean.androiddemos.common.log
import xyz.dean.androiddemos.model.MainPrefModel

class SplashActivity : BaseActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val firstLoadKey = MainPrefModel.last
        log.d(tag, "first load: $firstLoadKey")
        Handler().postDelayed({
            val clazz = demos[firstLoadKey]?.clazz ?: MainActivity::class.java
            startActivity(Intent(this, clazz))
        }, DELAY_TIME)
    }

    companion object {
        const val DELAY_TIME = 1500L
    }
}