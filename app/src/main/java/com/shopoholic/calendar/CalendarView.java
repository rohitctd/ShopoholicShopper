package com.shopoholic.calendar;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

import com.shopoholic.activities.TimeSlotsActivity;

import java.util.Date;
import java.util.List;



public class CalendarView extends ViewGroup {

    private static final String TAG = "CalendarView";

    private int selectPostion = -1;

    private CalendarAdapter adapter;
    private List<CalendarBean> data;
    private OnItemClickListener onItemClickListener;

    private int row = 6;
    private int column = 7;
    private int itemWidth;
    private int itemHeight;
    private boolean isToday;
    private View mParent;

    public void setParent(View parent) {
        this.mParent = parent;
    }

    public void setNumberOfRows(int numberOfRows) {
        this.row = numberOfRows;
    }

    public interface OnItemClickListener {
        void onItemClick(View view, int postion, CalendarBean bean);
        boolean onItemLongClick(View view, int postion, CalendarBean bean);
    }

    public CalendarView(Context context, int row) {
        super(context);
        this.row = row;
    }

    public void setOnItemClickListener(OnItemClickListener onItemClickListener) {
        this.onItemClickListener = onItemClickListener;
    }

    public int getItemHeight() {
        return itemHeight;
    }

    public CalendarView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setWillNotDraw(false);
    }

    public void setAdapter(CalendarAdapter adapter) {
        this.adapter = adapter;
    }

    public void setData(List<CalendarBean> data,boolean isToday) {
        this.data = data;
        this.isToday=isToday;
        setItem();
        requestLayout();
    }

    public int getSelectedItem(){
        return selectPostion;
    }

    private void setItem() {

        selectPostion = -1;
        if (adapter == null) {
            throw new RuntimeException("adapter is null,please setadapter");
        }

        for (int i = 0; i < data.size(); i++) {
            CalendarBean bean = data.get(i);
            View view = getChildAt(i);
            View chidView = adapter.getView(view, this, bean, i);

            if (view == null || view != chidView) {
                addViewInLayout(chidView, i, chidView.getLayoutParams(), true);
            }

            if(isToday&&selectPostion==-1){
                int[]date=CalendarUtil.getYMD(new Date());
                if(bean.year==date[0]&&bean.month==date[1]&&bean.day==date[2]){
                     selectPostion=i;
                }
            }else {
                if (selectPostion == -1 && bean.day == 1) {
                    selectPostion = i;
                }
            }

            chidView.setSelected(selectPostion==i);
            setItemClick(chidView, i, bean);
        }
    }

    public Object[] getSelect(){
         return new Object[]{getChildAt(selectPostion),selectPostion,data.get(selectPostion)};
    }

    public void setItemClick(final View view, final int potsion, final CalendarBean bean) {
        view.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (selectPostion != -1) {
                    getChildAt(selectPostion).setSelected(false);
                    getChildAt(potsion).setSelected(true);
                }
                selectPostion = potsion;
                updateUI();
                if (onItemClickListener != null) {
                    onItemClickListener.onItemClick(view, potsion, bean);
                }
             /*   if(CalendarLayout.type==TYPE_FOLD){
                    if(selectPostion>=15){
                        return;
                    }else{
                        if (selectPostion != -1) {
                            getChildAt(selectPostion).setSelected(false);
                            getChildAt(potsion).setSelected(true);
                        }
                        selectPostion = potsion;
                        updateUI();
                        if (onItemClickListener != null) {
                            onItemClickListener.onItemClick(view, potsion, bean);
                        }
                    }
                }else{
                    if (selectPostion != -1) {
                        getChildAt(selectPostion).setSelected(false);
                        getChildAt(potsion).setSelected(true);
                    }
                    selectPostion = potsion;
                    updateUI();
                    if (onItemClickListener != null) {
                        onItemClickListener.onItemClick(view, potsion, bean);
                    }
                }*/
            }
        });
        view.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                if (onItemClickListener != null) {
                    return onItemClickListener.onItemLongClick(view, potsion, bean);
                }
                return false;
            }
        });
    }

    void updateUI() {
        if (selectPostion!=-1 && mParent!=null && mParent instanceof CalendarDateView){
            ((CalendarDateView)mParent).setMinHeight((selectPostion / column)==row-1?1:1);
        }
    }

    public int[] getSelectPostion() {
        Rect rect = new Rect();
        try {
            getChildAt(selectPostion).getHitRect(rect);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return new int[]{rect.left, rect.top, rect.right, rect.bottom};
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);

        int parentWidth = MeasureSpec.getSize(MeasureSpec.makeMeasureSpec(widthMeasureSpec, MeasureSpec.EXACTLY));

        itemWidth = parentWidth / column;
        itemHeight = itemWidth;

        View view = getChildAt(0);
        if (view == null) {
            return;
        }
        LayoutParams params = view.getLayoutParams();
        if (params != null && params.height > 0) {
            itemHeight = params.height;
        }
        //setMeasuredDimension(parentWidth, itemHeight * row);
        setMeasuredDimension(parentWidth, itemHeight * row);


        for(int i=0;i<getChildCount();i++){
            View childView=getChildAt(i);
            childView.measure(MeasureSpec.makeMeasureSpec(itemWidth, MeasureSpec.EXACTLY), MeasureSpec.makeMeasureSpec(itemHeight, MeasureSpec.EXACTLY));
        }
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        for (int i = 0; i <getChildCount(); i++) {
            layoutChild(getChildAt(i), i, l, t, r, b);
        }
    }

    private void layoutChild(View view, int postion, int l, int t, int r, int b) {

        int cc = postion % column;
        int cr = postion / column;

        int itemWidth = view.getMeasuredWidth();
        int itemHeight = view.getMeasuredHeight();

        l = cc * itemWidth;
        t = cr * itemHeight;
        r = l + itemWidth;
        b = t + itemHeight;
        view.layout(l, t, r, b);

    }
}
