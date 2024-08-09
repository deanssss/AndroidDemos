package xyz.dean.util.pref

import android.content.Context
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.google.gson.Gson
import org.junit.After
import org.junit.Assert
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import xyz.dean.util.fromJson
import xyz.dean.util.toJson

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

        var obj1: Dt by objectField(
            default = Dt("default", 0),
            serializer = Dt.Companion::serializer,
            parser = Dt.Companion::parser)
        var obj2: Dt? by nullableObjectField(
            serializer = Dt.Companion::serializer,
            parser = Dt.Companion::parser)
        var obj3: Dt? by nullableObjectField(
            default = Dt("default-1", 1),
            serializer = Dt.Companion::serializer,
            parser = Dt.Companion::parser)
    }

    lateinit var spModel: TestModel

    @Before
    fun prepare() {
        val appContext = ApplicationProvider.getApplicationContext<Context>()
        spModel = TestModel(appContext)
    }

    @Test
    fun useAppContext() {
        spModel.run {
            Assert.assertEquals(string1, "zhangsan")
            Assert.assertEquals(string2, null)
            Assert.assertEquals(string3, "wangwu")

            Assert.assertEquals(int1, 23)
            Assert.assertEquals(int2, null)
            Assert.assertEquals(int3, 24)

            Assert.assertTrue(strSet1.isEmpty())
            Assert.assertTrue(strSet2 == null)
            Assert.assertTrue(strSet3?.first() == "default")

            Assert.assertEquals(long1, 0L)
            Assert.assertEquals(long2, null)
            Assert.assertEquals(long3, 0L)

            Assert.assertEquals(float1, 0f)
            Assert.assertEquals(float2, null)
            Assert.assertEquals(float3, 0f)

            Assert.assertEquals(boolean1, false)
            Assert.assertEquals(boolean2, null)
            Assert.assertEquals(boolean3, false)

            Assert.assertEquals(obj1.str, "default")
            Assert.assertEquals(obj1.int, 0)
            Assert.assertEquals(obj2, null)
            Assert.assertEquals(obj3?.str, "default-1")
            Assert.assertEquals(obj3?.int, 1)
        }

        // write
        spModel.run {
            string1 = "zhangsan1"
            Assert.assertEquals(string1, "zhangsan1")
            string2 = "lisi"
            Assert.assertEquals(string2, "lisi")
            string3 = "wangwu1"
            Assert.assertEquals(string3, "wangwu1")
        }

        spModel.run {
            int1 = 24
            Assert.assertEquals(int1, 24)
            int2 = 20
            Assert.assertEquals(int2, 20)
            int3 = 25
            Assert.assertEquals(int3, 25)
        }

        spModel.run {
            strSet1 = setOf("one", "two", "three")
            Assert.assertTrue(strSet1.size == 3)
            strSet2 = setOf("one", "two", "three")
            Assert.assertTrue(strSet2?.size == 3)
            strSet3 = setOf("one", "two", "three")
            Assert.assertTrue(strSet3?.size == 3)
        }

        spModel.run {
            long1 = 9999999999L
            Assert.assertEquals(long1, 9999999999L)
            long2 = 9999999999L
            Assert.assertEquals(long2, 9999999999L)
            long3 = 9999999999L
            Assert.assertEquals(long3, 9999999999L)
        }

        spModel.run {
            float1 = 3.1415926f
            Assert.assertEquals(float1, 3.1415926f)
            float2 = 3.1415926f
            Assert.assertEquals(float2, 3.1415926f)
            float3 = 3.1415926f
            Assert.assertEquals(float3, 3.1415926f)
        }

        spModel.run {
            boolean1 = true
            Assert.assertEquals(boolean1, true)
            boolean2 = true
            Assert.assertEquals(boolean2, true)
            boolean3 = true
            Assert.assertEquals(boolean3, true)
        }

        spModel.run {
            val dt = Dt("test", 100)
            obj1 = dt
            Assert.assertEquals(obj1.str, dt.str)
            Assert.assertEquals(obj1.int, dt.int)
            obj2 = dt
            Assert.assertEquals(obj2?.str, dt.str)
            Assert.assertEquals(obj2?.int, dt.int)
            obj3 = dt
            Assert.assertEquals(obj3?.str, dt.str)
            Assert.assertEquals(obj3?.int, dt.int)
        }

        // remove
        spModel.run {
            remove(::string1)
            Assert.assertEquals(string1, "zhangsan")
            string2 = null
             Assert.assertEquals(string2, null)
            string3 = null
            Assert.assertEquals(string3, "wangwu")
        }

        spModel.run {
            remove(::int1)
            Assert.assertEquals(int1, 23)
            int2 = null
            Assert.assertEquals(int2, null)
            int3 = null
            Assert.assertEquals(int3, 24)
        }

        spModel.run {
            remove(::strSet1)
            Assert.assertTrue(strSet1.isEmpty())
            strSet2 = null
            Assert.assertEquals(strSet2, null)
            strSet3 = null
            Assert.assertTrue(strSet3?.first() == "default")
        }

        spModel.run {
            remove(::long1)
            Assert.assertEquals(long1, 0L)
            long2 = null
            Assert.assertEquals(long2, null)
            long3 = null
            Assert.assertEquals(long3, 0L)
        }

        spModel.run {
            remove(::float1)
            Assert.assertEquals(float1, 0f)
            float2 = null
            Assert.assertEquals(float2, null)
            float3 = null
            Assert.assertEquals(float3, 0f)
        }

        spModel.run {
            remove(::boolean1)
            Assert.assertEquals(boolean1, false)
            boolean2 = null
            Assert.assertEquals(boolean2, null)
            boolean3 = null
            Assert.assertEquals(boolean3, false)
        }

        spModel.run {
            remove(::obj1)
            Assert.assertEquals(obj1.str, "default")
            Assert.assertEquals(obj1.int, 0)
            obj2 = null
            Assert.assertEquals(obj2, null)
            obj3 = null
            Assert.assertEquals(obj3?.str, "default-1")
            Assert.assertEquals(obj3?.int, 1)
        }
    }

    class Dt(
        val str: String,
        val int: Int
    ) {
        companion object {
            fun serializer(dt: Dt): String = dt.toJson()
            fun parser(json: String): Dt = Gson().fromJson(json)
        }
    }

    @After
    fun clear() {
        // clear
        spModel.clear()
    }
}