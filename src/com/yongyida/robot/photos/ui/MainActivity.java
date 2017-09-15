package com.yongyida.robot.photos.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.ListIterator;
import java.util.Map;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.yongyida.robot.photos.R;
import com.yongyida.robot.photos.adapter.StickyGridAdapter;
import com.yongyida.robot.photos.entity.GridItem;
import com.yongyida.robot.photos.utils.Constants;
import com.yongyida.robot.photos.utils.PathComparator;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class MainActivity extends Activity {
    DisplayImageOptions options;
    String appPath = Environment.getExternalStorageDirectory().toString() + "/PlayCamera";
    @ViewInject(R.id.iv_back)
    private ImageView mBack;

    @ViewInject(R.id.gv_photos)
    private GridView mGridView;

    @ViewInject(R.id.RLayout_time_line)
    private RelativeLayout mTimeLine;

    @ViewInject(R.id.RLayout_no_pic)
    private RelativeLayout mNoPic;

    private StickyGridAdapter mAdapter;

    private List<GridItem> mGirdList = new ArrayList<GridItem>();
    private static int section = 1;
    private Map<String, Integer> sectionMap = new HashMap<String, Integer>();

    // 接收广播
    public static void actionStart(Context sontext) {
        Intent intent = new Intent(sontext, MainActivity.class);
        Log.d("success", "public static void actionStart(Context sontext)");
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        sontext.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main_layout);
        ViewUtils.inject(this);
        initView();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        scanPhotos();
        mAdapter.notifyDataSetChanged();
        showView();
    }

    @Override
    protected void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }

    private void initView() {
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.pictures_no)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
                .cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();
        scanPhotos();
        addListener();
        mAdapter = new StickyGridAdapter(MainActivity.this, mGirdList, mGridView);
        mGridView.setAdapter(mAdapter);
        showView();
    }

    private void addListener() {
        mBack.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.this.finish();
            }
        });

        mGridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                startImagePagerActivity(position);
            }
        });
    }

    private String getImgPath(String imgUrl) {

        String imgPath = imgUrl.substring(7);
        return imgPath;
    }

    private String getAppImgName(String filePath) {
        int num = filePath.lastIndexOf("/");
        String appImgName = filePath.substring(num);
        return appPath + appImgName;
    }

    private String getPhotosName(String filePath) {
        int num = filePath.lastIndexOf("/");
        String picName = filePath.substring(num + 1);
        return picName;
    }

    private String getPhotosTime(String filePath) {
        int num = filePath.lastIndexOf("/");
        String picTime = filePath.substring(num + 1, num + 5) + "年"
                + filePath.substring(num + 5, num + 7) + "月"
                + filePath.substring(num + 7, num + 9) + "日";
        return picTime;
    }

    private Boolean scanPhotos() {

        Constants.listFile = new File(Constants.PHOTO_PATH);
        //拿到所有照片文件
        Constants.photos = Constants.listFile.listFiles();
        if (Constants.photos != null) {
            //imageUrls所有照片路径
            String[] imageUrls = getPhotosList(Constants.photos);
            return true;
        }
        return false;
    }

    @SuppressWarnings("unused")
    private String[] getPhotosList(File[] photoFiles) {
        String[] photosList = null;
        mGirdList.clear();
        if (photoFiles != null) {
            photosList = new String[photoFiles.length];
            Constants.map.clear();
            Constants.yearMouths.clear();
            for (int i = 0; i < photoFiles.length; i++) {
                String path = photoFiles[i].getAbsolutePath();//获取图片路径
                GridItem mGridItem = new GridItem(path, getPhotosTime(path));
                mGirdList.add(mGridItem);
                photosList[i] = "file://" + path;
                String listName = getPhotosName(photoFiles[i].getAbsolutePath());
                String mouth = listName.substring(0, 4) + "." + listName.substring(4, 6);
                //分离不同月份
                if (!Constants.yearMouths.contains(mouth)) {
                    Constants.yearMouths.add(mouth);
                }
                //封装同月份的照片到一个map的key中
                if (Constants.map.get(mouth) == null) {
                    List<String> list = new ArrayList<String>();
                    list.clear();
                    list.add(listName);
                    Constants.map.put(mouth, list);

                } else {
                    Constants.map.get(mouth).add(listName);

                }

            }
            //倒序月份
            Collections.sort(Constants.yearMouths, Collections.reverseOrder());
            Collections.sort(mGirdList, new PathComparator());

            for (int i = 0; i < Constants.yearMouths.size(); i++) {
                Collections.sort(Constants.map.get(Constants.yearMouths.get(i)));
            }

            for (ListIterator<GridItem> it = mGirdList.listIterator(); it.hasNext(); ) {
                GridItem mGridItem = it.next();
                String ym = mGridItem.getTime();
                if (!sectionMap.containsKey(ym)) {
                    mGridItem.setSection(section);
                    sectionMap.put(ym, section);
                    section++;
                } else {
                    mGridItem.setSection(sectionMap.get(ym));
                }
            }
        }
        return photosList;
    }

    private void showView(){
        if(mAdapter.getCount()>0){
            mTimeLine.setVisibility(View.VISIBLE);
            mNoPic.setVisibility(View.INVISIBLE);
        }else{
            mTimeLine.setVisibility(View.INVISIBLE);
            mNoPic.setVisibility(View.VISIBLE);
        }
    }

    private void startImagePagerActivity(int position) {
        // TODO Auto-generated method stub
        ArrayList<String> imagePathList = new ArrayList<String>();
        for (int i = 0; i < mGirdList.size(); i++) {
            imagePathList.add("file://" + mGirdList.get(i).getPath());
        }
        Intent intent = new Intent(MainActivity.this, ImagePagerActivity.class);
        intent.putStringArrayListExtra("image_urls", imagePathList);
        intent.putExtra("image_index", position);
        intent.putExtra("isVoice", false);
        startActivity(intent);
    }

}
