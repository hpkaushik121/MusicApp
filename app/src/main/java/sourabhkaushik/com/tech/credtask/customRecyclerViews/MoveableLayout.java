package sourabhkaushik.com.tech.credtask.customRecyclerViews;

import android.app.Activity;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.view.MotionEvent;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import androidx.annotation.RequiresApi;

import sourabhkaushik.com.tech.credtask.R;
import sourabhkaushik.com.tech.credtask.boommenu.Util;

/**
 * Created by Sourabh kaushik on 11/12/2019.
 */
public class MoveableLayout extends RelativeLayout {
    private int _xDelta;
    private int _yDelta;

    public MoveableLayout(Context context) {
        super(context);
    }

    public MoveableLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context,attrs,0);
    }

    public MoveableLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context,attrs,defStyleAttr);
    }

    private void init(Context context, AttributeSet attrs, int defStyleAttr) {
        TypedArray typedArray = context.obtainStyledAttributes(
                attrs, R.styleable.MoveableLayout, 0, 0);
        Drawable srcId= typedArray.getDrawable( R.styleable.MoveableLayout_moveable_headerImage);
        ImageView imageView=new ImageView(this.getContext());
        imageView.setImageDrawable(srcId);
        imageView.setLayoutParams(new LinearLayout.LayoutParams(300,300));

        addView(imageView);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MoveableLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        final int X = (int) event.getRawX();
        final int Y = (int) event.getRawY();

            int action = event.getAction();
            switch (action) {
                case MotionEvent.ACTION_DOWN:
                    RelativeLayout.LayoutParams lParams = (RelativeLayout.LayoutParams) getLayoutParams();
                    _xDelta = X - lParams.leftMargin;
                    _yDelta = Y - lParams.topMargin;
                    break;
                case MotionEvent.ACTION_UP:
                    DisplayMetrics displayMetrics = new DisplayMetrics();
                    ((Activity)this.getContext()).getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
                    int height = displayMetrics.heightPixels;
                    int width = displayMetrics.widthPixels;
                    RelativeLayout.LayoutParams layouts = (RelativeLayout.LayoutParams) getLayoutParams();
                    layouts.leftMargin = X<(width/2)?0:width-200;
                    layouts.topMargin = Y<150?150:(Y>(height-150))?height-150:layouts.topMargin;
                    layouts.bottomMargin = 250;
                    setLayoutParams(layouts);
//                    if (label != null) {
//                        label.onActionUp();
//                    }
//                    onActionUp();
                    break;

                case MotionEvent.ACTION_CANCEL:
//                    if (label != null) {
//                        label.onActionUp();
//                    }
//                    onActionUp();
                    break;
                case MotionEvent.ACTION_MOVE:
                    RelativeLayout.LayoutParams layoutParams = (RelativeLayout.LayoutParams) getLayoutParams();
                    layoutParams.leftMargin = X - _xDelta;
                    layoutParams.topMargin = Y - _yDelta;
                    layoutParams.rightMargin = -250;
                    layoutParams.bottomMargin = -250;
                    setLayoutParams(layoutParams);
                    break;
            }

        return true;
    }
}
