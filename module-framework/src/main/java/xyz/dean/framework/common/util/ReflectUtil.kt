package xyz.dean.framework.common.util

object ReflectUtil {
    @JvmStatic
    fun getDefaultValue(paramClass: Class<*>): Any? {
        return when (paramClass) {
            Int::class.java -> 0
            Byte::class.java -> 0.toByte()
            Short::class.java -> 0.toShort()
            Long::class.java -> 0L
            Float::class.java -> 0.0f
            Double::class.java -> 0.0
            Char::class.java -> '\u0000'
            Boolean::class.java -> false
            else -> null
        }
    }
}