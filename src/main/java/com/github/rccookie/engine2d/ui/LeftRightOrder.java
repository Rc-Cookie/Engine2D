package com.github.rccookie.engine2d.ui;

import com.github.rccookie.engine2d.UIObject;
import org.jetbrains.annotations.Nullable;

public class LeftRightOrder extends Structure {

    @Nullable
    private UIObject left, right;

    public LeftRightOrder(@Nullable UIObject parent, @Nullable UIObject left, @Nullable UIObject right) {
        super(parent);
        setLeft(left);
        setRight(right);
    }

    // Don't mark these methods with @Nullable to avoid "forcing" null-check
    @SuppressWarnings("NullableProblems")
    public UIObject getLeft() {
        return left;
    }

    @SuppressWarnings("NullableProblems")
    public UIObject getRight() {
        return right;
    }

    public void setLeft(@Nullable UIObject left) {
        if(this.left == left) return;
        if(this.left != null)
            this.left.setParent(null);
        this.left = left;
        if(left != null)
            left.setParent(this);
    }

    public void setRight(@Nullable UIObject right) {
        if(this.right == right) return;
        if(this.right != null)
            this.right.setParent(null);
        this.right = right;
        if(right != null)
            right.setParent(this);
    }

    @Override
    protected void updateStructure() {
        if(left != null)
            left.relativeLoc.x = -1;
        if(right != null)
            right.relativeLoc.x = 1;
    }
}
