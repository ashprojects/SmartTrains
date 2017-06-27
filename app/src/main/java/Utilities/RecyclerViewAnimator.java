package Utilities;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.RecyclerView;
import android.view.View;

/**
 * Created by root on 27/6/17.
 */

public class RecyclerViewAnimator extends RecyclerView.ItemAnimator {
    private int counter = 0;

    @Override
    public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @Nullable ItemHolderInfo postLayoutInfo) {
        System.out.println("IN disappearence");
        return false;
    }

    @Override
    public boolean animateAppearance(@NonNull RecyclerView.ViewHolder viewHolder, @Nullable ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
        System.out.println("in appearence");
        return false;
    }

    @Override
    public boolean animatePersistence(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
        System.out.println("in persistence");
        return false;
    }

    @Override
    public boolean animateChange(@NonNull final RecyclerView.ViewHolder oldHolder, @NonNull final RecyclerView.ViewHolder newHolder, @NonNull ItemHolderInfo preLayoutInfo, @NonNull ItemHolderInfo postLayoutInfo) {
        System.out.println("in change");
        ObjectAnimator rotateOldView = ObjectAnimator.ofFloat(oldHolder.itemView, View.ROTATION_X, 0, 90);
        ObjectAnimator rotateNewView = ObjectAnimator.ofFloat(newHolder.itemView, View.ROTATION_X, -90, 0);
        rotateOldView.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                dispatchAnimationStarted(oldHolder);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dispatchAnimationFinished(oldHolder);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        rotateNewView.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                dispatchAnimationStarted(newHolder);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                dispatchAnimationFinished(newHolder);
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }
        });
        AnimatorSet animation = new AnimatorSet();
        animation.playSequentially(rotateOldView, rotateNewView);
        animation.addListener(new Animator.AnimatorListener() {
            @Override
            public void onAnimationStart(Animator animation) {
                counter++;
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                counter--;
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }

            @Override
            public void onAnimationRepeat(Animator animation) {

            }


        });
        animation.start();
        return true;
    }

    @Override
    public void runPendingAnimations() {
        System.out.println("in run pending");
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        System.out.println("in end animation");
    }

    @Override
    public void endAnimations() {
        System.out.println("in end animations");
    }

    @Override
    public boolean isRunning() {
        System.out.println("in is running");
        return counter != 0;
    }
}
