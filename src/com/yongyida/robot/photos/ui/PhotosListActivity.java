package com.yongyida.robot.photos.ui;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.lidroid.xutils.ViewUtils;
import com.lidroid.xutils.view.annotation.ViewInject;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.yongyida.robot.photos.utils.Constants;
import com.yongyida.robot.photos.utils.ImageLoader.Type;
import com.yongyida.robot.photos.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.view.View.OnLongClickListener;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

public class PhotosListActivity extends Activity {

    protected ImageLoader imageLoader = ImageLoader.getInstance();
    DisplayImageOptions options;
    String appPath = Environment.getExternalStorageDirectory().toString() + "/PlayCamera";
    //    public static String bigPic = Environment.getExternalStorageDirectory().toString() + "/BigPhotosCamera";
    String[] imageUrls;
    String[] imageUrls1;

    @ViewInject(R.id.btn_back)
    private Button mBack;

    @ViewInject(R.id.gv_photos)
    private GridView mGridView;


    @ViewInject(R.id.tv_cancel)
    private TextView mCancel;

    @ViewInject(R.id.iv_trash)
    private ImageView mTeash;

    @ViewInject(R.id.tv_select_all)
    private TextView mAll;

    @ViewInject(R.id.tv_top_title)
    private TextView mTitle;

    @ViewInject(R.id.rl_main_del)
    private RelativeLayout mDel;

    private ImageAdapter mAdapter;

    private boolean isScan = false;

    private int flag;

    boolean isSelectMode = false;
    boolean isAll = false;
    private int count = -1;

    private com.yongyida.robot.photos.utils.ImageLoader imageLoad;
    HashMap<String, Boolean> photosSelectMap = new HashMap<String, Boolean>();
    private int photosCount = -1;
    String picName;
    String picAppName;
    String filePath;
    List<String> list = new ArrayList<String>();
    Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case 0:
                    mAdapter.notifyDataSetChanged();
                    break;
                case 1:
                    PhotosListActivity.this.finish();
                    break;
                default:
                    break;
            }

        }

        ;
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
        setContentView(R.layout.photos_layout);
        ViewUtils.inject(this);
        initView();
        imageLoad = com.yongyida.robot.photos.utils.ImageLoader.getInstance(3, Type.LIFO);
        startImagePagerActivity(0);
        finish();
    }

    @Override
    protected void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        rescanPhotoFiles();
        if (mAdapter != null) {
            mAdapter.notifyDataSetChanged();
        }
    }

    @Override
    protected void onDestroy() {
        if (imageLoader != null) {
            imageLoader.clearMemoryCache();
            imageLoader.clearDiscCache();
        }
        super.onDestroy();
    }

    private void initView() {
        options = new DisplayImageOptions.Builder()
                .showStubImage(R.drawable.pictures_no)
                .showImageForEmptyUri(R.drawable.ic_empty)
                .showImageOnFail(R.drawable.ic_error).cacheInMemory(true)
                .cacheOnDisc(true).bitmapConfig(Bitmap.Config.RGB_565)
                .build();

        flag = getIntent().getExtras().getInt("key");
        rescanPhotoFiles();
        mAdapter = new ImageAdapter();
        mGridView.setAdapter(mAdapter);
        mDel.setVisibility(View.GONE);
        mTitle.setText(Constants.yearMouths.get(flag));

        addListener();

    }

    @Override
    protected void onNewIntent(Intent intent) {
        flag = getIntent().getExtras().getInt("key");
        rescanPhotoFiles();
        mAdapter = new ImageAdapter();
        mGridView.setAdapter(mAdapter);
        mDel.setVisibility(View.GONE);
        mTitle.setText(Constants.yearMouths.get(flag));
        addListener();
    }

    private void addListener() {
        mBack.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                PhotosListActivity.this.finish();
            }
        });

        mAll.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                updateState();
            }
        });

        mCancel.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                cancel(false);
            }
        });

        mTeash.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                boolean hasSelectedPhotos = false;
                if (photosSelectMap.size() != 0) {
                    for (int i = 0; i < photosSelectMap.size(); i++) {
                        if (photosSelectMap.get(imageUrls[i])) {
                            hasSelectedPhotos = true;
                            break;
                        }
                    }
                    if (hasSelectedPhotos) {
                        deletePhotos();
                    } else {
                        Toast.makeText(PhotosListActivity.this, "主人你还没有选择照片！", Toast.LENGTH_SHORT).show();
                    }
                }

            }
        });


    }

    private void cancel(boolean isClear) {
        mDel.setVisibility(View.GONE);
        mBack.setVisibility(View.VISIBLE);
        mAll.setText("全选");
        isAll = false;
        if (photosSelectMap.size() != 0) {
            for (int i = 0; i < imageUrls.length; i++) {
                photosSelectMap.put(imageUrls[i], false);

            }
        }
        isSelectMode = false;
        if (isClear) {
            handler.sendEmptyMessage(1);
        } else {
            handler.sendEmptyMessage(0);
        }

    }

    private void updateState() {
        if (!isAll) {
            isAll = true;
            if (photosSelectMap.size() != 0) {
                for (int i = 0; i < imageUrls.length; i++) {
                    photosSelectMap.put(imageUrls[i], true);

                }
            }
            mAll.setText("全不选");
        } else {
            isAll = false;
            if (photosSelectMap.size() != 0) {
                for (int i = 0; i < imageUrls.length; i++) {
                    photosSelectMap.put(imageUrls[i], false);

                }
            }

            mAll.setText("全选");
        }

        handler.sendEmptyMessage(0);
    }

    private void deletePhotos() {

        final AlertDialog dialog = new AlertDialog.Builder(PhotosListActivity.this).create();
        dialog.show();
        dialog.getWindow().setContentView(R.layout.mydialog);
        dialog.getWindow().findViewById(R.id.delete_mydialog).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                deleteFiles();
                dialog.dismiss();
            }
        });
        dialog.getWindow().findViewById(R.id.cancle_mydialog).setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                // TODO Auto-generated method stub
                dialog.dismiss();

                cancel(false);

            }
        });
        dialog.show();
        dialog.setCancelable(false);
    }

    private void rescanPhotoFiles() {
        Constants.listFile = new File(Constants.PHOTO_PATH);
        Constants.photos = Constants.listFile.listFiles();
        int count = Constants.photos.length;
        if (Constants.photos != null && (count != 0)) {
            photosCount = Constants.photos.length;
            Constants.map.clear();
            Constants.yearMouths.clear();
            for (int i = 0; i < photosCount; i++) {
                int num = Constants.photos[i].getAbsolutePath().lastIndexOf("/");
                String picName = Constants.photos[i].getAbsolutePath().substring(num + 1);
                String mouth = picName.substring(0, 4) + "年" + picName.substring(4, 6) + "月";
                if (!Constants.yearMouths.contains(mouth)) {
                    Constants.yearMouths.add(mouth);
                }
                if (Constants.map.get(mouth) == null) {
                    List<String> list = new ArrayList<String>();
                    list.clear();
                    list.add(picName);
                    Constants.map.put(mouth, list);
                } else {
                    Constants.map.get(mouth).add(picName);
                }
            }
            Collections.sort(Constants.yearMouths, Collections.reverseOrder());
        }
        if (count != 0) {
            changeImageUrls();
        }
    }

    private void changeImageUrls() {

        if (flag >= Constants.yearMouths.size()) {
            flag = 0;
            list = Constants.map.get(Constants.yearMouths.get(flag));
            imageUrls = new String[list.size()];
            imageUrls1 = new String[list.size()];
            photosSelectMap.clear();
            for (int i = 0; i < imageUrls.length; i++) {
                imageUrls[i] = "file://" + Constants.PHOTO_PATH + "/" + list.get(i);
                imageUrls1[i] = Constants.PHOTO_PATH + "/" + list.get(i);
                photosSelectMap.put(imageUrls[i], false);
            }
            Arrays.sort(imageUrls, Collections.reverseOrder());
            Arrays.sort(imageUrls1, Collections.reverseOrder());
        } else {
            list = Constants.map.get(Constants.yearMouths.get(flag));
            imageUrls = new String[list.size()];
            imageUrls1 = new String[list.size()];
            photosSelectMap.clear();
            for (int i = 0; i < imageUrls.length; i++) {
                imageUrls[i] = "file://" + Constants.PHOTO_PATH + "/" + list.get(i);
                imageUrls1[i] = Constants.PHOTO_PATH + "/" + list.get(i);
                photosSelectMap.put(imageUrls[i], false);

            }
            Arrays.sort(imageUrls, Collections.reverseOrder());
            Arrays.sort(imageUrls1, Collections.reverseOrder());
        }
    }

    private void deleteFiles() {
        boolean isALL = false;
        if (imageUrls.length != 0 && photosSelectMap.size() != 0) {

            int count = 0;
            int size = photosSelectMap.size();

            List<String> imgFilePath = new ArrayList<String>();
            imgFilePath = Arrays.asList(imageUrls);
            Iterator<String> iterator = imgFilePath.iterator();
            while (iterator.hasNext()) {
                boolean isDeleted = false;
                String imgString = iterator.next();
                if (photosSelectMap.get(imgString)) {
                    count++;


                    filePath = getImgPath(imgString);
                    File file = new File(filePath);
                    if (file != null && file.exists() && file.isFile()) {
                        file.delete();
                        photosSelectMap.remove(imgString);
                    }

//					picName = getImgName(filePath);
//					File picFile = new File(picName);
//					if (picFile != null && picFile.exists() && picFile.isFile()) {
//						picFile.delete();
//					}

                    picAppName = getAppImgName(filePath);
                    File picAppFile = new File(picAppName);
                    if (picAppFile != null && picAppFile.exists() && picAppFile.isFile()) {
                        picAppFile.delete();
                    }
                }
//				else{
//					newArrays.add(imgString);
//				}
            }

            Intent intent = new Intent();
            intent.setAction("com.yydrobot.resource.close");
            PhotosListActivity.this.sendBroadcast(intent);
            PhotosListActivity.this.sendBroadcast(new Intent(Intent.ACTION_MEDIA_MOUNTED,
                    Uri.parse("file://" + Environment.getExternalStorageDirectory())));


            rescanPhotoFiles();
            isSelectMode = false;

            if (size == count && count != 0) {
                isALL = true;
            }

            if (isALL) {
                handler.sendEmptyMessage(1);
            } else {
                handler.sendEmptyMessage(0);
                cancel(false);
            }

        }
    }

    private String getImgPath(String imgUrl) {
        String imgPath = imgUrl.substring(7);
        return imgPath;
    }

//	private String getImgName(String filePath){
//		int num = filePath.lastIndexOf("/");
//		String picName = filePath.substring(num);
//		return bigPic+picName;
//	}

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

    class ImageAdapter extends BaseAdapter {


        @Override
        public int getCount() {

            if (imageUrls != null) {
                return imageUrls.length;
            } else {
                return 0;
            }
        }

        @Override
        public Object getItem(int position) {
            if (Constants.yearMouths != null) {
                return imageUrls[position];
            } else {
                return null;
            }
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = LayoutInflater.from(PhotosListActivity.this)
                        .inflate(R.layout.photos_gridview_item, null);
                holder = new ViewHolder();
                holder.photo = (ImageView) convertView.findViewById(R.id.iv_photos);
                holder.del = (ImageView) convertView.findViewById(R.id.iv_photos_del);

                if (imageUrls1 != null) {
                    holder.path = imageUrls1[position];
                }

                convertView.setTag(holder);
            } else {

                holder = (ViewHolder) convertView.getTag();

            }

            if (imageUrls1 != null) {
//				imageLoader.displayImage(imageUrls[position],holder.photo, options);
                if (holder.path != imageUrls1[position]) {
                    holder.photo.setImageResource(R.drawable.pictures_no);
                }
                com.yongyida.robot.photos.utils.ImageLoader.getInstance().
                        getInstance(3, com.yongyida.robot.photos.utils.ImageLoader.Type.LIFO).
                        loadImage(imageUrls1[position], holder.photo);
                holder.path = imageUrls1[position];
            }

//			Glide.with(PhotosListActivity.this)
//	            .load(imageUrls1[position])
//	            .diskCacheStrategy(DiskCacheStrategy.SOURCE)
//	            .into(holder.photo);

            if (isSelectMode == false) {
                holder.del.setVisibility(View.GONE);
                final ImageView imgDel = holder.del;
                holder.photo.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        Constants.listFile = new File(Constants.PHOTO_PATH);
                        Constants.photos = Constants.listFile.listFiles();
                        int count = Constants.photos.length;

                        if (photosCount == count) {
                            startImagePagerActivity(position);
                        } else {
                            if (count == 0) {
                                Toast.makeText(getApplication(), "主人，照片全部被手机app删除了！", Toast.LENGTH_SHORT).show();
                                PhotosListActivity.this.finish();

                            } else {
                                rescanPhotoFiles();
                                handler.sendEmptyMessage(0);
                                Toast.makeText(getApplication(), "手机app删除照片，更新相册！", Toast.LENGTH_SHORT).show();
                            }
                        }
                    }


                });
                holder.photo.setOnLongClickListener(new OnLongClickListener() {

                    @Override
                    public boolean onLongClick(View v) {

                        handler.sendEmptyMessage(0);
                        photosSelectMap.put(imageUrls[position], true);
                        isSelectMode = true;
                        imgDel.setVisibility(View.VISIBLE);

                        mBack.setVisibility(View.GONE);
                        mDel.setVisibility(View.VISIBLE);


                        handler.sendEmptyMessage(0);
                        return false;
                    }
                });

            } else {
                holder.del.setVisibility(View.VISIBLE);
                if (photosSelectMap.get(imageUrls[position])) {
                    holder.del.setImageResource(R.drawable.selecting_down);
                } else {
                    holder.del.setImageResource(R.drawable.selecting);
                }

                final ImageView imgDel = holder.del;
                imgDel.setOnClickListener(new OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (!photosSelectMap.get(imageUrls[position])) {
                            photosSelectMap.put(imageUrls[position], true);
                            imgDel.setImageResource(R.drawable.selecting_down);
                        } else {
                            photosSelectMap.put(imageUrls[position], false);
                            imgDel.setImageResource(R.drawable.selecting);
                        }
                    }
                });
                holder.photo.setOnClickListener(new OnClickListener() {

                    @Override
                    public void onClick(View v) {
                        if (!photosSelectMap.get(imageUrls[position])) {
                            photosSelectMap.put(imageUrls[position], true);
                            imgDel.setImageResource(R.drawable.selecting_down);
                        } else {
                            photosSelectMap.put(imageUrls[position], false);
                            imgDel.setImageResource(R.drawable.selecting);
                        }
                    }
                });
            }

            return convertView;
        }


    }


    class ViewHolder {
        ImageView photo, del;
        String path;
    }

    private void startImagePagerActivity(int position) {
        // TODO Auto-generated method stub
        if (imageUrls.length > 0 && imageUrls.length > position) {
            ArrayList<String> imagePathList = new ArrayList<String>();
            for (int i = 0; i < imageUrls.length; i++) {
                imagePathList.add(imageUrls[i]);
            }
            Intent intent = new Intent(this, ImagePagerActivity.class);
//			intent.putExtra("image_urls", imageUrls);
            intent.putStringArrayListExtra("image_urls", imagePathList);
            intent.putExtra("image_index", position);
            intent.putExtra("isVoice", false);
            startActivity(intent);
        }

    }


}
