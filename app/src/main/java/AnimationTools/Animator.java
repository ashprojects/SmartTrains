package AnimationTools;

import android.os.Build;
import android.transition.Explode;
import android.transition.Fade;
import android.view.Window;

/**
 * Created by root on 25/6/17.
 */

public class Animator {

    public enum Type {
        EXPLODE, FADE, SLIDE
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
}
