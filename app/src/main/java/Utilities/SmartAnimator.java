package Utilities;

import android.animation.Animator;
import android.os.Build;
import android.transition.Explode;
import android.transition.Fade;
import android.view.View;
import android.view.ViewAnimationUtils;
import android.view.Window;

/**
 * Created by root on 25/6/17.
 */

public class SmartAnimator {

    public enum Type {
        EXPLODE, FADE, SLIDE
    }

    public enum What {
        OPEN, CLOSE
    }

    public static void addActivityTransition(Window W, Type T, int duration) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP)
            return;
        switch (T) {
            case EXPLODE:
                Explode explode = new Explode();
                explode.setDuration(duration);
                W.setEnterTransition(explode);
                break;
            case FADE:
                Fade fade = new Fade();
                fade.setDuration(duration);
                W.setEnterTransition(fade);
        }
    }

    public static void circularRevealView(View view, int duration, What W, Animator.AnimatorListener listener) {
        if (Build.VERSION.SDK_INT < 21) {
            view.setVisibility(View.VISIBLE);
            return;
        }
        int centerX = view.getRight();
        int centerY = view.getBottom();
        int startRadius = 0;
        int endRadius = Math.max(view.getWidth(), view.getHeight());
        if (W == What.CLOSE) {
            int k = startRadius;
            startRadius = endRadius;
            endRadius = k;

        }
        android.animation.Animator anim = ViewAnimationUtils.createCircularReveal(view, centerX, centerY, startRadius, endRadius);
        view.setVisibility(View.VISIBLE);
        anim.setDuration(duration);
        if (listener != null)
            anim.addListener(listener);
        anim.start();
    }
}
