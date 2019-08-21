package xyz.dean.androiddemos

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import androidx.appcompat.app.AppCompatActivity
import xyz.dean.androiddemos.utils.log

abstract class BaseActivity : AppCompatActivity() {
    open val tag: String = this.javaClass.simpleName

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        log.d(tag, "=== onCreate ===")
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onSaveInstanceState(outState: Bundle?) {
        log.d(tag, "=== onSaveInstanceState ===")
        super.onSaveInstanceState(outState)
    }

    override fun onRestart() {
        log.d(tag, "=== onRestart ===")
        super.onRestart()
    }

    override fun onStart() {
        log.d(tag, "=== onStart === ")
        super.onStart()
    }

    override fun onResume() {
        log.d(tag, "=== onResume ===")
        super.onResume()
    }

    override fun onPause() {
        log.d(tag, "=== onPause ===")
        super.onPause()
    }

    override fun onStop() {
        log.d(tag, "=== onStop ===")
        super.onStop()
    }

    override fun onDestroy() {
        log.d(tag, "=== onDestroy ===")
        super.onDestroy()
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_base, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_go_home -> {
                startActivity(Intent(this, MainActivity::class.java))
                finish()
            }
            R.id.menu_first_load -> {
                log.d(tag, "设置为首加载")
            }
            R.id.menu_clear_first -> {
                log.d(tag, "清除首加载")
            }
        }
        return super.onOptionsItemSelected(item)
    }
}