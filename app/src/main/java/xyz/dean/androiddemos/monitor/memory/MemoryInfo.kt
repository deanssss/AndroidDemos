package xyz.dean.androiddemos.monitor.memory

data class MemoryInfo(
    val javaHeapMaxMem: Long,
    val javaHeapUsedMem: Long,
    val javaUsedRadio: Long,
    val javaPss: Int,
    val nativePss: Int,
    val maxNativeMemory: Long,
    val totalPss: Int,
    val rss: Long,
    val vss: Long,
    val threadsCount: Long
) {
    override fun toString(): String {
        return "MemoryInfo(" +
                "javaHeapMaxMem=${javaHeapMaxMem}MB" +
                ", javaHeapUsedMem=${javaHeapUsedMem}MB" +
                ", javaUsedRadio=${javaUsedRadio}%" +
                ", javaPss=${javaPss}MB" +
                ", nativePss=${nativePss}MB" +
                ", maxNativeMemory=${maxNativeMemory}MB" +
                ", totalPss=${totalPss}MB" +
                ", rss=${rss}MB" +
                ", vss=${vss}MB" +
                ", threadsCount=$threadsCount" +
                ")"
    }
}