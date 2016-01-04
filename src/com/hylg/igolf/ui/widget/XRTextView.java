package com.hylg.igolf.ui.widget;

import org.json.JSONArray;  


import com.hylg.igolf.MainApp;
import com.hylg.igolf.R;

import android.content.Context;  
import android.graphics.Canvas;  
import android.graphics.Color;  
import android.graphics.Paint;  
import android.text.TextPaint;
import android.util.AttributeSet;  
import android.view.View;  
import android.widget.TextView;  
  
public class XRTextView extends TextView{  
      
    private final String namespace = "rong.android.TextView";  
    private String text;  
    private float textSize;  
    private float paddingLeft;  
    private float paddingRight;  
    private float marginLeft;  
    private float marginRight;  
    private int textColor;  
    private JSONArray colorIndex;  
    private Paint paint1 = new Paint();  
    private Paint paintColor = new Paint();  
    
    private int lenth1 = 0,lenth2 = 0;
    
    private TextPaint textPaint = new TextPaint(paintColor);
    
    private float textShowWidth;  
    private float Spacing = 1;
    private float LineSpacing = 1.2f;//行与行的间距
      
    public XRTextView(Context context, AttributeSet attrs) {  
        super(context, attrs);  
        text = attrs.getAttributeValue(
                "http://schemas.android.com/apk/res/android", "text");  
        textSize = attrs.getAttributeIntValue(namespace, "textSize", (int) (15 * MainApp.getInstance().getGlobalData().getDisplayMetrics().density));//字体大小
        textColor = attrs.getAttributeIntValue(namespace, "textColor",Color.parseColor("#666666"));//字体颜色
        paddingLeft = attrs.getAttributeIntValue(namespace, "paddingLeft", 0);  
        paddingRight = attrs.getAttributeIntValue(namespace, "paddingRight", 0);  
        marginLeft = attrs.getAttributeIntValue(namespace, "marginLeft", 0);  
        marginRight = attrs.getAttributeIntValue(namespace, "marginRight", 0);  
        paint1.setTextSize(textSize);  
        paint1.setColor(textColor);
        paint1.setAntiAlias(true);  
        paintColor.setAntiAlias(true);  
        paintColor.setTextSize(textSize);  
        paintColor.setColor(context.getResources().getColor(R.color.color_friend_item_praiser_name));
        
        
    }  
      
    public XRTextView(Context context, float textSize, int textColor,   
            float paddingLeft, float paddingRight, float marginLeft, float marginRight){  
        super(context);  
        this.textSize = textSize;  
        this.textColor = textColor;  
        this.paddingLeft = paddingLeft;  
        this.paddingRight = paddingRight;  
        this.marginLeft = marginLeft;  
        this.marginRight = marginRight;  
        paint1.setTextSize(textSize);  
        paint1.setColor(textColor);  
        paint1.setAntiAlias(true);

        paintColor.setAntiAlias(true);  
        paintColor.setTextSize(textSize);  
        paintColor.setColor(context.getResources().getColor(R.color.color_friend_item_praiser_name));
    }  
  
      
    public JSONArray getColorIndex() {  
        return colorIndex;  
    }  
  
    public void setColorIndex(JSONArray colorIndex) {  
        this.colorIndex = colorIndex;  
    }  
    /** 
     * 传入一个索引，判断当前字是否被高亮 
     * @param index 
     * @return 
     * @throws Exception
     */  
    public boolean isColor(int index) throws Exception{
        if(colorIndex == null){  
            return false;  
        }  
        for(int i = 0 ; i < colorIndex.length() ; i ++){  
            JSONArray array = colorIndex.getJSONArray(i);  
            int start = array.getInt(0);  
            int end = array.getInt(1)-1;  
            if(index >= start && index <= end){  
                return true;  
            }  
              
        }  
          
          
        return false;  
    }  
      
  
    @Override  
    protected void onDraw(Canvas canvas) {  
//      super.onDraw(canvas);  
        View view=(View)this.getParent();  
        textShowWidth=view.getMeasuredWidth() - paddingLeft - paddingRight - marginLeft - marginRight;  
        int lineCount = 0;  
          
        text = this.getText().toString();//.replaceAll("\n", "\r\n");  
        if(text==null)return;  
        char[] textCharArray = text.toCharArray();  
        // 已绘的宽度  
        float drawedWidth = 0;  
        float charWidth;  
        for (int i = 0; i < textCharArray.length; i++) {  
            charWidth = paint1.measureText(textCharArray, i, 1);  
              
            if(textCharArray[i]=='\n'){  
                lineCount++;  
                drawedWidth = 0;  
                continue;  
            }  
            if (textShowWidth - drawedWidth < charWidth) {  
                lineCount++;  
                drawedWidth = 0;  
            }  
            boolean color = false;  
            try {  
                color = isColor(i);  
            } catch (Exception e1) {
                e1.printStackTrace();  
            }  
              
//            if(color){  
//                  
//                canvas.drawText(textCharArray, i, 1, paddingLeft + drawedWidth,  
//                        (lineCount + 1) * textSize * LineSpacing, paintColor);  
//            } else {  
//                  
//                canvas.drawText(textCharArray, i, 1, paddingLeft + drawedWidth, (lineCount + 1) * textSize * LineSpacing, paint1);  
//            }  
            if (i < lenth1) {
            	
            	 canvas.drawText(textCharArray, i, 1, paddingLeft + drawedWidth, (lineCount + 1) * textSize * LineSpacing, paintColor); 
            	 
            } else {
            	
            	if (lenth2 > 0) {
            		
            		if (i > (lenth1+1) && i < (lenth1+2+lenth2) ) {
            			
            			canvas.drawText(textCharArray, i, 1, paddingLeft + drawedWidth, (lineCount + 1) * textSize * LineSpacing, paintColor);

            		} else {
            			
            			canvas.drawText(textCharArray, i, 1, paddingLeft + drawedWidth, (lineCount + 1) * textSize * LineSpacing, paint1);
            		}
            		
            	} else {
            		
            		canvas.drawText(textCharArray, i, 1, paddingLeft + drawedWidth, (lineCount + 1) * textSize * LineSpacing, paint1);

            	}
            	
            }
            
            
            if(textCharArray[i] > 127 && textCharArray[i] != '、'   
                    && textCharArray[i] != '，' && textCharArray[i] != '。'   
                    && textCharArray[i] != '：' && textCharArray[i] != '！'){  
                drawedWidth += charWidth + Spacing;  
                  
            }else{  
                drawedWidth += charWidth;  
  
            }  
        }  
        setHeight((int) ((lineCount + 1) * (int) textSize * LineSpacing + 10));  
    }  
    public float getSpacing() {  
        return Spacing;  
    }  
    public void setSpacing(float spacing) {  
        Spacing = spacing;  
    }  
    public float getMYLineSpacing() {  
        return LineSpacing;  
    }  
    public void setMYLineSpacing(float lineSpacing) {  
        LineSpacing = lineSpacing;  
    }  
    public float getMYTextSize() {  
        return textSize;  
    }  
    public void setMYTextSize(float textSize) {  
        this.textSize = textSize;  
        paint1.setTextSize(textSize);  
        paintColor.setTextSize(textSize);  
    }  
    
    public void setLinkTextLenth (int leng1,int leng2) {
    	
    	this.lenth1 =  leng1;
    	this.lenth2 = leng2;
    	
    	invalidate();
    }
}  
