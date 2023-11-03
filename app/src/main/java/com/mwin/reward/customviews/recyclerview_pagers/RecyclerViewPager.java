package com.mwin.reward.customviews.recyclerview_pagers;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.PagerSnapHelper;
import androidx.recyclerview.widget.RecyclerView;
import androidx.recyclerview.widget.SnapHelper;

import com.mwin.reward.R;
import com.mwin.reward.async.models.HomeSliderItem;

import java.util.ArrayList;
import java.util.List;
import java.util.Timer;
import java.util.TimerTask;


public class RecyclerViewPager extends RecyclerView {

    private int count;
    private Timer timer = new Timer();
    private PagerAdapter adapter;
    private List<HomeSliderItem> pagerModels = new ArrayList<>();

    private boolean runAuto = false;
    private int colorActiveIndicator = 0xDE000000;
    private int colorInactiveIndicator = 0x33000000;
    private String TAG = "RecyclerViewPager";
    private int millis = 2000;


    public RecyclerViewPager(@NonNull Context context) {
        super(context);
        initState();
    }


    public RecyclerViewPager(@NonNull Context context, @Nullable AttributeSet attrs) {

        super(context, attrs);

        initState(context, attrs);

    }

    public RecyclerViewPager(@NonNull Context context, @Nullable AttributeSet attrs, int defStyle) {

        super(context, attrs, defStyle);

        initState(context, attrs);

    }

    private void initState() {
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(lm);

        setClipToPadding(false);
        setPaddingRelative((int) Const.convertDpToPixel(5, getContext()), 0, (int) Const.convertDpToPixel(5, getContext()), 0);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(this);

    }

    private void initState(Context context, AttributeSet attrs) {
        @SuppressLint("Recycle") final TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.RecyclerViewPager);
        LinearLayoutManager lm = new LinearLayoutManager(getContext());
        lm.setOrientation(LinearLayoutManager.HORIZONTAL);
        setLayoutManager(lm);

        setClipToPadding(false);
        setPaddingRelative((int) Const.convertDpToPixel(5, getContext()), 0, (int) Const.convertDpToPixel(5, getContext()), 0);

        SnapHelper snapHelper = new PagerSnapHelper();
        snapHelper.attachToRecyclerView(this);
        runAuto = a.getBoolean(R.styleable.RecyclerViewPager_svp_runAuto, false);
        colorActiveIndicator = a.getColor(R.styleable.RecyclerViewPager_svp_colorActiveIndicator, 0xDE000000);
        colorInactiveIndicator = a.getColor(R.styleable.RecyclerViewPager_svp_colorInactiveIndicator, 0x33000000);
        millis = a.getInt(R.styleable.RecyclerViewPager_svp_timeMillis, 2000);


    }

    private void scrollToNext() {
        try {
            if (count >= getAdapter().getItemCount() - 1)
                count = -1;

            smoothScrollToPosition(count + 1);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void addItem(HomeSliderItem pagerModel) {
        this.pagerModels.add(pagerModel);
    }

    public void addAll(ArrayList<HomeSliderItem> pagerModel) {
        this.pagerModels.addAll(pagerModel);
    }

    public void start() {
        try {
            adapter = new PagerAdapter(getContext(), pagerModels);
            setAdapter(adapter);
            if (runAuto) {
                if (timer == null)
                    timer = new Timer();

                timer.schedule(new TimerTask() {
                    @Override
                    public void run() {
                        scrollToNext();
                    }
                }, millis, millis);
            } else {
                timer = null;
            }

            addItemDecoration(new Indicator(colorActiveIndicator, colorInactiveIndicator));
            addOnScrollListener(new OnScrollListener() {
                @Override
                public void onScrollStateChanged(@NonNull RecyclerView recyclerView, int newState) {
                    super.onScrollStateChanged(recyclerView, newState);
                    LinearLayoutManager layoutManager = ((LinearLayoutManager) getLayoutManager());
                    int firstVisiblePosition = layoutManager.findFirstVisibleItemPosition();
                    count = firstVisiblePosition;
                }

                @Override
                public void onScrolled(@NonNull RecyclerView recyclerView, int dx, int dy) {
                    super.onScrolled(recyclerView, dx, dy);
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void setAutoRun(boolean auto) {
        this.runAuto = auto;
    }

    public void setColorActiveIndicator(int color) {
        this.colorActiveIndicator = color;
    }

    public void setColorInactiveIndicator(int color) {
        this.colorInactiveIndicator = color;
    }

    public void setTime(int timeMillis) {
        this.millis = millis;
    }

    public void setClear() {
        this.pagerModels.clear();
    }

    public void setOnItemClickListener(PagerAdapter.OnItemClickListener itemClickListener) {
        try {

            ((PagerAdapter) getAdapter()).setOnclickItemListener(itemClickListener);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }
}
