package xyz.dean.androiddemos.demos.dcalendar

import android.content.Context
import android.util.AttributeSet
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.ListAdapter

class AdaptiveGridView constructor(
    context: Context, attrs: AttributeSet? = null
) : GridView(context, attrs) {
    
    override fun setAdapter(adapter: ListAdapter?) {
        val newAdapter = adapter?.let { AdaptiveAdapter(it)  }
        super.setAdapter(newAdapter)
    }

    override fun getAdapter(): ListAdapter? {
        val adaptiveAdapter = super.getAdapter()
        return if (adaptiveAdapter is AdaptiveAdapter) adaptiveAdapter.adapter 
        else adaptiveAdapter
    }

    inner class AdaptiveAdapter(val adapter: ListAdapter): ListAdapter by adapter {
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            val view = adapter.getView(position, convertView, parent)
            val height = ((measuredHeight - verticalSpacing * 4 - 50) / 5f).toInt()
            val width = ((measuredWidth - horizontalSpacing * 6) / 7f).toInt()

            if (width != 0 && height != 0) {
                val lp = view.layoutParams
                lp.width = width
                lp.height = height
            }
            return view
        }
    }
}