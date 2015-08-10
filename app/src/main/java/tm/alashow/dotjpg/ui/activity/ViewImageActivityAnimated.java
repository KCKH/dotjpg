/*
 * Copyright (c) 2015  Alashov Berkeli
 * It is licensed under GNU GPL v. 2 or later. For full terms see the file LICENSE.
 */

package tm.alashow.dotjpg.ui.activity;

import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewTreeObserver;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.Interpolator;
import android.widget.ImageView;

import com.nineoldandroids.animation.ObjectAnimator;

import butterknife.Bind;
import butterknife.ButterKnife;
import tm.alashow.dotjpg.Config;
import tm.alashow.dotjpg.R;
import tm.alashow.dotjpg.util.DotjpgUtils;
import uk.co.senab.photoview.PhotoViewAttacher;

/**
 * This sub-activity shows a zoomed-in view of a specific photo, along with the
 * picture's text description. Most of the logic is for the animations that will
 * be run when the activity is being launched and exited. When launching,
 * the large version of the picture will resize from the thumbnail version in the
 * main activity, colorizing it from the thumbnail's grayscale version at the
 * same time. Meanwhile, the black background of the activity will fade in and
 * the description will eventually slide into place. The exit animation runs all
 * of this in reverse.
 */
public class ViewImageActivityAnimated extends Activity {

    private static final Interpolator sAccelerator = new AccelerateInterpolator();
    private static final int ANIM_DURATION = 150;
    private static final int ANIM_SCALE = 1;

    private ColorDrawable mBackground;
    private int mLeftDelta;
    private int mTopDelta;
    private float mWidthScale;
    private float mHeightScale;
    private int mOriginalOrientation;

    @Bind(R.id.imageView) ImageView mImageView;
    @Bind(R.id.topLevelLayout) View mTopLevelLayout;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_image_animated);

        ButterKnife.bind(this);

        Bundle bundle = getIntent().getExtras();
        String imageUrl = bundle.getString(Config.EXTRA_URL);
        final int thumbnailTop = bundle.getInt(Config.EXTRA_TOP);
        final int thumbnailLeft = bundle.getInt(Config.EXTRA_LEFT);
        final int thumbnailWidth = bundle.getInt(Config.EXTRA_WIDTH);
        final int thumbnailHeight = bundle.getInt(Config.EXTRA_HEIGHT);
        mOriginalOrientation = bundle.getInt(Config.EXTRA_ORIENTATION);

        DotjpgUtils.loadImage(mImageView, Uri.parse(imageUrl));
        new PhotoViewAttacher(mImageView);

        mBackground = new ColorDrawable(Color.BLACK);
        mTopLevelLayout.setBackgroundDrawable(mBackground);

        if (savedInstanceState == null) {
            ViewTreeObserver observer = mImageView.getViewTreeObserver();
            observer.addOnPreDrawListener(new ViewTreeObserver.OnPreDrawListener() {

                @Override
                public boolean onPreDraw() {
                    mImageView.getViewTreeObserver().removeOnPreDrawListener(this);

                    int[] screenLocation = new int[2];
                    mImageView.getLocationOnScreen(screenLocation);
                    mLeftDelta = thumbnailLeft - screenLocation[0];
                    mTopDelta = thumbnailTop - screenLocation[1];

                    mWidthScale = (float) thumbnailWidth / mImageView.getWidth();
                    mHeightScale = (float) thumbnailHeight / mImageView.getHeight();

                    runEnterAnimation();
                    return true;
                }
            });
        }
    }

    public static void openImage(Activity activity, ImageView imageView, String imageUrl) {

        int[] screenLocation = new int[2];
        imageView.getLocationOnScreen(screenLocation);

        int orientation = activity.getResources().getConfiguration().orientation;

        Intent intent = new Intent(activity, ViewImageActivityAnimated.class);
        intent.putExtra(Config.EXTRA_URL, imageUrl)
            .putExtra(Config.EXTRA_ORIENTATION, orientation)
            .putExtra(Config.EXTRA_LEFT, screenLocation[0])
            .putExtra(Config.EXTRA_TOP, screenLocation[1])
            .putExtra(Config.EXTRA_WIDTH, imageView.getWidth())
            .putExtra(Config.EXTRA_HEIGHT, imageView.getHeight());

        activity.startActivity(intent);
        activity.overridePendingTransition(0, 0);
    }

    /**
     * The enter animation scales the picture in from its previous thumbnail
     * size/location, colorizing it in parallel. In parallel, the background of the
     * activity is fading in. When the pictue is in place, the text description
     * drops down.
     */
    public void runEnterAnimation() {
        final long duration = (long) (ANIM_DURATION * ANIM_SCALE);

        mImageView.setPivotX(0);
        mImageView.setPivotY(0);
        mImageView.setScaleX(mWidthScale);
        mImageView.setScaleY(mHeightScale);
        mImageView.setTranslationX(mLeftDelta);
        mImageView.setTranslationY(mTopDelta);


        // Animate scale and translation to go from thumbnail to full size
        mImageView.animate().setDuration(duration).
            scaleX(1).scaleY(1).
            translationX(0).translationY(0).
            setInterpolator(sAccelerator);

        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0, 255);
        bgAnim.setDuration(duration);
        bgAnim.start();
    }

    /**
     * The exit animation is basically a reverse of the enter animation, except that if
     * the orientation has changed we simply scale the picture back into the center of
     * the screen.
     *
     * @param endAction This action gets run after the animation completes (this is
     *                  when we actually switch activities)
     */
    public void runExitAnimation(final Runnable endAction) {
        final long duration = (long) (ANIM_DURATION * ANIM_SCALE);

        // No need to set initial values for the reverse animation; the image is at the
        // starting size/location that we want to start from. Just animate to the
        // thumbnail size/location that we retrieved earlier 

        // Caveat: configuration change invalidates thumbnail positions; just animate
        // the scale around the center. Also, fade it out since it won't match up with
        // whatever's actually in the center
        final boolean fadeOut;
        if (getResources().getConfiguration().orientation != mOriginalOrientation) {
            mImageView.setPivotX(mImageView.getWidth() / 2);
            mImageView.setPivotY(mImageView.getHeight() / 2);
            mLeftDelta = 0;
            mTopDelta = 0;
            fadeOut = true;
        } else {
            fadeOut = false;
        }

        mImageView.animate().setDuration(duration).
            scaleX(mWidthScale).scaleY(mHeightScale).
            translationX(mLeftDelta).translationY(mTopDelta).
            withEndAction(endAction);
        if (fadeOut) {
            mImageView.setImageAlpha(0);
        }

        ObjectAnimator bgAnim = ObjectAnimator.ofInt(mBackground, "alpha", 0);
        bgAnim.setDuration(duration);
        bgAnim.start();
    }

    @Override
    public void onBackPressed() {
        runExitAnimation(new Runnable() {
            public void run() {
                // *Now* go ahead and exit the activity
                finish();
            }
        });
    }

    @Override
    public void finish() {
        super.finish();
        // override transitions to skip the standard window animations
        overridePendingTransition(0, 0);
    }
}
