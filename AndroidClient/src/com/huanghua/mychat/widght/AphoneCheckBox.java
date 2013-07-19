
package com.huanghua.mychat.widght;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.RectF;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.ViewConfiguration;
import android.view.ViewParent;
import android.view.animation.DecelerateInterpolator;
import android.widget.Checkable;
import android.widget.ImageView;
import android.widget.ScrollView;
import android.widget.Scroller;

import com.huanghua.mychat.R;

public class AphoneCheckBox extends ImageView implements Checkable {

    private static final String TAG = "AphoneCheckBox";

    private static final boolean DEBUG = false;

    private VelocityTracker mVelocityTracker;

    private int mMaximumVelocity;

    private float mLastMotionX;

    private ScrollView mAphoneScrollView;

    private BounceFlingRunnable mBounceFlingRunnable;

    private OnCheckedChangeListener mOnCheckedChangeListener;

    private GestureDetector mGestureDetector;

    private float scrollX = 0.0f;

    private boolean mChecked;

    private boolean mBroadcasting;

    private boolean mClicked;

    private boolean mFlag;

    private int mHeight = 26;

    private int mWigth = 61;

    int x = 0;

    int y = 0;

    int deltaX;

    private static final float EDGEGLOWLEFT = 30.0f;

    private static final float EDGEGLOWRIGHT = 0.0f;

    public AphoneCheckBox(Context context, AttributeSet attrs) {
        super(context, attrs);
        initImageView(context);
    }

    private void initImageView(Context context) {
        final ViewConfiguration configuration = ViewConfiguration.get(getContext());
        mMaximumVelocity = configuration.getScaledMaximumFlingVelocity();
        mGestureDetector = new GestureDetector(context, new TouchGesture());
        setMinimumHeight(mHeight);
        setMinimumWidth(mWigth);
        setMaxHeight(mHeight);
        setMaxWidth(mWigth);
    }

    /*
     * (non-Javadoc)
     * @see android.view.View#performClick()
     */
    @Override
    public boolean performClick() {
        toggle();
        return super.performClick();
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        super.onTouchEvent(ev);

        if (ev.getAction() == MotionEvent.ACTION_DOWN && ev.getEdgeFlags() != 0) {
            return false;
        }

        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(ev);

        final int action = ev.getAction();
        final float eX = ev.getX();

        switch (action) {
            case MotionEvent.ACTION_DOWN:
                printLogcat("ACTION DOWN");
                getParentView(getParent());
                if (mAphoneScrollView != null) {
                    mAphoneScrollView.setScrollContainer(true);
                }
                if (Math.abs(scrollX) != EDGEGLOWLEFT && scrollX != EDGEGLOWRIGHT) {
                    return false;
                }
                mLastMotionX = eX;
                mClicked = true;
                mChecked = !mChecked;
                break;
            case MotionEvent.ACTION_MOVE:
                printLogcat("ACTION MOVE");
                deltaX = (int) (mLastMotionX - eX);
                mLastMotionX = eX;
                break;
            case MotionEvent.ACTION_UP:
                printLogcat("ACTION UP");
                final VelocityTracker velocityTracker = mVelocityTracker;
                velocityTracker.computeCurrentVelocity(1000, mMaximumVelocity);

                printLogcat("mClicked = " + mClicked + ", scrollX = " + scrollX);
                if (mClicked) {
                    if (Math.abs(scrollX) == EDGEGLOWLEFT || Math.abs(scrollX) == EDGEGLOWRIGHT) {
                        if (Math.abs(scrollX) >= EDGEGLOWLEFT) {
                            scrollX = EDGEGLOWLEFT;
                            // mBounceFlingRunnable.start(scrollX, 0);
                            startBounceFling(scrollX, 0);
                        } else if (Math.abs(scrollX) <= EDGEGLOWRIGHT) {
                            scrollX = EDGEGLOWRIGHT;
                            // mBounceFlingRunnable.start(0, EDGEGLOWLEFT);
                            startBounceFling(0, EDGEGLOWLEFT);
                        }
                    }
                } else {
                    if (Math.abs(scrollX) > EDGEGLOWLEFT / 2) {
                        mChecked = false;
                        startBounceFling(-scrollX, EDGEGLOWLEFT);
                        // mBounceFlingRunnable.start(-scrollX, EDGEGLOWLEFT);
                    } else {
                        mChecked = true;
                        startBounceFling(-scrollX, 0);
                        // mBounceFlingRunnable.start(-scrollX, 0);
                    }
                }

                if (mAphoneScrollView != null) {
                    mAphoneScrollView.setScrollContainer(false);
                }

                if (mVelocityTracker != null) {
                    mVelocityTracker.recycle();
                    mVelocityTracker = null;
                }
                break;
        }
        return mGestureDetector.onTouchEvent(ev);
    }

    private void startBounceFling(float start, float end) {
        if (mBounceFlingRunnable == null) {
            mBounceFlingRunnable = new BounceFlingRunnable();
        }
        mBounceFlingRunnable.start(start, end);
    }

    private void getParentView(ViewParent viewParent) {
        if (viewParent != null) {
            if (viewParent instanceof ScrollView) {
                mAphoneScrollView = (ScrollView) viewParent;
            } else {
                getParentView(viewParent.getParent());
            }
        }
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        Paint paint = new Paint();
        paint.setFilterBitmap(false);
        int sc = canvas.saveLayer(x, y, x + mWigth, y + mHeight, null, Canvas.MATRIX_SAVE_FLAG
                | Canvas.CLIP_SAVE_FLAG | Canvas.HAS_ALPHA_LAYER_SAVE_FLAG
                | Canvas.FULL_COLOR_LAYER_SAVE_FLAG | Canvas.CLIP_TO_LAYER_SAVE_FLAG);

        canvas.translate(0, 0);
        paint.setColor(Color.WHITE);
        canvas.drawRoundRect(new RectF(0, 0, mWigth, mHeight), 2, 2, paint);
        Bitmap bm = BitmapFactory.decodeResource(getResources(), R.drawable.btn_check_on_off);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        if (scrollX < -EDGEGLOWLEFT) {
            scrollX = -EDGEGLOWLEFT;
        } else if (scrollX > EDGEGLOWRIGHT) {
            scrollX = EDGEGLOWRIGHT;
        }
        canvas.drawBitmap(bm, scrollX, 0, paint);
        paint.setXfermode(null);
        canvas.restoreToCount(sc);
    }

    private class TouchGesture extends GestureDetector.SimpleOnGestureListener {

        @Override
        public boolean onDown(MotionEvent e) {
            printLogcat("TouchGesture .... onDown");
            mClicked = true;
            return true;
        }

        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
            printLogcat("TouchGesture .... onScroll");
            scrollX -= deltaX;
            invalidate();
            mClicked = false;
            return true;
        }
    }

    private class BounceFlingRunnable implements Runnable {

        Scroller mScroller;

        public BounceFlingRunnable() {
            mScroller = new Scroller(getContext(), new DecelerateInterpolator());
        }

        public void start(float scrollX, float leftScrollX) {
            mScroller.startScroll(-(int) scrollX, 0, (int) (scrollX - leftScrollX), 0);
            mScroller.extendDuration(300);
            mFlag = false;
            post(this);
        }

        public void run() {
            final Scroller scroller = mScroller;
            boolean more = scroller.computeScrollOffset();
            final int x = scroller.getCurrX();
            scrollX = x;
            if (more) {
                printLogcat("run ... scrollX = " + scrollX);
                invalidate();
                post(this);
                mClicked = true;
                if (scrollX == EDGEGLOWRIGHT) {
                    mChecked = true;
                } else if (scrollX == -EDGEGLOWLEFT) {
                    mChecked = false;
                }
                if (mOnCheckedChangeListener != null && !mFlag) {
                    mFlag = !mFlag;
                    mOnCheckedChangeListener.onCheckedChanged(AphoneCheckBox.this, mChecked);
                }
            }
        }
    }

    @Override
    public boolean isChecked() {
        return mChecked;
    }

    @Override
    public void setChecked(boolean checked) {
        mChecked = checked;
        if (mChecked) {
            scrollX = EDGEGLOWRIGHT;
        } else {
            scrollX = -EDGEGLOWLEFT;
        }
        invalidate();
    }

    @Override
    public void toggle() {
        // setChecked(!mChecked);
    }

    public void setOnCheckedChangeListener(OnCheckedChangeListener listener) {
        mOnCheckedChangeListener = listener;
    }

    public static interface OnCheckedChangeListener {
        void onCheckedChanged(AphoneCheckBox aphoneCheckBox, boolean isChecked);
    }

    private void printLogcat(String msg) {
        if (DEBUG) {
            android.util.Log.e(TAG, msg);
        }
    }
}
