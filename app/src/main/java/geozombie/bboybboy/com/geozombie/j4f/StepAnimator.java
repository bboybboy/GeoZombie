package geozombie.bboybboy.com.geozombie.j4f;

import android.animation.ObjectAnimator;
import android.animation.PropertyValuesHolder;
import android.animation.ValueAnimator;
import android.view.View;
import android.view.animation.AccelerateDecelerateInterpolator;

import static android.view.View.ROTATION;

final class StepAnimator {
    private static final int MIN_STEP_DURATION = 2400;
    private static final int MAX_STEP_DURATION = 3400;
    private static final int MAX_STEP_ANGLE = 25;

    private StepAnimator() {

    }

    static void animateZombieSteps(final View zombieView) {
        int stepAngle = RandomGenerator.randomWithMax(MAX_STEP_ANGLE);
        PropertyValuesHolder rotationHolder = PropertyValuesHolder.ofFloat(ROTATION, -stepAngle, stepAngle);
        ObjectAnimator objectAnimator = ObjectAnimator.ofPropertyValuesHolder(zombieView, rotationHolder);
        int duration = RandomGenerator.randomInRange(MIN_STEP_DURATION, MAX_STEP_DURATION);
        objectAnimator.setDuration(duration);
        objectAnimator.setInterpolator(new AccelerateDecelerateInterpolator());
        objectAnimator.setRepeatCount(ValueAnimator.INFINITE);
        objectAnimator.setRepeatMode(ValueAnimator.REVERSE);
        objectAnimator.start();
        setupViewPivot(zombieView);
    }

    private static void setupViewPivot(final View zombieView) {
        zombieView.post(new Runnable() {
            @Override
            public void run() {
                zombieView.setPivotY(zombieView.getMeasuredHeight() * 4 / 5);
                zombieView.setPivotX(zombieView.getMeasuredWidth() / 2);
            }
        });
    }
}