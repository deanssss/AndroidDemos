package xyz.dean.androiddemos

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import xyz.dean.androiddemos.common.SP_KEY_FIRST_LOAD
import xyz.dean.androiddemos.common.SP_NAME_MAIN
import xyz.dean.androiddemos.common.log

abstract class BaseActivity : AppCompatActivity() {
    open val tag: String = this.javaClass.simpleName

    /**
     * Entrance activity of the demo need override this method
     * and return a DemoItem object for registering the demo.
     * Then add the item into the global demos map.
     */
    open fun getDemoItem(): DemoItem? = null

    override fun onCreate(savedInstanceState: Bundle?, persistentState: PersistableBundle?) {
        log.d(tag, "=== onCreate ===")
        super.onCreate(savedInstanceState, persistentState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
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
        if (getDemoItem() == null) {
            menu.removeItem(R.id.menu_first_load)
            menu.removeItem(R.id.menu_clear_first)
        }
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.menu_go_home -> {
                val intent = MainActivity
                    .createIntent(this)
                    .addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK or Intent.FLAG_ACTIVITY_NEW_TASK)
                startActivity(intent)
            }
            R.id.menu_first_load -> {
                val demoItem = getDemoItem()
                if (demoItem != null) {
                    val spf = getSharedPreferences(SP_NAME_MAIN, Context.MODE_PRIVATE)
                    spf.edit().putString(SP_KEY_FIRST_LOAD, demoItem.key)
                        .apply()
                    Toast.makeText(this, getString(R.string.msg_set_first_load_success), Toast.LENGTH_SHORT).show()
                }
            }
            R.id.menu_clear_first -> {
                val spf = getSharedPreferences(SP_NAME_MAIN, Context.MODE_PRIVATE)
                spf.edit().remove(SP_KEY_FIRST_LOAD).apply()
                Toast.makeText(this, getString(R.string.msg_set_first_load_success), Toast.LENGTH_SHORT).show()
            }
        }
        return super.onOptionsItemSelected(item)
    }
}