package sourabhkaushik.com.tech.credtask.customRecyclerViews;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;
import android.widget.RelativeLayout;

/**
 * Created by Sourabh kaushik on 11/12/2019.
 */
public class ResizeWidthAnimation extends Animation {
    private RelativeLayout.LayoutParams mWidth;
    private int mStartWidth;
    private View mView;

    public ResizeWidthAnimation(View view, RelativeLayout.LayoutParams width) {
        mView = view;
        mWidth = width;
        mStartWidth = view.getWidth();
    }

    @Override
    protected void applyTransformation(float interpolatedTime, Transformation t) {
        mView.setLayoutParams(mWidth);
        mView.requestLayout();
    }

    @Override
    public void initialize(int width, int height, int parentWidth, int parentHeight) {
        super.initialize(width, height, parentWidth, parentHeight);
    }

    @Override
    public boolean willChangeBounds() {
        return true;
    }
}
