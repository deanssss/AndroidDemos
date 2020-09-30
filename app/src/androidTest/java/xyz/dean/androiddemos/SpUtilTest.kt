package xyz.dean.androiddemos

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import xyz.dean.androiddemos.utils.pref_util.*

@RunWith(AndroidJUnit4::class)
class SpUtilTest {
    class TestModel(context: Context) : PrefModel("test", Context.MODE_PRIVATE, { context }) {
        var string1: String by stringField(default = "zhangsan")
        var string2: String? by nullableStringField()
        var string3: String? by nullableStringField(default = "wangwu")

        var int1: Int by intField(default = 23)
        var int2: Int? by nullableIntField()
        var int3: Int? by nullableIntField(default = 24)

        var strSet1: Set<String> by stringSetField(default = emptySet())
        var strSet2: Set<String>? by nullableStringSetField()
        var strSet3: Set<String>? by nullableStringSetField(default = setOf("default"))

        var long1: Long by longField(default = 0L)
        var long2: Long? by nullableLongField()
        var long3: Long? by nullableLongField(default = 0L)

        var float1: Float by floatField(default = 0f)
        var float2: Float? by nullableFloatField()
        var float3: Float? by nullableFloatField(default = 0f)

        var boolean1: Boolean by booleanField(default = false)
        var boolean2: Boolean? by nullableBooleanField()
        var boolean3: Boolean? by nullableBooleanField(default = false)

        override fun setAlias() {
            ::string1 alias "student"
        }
    }

    lateinit var spModel: TestModel

    @Before
    fun prepare() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        spModel = TestModel(appContext)
    }

    @Test
    fun useAppContext() {
        Assert.assertEquals(spModel.string1, "zhangsan")
        Assert.assertEquals(spModel.string2, null)
        Assert.assertEquals(spModel.string3, "wangwu")

        Assert.assertEquals(spModel.int1, 23)
        Assert.assertEquals(spModel.int2, null)
        Assert.assertEquals(spModel.int3, 24)

        Assert.assertTrue(spModel.strSet1.isEmpty())
        Assert.assertTrue(spModel.strSet2 == null)
        Assert.assertTrue(spModel.strSet3?.first() == "default")

        // write
        spModel.string1 = "zhangsan1"
        Assert.assertEquals(spModel.string1, "zhangsan1")
        spModel.string2 = "lisi"
        Assert.assertEquals(spModel.string2, "lisi")
        spModel.string3 = "wangwu1"
        Assert.assertEquals(spModel.string3, "wangwu1")

        spModel.int1 = 24
        Assert.assertEquals(spModel.int1, 24)
        spModel.int2 = 20
        Assert.assertEquals(spModel.int2, 20)
        spModel.int3 = 25
        Assert.assertEquals(spModel.int3, 25)

        // remove
        spModel.remove(spModel::string1)
        Assert.assertEquals(spModel.string1, "zhangsan")
        spModel.string2 = null
        Assert.assertTrue(spModel.string2 == null)
        spModel.string3 = null
        Assert.assertEquals(spModel.string3, "wangwu")

        spModel.remove(spModel::int1)
        Assert.assertEquals(spModel.int1, 23)
        spModel.int2 = null
        Assert.assertTrue(spModel.int2 == null)
        spModel.int3 = null
        Assert.assertEquals(spModel.int3, 24)

        // clear
        spModel.clear()
    }

    @After
    fun clear() {

    }
}