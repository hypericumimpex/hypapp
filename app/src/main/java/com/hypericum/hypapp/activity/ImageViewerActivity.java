package com.hypericum.hypapp.activity;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;

import com.hypericum.hypapp.R;
import com.hypericum.hypapp.adapter.ProductImageViewPagerAdapter;
import com.hypericum.hypapp.customview.pinchtozoom.ImageMatrixTouchHandler;
import com.hypericum.hypapp.model.CategoryList;
import com.hypericum.hypapp.utils.BaseActivity;
import com.hypericum.hypapp.utils.RequestParamUtils;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import java.util.List;

import butterknife.ButterKnife;
import butterknife.OnClick;

public class ImageViewerActivity extends BaseActivity {
    private int pos;
    private int cat_id;


    /**
     * Step 1: Download and set up v4 support library: http://developer.android.com/tools/support-library/setup.html
     * Step 2: Create ExtendedViewPager wrapper which calls TouchImageView.canScrollHorizontallyFroyo
     * Step 3: ExtendedViewPager is a custom view and must be referred to by its full package name in XML
     * Step 4: Write TouchImageAdapter, located below
     * Step 5. The ViewPager in the XML should be ExtendedViewPager
     */

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_image_viewer);
        ButterKnife.bind(this);
        pos = getIntent().getIntExtra(RequestParamUtils.pos, 0);
        cat_id = getIntent().getIntExtra(RequestParamUtils.cat_id, 0);
        ViewPager mViewPager = (ViewPager) findViewById(R.id.view_pager);

        mViewPager.setAdapter(new TouchImageAdapter(this));
        mViewPager.setCurrentItem(pos);
    }


    @OnClick(R.id.tvImageDone)
    public void tvImageDoneClick() {
        onBackPressed();
    }


    class TouchImageAdapter extends PagerAdapter {

        private List<CategoryList.Image> imageList = ProductImageViewPagerAdapter.list;
        private Context context;
        private LayoutInflater layoutInflater;

        public TouchImageAdapter(Context context) {
            this.context = context;
        }

        @Override
        public int getCount() {
            return imageList.size();
        }

        @Override
        public View instantiateItem(ViewGroup container, int position) {
            layoutInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            View view = layoutInflater.inflate(R.layout.item_full_image, container, false);
            ImageView img = view.findViewById(R.id.ivImage);
            final ProgressBar progress_bar = view.findViewById(R.id.progress_bar);
//        imageView.setImageResource(R.drawable.banner);
            container.addView(view);
            Picasso.with(context).load(imageList.get(position).src).into(img, new Callback() {
                @Override
                public void onSuccess() {
                    progress_bar.setVisibility(View.GONE);
                }

                @Override
                public void onError() {
                    // TODO Auto-generated method stub

                }
            });
            ImageMatrixTouchHandler imageMatrixTouchHandler = new ImageMatrixTouchHandler(context);
            img.setOnTouchListener(imageMatrixTouchHandler);
            return view;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            container.removeView((View) object);
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

    }
}
