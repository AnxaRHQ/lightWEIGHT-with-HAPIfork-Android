package com.ui.custom;

import java.util.ArrayList;

import com.hapilabs.lightweight.R;

import android.content.Context;
import android.graphics.Bitmap;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.ImageView;

public class MyPagerAdapter extends PagerAdapter {

	ArrayList<Bitmap> items;
	LayoutInflater layoutInflater;
	String inflater = Context.LAYOUT_INFLATER_SERVICE;
	OnClickListener listener;
	public MyPagerAdapter(Context context, OnClickListener listener){
		super();
		items = new ArrayList<Bitmap>();
		this.listener =listener;
		layoutInflater = (LayoutInflater)context.getSystemService( inflater );

	}
/** TODO Auto-generated method stub
	if (items!=null && arg0 < items.size())
	return (Bitmap)items.get(arg0);
	return null;
	**/
	public void setImages(ArrayList<Bitmap> items){

		if (items!=null)
			this.items = items;

	}
	 @Override
	    public Object instantiateItem(ViewGroup collection, int position) {
	        View v = layoutInflater.inflate(R.layout.tourpage_item,null);

	        ImageView img = (ImageView)v.findViewById(R.id.image);
	        img.setImageBitmap(items.get(position));

	       ((ViewPager) collection).addView(v,0);
	        v.setOnClickListener(listener);
	        return v;
	    }
	 @Override
     public void destroyItem(ViewGroup collection, int position, Object view) {
             collection.removeView((View) view);
     }


@Override
public int getCount() {
	// TODO Auto-generated method stub
	return items.size();
}





@Override
public boolean isViewFromObject(View view, Object object) {
        return (view==object);

}


}
