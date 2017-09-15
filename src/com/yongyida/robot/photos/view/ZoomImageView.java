package com.yongyida.robot.photos.view;

import android.content.Context;
import android.content.Intent;
import android.graphics.Matrix;
import android.graphics.RectF;
import android.graphics.drawable.Drawable;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.ScaleGestureDetector;
import android.view.ScaleGestureDetector.OnScaleGestureListener;
import android.view.View;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.ImageView;
import android.view.View.OnTouchListener;
import android.view.ViewConfiguration;

public class ZoomImageView extends ImageView implements OnGlobalLayoutListener,OnScaleGestureListener,OnTouchListener{
	
	private boolean mOnce;
	private Context mContext;
	private float mInitScale;//图片的初始缩放值
	private float mMaxScale;//点击放大的缩放最大值
	private float mMidScale;//双击一次缩放的值
	
	private Matrix mMatrix;//图片平移缩放
	
	private ScaleGestureDetector mScaleGesture;//多指触控的手势
	private GestureDetector mGesture;//双击的手势
	
	public ZoomImageView(Context context) {
		this(context, null);
		// TODO Auto-generated constructor stub
	}
	
	public ZoomImageView(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
		// TODO Auto-generated constructor stub
	}
	
	public ZoomImageView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init(context);
	}

	private void init(Context context) {
		mContext=context;
		mOnce=true;
		mMatrix=new Matrix();
		setScaleType(ScaleType.MATRIX);
		mTouchSlop=ViewConfiguration.get(context).getScaledTouchSlop();
		setOnTouchListener(this);
		mScaleGesture=new ScaleGestureDetector(context, this);
		mGesture=new GestureDetector(context,gestureDetector);
	}
	/**
	 * 双击的手势
	 */
	private SimpleOnGestureListener gestureDetector=new SimpleOnGestureListener(){
		public boolean onDoubleTap(MotionEvent e) {
			if(isAutoScale)
				return true;
			float x=e.getX();
			float y=e.getY();
			if(getScale()<mMidScale){
				postDelayed(new AutoScaleRunnable(mMidScale,x,y), 16);
				isAutoScale=true;
			}else if(getScale()>=mMidScale){
				postDelayed(new AutoScaleRunnable(mInitScale, x, y), 16);
				isAutoScale=true;
			}
			return true;
		};
		public boolean onSingleTapConfirmed(MotionEvent e) {
			Intent intent = new Intent();
			intent.setAction("com.intent.action.photo.finish");
			mContext.sendBroadcast(intent);
			return true;
			
		};
	};
	
	@Override
	protected void onAttachedToWindow() {
		// TODO Auto-generated method stub
		super.onAttachedToWindow();
		getViewTreeObserver().addOnGlobalLayoutListener(this);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		// TODO Auto-generated method stub
		super.onDetachedFromWindow();
		getViewTreeObserver().removeGlobalOnLayoutListener(this);
	}

	@Override
	public void onGlobalLayout() {
		if(mOnce){
			//控件的宽高
			int width=getWidth();
			int height=getHeight();
			
			Drawable drawable = getDrawable();
			if(drawable==null)
				return;
			//图片的宽高
			int bitmapWidth=drawable.getIntrinsicWidth();
			int bitmapHeight=drawable.getIntrinsicHeight();
			/**
			 * 根据图片的宽高和控件的宽高来缩放图片
			 */
			float scale=1.0f;
			if(bitmapWidth <= width && bitmapHeight > height){
				scale=height*1.0f/bitmapHeight;
			}else if(bitmapWidth > width && bitmapHeight <= height){
				scale=width*1.0f/bitmapWidth;
			}else if((bitmapWidth<width && bitmapHeight<height)||(bitmapWidth>width&&bitmapHeight>height)){
				scale=Math.min(width*1.0f/bitmapWidth,height*1.0f/bitmapHeight);
			}
			mInitScale=scale;
			mMaxScale=scale*4;
			mMidScale=scale*2;
			
			/**
			 * 设置缩放后图片在控件的中间显示
			 */
			int translationX=width/2-bitmapWidth/2;
			int translationY=height/2-bitmapHeight/2;
			mMatrix.postTranslate(translationX, translationY);
			mMatrix.postScale(mInitScale, mInitScale, width/2, height/2);
			setImageMatrix(mMatrix);
			mOnce=false;
			Log.e("globle", "ImageWidth="+bitmapWidth+"---ImageHeight="+bitmapHeight+"--scale="+mInitScale);
		}
	}

	/**
	 * 多指触控，将要缩放的比例
	 */
	@Override
	public boolean onScale(ScaleGestureDetector detector) {
		float scaleFactor = detector.getScaleFactor();
		float scale=getScale();
		if(getDrawable()==null)
			return true;
		//可以放大或者缩小
		if((scaleFactor<mMaxScale&getScale()>1.0f)||(scaleFactor>mInitScale&getScale()<1.0f)){
			if(scale*scaleFactor<mInitScale){
				scaleFactor=mInitScale/scale;
			}
			if(scale*scaleFactor>mMaxScale){
				scaleFactor=mMaxScale/scale;
			}
			mMatrix.postScale(scaleFactor, scaleFactor, detector.getFocusX(), detector.getFocusY());
			checkoutImageBorder();
			setImageMatrix(mMatrix);
		}
		return true;
	}

	@Override
	public boolean onScaleBegin(ScaleGestureDetector detector) {
		// TODO Auto-generated method stub
		return true;
	}

	@Override
	public void onScaleEnd(ScaleGestureDetector detector) {
		// TODO Auto-generated method stub
		
	}
	/**
	 * 获取当前图片的缩放比列
	 * @return
	 */
	private float getScale(){
		float[] values=new float[9];
		mMatrix.getValues(values);
		return values[Matrix.MSCALE_X];
	}
	
	/**
	 * 多指触控以后，检查图片边界
	 */
	private void checkoutImageBorder(){
		RectF rectF=getMatrixRectF();
		
		int bitmapWidth=(int) rectF.width();
		int bitmapHeight=(int) rectF.height();
		int width=getWidth();
		int height=getHeight();
		
		float detalX=0;
		float detalY=0;
		
		if(bitmapWidth>=width){
			if(rectF.left>0){
				detalX= -rectF.left;
			}
			if(rectF.right<width){
				detalX=width-rectF.right;
			}
		}
		
		if(bitmapHeight>=height){
			if(rectF.top>0){
				detalY=-rectF.top;
			}
			if(rectF.bottom<height){
				detalY=height-rectF.bottom;
			}
		}
		if(bitmapWidth<width){
			detalX=width/2-rectF.right+bitmapWidth/2;
		}
		if(bitmapHeight<height){
			detalX=height/2-rectF.bottom+bitmapHeight/2;
		}
		mMatrix.postTranslate(detalX, detalY);
	}
	/**
	 * 获取图片缩放后的宽高，和l,t,r,b;
	 * @return
	 */
	private RectF getMatrixRectF(){
		Matrix matrix=mMatrix;
		RectF rectF=new RectF();
		if(getDrawable()!=null){
			rectF.set(0, 0, getDrawable().getIntrinsicWidth(), getDrawable().getIntrinsicHeight());
			matrix.mapRect(rectF);
		}
		return rectF;
	}
	
	/**
	 * 控制图片的自由移动
	 */
	private int mLastTouchPointCount;//多指触控的数量
	private float mLastX;			 //多指触控的中心点的X
	private float mLastY;			 //多指触控的中心点的Y
	
	private int mTouchSlop;			//系统判断是否是移动指令的最小值
	private boolean isCanDrag;		//可以拖拽
	
	@Override
	public boolean onTouch(View v, MotionEvent event) {
		if(mGesture.onTouchEvent(event))
			return true;
		mScaleGesture.onTouchEvent(event);
		int pointerCount = event.getPointerCount();
		float x=0;
		float y=0;
		for(int i=0;i<pointerCount;i++){
			x+=event.getX(i);
			y+=event.getY(i);
		}
		x=x/pointerCount;
		y=y/pointerCount;
		
		if(mLastTouchPointCount!=pointerCount){
			isCanDrag=false;
			mLastX=x;
			mLastY=y;
		}
		mLastTouchPointCount=pointerCount;
		RectF rect = getMatrixRectF();
		switch (event.getAction()) {
		case MotionEvent.ACTION_DOWN:
			//当图片处于放大的状态时，请求Viewpager不要拦截滑动事件
			if(rect.width()>getWidth()+0.01||rect.height()>getHeight()+0.01){
				if(getParent().getParent() instanceof ViewPager){
					getParent().getParent().requestDisallowInterceptTouchEvent(true);
				}
			}
			break;
		case MotionEvent.ACTION_MOVE:
			
			if(rect.width()>getWidth()+0.01||rect.height()>getHeight()+0.01){
				if(getParent().getParent() instanceof ViewPager){
					getParent().getParent().requestDisallowInterceptTouchEvent(true);
				}
			}
			float detalX=x-mLastX;
			float detalY=y-mLastY;
			
			if(isMoveAction(detalX, detalY)){
				isCanDrag=true;
			}
			if(isCanDrag){
				if(getDrawable()!=null){
					if(rect.width()<getWidth()){
						detalX=0;
					}
					if(rect.height()<getHeight()){
						detalY=0;
					}
					mMatrix.postTranslate(detalX, detalY);
					checkoutImageBorder();
					setImageMatrix(mMatrix);
				}
			}
			mLastX=x;
			mLastY=y;
			break;
		case MotionEvent.ACTION_UP:
		case MotionEvent.ACTION_CANCEL:
			mLastTouchPointCount=0;
			break;
		}
		return true;
	}
	/**
	 * 判断移动的距离是不是达到移动的条件
	 * @param detalX
	 * @param detalY
	 * @return
	 */
	private boolean isMoveAction(float detalX, float detalY) {
		return Math.sqrt(detalX*detalX+detalY*detalY)>mTouchSlop;
	}
	/**
	 * 双击缓慢放大或缩小
	 */
	private float tempScale;
	private static final float BIGGER=1.07f;
	private static final float SMALLER=0.93f;
	private boolean isAutoScale;
	private class AutoScaleRunnable implements Runnable{
		private float mTargetScale;
		private float x;
		private float y;
		public AutoScaleRunnable(float targetScale, float x, float y) {
			this.mTargetScale = targetScale;
			this.x = x;
			this.y = y;
			if(getScale()<mTargetScale){
				tempScale=BIGGER;
			}else if(getScale()>mTargetScale){
				tempScale=SMALLER;
			}
		}
		@Override
		public void run() {	
			mMatrix.postScale(tempScale, tempScale,x,y);
			checkoutImageBorder();
			setImageMatrix(mMatrix);
			float currentScale=getScale();
			if(tempScale>1.0f&currentScale<mTargetScale||tempScale<1.0f&currentScale>mTargetScale){
				postDelayed(this, 16);
			}else{
				float scale=mTargetScale/currentScale;
				mMatrix.postScale(scale, scale,x,y);
				checkoutImageBorder();
				setImageMatrix(mMatrix);
				isAutoScale=false;
			}
		}	
	}
}
