package com.yongyida.robot.photos.adapter;

import android.content.Context;
import android.graphics.Point;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.TextView;

import com.tonicartos.widget.stickygridheaders.StickyGridHeadersSimpleAdapter;
import com.yongyida.robot.photos.R;
import com.yongyida.robot.photos.entity.GridItem;
import com.yongyida.robot.photos.view.MyImageView;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Iterator;
import java.util.List;

public class StickyGridAdapter extends BaseAdapter implements
        StickyGridHeadersSimpleAdapter {

    private List<GridItem> list;
    private LayoutInflater mInflater;
    private GridView mGridView;
    private Point mPoint = new Point(0, 0);//用来封装ImageView的宽和高的对象

    private String mToDay;

    public StickyGridAdapter(Context context, List<GridItem> list,
                             GridView mGridView) {
        this.list = list;
        mInflater = LayoutInflater.from(context);
        this.mGridView = mGridView;
        mToDay = getToday();
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder mViewHolder;
        if (convertView == null) {
            mViewHolder = new ViewHolder();
            convertView = mInflater.inflate(R.layout.grid_item, parent, false);
            mViewHolder.mImageView = (MyImageView) convertView
                    .findViewById(R.id.grid_item);
            convertView.setTag(mViewHolder);

            //用来监听ImageView的宽和高
            mViewHolder.mImageView.setOnMeasureListener(new MyImageView.OnMeasureListener() {

                @Override
                public void onMeasureSize(int width, int height) {
                    mPoint.set(width, height);
                }
            });

        } else {
            mViewHolder = (ViewHolder) convertView.getTag();
        }

        String path = list.get(position).getPath();
        mViewHolder.mImageView.setTag(path);

        com.yongyida.robot.photos.utils.ImageLoader.getInstance().
                getInstance(3, com.yongyida.robot.photos.utils.ImageLoader.Type.LIFO).
                loadImage(path, mViewHolder.mImageView);

        return convertView;
    }


    @Override
    public View getHeaderView(int position, View convertView, ViewGroup parent) {
        HeaderViewHolder mHeaderHolder;
        if (convertView == null) {
            mHeaderHolder = new HeaderViewHolder();
            convertView = mInflater.inflate(R.layout.grid_header, parent, false);
            mHeaderHolder.mTvDate = (TextView) convertView
                    .findViewById(R.id.tv_data);
            mHeaderHolder.mTvNum = (TextView) convertView
                    .findViewById(R.id.tv_num);
            convertView.setTag(mHeaderHolder);
        } else {
            mHeaderHolder = (HeaderViewHolder) convertView.getTag();
        }

        String date = list.get(position).getTime();
        if (mToDay.equals(date)) {//判断图片的日期是否是今天，是的话不显示日期，直接显示今天
            mHeaderHolder.mTvDate.setText("今天");
        } else {
            if (date.contains(mToDay.substring(0, 5)))//判断图片的日期是否是今年，如果是今年则日期不显示年份
                mHeaderHolder.mTvDate.setText(date.substring(5));
            else
                mHeaderHolder.mTvDate.setText(date);
        }
        mHeaderHolder.mTvNum.setText(getGroupNum(list.get(position).getTime()) + "张");

        return convertView;
    }

    public static class ViewHolder {
        public MyImageView mImageView;
    }

    public static class HeaderViewHolder {
        public TextView mTvDate;
        public TextView mTvNum;
    }

    @Override
    public long getHeaderId(int position) {
        return list.get(position).getSection();
    }

    /**
     * 获取分组的数目
     *
     * @param time
     * @return
     */
    public int getGroupNum(String time) {
        int i = 0;
        Iterator<GridItem> iterator = list.iterator();
        while (iterator.hasNext()) {
            if (iterator.next().getTime().equals(time)) {
                i++;
            }
        }
        return i;
    }

    /**
     * 获取今天的日期
     *
     * @return
     */
    private String getToday() {
        SimpleDateFormat format = new SimpleDateFormat("yyyy年MM月dd日");
        return format.format(Calendar.getInstance().getTime());
    }

}
