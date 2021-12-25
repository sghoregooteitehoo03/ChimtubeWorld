package com.sghore.chimtubeworld.ui.custom

import android.content.Context
import android.graphics.Rect
import android.util.TypedValue
import android.view.View
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView


class GridItemDecoration(
    context: Context,
    private val spanCount: Int,
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
        val position = parent.getChildAdapterPosition(view)
        val itemCount = state.itemCount // 전체 아이템 수

        //상하 설정
        if (position == 0 || position == 1) {
            // 첫번 째 줄 아이템
            outRect.bottom = fullSize
        } else {
            outRect.bottom = fullSize
        }

        // spanIndex = 0 -> 왼쪽
        // spanIndex = 1 -> 오른쪽
        val lp = view.layoutParams as GridLayoutManager.LayoutParams
        val spanIndex = lp.spanIndex

        if (spanCount == 2) {
            if (spanIndex == 0) {
                //왼쪽 아이템
                outRect.right = halfSize
            } else if (spanIndex == 1) {
                //오른쪽 아이템
                outRect.left = halfSize
            }
        } else if (spanCount >= 3) {
            when (spanIndex) {
                0 -> {
                    //왼쪽 아이템
                    outRect.right = halfSize
                }
                spanCount - 1 -> { // 오른쪽 아이템
                    outRect.left = halfSize
                }
                else -> { //중간 아이템
                    outRect.right = halfSize
                    outRect.left = halfSize
                }
            }
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