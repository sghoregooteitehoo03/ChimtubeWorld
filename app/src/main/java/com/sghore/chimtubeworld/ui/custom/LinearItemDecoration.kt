package com.sghore.chimtubeworld.ui.custom

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.RecyclerView

class LinearItemDecoration(
    context: Context,
    marginSize: Int
) : RecyclerView.ItemDecoration() {
    private val fullSize = dpToPx(context, marginSize)
    private val halfSize = dpToPx(context, marginSize / 2)

    override fun getItemOffsets(
        outRect: Rect,
        view: View,
        parent: RecyclerView,
        state: RecyclerView.State
    ) {
        super.getItemOffsets(outRect, view, parent, state)
        val itemCount = state.itemCount // 전체 아이템 수

        // 리스트에 맨 앞에만 margin 값 부여
        if (parent.getChildAdapterPosition(view) == 0) {
            outRect.left += fullSize
        }
    }

    // dp -> pixel 단위로 변경
    private fun dpToPx(context: Context, dp: Int): Int {
        return TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            dp.toFloat(),
            context.resources.displayMetrics
        ).toInt()
    }
}