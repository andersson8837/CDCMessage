package com.example.cdcmessage.cdcmessage.customviews;

import android.content.Context;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.PopupMenu;
import android.widget.TextView;
import android.widget.Toast;

import com.example.cdcmessage.cdcmessage.MainActivity;
import com.example.cdcmessage.cdcmessage.R;


/**
 * Created by Administrator on 1/30/2017.
 */

public class FilterButton extends LinearLayout implements ImageButton.OnClickListener {

    private Context mContext;

    TextView mColumnheader;
    ImageButton mFilterbtn;

    public FilterButton(Context pContext) {
        super(pContext);
        init(pContext, null);
    }

    public FilterButton(Context pContext, AttributeSet attrs) {
        super(pContext, attrs);
        init(pContext, attrs);
    }

    public FilterButton(Context pContext, AttributeSet attrs, int defStyleAttr) {
        super(pContext, attrs, defStyleAttr);
        init(pContext, attrs);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public FilterButton(Context pContext, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(pContext, attrs, defStyleAttr, defStyleRes);
        init(pContext, attrs);
    }

    private void init(Context pContext, AttributeSet attrs) {
        mContext = pContext;

        setOrientation(LinearLayout.HORIZONTAL);
        setGravity(Gravity.CENTER_VERTICAL);

        mColumnheader = new TextView(mContext);
        LinearLayout.LayoutParams params1 = new LinearLayout.LayoutParams (0, ViewGroup.LayoutParams.WRAP_CONTENT);
        params1.weight = 20;
        params1.gravity = Gravity.CENTER_HORIZONTAL | Gravity.CENTER_VERTICAL;
        params1.leftMargin = 2;
        params1.rightMargin = 2;
        params1.topMargin = 2;
        params1.bottomMargin = 2;
        this.addView(mColumnheader, 0, params1);

        mFilterbtn = new ImageButton(mContext);
        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams (ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params2.weight = 1;
        mFilterbtn.setImageDrawable( getResources().getDrawable(android.R.drawable.arrow_down_float));
        mFilterbtn.setAdjustViewBounds(true);
        mFilterbtn.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        this.addView(mFilterbtn, 1, params2);

        mFilterbtn.setOnClickListener(this);
    }

    public void setColumnHeader(String pTitle) {
        mColumnheader.setText(pTitle);
    }

    public void onClick(View v) {
        //Creating the instance of PopupMenu
        PopupMenu popup = new PopupMenu(mContext, v);
        //Inflating the Popup using xml file
        popup.getMenuInflater().inflate(R.menu.menu_filters, popup.getMenu());

        //registering popup with OnMenuItemClickListener
        popup.setOnMenuItemClickListener(new PopupMenu.OnMenuItemClickListener() {
            public boolean onMenuItemClick(MenuItem item) {
                Toast.makeText(mContext, "You Clicked : " + item.getTitle(), Toast.LENGTH_SHORT).show();
                return true;
            }
        });
        popup.show();
    }
}
