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
        val clazz = demos[firstLoadKey]?.clazz ?: MainActivity::class.java
        val intent = Intent(this, clazz)
        Handler().postDelayed({
            startActivity(intent)
        }, DELAY_TIME)
    }

    companion object {
        const val DELAY_TIME = 1500L
    }
}