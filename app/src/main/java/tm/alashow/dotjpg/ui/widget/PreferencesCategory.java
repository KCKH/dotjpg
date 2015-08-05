/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.ui.widget;

import android.content.Context;
import android.preference.PreferenceCategory;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import tm.alashow.dotjpg.R;


/**
 * Created by alashov on 18/01/15.
 */
public class PreferencesCategory extends PreferenceCategory {
    public PreferencesCategory(Context context) {
        super(context);
    }

    public PreferencesCategory(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public PreferencesCategory(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    @Override
    protected View onCreateView(ViewGroup parent) {
        TextView categoryTitle = (TextView) super.onCreateView(parent);
        categoryTitle.setTextColor(categoryTitle.getContext().getResources().getColor(R.color.primary));
        return categoryTitle;
    }
}
