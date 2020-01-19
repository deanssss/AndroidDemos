package xyz.dean.androiddemos.demos.behavior;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.core.view.ViewCompat;

import org.jetbrains.annotations.NotNull;

public class ViewOffsetBehavior<V extends View> extends CoordinatorLayout.Behavior<V> {
    private ViewOffsetHelper viewOffsetHelper;
    private int tempTopBottomOffset = 0;
    private int tempLeftRightOffset = 0;

    public ViewOffsetBehavior() {
    }

    public ViewOffsetBehavior(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public boolean onLayoutChild(@NotNull CoordinatorLayout parent, @NotNull V child, int layoutDirection) {
        this.layoutChild(parent, child, layoutDirection);
        if (this.viewOffsetHelper == null) {
            this.viewOffsetHelper = new ViewOffsetHelper(child);
        }

        this.viewOffsetHelper.onViewLayout();
        if (this.tempTopBottomOffset != 0) {
            this.viewOffsetHelper.setTopAndBottomOffset(this.tempTopBottomOffset);
            this.tempTopBottomOffset = 0;
        }

        if (this.tempLeftRightOffset != 0) {
            this.viewOffsetHelper.setLeftAndRightOffset(this.tempLeftRightOffset);
            this.tempLeftRightOffset = 0;
        }

        return true;
    }

    private void layoutChild(CoordinatorLayout parent, V child, int layoutDirection) {
        parent.onLayoutChild(child, layoutDirection);
    }

    public boolean setTopAndBottomOffset(int offset) {
        if (this.viewOffsetHelper != null) {
            return this.viewOffsetHelper.setTopAndBottomOffset(offset);
        } else {
            this.tempTopBottomOffset = offset;
            return false;
        }
    }

    public boolean setLeftAndRightOffset(int offset) {
        if (this.viewOffsetHelper != null) {
            return this.viewOffsetHelper.setLeftAndRightOffset(offset);
        } else {
            this.tempLeftRightOffset = offset;
            return false;
        }
    }

    public int getTopAndBottomOffset() {
        return this.viewOffsetHelper != null ? this.viewOffsetHelper.getTopAndBottomOffset() : 0;
    }

    public int getLeftAndRightOffset() {
        return this.viewOffsetHelper != null ? this.viewOffsetHelper.getLeftAndRightOffset() : 0;
    }
}

class ViewOffsetHelper {
    private final View view;
    private int layoutTop;
    private int layoutLeft;
    private int offsetTop;
    private int offsetLeft;

    ViewOffsetHelper(View view) {
        this.view = view;
    }

    void onViewLayout() {
        this.layoutTop = this.view.getTop();
        this.layoutLeft = this.view.getLeft();
        this.updateOffsets();
    }

    private void updateOffsets() {
        ViewCompat.offsetTopAndBottom(this.view, this.offsetTop - (this.view.getTop() - this.layoutTop));
        ViewCompat.offsetLeftAndRight(this.view, this.offsetLeft - (this.view.getLeft() - this.layoutLeft));
    }

    boolean setTopAndBottomOffset(int offset) {
        if (this.offsetTop != offset) {
            this.offsetTop = offset;
            this.updateOffsets();
            return true;
        } else {
            return false;
        }
    }

    boolean setLeftAndRightOffset(int offset) {
        if (this.offsetLeft != offset) {
            this.offsetLeft = offset;
            this.updateOffsets();
            return true;
        } else {
            return false;
        }
    }

    int getTopAndBottomOffset() {
        return this.offsetTop;
    }

    int getLeftAndRightOffset() {
        return this.offsetLeft;
    }

    public int getLayoutTop() {
        return this.layoutTop;
    }

    public int getLayoutLeft() {
        return this.layoutLeft;
    }
}