package geozombie.bboybboy.com.geozombie.j4f;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.os.Handler;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import static android.view.View.TRANSLATION_X;
import static android.view.View.TRANSLATION_Y;


final class RandomZombieWalkAnimator implements Animator.AnimatorListener {
    private static final int MIN_WALK_TIME = 20000;
    private static final int MAX_WALK_TIME = 40000;
    private static final int STAY_STILL_TIME = 2000;
    private boolean cancelled = false;
    private final View zombieView;
    private final int width;
    private final int height;
    private ObjectAnimator objectAnimator;

    RandomZombieWalkAnimator(View zombieView, int width, int height) {
        this.zombieView = zombieView;
        this.width = width;
        this.height = height;
    }

    void startAnimatingZombie() {
        if (cancelled) return;
        prepareObjectAnimator();
        startAnimation();
    }

    private void prepareObjectAnimator() {
        int x = RandomGenerator.randomWithMax(width);
        int y = RandomGenerator.randomWithMax(height);

        PropertyValuesHolder pvhX = PropertyValuesHolder.ofFloat(TRANSLATION_X, x);
        PropertyValuesHolder pvhY = PropertyValuesHolder.ofFloat(TRANSLATION_Y, y);

        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(zombieView, pvhX, pvhY);
        int duration = RandomGenerator.randomInRange(MIN_WALK_TIME, MAX_WALK_TIME);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.addListener(this);
        this.objectAnimator = objectAnimator;
    }

    private void startAnimation() {
        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                objectAnimator.start();
            }
        }, STAY_STILL_TIME);
    }

    @Override
    public void onAnimationStart(Animator animation) {
    }

    @Override
    public void onAnimationEnd(Animator animation) {
        startAnimatingZombie();
    }

    @Override
    public void onAnimationCancel(Animator animation) {

    }

    @Override
    public void onAnimationRepeat(Animator animation) {

    }

    void cancel() {
        cancelled = true;
    }
}