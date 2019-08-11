package c.vadiole.recyclerview;


import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.text.TextPaint;

import android.util.Log;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.RecyclerView;

public class SwipeToDeleteCallback extends ItemTouchHelper.SimpleCallback {


    private static final String RIPPLE = "RIPPLE";
    private static final String SWIPE = "SWIPE";
    private static float textAndIconScale = 0.8f;
    private static float swipeThreshold = 0.3f;
    private static int vibrationTime = 40;

    private enum status {
        DEFAULT,
        ANIMATED_INC,
        ANIMATED_DEC
    }

    private enum trigger {
        NULL,
        TO_LEFT,
        TO_RIGHT
    }

    private MyRecyclerViewAdapter mAdapter;

    private final Drawable icon;
    private final Drawable background;
    private final Drawable whiteBackgroundTop;
    private final Drawable whiteBackgroundRight;
    private final Drawable whiteBackgroundBottom;

    private final TextPaint textPaint;


    //icon anim vars
    private status iconStatus = status.DEFAULT;
    private float iconScaleNow = 1;
    private float iconScaleMax = 1.1f;
    private float iconScaleMin = 1f;
    private float iconScaleInc = 0.05f;
    private float lastValue = 0;

    //ripple anim vars
    private Paint paint;
    private float rippleRadius = 0;
    private float rippleStartSpeed = MyUtils.dpToPx(10);
    private float rippleEndSpeed = MyUtils.dpToPx(24);
    private float rippleSpeed = MyUtils.dpToPx(10);
    private float rippleAcceleration = MyUtils.dpToPx(13) * 0.1f;
    private trigger swipeStatus = trigger.TO_RIGHT;


    SwipeToDeleteCallback(MyRecyclerViewAdapter adapter) {
        super(0, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mAdapter = adapter;


        icon = ContextCompat.getDrawable(App.getAppContext(),
                R.drawable.ic_delete_forever_black_48dp);
        background = new ColorDrawable(App
                .getAppContext()
                .getResources()
                .getColor(R.color.secondaryColor));
        whiteBackgroundTop = new ColorDrawable(Color.WHITE);
        whiteBackgroundRight = new ColorDrawable(Color.WHITE);
        whiteBackgroundBottom = new ColorDrawable(Color.WHITE);

        textPaint = new TextPaint();
        textPaint.setTextSize(MyUtils.dpToPx(16) * textAndIconScale);
        textPaint.setTextAlign(Paint.Align.CENTER);
        textPaint.setColor(Color.WHITE);
        textPaint.setTypeface(Typeface.create("Roboto", Typeface.BOLD));
        textPaint.setAntiAlias(true);

        paint = new Paint();
        paint.setColor(App
                .getAppContext()
                .getResources()
                .getColor(R.color.swipeToDismissColor));
        paint.setAntiAlias(true);


    }

    @Override
    public int getMovementFlags(@NonNull RecyclerView recyclerView,
                                @NonNull RecyclerView.ViewHolder viewHolder) {
        int dragFlags = ItemTouchHelper.UP | ItemTouchHelper.DOWN;
        int swipeFlags = ItemTouchHelper.START;
        return makeMovementFlags(dragFlags, swipeFlags);
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
    public boolean onMove(@NonNull RecyclerView recyclerView,
                          @NonNull RecyclerView.ViewHolder viewHolder,
                          @NonNull RecyclerView.ViewHolder target) {
        mAdapter.onItemMove(viewHolder.getAdapterPosition(),
                target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
        mAdapter.onItemDismiss(viewHolder.getAdapterPosition());
    }

    @Override
    public float getSwipeThreshold(@NonNull RecyclerView.ViewHolder viewHolder) {
        return swipeThreshold;
    }

    @Override
    public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
        super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
        Log.d(SWIPE, "dX " + dX);

        if (dX < 0) {
            View itemView = viewHolder.itemView;

            int backgroundCornerOffset = 16;
            int marginRight = (int) (MyUtils.dpToPx(56) * textAndIconScale);
            int positionX = itemView.getRight() - marginRight;
            int positionY = itemView.getTop() + itemView.getHeight() / 2;

            int iconHeight;
            int iconWidth;


            //icon animation
            switch (iconStatus) {
                case DEFAULT: {
                    switch (isTriggered(App.getWidth(), dX)) {
                        case TO_RIGHT: {
                            App.makeVibration(vibrationTime);
                            rippleRadius = MyUtils.takeDistanse(positionX, positionY, (int) (App.getWidth() + dX), itemView.getTop());
                            rippleSpeed = MyUtils.dpToPx(11);
                            break;
                        }
                        case TO_LEFT: {
                            App.makeVibration(vibrationTime);
                            iconStatus = status.ANIMATED_INC;
                            break;
                        }
                    }
                    break;
                }
                case ANIMATED_INC: {
                    updateAnimationParams(status.ANIMATED_INC);

                    if (isMaxScale()) {
                        iconStatus = status.ANIMATED_DEC;
                    }
                    break;
                }
                case ANIMATED_DEC: {
                    updateAnimationParams(status.ANIMATED_DEC);
                    if (isMinScale()) {
                        iconStatus = status.DEFAULT;
                    }
                    break;
                }
            }


            //swipe animation
            switch (swipeStatus) {
                case TO_RIGHT: {
                    if (rippleRadius > 0) {
                        updateRippleRadius(trigger.TO_RIGHT);
                    }
                    break;
                }
                case TO_LEFT: {
                    if (rippleRadius < positionX * 1.1) {
                        updateRippleRadius(trigger.TO_LEFT);
                    }
                    break;
                }
            }


            iconHeight = (int) (MyUtils.dpToPx(48) * textAndIconScale * iconScaleNow);
            iconWidth = (int) (MyUtils.dpToPx(48) * textAndIconScale * iconScaleNow);

            int iconTop = (int) (positionY - iconHeight * 0.5 - MyUtils.dpToPx(12) * textAndIconScale);
            int iconBottom = (int) (positionY + iconHeight / 2 - MyUtils.dpToPx(12) * textAndIconScale);
            int iconLeft = positionX - iconWidth / 2;
            int iconRight = positionX + iconWidth / 2;

            int textX = positionX;
            int textY = (int) (positionY - (int) ((textPaint.descent() + textPaint.ascent()) / 2) + MyUtils.dpToPx(24) * textAndIconScale);


            icon.setBounds(
                    iconLeft,
                    iconTop,
                    iconRight,
                    iconBottom);
            background.setBounds(
                    itemView.getRight() + ((int) dX) - backgroundCornerOffset,
                    itemView.getTop(),
                    itemView.getRight(),
                    itemView.getBottom());
            whiteBackgroundTop.setBounds(
                    0,
                    0,
                    App.getWidth(),
                    itemView.getTop());
            whiteBackgroundRight.setBounds(
                    itemView.getRight(),
                    itemView.getTop(),
                    App.getWidth(),
                    itemView.getBottom());
            whiteBackgroundBottom.setBounds(
                    0,
                    itemView.getBottom(),
                    App.getWidth(),
                    App.getHeight());


            //draw
            background.draw(c);
            c.drawCircle(positionX, positionY, rippleRadius, paint);
            whiteBackgroundTop.draw(c);
            whiteBackgroundRight.draw(c);
            whiteBackgroundBottom.draw(c);
            icon.draw(c);
            c.drawText(App.getAppContext().getResources().getString(R.string.swipe_to_dismiss_icon_text), textX, textY, textPaint);


        } else { // view is unSwiped
            Log.d(SWIPE, "Reset, dX " + dX);
            background.setBounds(0, 0, 0, 0);
            lastValue = 0;
            rippleRadius = 0;
            rippleSpeed = rippleStartSpeed;
            swipeStatus = trigger.TO_RIGHT;
        }
    }

    private void updateRippleRadius(trigger direction) {
        Log.d(RIPPLE, rippleRadius + " " + rippleSpeed + " " + rippleAcceleration);
        switch (direction) {
            case TO_LEFT: {
                rippleRadius += rippleSpeed;
                if (rippleSpeed < rippleEndSpeed) {
                    rippleSpeed += rippleAcceleration;
                }
                break;
            }
            case TO_RIGHT: {
                rippleRadius -= rippleSpeed;
                if (rippleSpeed > rippleStartSpeed) {
                    rippleSpeed -= rippleAcceleration;
                }
                break;
            }
        }
    }

    private void updateAnimationParams(status s) {
        switch (s) {
            case ANIMATED_INC: {
                iconScaleNow += iconScaleInc;
                break;
            }
            case ANIMATED_DEC: {
                iconScaleNow -= iconScaleInc;
                break;
            }
        }
    }

    private boolean isMaxScale() {
        return iconScaleNow >= iconScaleMax;
    }

    private boolean isMinScale() {
        return iconScaleNow <= iconScaleMin;
    }

    private boolean isSwiped(int width, float value) {
        return Math.abs(value) > width * swipeThreshold;
    }

    private trigger isTriggered(int width, float value) {
        if (!isSwiped(width, lastValue) && isSwiped(width, value)) {
            Log.d(SWIPE, "Triggered to left");
            swipeStatus = trigger.TO_LEFT;
            lastValue = value;
            return trigger.TO_LEFT;
        } else if (isSwiped(width, lastValue) && ! isSwiped(width, value)) {
            Log.d(SWIPE, "Triggered to right");
            swipeStatus = trigger.TO_RIGHT;
            lastValue = value;
            return trigger.TO_RIGHT;
        } else {
            //Log.d(SWIPE, "don't triggered");
            lastValue = value;
            return trigger.NULL;
        }
    }

}


