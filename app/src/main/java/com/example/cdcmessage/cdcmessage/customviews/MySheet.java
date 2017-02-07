
package com.example.cdcmessage.cdcmessage.customviews;

import android.annotation.TargetApi;
import android.content.*;
import android.content.res.AssetManager;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.text.Layout;
import android.util.AttributeSet;
import android.view.Gravity;
import android.view.View;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.*;

import com.example.cdcmessage.cdcmessage.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by Administrator on 1/29/2017.
 */

public class MySheet extends GridView {
    private Context mContext;



    public MySheet(Context pContext) {
        super(pContext);
        init(pContext);
    }

    public MySheet(Context pContext, AttributeSet attrs) {
        super(pContext, attrs);
        init(pContext);
    }

    public MySheet(Context pContext, AttributeSet attrs, int defStyleAttr) {
        super(pContext, attrs, defStyleAttr);
        init(pContext);
    }

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    public MySheet(Context pContext, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(pContext, attrs, defStyleAttr, defStyleRes);
        init(pContext);
    }

    private void init(Context pContext) {
        mContext = pContext;
    }



//    private void defineCellSize() {
//        if (mHeader.mData.length == 0) {
//            return;
//        }
//        if (mColWidth == null) { mColWidth = new int[mColCnt]; }
//        if (mRowHeight == null) { mRowHeight = new int[mRowCnt]; }
//        int i, j;
//        mColWidth[0] = 3;
//        for (i=1; i<mColCnt; i++) {
//            mColWidth[i] = mHeader[i-1].length() + 5;
//            for (j=1; j<mRowCnt; j++) {
//                int w = mDataList.get(j-1)[i-1].length();
//                int tmp = (w>mMaxWidth)?mMaxWidth:w;
//                if ( mColWidth[i] < tmp ) mColWidth[i] = tmp;
//            }
//        }
//
//        mRowHeight[0] = 1;
//        for (j=1; j<mRowCnt; j++) {
//            mRowHeight[j] = 1;
//            for (i=1; i<mColCnt; i++) {
//                int h = (int)(mDataList.get(j-1)[i-1].length() / mMaxWidth) + 1;
//                if ( mRowHeight[j] < h ) mRowHeight[j] = h;
//            }
//        }
//    }

//    private GridLayout.LayoutParams getSpecLayoutParam(int width, int height, int gravity, int row, int col, int rightMargin, int leftMargin) {
//        GridLayout.LayoutParams param =new GridLayout.LayoutParams();
//        param.height = height;
//        param.width = width;
//        param.rightMargin = rightMargin;
//        param.topMargin = leftMargin;
//        param.setGravity(gravity);
//        param.columnSpec = GridLayout.spec(col);
//        param.rowSpec = GridLayout.spec(row);
//        return param;
//    }


}
