package com.ui.custom;

import android.content.Context;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;

public class DemoPopupWindow extends QuickActionWindowBasic {
	
	int resID;
	OnClickListener listener;
	int tobeRemoved = -1;

	public DemoPopupWindow(View anchor,int resID,OnClickListener listener) {
		super(anchor);
		this.resID =resID;
		this.listener = listener;
		createView();
	
	}
	public DemoPopupWindow(View anchor,int resID,OnClickListener listener, int tobeRemoved) {
		
		super(anchor);
		this.tobeRemoved = tobeRemoved;
		this.resID =resID;
		this.listener = listener;
		createView();
	}
	
private void createView(){
	// inflate layout
	LayoutInflater inflater =
			(LayoutInflater) this.anchor.getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
	ViewGroup root = (ViewGroup) inflater.inflate(resID, null);
	
	// setup button events
	for(int i = 0, icount = root.getChildCount() ; i < icount ; i++) {
		View v = root.getChildAt(i);
		if(v instanceof LinearLayout) {
			LinearLayout row = (LinearLayout) v;
			row.setOnClickListener(listener);
		/*	for(int j = 0, jcount = row.getChildCount() ; j < jcount ; j++) {
				View item = row.getChildAt(j);
				if(item instanceof LinearLayout) {
					LinearLayout b = (LinearLayout) item;
					b.setOnClickListener(listener);
				}
			}*/
		}
	}
	if (tobeRemoved >= 0)
	root.removeViewAt(tobeRemoved);

	// set the inflated view as what we want to display
	this.setContentView(root);
	
}
	@Override
	protected void onCreate() {
	
	}


	public void dismissPopUp(View v) {
		// we'll just display a simple toast on a button click
	this.dismiss();
	}
}