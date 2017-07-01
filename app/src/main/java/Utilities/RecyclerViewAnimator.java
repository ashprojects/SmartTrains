package Utilities;

import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Interpolator;
import android.view.animation.TranslateAnimation;

import jpro.smarttrains.adapters.ListAdapterTrainBetweenStation;

public class RecyclerViewAnimator extends RecyclerView.ItemAnimator {
    private int counter = 0;
    private Interpolator mInterpolator = new DecelerateInterpolator();
    private RecyclerView recyclerView;
    private ListAdapterTrainBetweenStation adapter;

    public RecyclerViewAnimator(RecyclerView recyclerView, ListAdapterTrainBetweenStation adapter) {
        this.recyclerView = recyclerView;
        this.adapter = adapter;
    }

    @Override
    public boolean animateDisappearance(@NonNull RecyclerView.ViewHolder viewHolder, @NonNull ItemHolderInfo preLayoutInfo, @Nullable ItemHolderInfo postLayoutInfo) {
        System.out.println("IN disappearence");
        recyclerView.getLayoutManager().removeView(viewHolder.itemView);
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
        int diff = 0;
        LinearLayoutManager manager = (LinearLayoutManager) recyclerView.getLayoutManager();
        int startIndex = manager.findFirstVisibleItemPosition();
        int lastIndex = manager.findLastVisibleItemPosition();
        int prevIndex = adapter.prevTrains.indexOf(((ListAdapterTrainBetweenStation.TrainViewHolder) newHolder).item);
        if (prevIndex < startIndex) {
            diff = recyclerView.getTop() - recyclerView.getHeight() / 2;
        } else if (prevIndex > lastIndex) {
            diff = recyclerView.getBottom() + recyclerView.getHeight() / 2;
        } else {
            diff = recyclerView.getChildAt(prevIndex).getTop() - newHolder.itemView.getTop();
        }
        TranslateAnimation animation = new TranslateAnimation(0.0f, 0.0f, diff, 0.0f);
        animation.setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {
                dispatchAnimationStarted(newHolder);
                counter++;
            }

            @Override
            public void onAnimationEnd(Animation animation) {
                dispatchAnimationFinished(newHolder);
                counter--;
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        animation.setInterpolator(mInterpolator);
        animation.setDuration(333);
        newHolder.itemView.startAnimation(animation);
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
        return counter != 0;
    }
}
