package geozombie.bboybboy.com.geozombie.j4f;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;

public final class CircleZombiesContainer extends AZombiesContainer {
    private final int circleCenterX;
    private final int circleCenterY;
    private final int count;
    private final int radius;
    private final Handler handler;
    private final int drawableRes;

    public CircleZombiesContainer(ViewGroup rootView, int count, int userStatusDrawableRes) {
        super(rootView);
        this.count = count;
        this.drawableRes = userStatusDrawableRes;
        int width = rootView.getMeasuredWidth();
        int height = rootView.getMeasuredHeight();
        this.circleCenterX = width / 2 - 100;
        this.circleCenterY = height / 2 - 100;
        this.radius = width * 2 / 5;
        this.handler = new Handler();
    }

    @Override
    public void populateZombies() {
        for (int i = 0; i < count; i++)
            postNewZombie(i);
        drawUserStatus();
    }

    private void postNewZombie(int delay) {
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                generateRandomCircleZombie();
            }
        }, 5 * delay);
    }

    private void generateRandomCircleZombie() {
        int minX = circleCenterX - radius;
        int maxX = circleCenterX + radius;
        final int randomX = RandomGenerator.randomInRange(minX, maxX);
        int sign = zombieViews.size() % 2 > 0 ? 1 : -1;
        final double y = sign * Math.sqrt(Math.pow(radius, 2) - Math.pow(randomX - circleCenterX, 2)) + circleCenterY;
        generateAnimatedZombie(randomX, (int) Math.floor(y));
    }

    @Override
    void setupAdditionalZombieAnimations(View view) {
        //no additional animations required
    }

    private void drawUserStatus() {
        ImageView imageView = new ImageView(context);
        Drawable drawable = context.getResources().getDrawable(drawableRes);
        ViewGroup.LayoutParams layoutParams = new FrameLayout.LayoutParams(radius, radius);
        imageView.setLayoutParams(layoutParams);
        imageView.setX(circleCenterX - radius / 4);
        imageView.setY(circleCenterY - radius / 4);
        imageView.setImageDrawable(drawable);
        rootView.addView(imageView);
    }
}