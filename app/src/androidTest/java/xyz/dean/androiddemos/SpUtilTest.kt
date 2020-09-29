package xyz.dean.androiddemos

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.Assert
import org.junit.Test
import org.junit.runner.RunWith
import xyz.dean.androiddemos.utils.prefrences.*

@RunWith(AndroidJUnit4::class)
class SpUtilTest {
    @Test
    fun useAppContext() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()

        val spModel = object : PrefModel("test", Context.MODE_PRIVATE, { appContext }) {
            @Key("student") var name1: String by stringFiled(default = "zhangsan")
            var name2: String? by nullableStringFiled()
            var name3: String? by nullableStringFiled(default = "wangwu")

            var age1: Int by intFiled(default = 23)
            var age2: Int? by nullableIntFiled()
            var age3: Int? by nullableIntFiled(default = 24)
        }

        Assert.assertEquals(spModel.name1, "zhangsan")
        Assert.assertEquals(spModel.name2, null)
        Assert.assertEquals(spModel.name3, "wangwu")

        Assert.assertEquals(spModel.age1, 23)
        Assert.assertEquals(spModel.age2, null)
        Assert.assertEquals(spModel.age3, 24)

        // write
        spModel.name1 = "zhangsan1"
        Assert.assertEquals(spModel.name1, "zhangsan1")
        spModel.name2 = "lisi"
        Assert.assertEquals(spModel.name2, "lisi")
        spModel.name3 = "wangwu1"
        Assert.assertEquals(spModel.name3, "wangwu1")

        spModel.age1 = 24
        Assert.assertEquals(spModel.age1, 24)
        spModel.age2 = 20
        Assert.assertEquals(spModel.age2, 20)
        spModel.age3 = 25
        Assert.assertEquals(spModel.age3, 25)

        // remove
        spModel.remove(spModel::name1)
        Assert.assertEquals(spModel.name1, "zhangsan")
        spModel.name2 = null
        Assert.assertTrue(spModel.name2 == null)
        spModel.name3 = null
        Assert.assertEquals(spModel.name3, "wangwu")

        spModel.remove(spModel::age1)
        Assert.assertEquals(spModel.age1, 23)
        spModel.age2 = null
        Assert.assertTrue(spModel.age2 == null)
        spModel.age3 = null
        Assert.assertEquals(spModel.age3, 24)

        // clear
        spModel.clear()
    }
}