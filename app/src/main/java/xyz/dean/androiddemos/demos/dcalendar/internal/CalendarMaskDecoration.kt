package xyz.dean.androiddemos.demos.dcalendar.internal

import android.graphics.Canvas
import androidx.core.view.children
import androidx.recyclerview.widget.RecyclerView

class CalendarMaskDecoration : RecyclerView.ItemDecoration() {
    private var gridWidth: Int = 0
    private var gridHeight: Int = 0
    private var gridSpacing: Float = 1f

    private val masks = mutableListOf<MaskItemInfo>()

    fun setGridDimension(width: Int, height: Int, spacing: Float) {
        gridWidth = width
        gridHeight = height
        gridSpacing = spacing
    }

    fun addMask(maskItem: MaskItemInfo) {
        if (!masks.contains(maskItem)) {
            masks.add(maskItem)
        }
    }

    fun removeMask(maskItem: MaskItemInfo) {
        masks.remove(maskItem)
    }

    fun clearAll() {
        masks.clear()
    }

    override fun onDrawOver(c: Canvas, parent: RecyclerView, state: RecyclerView.State) {
        for (child in parent.children) {
            val childAdapterPos = parent.getChildAdapterPosition(child)
            val maskItem = masks.find { it.adapterPos == childAdapterPos }
                ?: continue
            val offsetPX = (gridWidth + gridSpacing) * maskItem.spanIndexOffset
            val left = child.left + offsetPX
            val right = child.left + (gridWidth + gridSpacing) * maskItem.spanSize + offsetPX
            maskItem.drawMask(maskItem, c, left, child.top.toFloat(), right, child.bottom.toFloat())
        }
    }
}

data class MaskItemInfo(
    val adapterPos: Int,
    val spanIndexOffset: Int,
    val spanSize: Int,
    val drawMask: (maskInfo: MaskItemInfo, canvas: Canvas, l: Float, t: Float, r: Float, b: Float) -> Unit
)