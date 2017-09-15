package com.yongyida.robot.photos.ui;

import com.yongyida.robot.photos.view.KCornerDialog;
import com.yongyida.robot.photos.view.PinchImageView;
import com.yongyida.robot.photos.view.ZoomImageView;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;
import com.yongyida.robot.photos.R;
import com.yongyida.robot.photos.utils.Constants;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Parcelable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.app.FragmentStatePagerAdapter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class ImagePagerActivity extends FragmentActivity {

    private final String TAG = "ImagePagerActivity";

    private static final String STATE_POSITION = "STATE_POSITION";
    public static final String EXTRA_IMAGE_INDEX = "image_index";
    public static final String EXTRA_IMAGE_URLS = "image_urls";

    private ViewPager mPager;
    private int pagerPosition;
    private RelativeLayout mTitleRLayout;
    private TextView indicator;
    //	ZoomImageView imageView;
    private KCornerDialog mDeleteDialog;//删除图片对话框

    PinchImageView imageView;
    ImagePagerAdapter mAdapter;
    //    String[] urls;
    ArrayList<String> urls = new ArrayList<String>();

    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            // TODO Auto-generated method stub
            if (intent.getAction().equals("com.intent.action.photo.finish")) {
                Constants.listFile = new File(Constants.PHOTO_PATH);
                Constants.photos = Constants.listFile.listFiles();
                int count = Constants.photos.length;
                if (count == 0) {
                    Toast.makeText(getApplication(), "主人，照片全部被手机app删除了!", Toast.LENGTH_SHORT).show();
                }

                ImagePagerActivity.this.finish();

            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.image_detail_pager);
        initView();
        setListener();

        pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
//        urls = getIntent().getStringArrayExtra(EXTRA_IMAGE_URLS);
        urls = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS);
        if (urls == null || urls.size() <= 0) return;

        mAdapter = new ImagePagerAdapter(urls);
        mPager.setAdapter(mAdapter);
        updateUI();
        if (savedInstanceState != null) {
            pagerPosition = savedInstanceState.getInt(STATE_POSITION);
        }


    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        pagerPosition = getIntent().getIntExtra(EXTRA_IMAGE_INDEX, 0);
        urls = getIntent().getStringArrayListExtra(EXTRA_IMAGE_URLS);
        if (urls != null || urls.size() >= 1) {
            mAdapter.notifyDataSetChanged();
            updateUI();
        }

    }

    private void initView() {
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.intent.action.photo.finish");
        registerReceiver(myBroadcastReceiver, filter);

        mPager = (ViewPager) findViewById(R.id.pager);
        mTitleRLayout = (RelativeLayout) findViewById(R.id.RLayout_title);
        indicator = (TextView) findViewById(R.id.indicator);
    }

    private void updateUI() {
        CharSequence text = getString(R.string.viewpager_indicator, pagerPosition + 1, mPager
                .getAdapter().getCount());
        indicator.setText(text);
        mPager.setCurrentItem(pagerPosition);
    }

    private void setListener() {
        // 更新下标
        mPager.setOnPageChangeListener(new OnPageChangeListener() {

            @Override
            public void onPageScrollStateChanged(int arg0) {
            }

            @Override
            public void onPageScrolled(int arg0, float arg1, int arg2) {
            }

            @Override
            public void onPageSelected(int arg0) {
                CharSequence text = getString(R.string.viewpager_indicator,
                        arg0 + 1, mPager.getAdapter().getCount());
                indicator.setText(text);
            }

        });
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        unregisterReceiver(myBroadcastReceiver);
    }

    @Override
    public void onSaveInstanceState(Bundle outState) {
        outState.putInt(STATE_POSITION, mPager.getCurrentItem());
    }

    private class ImagePagerAdapter extends PagerAdapter {

        private List<String> images;
        private LayoutInflater inflater;

        ImagePagerAdapter(List<String> images) {
            this.images = images;
            inflater = getLayoutInflater();
        }

        @Override
        public int getItemPosition(Object object) {
            return POSITION_NONE;
        }

        @Override
        public void destroyItem(ViewGroup container, int position, Object object) {
            ((ViewPager) container).removeView((View) object);
        }

        @Override
        public void finishUpdate(View container) {
        }

        @Override
        public int getCount() {
            return images.size();
        }

        @Override
        public Object instantiateItem(ViewGroup view, int position) {
            View imageLayout = inflater.inflate(R.layout.item_pager_image, view, false);
            imageView = (PinchImageView) imageLayout.findViewById(R.id.image_pager);

            imageView.setOnClickListener(new OnClickListener() {

                @Override
                public void onClick(View v) {
//					ImagePagerActivity.this.finish();
                    if (mTitleRLayout.getVisibility() == View.INVISIBLE) {
                        mTitleRLayout.setVisibility(View.VISIBLE);
                    } else {
                        mTitleRLayout.setVisibility(View.INVISIBLE);
                    }
                }
            });
            final ProgressBar spinner = (ProgressBar) imageLayout.findViewById(R.id.loading);
            ImageLoader.getInstance().displayImage(images.get(position), imageView,
                    new ImageLoadingListener() {
                        @Override
                        public void onLoadingStarted(String imageUri, View view) {
                            spinner.setVisibility(View.VISIBLE);
                        }

                        @Override
                        public void onLoadingFailed(String imageUri, View view,
                                                    FailReason failReason) {
                            String message = null;
                            switch (failReason.getType()) { // 获取图片失败类型
                                case IO_ERROR: // 文件I/O错误
                                    message = "文件打开错误！";
                                    break;
                                case DECODING_ERROR: // 解码错误
                                    message = "解码错误！";
                                    break;
                                case NETWORK_DENIED: // 网络延迟
                                    message = "Downloads are denied";
                                    break;
                                case OUT_OF_MEMORY: // 内存不足
                                    message = "内存不足！";
                                    break;
                                case UNKNOWN: // 原因不明
                                    message = "Unknown error";
                                    break;
                            }
                            Toast.makeText(ImagePagerActivity.this, message,
                                    Toast.LENGTH_SHORT).show();

                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingComplete(String imageUri,
                                                      View view, Bitmap loadedImage) {
                            spinner.setVisibility(View.GONE);
                        }

                        @Override
                        public void onLoadingCancelled(String arg0, View arg1) {
                            // TODO Auto-generated method stub

                        }
                    });

            ((ViewPager) view).addView(imageLayout, 0);
            return imageLayout;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view.equals(object);
        }

        @Override
        public void restoreState(Parcelable state, ClassLoader loader) {
        }

        @Override
        public Parcelable saveState() {
            return null;
        }

        @Override
        public void startUpdate(View container) {
        }
    }

    public void onBackIvClk(View v) {
        finish();
    }

    /**
     * 删除图片
     */
    public void onDelIvClk() {
        int currentPosition = mPager.getCurrentItem();
        String imgPath = urls.get(currentPosition).substring(7);
        File file = new File(imgPath);
        if (file != null && file.exists() && file.isFile()) {
            file.delete();
            urls.remove(currentPosition);
            Toast.makeText(getApplication(), R.string.delete_success, Toast.LENGTH_SHORT).show();
            if (urls.size() > 0) {
                if (currentPosition > 0)//如果现在当前的位置大于0，则删除之后viewpager的位置为当前位置-1
                    pagerPosition = currentPosition - 1;
                else                    //如果现在当前的位置等于0，则删除之后viewpager的位置为0
                    pagerPosition = 0;
                mAdapter.notifyDataSetChanged();
                updateUI();
            } else {
                finish();
            }
        }
    }

    /**
     * 显示删除图片对话框
     */
    public void showDeleteDialog(View v) {
        LayoutInflater inflater = (LayoutInflater) getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View dialogLayout = inflater.inflate(R.layout.dialog_alert, (ViewGroup) findViewById(R.id.alert_root_RLayout));
        Button btnCancle = (Button) dialogLayout.findViewById(R.id.dialog_alert_btn_cancle);
        Button btnConfirm = (Button) dialogLayout.findViewById(R.id.dialog_alert_btn_confirm);

        mDeleteDialog = new KCornerDialog(this, 0, 0, dialogLayout, R.style.KCornerDialog);
        mDeleteDialog.show();
        btnCancle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (mDeleteDialog != null && mDeleteDialog.isShowing())
                    mDeleteDialog.dismiss();
            }
        });
        btnConfirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                onDelIvClk();
                mDeleteDialog.dismiss();
            }
        });

    }

}