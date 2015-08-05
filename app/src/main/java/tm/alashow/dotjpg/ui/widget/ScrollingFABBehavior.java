/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.ui.widget;

import android.content.Context;
import android.support.design.widget.AppBarLayout;
import android.support.design.widget.CoordinatorLayout;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.view.ViewCompat;
import android.util.AttributeSet;
import android.view.View;

import tm.alashow.dotjpg.util.U;

public class ScrollingFABBehavior extends FloatingActionButton.Behavior {
    private int toolbarHeight;

    public ScrollingFABBehavior(Context context, AttributeSet attrs) {
        super();
        this.toolbarHeight = U.getToolbarHeight(context);
    }

    @Override
    public boolean layoutDependsOn(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
        return super.layoutDependsOn(parent, fab, dependency) || (dependency instanceof AppBarLayout);
    }

    @Override
    public boolean onDependentViewChanged(CoordinatorLayout parent, FloatingActionButton fab, View dependency) {
        boolean returnValue = super.onDependentViewChanged(parent, fab, dependency);
        if (dependency instanceof AppBarLayout) {
            CoordinatorLayout.LayoutParams lp = (CoordinatorLayout.LayoutParams) fab.getLayoutParams();
            int fabBottomMargin = lp.bottomMargin;
            int distanceToScroll = fab.getHeight() + fabBottomMargin;
            float ratio = ViewCompat.getY(dependency) / (float) toolbarHeight;
            ViewCompat.setTranslationY(fab, - distanceToScroll * ratio);
        }
        return returnValue;
    }
}