package geozombie.bboybboy.com.geozombie.j4f;

import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;

import java.util.ArrayList;
import java.util.List;

public final class WalkingZombiesContainer extends AZombiesContainer {
    private List<RandomZombieWalkAnimator> randomZombieWalkAnimators = new ArrayList<>();
    private final int width;
    private final int height;
    private final int count;
    private final Handler handler;

    public WalkingZombiesContainer(ViewGroup rootView, int count) {
        super(rootView);
        this.width = rootView.getMeasuredWidth() - 100;
        this.height = rootView.getMeasuredHeight() - 150;
        this.count = count;
        this.handler = new Handler();
    }


    @Override
    public void populateZombies() {
        for (int i = 0; i < count; i++)
            postNewZombie(i);
    }

    private void postNewZombie(int delay) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                generateAnimatedViewAtRandomPosition();
            }
        }, 10 * delay);
    }

    private void generateAnimatedViewAtRandomPosition() {
        int x = RandomGenerator.randomWithMax(width);
        int y = RandomGenerator.randomWithMax(height);
        generateAnimatedZombie(x, y);
    }

    @Override
    void setupAdditionalZombieAnimations(View view) {
        RandomZombieWalkAnimator zombieWalkAnimator = new RandomZombieWalkAnimator(view, width, height);
        randomZombieWalkAnimators.add(zombieWalkAnimator);
        zombieWalkAnimator.startAnimatingZombie();
    }

    @Override
    public void cancel() {
        super.cancel();
        for (RandomZombieWalkAnimator animator : randomZombieWalkAnimators)
            animator.cancel();
        randomZombieWalkAnimators.clear();
    }
}