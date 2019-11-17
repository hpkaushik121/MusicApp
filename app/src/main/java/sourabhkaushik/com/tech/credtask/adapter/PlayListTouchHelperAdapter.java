package sourabhkaushik.com.tech.credtask.adapter;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.view.View;
import android.view.animation.RotateAnimation;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

import sourabhkaushik.com.tech.credtask.MainApplication;
import sourabhkaushik.com.tech.credtask.R;

/**
 * Created by Sourabh kaushik on 11/17/2019.
 */
public class PlayListTouchHelperAdapter extends ItemTouchHelper.Callback {
    private final ItemTouchHelperContract mAdapter;
    private boolean start;
    private int degree=0;
    public PlayListTouchHelperAdapter(ItemTouchHelperContract adapter) {
        mAdapter = adapter;
    }

    @Override
    public boolean isLongPressDragEnabled() {
        return true;
    }

    @Override
    public boolean isItemViewSwipeEnabled() {
        return true;
    }


    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        if (actionState == ItemTouchHelper.ACTION_STATE_SWIPE) {

            View itemView = viewHolder.itemView;

            Paint paint = new Paint();
            Bitmap bitmap, bitmap_cap, bitmap_body;

            if (dX > 0) { // swiping right
                paint.setColor(Color.RED);
                bitmap = BitmapFactory.decodeResource(MainApplication.getAppContext().getResources(), R.drawable.trash_30);
                float height = (itemView.getHeight() / 2) - (bitmap.getHeight() / 2);

                c.drawRect((float) itemView.getLeft(), (float) itemView.getTop(), dX, (float) itemView.getBottom(), paint);
                c.drawBitmap(bitmap, 96f, (float) itemView.getTop() + height, null);

            } else { // swiping left
                paint.setColor(Color.RED);
                bitmap_cap = BitmapFactory.decodeResource(MainApplication.getAppContext().getResources(), R.drawable.trash_30_cap);
                bitmap_body = BitmapFactory.decodeResource(MainApplication.getAppContext().getResources(), R.drawable.trash_30_body);
                float height_cap = (itemView.getHeight() / 2) - (bitmap_cap.getHeight() / 2);
                float height_body = (itemView.getHeight() / 2) - (bitmap_body.getHeight() / 2);
                float bitmapCapWidth = bitmap_cap.getWidth();
                float bitmapBodyWidth = bitmap_body.getWidth();

                c.drawRect((float) itemView.getRight() + dX, (float) itemView.getTop(), (float) itemView.getRight(), (float) itemView.getBottom(), paint);


                if ((-dX / 15) < 30) {
                    Matrix matrix = new Matrix();
                    degree=0;
                    start=false;
                    matrix.postRotate(-dX / 15);
                    bitmap_cap = Bitmap.createBitmap(bitmap_cap, 0, 0, bitmap_cap.getWidth(), bitmap_cap.getHeight(),
                            matrix, true);
                    c.drawBitmap(bitmap_cap, ((float) itemView.getRight() - bitmapCapWidth) - 96f, (float) itemView.getTop() + height_cap, null);
                    c.drawBitmap(bitmap_body, ((float) itemView.getRight() - bitmapBodyWidth) - 96f, (float) itemView.getTop() + height_body, null);

                } else {

                    Matrix matrix_cap = new Matrix();
                    Matrix matrix_body = new Matrix();
                    if(!start){

                        degree++;
                        if(degree==30){
                            start=true;
                        }
                    }else {
                        degree--;
                        if(degree==-60){
                            start=false;
                        }
                    }
                    matrix_cap.postRotate(degree);
                    matrix_body.postRotate(degree);
                    bitmap_cap = Bitmap.createBitmap(bitmap_cap, 0, 0, bitmap_cap.getWidth(), bitmap_cap.getHeight(),
                            matrix_cap, true);
                    bitmap_body = Bitmap.createBitmap(bitmap_body, 0, 0, bitmap_body.getWidth(), bitmap_body.getHeight(),
                            matrix_body, true);



                    c.drawBitmap(bitmap_cap, ((float) itemView.getRight() - bitmapCapWidth) - 96f, (float) itemView.getTop() + height_cap, null);
                    c.drawBitmap(bitmap_body, ((float) itemView.getRight() - bitmapBodyWidth) - 96f, (float) itemView.getTop() + height_body, null);

                }
            }


            super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);


        }
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public int getMovementFlags(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START | ItemTouchHelper.END;
        return makeMovementFlags(dragFlags, swipeFlags);
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder,
                          RecyclerView.ViewHolder target) {
        mAdapter.onRowMoved(viewHolder.getAdapterPosition(), target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSelectedChanged(RecyclerView.ViewHolder viewHolder,
                                  int actionState) {


        if (actionState != ItemTouchHelper.ACTION_STATE_IDLE) {
            if (viewHolder instanceof PlayListModalAdapter.GenericAdapter) {
                PlayListModalAdapter.GenericAdapter myViewHolder =
                        (PlayListModalAdapter.GenericAdapter) viewHolder;
                mAdapter.onRowSelected(myViewHolder);
            }

        }

        super.onSelectedChanged(viewHolder, actionState);
    }

    @Override
    public void clearView(RecyclerView recyclerView,
                          RecyclerView.ViewHolder viewHolder) {
        super.clearView(recyclerView, viewHolder);

        if (viewHolder instanceof PlayListModalAdapter.GenericAdapter) {
            PlayListModalAdapter.GenericAdapter myViewHolder =
                    (PlayListModalAdapter.GenericAdapter) viewHolder;
            mAdapter.onRowClear(myViewHolder);
        }
    }

    public interface ItemTouchHelperContract {

        void onRowMoved(int fromPosition, int toPosition);

        void onRowSelected(PlayListModalAdapter.GenericAdapter myViewHolder);

        void onRowClear(PlayListModalAdapter.GenericAdapter myViewHolder);

        void onItemDismiss(int position);

    }
}
