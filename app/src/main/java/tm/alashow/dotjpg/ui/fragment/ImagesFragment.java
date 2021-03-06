/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.ui.fragment;


import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.LinearLayoutManager;
import android.view.View;
import android.widget.Button;

import com.loopj.android.http.JsonHttpResponseHandler;
import com.loopj.android.http.RequestParams;
import com.pnikosis.materialishprogress.ProgressWheel;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;

import butterknife.Bind;
import cz.msebera.android.httpclient.Header;
import tm.alashow.dotjpg.Config;
import tm.alashow.dotjpg.R;
import tm.alashow.dotjpg.interfaces.OnTitleClickedListener;
import tm.alashow.dotjpg.model.Image;
import tm.alashow.dotjpg.ui.adapter.ImagesAdapter;
import tm.alashow.dotjpg.ui.view.EndlessRecyclerView;
import tm.alashow.dotjpg.util.ApiClient;
import tm.alashow.dotjpg.util.U;

/**
 * Created by alashov on 31/07/15.
 */
public class ImagesFragment extends BaseFragment implements OnTitleClickedListener, EndlessRecyclerView.Pager {

    private String mImagesType;
    private String mGalleryId;

    private Handler mHandler;

    private ArrayList<Image> images = new ArrayList<>();
    private ImagesAdapter imagesAdapter;

    private boolean loadingMore = false;
    private boolean stopLoadMore = false;
    private int pagination = 0;

    public static final int TYPE_NEW = 0;
    public static final int TYPE_REFRESH = 1;
    public static final int TYPE_PAGINATION = 2;

    @Bind(R.id.refresh) SwipeRefreshLayout refreshLayout;
    @Bind(R.id.recyclerView) EndlessRecyclerView recyclerView;
    @Bind(R.id.progress) ProgressWheel progressBar;
    @Bind(R.id.retry) Button retryView;

    public static ImagesFragment createInstance(String imagesType) {
        return createInstance(imagesType, null);
    }

    public static ImagesFragment createInstance(String imagesType, String galleryId) {
        ImagesFragment imagesFragment = new ImagesFragment();
        Bundle bundle = new Bundle();
        bundle.putString(Config.EXTRA_IMAGES_TYPE, imagesType);
        if (galleryId != null) {
            bundle.putString(Config.EXTRA_GALLERY_ID, galleryId);
        }
        imagesFragment.setArguments(bundle);
        return imagesFragment;
    }

    public ImagesFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        mHandler = new Handler();

        if (getArguments() != null) {
            this.mImagesType = getArguments().getString(Config.EXTRA_IMAGES_TYPE, Config.API_ACTION_GET_SPECIAL);
            if (mImagesType.equals(Config.API_ACTION_GET_GALLERY)) {
                this.mGalleryId = getArguments().getString(Config.EXTRA_GALLERY_ID, null);
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        initViews();

        loadImages(TYPE_NEW, new OnImagesLoadedListener(TYPE_NEW) {
        });

        getBaseActivity().onTitleClickedListeners.add(this);
    }

    private void initViews() {
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(linearLayoutManager);

        refreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                pagination = 0;
                stopLoadMore = false;
                loadImages(TYPE_REFRESH, new OnImagesLoadedListener(TYPE_REFRESH) {
                });
            }
        });

        U.setColorScheme(refreshLayout);

        retryView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadImages(0, new OnImagesLoadedListener(0) {
                });
            }
        });
        recyclerView.setProgressView(R.layout.view_list_loading);
        recyclerView.setPager(this);
    }

    private void setImagesAdapter() {
        imagesAdapter = new ImagesAdapter(getActivity(), images);
        recyclerView.setAdapter(imagesAdapter);
    }

    private void loadImages(final int type, final OnImagesLoadedListener onImagesLoadedListener) {
        U.hideView(retryView);

        RequestParams requestParams = new RequestParams();
        requestParams.put(Config.API_CONTROLLER_PARAM, Config.API_CONTROLLER_IMAGE);
        requestParams.put(Config.API_ACTION_PARAM, mImagesType);
        requestParams.put(Config.API_SESSION_ID_PARAM, getBaseActivity().preferencesManager.getSessionId());

        requestParams.put(Config.API_PAGE_PARAM, pagination);

        if (mImagesType.equals(Config.API_ACTION_GET_ALL_MY)) {
            requestParams.put(Config.API_SESSION_ID_PARAM, getBaseActivity().preferencesManager.getSessionId());
        } else if (mImagesType.equals(Config.API_ACTION_GET_GALLERY) && mGalleryId != null) {
            requestParams.put(Config.API_GALLERY_ID_PARAM, mGalleryId);
        }

        ApiClient.get(Config.API, requestParams, new JsonHttpResponseHandler() {

            @Override
            public void onStart() {
                switch (type) {
                    case TYPE_NEW:
                        U.showView(progressBar);
                        break;
                    case TYPE_REFRESH:
                        refreshLayout.setRefreshing(true);
                        break;
                    case TYPE_PAGINATION:
                        loadingMore = true;
                        break;
                }
            }

            @Override
            public void onSuccess(int statusCode, Header[] headers, JSONObject response) {
                try {
                    if (! response.getBoolean("success")) {
                        throw new IllegalStateException("Server returned error. Error: " + response.getString("error_msg"));
                    }

                    ArrayList<Image> newImages = new ArrayList<>();
                    JSONArray imagesJson = response.getJSONArray("data");

                    //empty data
                    if (imagesJson.length() == 0) {
                        onImagesLoadedListener.onEmptyResult(response);
                        return;
                    }

                    for(int i = 0; i < imagesJson.length(); i++)
                        newImages.add(new Image((JSONObject) imagesJson.get(i)));

                    pagination++;
                    onImagesLoadedListener.onSuccess(response, newImages);
                } catch (Exception e) {
                    e.printStackTrace();
                    onImagesLoadedListener.onException(e);
                }
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, String responseString, Throwable throwable) {
                onImagesLoadedListener.onFail(throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONObject errorResponse) {
                onImagesLoadedListener.onFail(throwable);
            }

            @Override
            public void onFailure(int statusCode, Header[] headers, Throwable throwable, JSONArray errorResponse) {
                onImagesLoadedListener.onFail(throwable);
            }

            @Override
            public void onFinish() {
                switch (type) {
                    case TYPE_NEW:
                        U.hideView(progressBar);
                        break;
                    case TYPE_REFRESH:
                        refreshLayout.setRefreshing(false);
                        break;
                    case TYPE_PAGINATION:
                        loadingMore = false;
                        stopLoadMore = true;
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                stopLoadMore = false;
                            }
                        }, 2500);
                        break;
                }
            }
        });
    }

    public abstract class OnImagesLoadedListener {
        private int type;

        public OnImagesLoadedListener(int type) {
            this.type = type;
        }

        void onSuccess(JSONObject response, ArrayList<Image> newImages) {
            if (type == TYPE_PAGINATION) {
                int oldSize = images.size();
                images.addAll(newImages);
                imagesAdapter.notifyItemRangeInserted(oldSize, newImages.size());
            } else {
                images = newImages;
                setImagesAdapter();
            }
        }

        void onFail(Throwable e) {
            U.showNetworkError(getView());

            //when refreshing with empty list and first loading
            if (type == TYPE_REFRESH && images.isEmpty() || type == TYPE_NEW) {
                U.showView(retryView);
            }

            if (Config.DEBUG) {
                e.printStackTrace();
            }
        }

        void onException(Exception e) {
            U.showError(getView(), R.string.exception);

            //when refreshing with empty list and first loading
            if (type == TYPE_REFRESH && images.isEmpty() || type == TYPE_NEW) {
                U.showView(retryView);
            }

            if (Config.DEBUG) {
                e.printStackTrace();
            }
        }

        /**
         * Override it if don't want problems
         */
        void onEmptyResult(JSONObject response) {
            if (mImagesType.equals(Config.API_ACTION_GET_GALLERY)) {
                U.showCenteredToast(getActivity(), R.string.images_gallery_empty);
                getActivity().finish(); //will album found if user will wait? No. So close activity.
                return;
            }

            if (mImagesType.equals(Config.API_ACTION_GET_ALL_MY)) {
                images.clear();
                setImagesAdapter();
                U.showView(retryView);
                U.showError(getView(), R.string.images_my_empty);
            }
        }
    }

    @Override
    public boolean shouldLoad() {
        return ! loadingMore && ! stopLoadMore;
    }

    @Override
    public void loadNextPage() {
        loadImages(TYPE_PAGINATION, new OnImagesLoadedListener(TYPE_PAGINATION) {

            @Override
            void onEmptyResult(JSONObject response) {
                //reached end. stop load more
                stopLoadMore = true;
                recyclerView.setRefreshing(false);
            }
        });
    }

    @Override
    public void scrollUp() {
        if (recyclerView != null) {
            recyclerView.smoothScrollToPosition(0);
        }
    }

    @Override
    protected int getLayout() {
        return R.layout.fragment_images;
    }
}
