package com.jaej.demo.util;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.material.floatingactionbutton.FloatingActionButton;

//enable floating action button to be draggable/movable
public class DraggableFloatingActionButton extends FloatingActionButton implements View.OnTouchListener {

    private final static float CLICK_DRAG_TOLERANCE = 10;

    private float downRawX, downRawY;
    private float dX, dY;

    public DraggableFloatingActionButton(Context context) {
        super(context);
        init();
    }

    public DraggableFloatingActionButton(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public DraggableFloatingActionButton(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    private void init() {
        setOnTouchListener(this);
    }

    @Override
    public boolean onTouch(View view, MotionEvent motionEvent){

        ViewGroup.MarginLayoutParams layoutParams = (ViewGroup.MarginLayoutParams)view.getLayoutParams();

        int action = motionEvent.getAction();
        if (action == MotionEvent.ACTION_DOWN) {
            //starting to move the FAB
            downRawX = motionEvent.getRawX();
            downRawY = motionEvent.getRawY();
            dX = view.getX() - downRawX;
            dY = view.getY() - downRawY;

            return true;

        }
        else if (action == MotionEvent.ACTION_MOVE) {
            //start moving location of the FAB

            int viewWidth = view.getWidth();
            int viewHeight = view.getHeight();

            View viewParent = (View)view.getParent();
            int parentWidth = viewParent.getWidth();
            int parentHeight = viewParent.getHeight();

            float newX = motionEvent.getRawX() + dX;
            //limit FAB location to not exceed parent
            newX = Math.max(layoutParams.leftMargin, newX);
            newX = Math.min(parentWidth - viewWidth - layoutParams.rightMargin, newX);

            float newY = motionEvent.getRawY() + dY;
            //limit FAB location to not exceed parent
            newY = Math.max(layoutParams.topMargin, newY);
            newY = Math.min(parentHeight - viewHeight - layoutParams.bottomMargin, newY);

            view.animate()
                    .x(newX)
                    .y(newY)
                    .setDuration(0)
                    .start();

            return true;

        }
        else if (action == MotionEvent.ACTION_UP) {
            //setting FAB location. motionEvent now carries final release location of the movement

            float upRawX = motionEvent.getRawX();
            float upRawY = motionEvent.getRawY();

            float upDX = upRawX - downRawX;
            float upDY = upRawY - downRawY;

            if (Math.abs(upDX) < CLICK_DRAG_TOLERANCE && Math.abs(upDY) < CLICK_DRAG_TOLERANCE) {
                return performClick();
            }
            else {
                return true;
            }

        }
        else {
            return super.onTouchEvent(motionEvent);
        }

    }

}
