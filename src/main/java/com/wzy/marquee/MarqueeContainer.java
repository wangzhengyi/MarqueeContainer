package com.wzy.marquee;

import android.content.Context;
import android.content.res.TypedArray;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageLoader;
import com.wzy.marquee.volley.BitmapCache;
import com.wzy.marquee.volley.VolleyManager;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by wzy on 2016/10/14.
 * 无限轮播的ViewPager容器.
 * 可以继承MarqueeContainer进行自定义,因此MarqueeContainer大部分成员属性为protected类型.
 */
public class MarqueeContainer extends FrameLayout implements View.OnClickListener {
    /**
     * ViewPager播放的View列表.
     */
    protected List<View> mDisplayViewsList;

    /**
     * ViewPager指示器的View列表
     */
    protected List<View> mNvDotsList;

    /**
     * 布局加载器
     */
    protected LayoutInflater mInflater;

    /**
     * 异步图片请求加载器
     */
    protected ImageLoader mImageLoader;

    /**
     * ViewPager控件
     */
    protected ViewPager mViewPager;

    /**
     * 用于存放导航ViewPager的view的LinearLayout
     */
    protected LinearLayout mNavigatorLayout;

    /**
     * 需要展示的图片数量.
     */
    protected int mImagesCount;

    /**
     * 当前ViewPager显示的view位置
     */
    protected int mCurrentIndex;

    /**
     * 图片加载过程中用于默认显示的图片
     */
    protected int mDefaultImageResId = 0;

    /**
     * 图片加载失败时显示的图片
     */
    protected int mErrorImageResId = 0;

    /**
     * 默认指示器图标置灰的图片
     */
    protected int mIndicatorDisableResId = R.drawable.ic_indicator_disable;

    /**
     * 默认指示器图标高亮的图片
     */
    protected int mIndicatorEnableResId = R.drawable.ic_indicator_enable;

    public MarqueeContainer(Context context) {
        this(context, null);
    }

    public MarqueeContainer(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MarqueeContainer(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initAttrs(context, attrs);
        init();
    }

    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.getResources().obtainAttributes(attrs, R.styleable.MarqueeContainer);
        if (ta.hasValue(R.styleable.MarqueeContainer_defaultImageResId)) {
            mDefaultImageResId = ta.getResourceId(R.styleable.MarqueeContainer_defaultImageResId, 0);
        }

        if (ta.hasValue(R.styleable.MarqueeContainer_errorImageResId)) {
            mErrorImageResId = ta.getResourceId(R.styleable.MarqueeContainer_errorImageResId, 0);
        }

        if (ta.hasValue(R.styleable.MarqueeContainer_indicatorEnableResId)) {
            mIndicatorEnableResId = ta.getResourceId(
                    R.styleable.MarqueeContainer_indicatorEnableResId, R.drawable.ic_indicator_enable);
        }

        if (ta.hasValue(R.styleable.MarqueeContainer_indicatorDisableResId)) {
            mIndicatorEnableResId = ta.getResourceId(
                    R.styleable.MarqueeContainer_indicatorDisableResId, R.drawable.ic_indicator_disable);
        }
        ta.recycle();
    }

    private void init() {
        initData();
        initLayout();
    }

    private void initData() {
        mInflater = LayoutInflater.from(getContext());
        mDisplayViewsList = new ArrayList<>();
        mNvDotsList = new ArrayList<>();
        RequestQueue requestQueue = VolleyManager.getInstance(getContext()).getRequestQueue();
        mImageLoader = new ImageLoader(requestQueue, new BitmapCache());
    }

    private void initLayout() {
        View view = mInflater.inflate(R.layout.view_marquee_container_layout, this, false);
        mViewPager = (ViewPager) view.findViewById(R.id.id_mc_viewpager);
        mNavigatorLayout = (LinearLayout) view.findViewById(R.id.id_mc_navigator_layout);
        addView(view);
    }

    public void setImageUrls(List<String> urls) {
        reset();
        initImageWithUrls(urls);
    }

    public void setImageResIds(List<Integer> resIds) {
        reset();
        initImageWithResIds(resIds);
    }

    private void reset() {
        mDisplayViewsList.clear();
        mNvDotsList.clear();
        mNavigatorLayout.removeAllViews();
    }

    protected void initImageWithUrls(List<String> urls) {
        mImagesCount = urls == null ? 0 : urls.size();
        if (mImagesCount == 0) {
            return;
        }

        initNavigatorDots();
        initNetworkImages(urls);
        initViewPager();
    }

    protected void initImageWithResIds(List<Integer> resIds) {
        mImagesCount = resIds == null ? 0 : resIds.size();
        if (mImagesCount == 0) {
            return;
        }

        initNavigatorDots();
        initLocalImages(resIds);
        initViewPager();
    }

    protected void initNavigatorDots() {
        for (int i = 0; i < mImagesCount; i ++) {
            ImageView dot = new ImageView(getContext());
            LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            layoutParams.leftMargin = 6;
            layoutParams.rightMargin = 6;
            dot.setImageResource(mIndicatorDisableResId);
            dot.setLayoutParams(layoutParams);
            mNavigatorLayout.addView(dot);
            mNvDotsList.add(dot);
        }
        if (mNvDotsList.size() > 0) {
            ((ImageView)mNvDotsList.get(0)).setImageResource(mIndicatorEnableResId);
        }
    }

    protected void initNetworkImages(List<String> urls) {
        for (int  i = 0; i < mImagesCount + 2; i ++) {
            ImageView img = new ImageView(getContext());
            img.setOnClickListener(this);
            ImageLoader.ImageListener imageListener = ImageLoader.getImageListener(
                    img, mDefaultImageResId, mErrorImageResId);
            String imgUrl;
            if (i == 0) {
                imgUrl = urls.get(mImagesCount - 1);
            } else if (i == mImagesCount + 1) {
                imgUrl = urls.get(0);
            } else {
                imgUrl = urls.get(i - 1);
            }
            mImageLoader.get(imgUrl, imageListener, 0, 0, ImageView.ScaleType.FIT_XY);
            mDisplayViewsList.add(img);
        }
    }

    protected void initLocalImages(List<Integer> resIds) {
        for (int  i = 0; i < mImagesCount + 2; i ++) {
            ImageView img = new ImageView(getContext());
            img.setScaleType(ImageView.ScaleType.FIT_XY);
            img.setOnClickListener(this);
            int resId;
            if (i == 0) {
                resId = resIds.get(mImagesCount - 1);
            } else if (i == mImagesCount + 1) {
                resId = resIds.get(0);
            } else {
                resId = resIds.get(i - 1);
            }
            img.setImageResource(resId);
            mDisplayViewsList.add(img);
        }
    }

    protected void initViewPager() {
        mViewPager.setAdapter(new MarqueeContainerPagerAdapter());
        mViewPager.addOnPageChangeListener(new MarqueeContainerPageChangeListener());
        mViewPager.setFocusable(true);
        mViewPager.setCurrentItem(1);
        mCurrentIndex = 1;
    }

    protected class MarqueeContainerPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return mImagesCount + 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup container, int position) {
            container.addView(mDisplayViewsList.get(position));
            return mDisplayViewsList.get(position);
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            position = position % mDisplayViewsList.size();
            container.removeView(mDisplayViewsList.get(position));
        }
    }

    protected class MarqueeContainerPageChangeListener implements ViewPager.OnPageChangeListener {

        @Override
        public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

        }

        @Override
        public void onPageSelected(int position) {
            for (int i = 0; i < mNvDotsList.size(); i ++) {
                if (i == position - 1) {
                    ((ImageView)mNvDotsList.get(i)).setImageResource(R.drawable.ic_indicator_enable);
                } else {
                    ((ImageView)mNvDotsList.get(i)).setImageResource(R.drawable.ic_indicator_disable);
                }
            }
        }

        @Override
        public void onPageScrollStateChanged(int state) {
            switch (state) {
                case ViewPager.SCROLL_STATE_IDLE:
                    mCurrentIndex = mViewPager.getCurrentItem();
                    if (mCurrentIndex == 0) {
                        mViewPager.setCurrentItem(mImagesCount, false);
                    } else if (mCurrentIndex == mImagesCount + 1) {
                        mViewPager.setCurrentItem(1, false);
                    }
                    mCurrentIndex = mViewPager.getCurrentItem();
                    break;
                case ViewPager.SCROLL_STATE_DRAGGING:
                    break;
                case ViewPager.SCROLL_STATE_SETTLING:
                    break;
            }
        }
    }

    @Override
    public void onClick(View v) {
        if (mItemClickListener != null) {
            mItemClickListener.onClick(v);
        }
    }

    protected OnMarqueeItemClickListener mItemClickListener;

    public void setItemClickListener(OnMarqueeItemClickListener listener) {
        this.mItemClickListener = listener;
    }

    public interface OnMarqueeItemClickListener {
        void onClick(View v);
    }
}
