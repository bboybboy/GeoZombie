package geozombie.bboybboy.com.geozombie.j4f;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

import java.util.ArrayList;
import java.util.List;

import geozombie.bboybboy.com.geozombie.R;

public abstract class AZombiesContainer {
    final ViewGroup rootView;
    final Context context;
    public List<View> zombieViews = new ArrayList<>();

    AZombiesContainer(ViewGroup rootView) {
        this.rootView = rootView;
        this.context = rootView.getContext();
    }

    final void generateAnimatedZombie(int x, int y) {
        final ImageView newImageView = new ImageView(context);
        newImageView.setX(x);
        newImageView.setY(y);
        newImageView.setImageDrawable(context.getResources().getDrawable(R.mipmap.zmb));
        ViewGroup.LayoutParams layoutParams = new FrameLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        newImageView.setLayoutParams(layoutParams);
        rootView.addView(newImageView);
        setStepAnimation(newImageView);
        setupAdditionalZombieAnimations(newImageView);
        zombieViews.add(newImageView);
    }

    private void setStepAnimation(View view) {
        StepAnimator.animateZombieSteps(view);
    }

    public void cancel() {
        for (View zombiewView : zombieViews)
            rootView.removeView(zombiewView);
        zombieViews.clear();
    }

    public abstract void populateZombies();

    abstract void setupAdditionalZombieAnimations(View view);
}