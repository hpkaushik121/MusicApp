package sourabhkaushik.com.tech.credtask.customRecyclerViews.internal;

import android.view.animation.Interpolator;

import sourabhkaushik.com.tech.credtask.customRecyclerViews.Direction;

public interface AnimationSetting {
    Direction getDirection();
    int getDuration();
    Interpolator getInterpolator();
}
