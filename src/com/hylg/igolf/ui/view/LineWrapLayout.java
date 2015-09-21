package com.hylg.igolf.ui.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;

public class LineWrapLayout extends ViewGroup
{
  private final int cellHeight = 40;

 /**
  * 知识点： 1、该自定义控件必须实现这个构造方法，不然会报android.view.InflateException: Binary XML
  * file line #异常 2、另外两种构造方法 MyLinearLayout(Context
  * context)、MyLinearLayout(Context context, AttributeSet attrs, int
  * defStyle)可不实现!
  */
  public LineWrapLayout(Context context, AttributeSet attrs)
  {
   super(context, attrs);
  }

  /**
  * 控制子控件的换行
  */
  @Override
  protected void onLayout(boolean changed, int l, int t, int r, int b)
  {
   // TODO Auto-generated method stub
   int left = 0;// 子控件的左上角x坐标
   int top = 0;// 子控件的左上角y坐标
   int count = getChildCount();// 获得子控件的数目
   int remainingWidth = 0;// 计算一行中剩下的宽度
	
	   for (int j = 0; j < count; j++)
	   {
	    View childView = getChildAt(j);
	    // 获取子控件Child的宽高
	    int w = childView.getMeasuredWidth();
	    int h = childView.getMeasuredHeight();
	
	    if (left != 0 && remainingWidth < w)// 如果即将显示的子控件不是位于第一列且该行位置已容不下该控件，则修改坐标参数换行显示！
	    {
	     left = 0;
	     top += (cellHeight + 0);
	    }
	    
	    childView.layout(left, top, left + w, top + h);
	    remainingWidth = r - l - left - w ;
	    left += (w + 2);
	   }
  }

  /**
  * 计算控件及子控件所占区域
  */
  @Override
  protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec)
  {
   // 创建测量参数
   int cellWidthSpec = MeasureSpec.makeMeasureSpec(0,
     MeasureSpec.UNSPECIFIED);// 不指定子控件的宽度
   int cellHeightSpec = MeasureSpec.makeMeasureSpec(cellHeight,
     MeasureSpec.EXACTLY);// 精确定义子控件的高度
   int count = getChildCount();// 记录ViewGroup中Child的总个数

   // 设置子控件Child的宽高
   for (int i = 0; i < count; i++)
   {
    View childView = getChildAt(i);
    childView.measure(cellWidthSpec, cellHeightSpec);
   }

   // 设置容器控件所占区域大小
   setMeasuredDimension(resolveSize(0, widthMeasureSpec),resolveSize(cellHeight * count, heightMeasureSpec));
  }
}