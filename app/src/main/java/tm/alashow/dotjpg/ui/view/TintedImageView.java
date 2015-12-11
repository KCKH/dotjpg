/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.ui.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.AttributeSet;
import android.widget.ImageView;

import tm.alashow.dotjpg.R;

/**
 * Tintes drawable with given color
 */
public class TintedImageView extends ImageView {
    private int mTintColor = Color.parseColor("#212121");

    public TintedImageView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs, 0);
    }

    private void init(AttributeSet attrs, int defStyle) {
        // Load attributes
        final TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.TintedImageView, defStyle, 0);

        mTintColor = a.getColor(R.styleable.TintedImageView_tintColor, mTintColor);
        a.recycle();

        invalidateTint();
    }

    private void invalidateTint() {
        Drawable tinted = DrawableCompat.wrap(getDrawable());
        DrawableCompat.setTint(tinted, mTintColor);
        setImageDrawable(tinted);
        invalidate();
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
    }

    /**
     * Gets the example color attribute value.
     *
     * @return The example color attribute value.
     */
    public int getTintColor() {
        return mTintColor;
    }

    /**
     * Sets the view's example color attribute value. In the example view, this color
     * is the font color.
     *
     * @param tintColor The example color attribute value to use.
     */
    public void setTintColor(int tintColor) {
        mTintColor = tintColor;
        invalidateTint();
    }
}
