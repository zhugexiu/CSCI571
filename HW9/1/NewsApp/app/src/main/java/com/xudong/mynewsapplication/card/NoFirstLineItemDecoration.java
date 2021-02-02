package com.xudong.mynewsapplication.card;

import android.graphics.Canvas;
import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.view.View;

import androidx.recyclerview.widget.RecyclerView;

public class NoFirstLineItemDecoration extends RecyclerView.ItemDecoration {
    private Drawable mDivider;

    public NoFirstLineItemDecoration(Drawable divider) {
        mDivider = divider;
    }

    @Override
    public void getItemOffsets(Rect outRect, View view, RecyclerView parent, RecyclerView.State state) {
        int pos = parent.getChildAdapterPosition(view);
        if (pos != 0 &&
                pos != parent.getLayoutManager().getItemCount() - 1) {
            outRect.bottom = mDivider.getIntrinsicHeight();
        }
    }

    @Override
    public void onDraw(Canvas canvas, RecyclerView parent, RecyclerView.State state) {
        int dividerLeft = parent.getPaddingLeft();
        int dividerRight = parent.getWidth() - parent.getPaddingRight();

        int childCount = parent.getChildCount();
        for (int i = 0; i < childCount; i++) {
            View child = parent.getChildAt(i);

            int pos = parent.getChildAdapterPosition(child);
            if (pos != 0 &&
                    pos != parent.getLayoutManager().getItemCount() - 1) {

                RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

                int dividerTop = child.getBottom() + params.bottomMargin;
                int dividerBottom = dividerTop + mDivider.getIntrinsicHeight();
                mDivider.setBounds(dividerLeft, dividerTop, dividerRight, dividerBottom);
                mDivider.draw(canvas);
            }
        }
    }
}
