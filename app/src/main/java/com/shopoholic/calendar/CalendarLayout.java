package com.shopoholic.calendar;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Rect;
import android.support.v4.view.VelocityTrackerCompat;
import android.support.v4.view.ViewCompat;
import android.support.v4.widget.ScrollerCompat;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.VelocityTracker;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.view.animation.Interpolator;
import android.widget.AbsListView;
import android.widget.FrameLayout;
import android.widget.ListView;

import com.shopoholic.R;

/**
 * Created by Akash Jain on 19/07/2017.
 */

public class CalendarLayout extends FrameLayout {

    //Unfolded
    public static final int TYPE_OPEN = 0;
    //Fold
    public static final int TYPE_FOLD = 1;
    private static final String TAG = "CalendarLayout";
    private static final Interpolator sInterpolator = new Interpolator() {
        @Override
        public float getInterpolation(float t) {
            t -= 1.0f;
            return t * t * t * t * t + 1.0f;
        }
    };
    public static int type = TYPE_FOLD;
    float oy, ox;
    boolean isClickBtottomView = false;
    int oldY = 0;
    private View view1;
    private ViewGroup view2;
    private CalendarTopView mTopView;
    //Whether it is in sliding
    private boolean isSilde = false;
    private int topHeigth;
    private int itemHeight;
    private int bottomViewTopHeight;
    private int maxDistance;
    private ScrollerCompat mScroller;
    private float mMaxVelocity;
    //    private float mMinVelocity;
    private int activitPotionerId;
    private VelocityTracker mVelocityTracker;

    public CalendarLayout(Context context) {
        super(context);
        //init();
    }

    public CalendarLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(attrs);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();

        final CalendarTopView viewPager = (CalendarTopView) getChildAt(0);

        mTopView = viewPager;
        view1 = (View) viewPager;
        view2 = (ViewGroup) getChildAt(1);
        mTopView.setCaledarTopViewChangeListener(new CalendarTopViewChangeListener() {
            @Override
            public void onLayoutChange(CalendarTopView topView) {
//                CalendarLayout.this.requestLayout();
            }
        });
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        itemHeight = mTopView.getItemHeight();
        topHeigth = view1.getMeasuredHeight();
        maxDistance = topHeigth - itemHeight;

        switch (type) {
            case TYPE_FOLD:
                bottomViewTopHeight = itemHeight;
                break;
            case TYPE_OPEN:
                bottomViewTopHeight = topHeigth;
                break;
        }
        view2.measure(widthMeasureSpec, MeasureSpec.makeMeasureSpec(MeasureSpec.getSize(heightMeasureSpec) - mTopView.getItemHeight(), MeasureSpec.EXACTLY));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        super.onLayout(changed, left, top, right, bottom);
        view2.offsetTopAndBottom(bottomViewTopHeight);
        int[] selectRct = getSelectRect();
        if (type == TYPE_FOLD) {
            view1.offsetTopAndBottom(-selectRct[1]);
        }
    }

    private void init(AttributeSet attrs) {
        if (attrs != null) {
            TypedArray a = getContext().obtainStyledAttributes(attrs, R.styleable.CalendarLayout);
            type = a.getInteger(R.styleable.CalendarLayout_calendar_type, TYPE_FOLD);
            a.recycle();
        }
        final ViewConfiguration vc = ViewConfiguration.get(getContext());
        mMaxVelocity = vc.getScaledMaximumFlingVelocity();
//        mMinVelocity = vc.getScaledMinimumFlingVelocity();
        mScroller = ScrollerCompat.create(getContext(), sInterpolator);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {
        boolean isflag = false;
              switch (ev.getAction()) {
                  case MotionEvent.ACTION_DOWN:
                      oy = ev.getY();
                      ox = ev.getX();
                     // if (!AppSharedPreference.getBoolean(getContext(), AppSharedPreference.PREF_KEY.CALENDAR_PREVIEW)) {
                          isClickBtottomView = isClickView(view2, ev);
                          cancel();
                          activitPotionerId = ev.getPointerId(0);

                          int top = view2.getTop();

                          if (top < topHeigth) {
                              type = TYPE_FOLD;
                             // open();
                          } else {
                              type = TYPE_OPEN;
                             // flod();
                          }
                          if(isClickBtottomView){
                           // toggleFotter();
                          }

                    //  }
                      break;
                  case MotionEvent.ACTION_MOVE:
                      float y = ev.getY();
                      float x = ev.getX();

                      float xdiff = x - ox;
                      float ydiff = y - oy;
                       if (Math.abs(ydiff) > 50 && Math.abs(ydiff) > Math.abs(xdiff)) {
                              isflag = true;
                              if (isClickBtottomView) {
                                  boolean isScroll = isScroll(view2);
                                  if (ydiff > 10) {
                                      //down
                                      if (type == TYPE_OPEN) {
                                          return super.onInterceptTouchEvent(ev);
                                      } else {
                                          if (isScroll) {
                                              return super.onInterceptTouchEvent(ev);
                                          }
                                      }
                                  } else {
                                      //Improvement
                                      if (type == TYPE_FOLD) {
                                          return super.onInterceptTouchEvent(ev);
                                      } else {
                                          if (isScroll) {
                                              return super.onInterceptTouchEvent(ev);
                                          }
                                      }
                                  }
                              }
                          }

                      ox = x;
                      oy = y;
                      break;
                  case MotionEvent.ACTION_UP:
                      break;
              }
       // if (!AppSharedPreference.getBoolean(getContext(), AppSharedPreference.PREF_KEY.CALENDAR_PREVIEW)) {
            return isSilde || isflag || super.onInterceptTouchEvent(ev);
        /*}
        else
            return true;*/
    }


    private boolean isScroll(ViewGroup view2) {
        View fistChildView = view2.getChildAt(0);
        if (fistChildView == null) {
            return false;
        }

        if (view2 instanceof ListView) {
            AbsListView list = (AbsListView) view2;
            if (fistChildView.getTop() != 0) {
                return true;
            } else {
                if (list.getPositionForView(fistChildView) != 0) {
                    return true;
                }
            }

        }

        return false;
    }

    public boolean isClickView(View view, MotionEvent ev) {
        Rect rect = new Rect();
        view.getHitRect(rect);
        boolean isClick = rect.contains((int) ev.getX(), (int) ev.getY());
//        Log.d(TAG, "isClickView() called with: isClick = [" + isClick + "]");
        return isClick;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
       processTouchEvent(event);
        return true;
    }

    public void processTouchEvent(MotionEvent event) {
        //  if (!AppSharedPreference.getBoolean(getContext(), AppSharedPreference.PREF_KEY.CALENDAR_PREVIEW)) {
        if (mVelocityTracker == null) {
            mVelocityTracker = VelocityTracker.obtain();
        }
        mVelocityTracker.addMovement(event);

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                break;
            case MotionEvent.ACTION_MOVE:
                if (isSilde) {
                    return;
                }
                float cy = event.getY();
                int dy = (int) (cy - oy);

                if (dy == 0) {
                    return;
                }
                oy = cy;
                move(dy);

                break;
            case MotionEvent.ACTION_UP:

                if (isSilde) {
                    cancel();
                    return;
                }

                //Judge the speed
                final int pointerId = activitPotionerId;
                mVelocityTracker.computeCurrentVelocity(1000, mMaxVelocity);
                float crrentV = VelocityTrackerCompat.getYVelocity(mVelocityTracker, pointerId);

                if (Math.abs(crrentV) > 2000) {
                    if (crrentV > 0) {
                        open();
                    } else {
                        flod();
                    }
                    cancel();
                    return;
                }

                int top = view2.getTop() - topHeigth;
                int maxd = maxDistance;


                if (Math.abs(top) < maxd / 2) {
                    open();
                } else {
                    flod();
                }
                cancel();

                break;
            case MotionEvent.ACTION_CANCEL:
                cancel();
                break;
        }
        // }
    }

    public void open() {
        startScroll(view2.getTop(), topHeigth);
        type=TYPE_OPEN;
    }

    public void flod() {
        startScroll(view2.getTop(), topHeigth - maxDistance);
        type=TYPE_FOLD;
    }

    private int[] getSelectRect() {
        return mTopView.getCurrentSelectPositon();
    }

    private void move(int dy) {

        int[] selectRect = getSelectRect();
        int itemHeight = mTopView.getItemHeight();

        int dy1 = getAreaValue(view1.getTop(), dy, -selectRect[1], 0);
        int dy2 = getAreaValue(view2.getTop() - topHeigth, dy, -(topHeigth - itemHeight), 0);

        if (dy1 != 0) {
            ViewCompat.offsetTopAndBottom(view1, dy1);
        }

        if (dy2 != 0) {
            ViewCompat.offsetTopAndBottom(view2, dy2);
        }

    }

    private int getAreaValue(int top, int dy, int minValue, int maxValue) {

        // Log.e("move -);

        if (top + dy < minValue) {
            return minValue - top;
        }

        if (top + dy > maxValue) {
            return maxValue - top;
        }
        return dy;
    }

    private void startScroll(int starty, int endY) {

        float distance = endY - starty;
        float t = distance / maxDistance * 600;

        mScroller.startScroll(0, 0, 0, endY - starty, (int) Math.abs(t));
        postInvalidate();
    }

    @Override
    public void computeScroll() {
        super.computeScroll();

        bottomViewTopHeight = view2.getTop();
        if (mScroller.computeScrollOffset()) {
            isSilde = true;
            int cy = mScroller.getCurrY();
            int dy = cy - oldY;
            move(dy);
            oldY = cy;
            postInvalidate();
        } else {
            oldY = 0;
            isSilde = false;
        }
    }

    public void cancel() {
       // if (!AppSharedPreference.getBoolean(getContext(), AppSharedPreference.PREF_KEY.CALENDAR_PREVIEW)) {
            if (mVelocityTracker != null) {
                mVelocityTracker.recycle();
                mVelocityTracker = null;
            }
       // }
    }

}
