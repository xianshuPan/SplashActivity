package com.hylg.igolf.ui.view;

import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

 public class NoLineClickSpan extends ClickableSpan {
	String text;

	public NoLineClickSpan() {
	    super();
	   // this.text = text;
	}

	@Override
	public void updateDrawState(TextPaint ds) {
	    ds.setColor(ds.linkColor);
	    ds.setUnderlineText(false); //去掉下划线
	}

	@Override
	public void onClick(View widget) {
		
	    //processHyperLinkClick(text); //点击超链接时调用
	}
}
