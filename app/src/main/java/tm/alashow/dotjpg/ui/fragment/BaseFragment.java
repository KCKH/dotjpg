/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.ui.fragment;

import android.os.Bundle;
import android.support.annotation.LayoutRes;
import android.support.annotation.StringRes;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import butterknife.ButterKnife;
import tm.alashow.dotjpg.R;
import tm.alashow.dotjpg.ui.activity.BaseActivity;

/**
 * Created by alashov on 31/07/15.
 */
public abstract class BaseFragment extends Fragment {

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(getLayout(), container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    public BaseActivity getBaseActivity() {
        return ((BaseActivity) super.getActivity());
    }

    protected
    @StringRes
    int getTitle() {
        return R.string.app_name;
    }

    protected abstract
    @LayoutRes
    int getLayout();
}
