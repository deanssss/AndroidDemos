package xyz.dean.androiddemos.demos.rating_bar

import android.os.Bundle
import androidx.databinding.DataBindingUtil
import xyz.dean.androiddemos.BaseActivity
import xyz.dean.androiddemos.DemoItem
import xyz.dean.androiddemos.R
import xyz.dean.androiddemos.common.log
import xyz.dean.androiddemos.databinding.ActivityCusRatingBarDemoBinding

class CusRatingBarDemoActivity : BaseActivity() {
    private lateinit var binding: ActivityCusRatingBarDemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_cus_rating_bar_demo)

        binding.rating.onRatingBarChanged = {
            log.d("Dean", "$it")
        }
    }

    companion object {
        val demoItem = DemoItem("cus-rating",
            R.string.cus_rating_bar_demo_name,
            R.string.cus_rating_bar_describe,
            CusRatingBarDemoActivity::class.java, R.mipmap.img_practice)
    }
}