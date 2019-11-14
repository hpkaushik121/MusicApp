package sourabhkaushik.com.tech.credtask.customRecyclerViews;

import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PathEffect;
import android.graphics.PathMeasure;
import android.graphics.RectF;
import android.os.Build;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;

import sourabhkaushik.com.tech.credtask.R;

/**
 * Created by Sourabh kaushik on 11/12/2019.
 */
public class ExpandingView extends RelativeLayout {

    Path path;
    Paint paint;
    int width=getWidth();
    float length;

    public ExpandingView(Context context) {
        super(context);

//        init();
    }

    public ExpandingView(Context context, AttributeSet attrs) {
        super(context, attrs);
//        width=getWidth();
//        init();
    }

    public ExpandingView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
//        width=getWidth();
//        init();
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public ExpandingView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void onLayout(boolean b, int i, int i1, int i2, int i3) {
        init();
    }

    @Override
    protected void onAttachedToWindow() {
        super.onAttachedToWindow();

    }

    public void init() {
        width=getWidth()-50;
        paint = new Paint();
        paint.setColor(Color.BLACK);
        paint.setStrokeWidth(10);
        paint.setStyle(Paint.Style.STROKE);
        path = new Path();
        path.moveTo(width,0);
        path.lineTo(200,0);
        path.lineTo(100,0);
        path.lineTo(50,0);
        // Measure the path
        PathMeasure measure = new PathMeasure(path, false);
        length = measure.getLength();

        float[] intervals = new float[]{length, length};

        ObjectAnimator animator = ObjectAnimator.ofFloat(ExpandingView.this, "phase", 1.0f, 0.0f);
        animator.setDuration(10000);
        animator.start();
    }

    //is called by animtor object
    public void setPhase(float phase) {
        Log.d("pathview", "setPhase called with:" + String.valueOf(phase));
        paint.setPathEffect(createPathEffect(length, phase, 0.0f));
        invalidate();//will calll onDraw
    }

    private static PathEffect createPathEffect(float pathLength, float phase, float offset) {
        return new DashPathEffect(new float[]{pathLength, pathLength},
                Math.max(phase * pathLength, offset));
    }

    @Override
    public void onDraw(Canvas c) {
        super.onDraw(c);
//        width=getWidth();
        c.drawPath(path, paint);
    }
}
