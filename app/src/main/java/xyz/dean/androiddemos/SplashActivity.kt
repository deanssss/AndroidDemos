package xyz.dean.androiddemos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import xyz.dean.androiddemos.common.SP_KEY_FIRST_LOAD
import xyz.dean.androiddemos.common.SP_NAME_MAIN
import xyz.dean.androiddemos.common.log

class SplashActivity : BaseActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_splash)

        val spf = getSharedPreferences(SP_NAME_MAIN, Context.MODE_PRIVATE)
        val firstLoadkey = spf.getString(SP_KEY_FIRST_LOAD, "")!!
        log.d(tag, "first load: $firstLoadkey")
        val clazz = demos[firstLoadkey]?.clazz ?: MainActivity::class.java
        val intent = Intent(this, clazz)
        Handler().postDelayed({
            startActivity(intent)
        }, DELAY_TIME)
    }

    companion object {
        const val DELAY_TIME = 1500L
    }
}