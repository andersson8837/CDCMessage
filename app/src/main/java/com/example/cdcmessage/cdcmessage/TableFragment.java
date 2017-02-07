package com.example.cdcmessage.cdcmessage;

import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.design.widget.Snackbar;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;

import com.example.cdcmessage.cdcmessage.customviews.FilterButton;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

/**
 * A Table fragment containing a simple view.
 */
public class TableFragment extends android.support.v4.app.Fragment implements CheckBox.OnClickListener, EditText.OnFocusChangeListener{
    /**
     * The fragment argument representing the section number for this
     * fragment.
     */
    private HeaderData mHeader = null;
    private List<RowData> mDataList = new ArrayList<RowData>();
    private static int mValidIndex = 0;
    private TableAdapter mAdapter = null;

    private LinearLayout mHeaderView = null;
    private ListView mGridView = null;

    private int mRowCnt;
    final static int mUnitCol = 10;
    final static int mUnitRow = 35;
    final static int chkSize = 5, defSize = 4;
    final static int validColor = 0xFFFFFF, invalidColor = 0xFF0000;
    final static int FIXED_BITCOUNT = 6;

    private class HeaderData{
        private boolean mIsChecked = false;
        private String[] mData = null;
        private int[] mColWidth = null;
        HeaderData(String csv_line) {
            mIsChecked = false;
            mData = csv_line.split(",");
            calcColWidth();
        }
        HeaderData(String[] pData, boolean isChecked) {
            mIsChecked = isChecked;
            mData = new String[pData.length];
            for (int i=0; i<pData.length; i++) {
                mData[i] = pData[i];
            }
            calcColWidth();
        }
        void calcColWidth() {
            mColWidth = new int[getColumnCount()];
            for (int i=0; i<getColumnCount(); i++) {
                mColWidth[i] = (i==0) ? chkSize : (mData[i-1].length()+defSize);
            }
        }
        int getColumnCount() {
            return mData.length+1;
        }
        int getColumnWidth(int col) {
            return mColWidth[col];
        }
        boolean checkColumnWidth(int col, int newWidth) {
            if (mColWidth[col] < newWidth+defSize) {
                mColWidth[col] = newWidth+defSize;
                return true;
            } else {
                return false;
            }
        }
        String getColumnHeader(int col) {
            return (col==0)?" ":mData[col-1];
        }
        void setIsChecked(boolean val) { mIsChecked = val; }
        boolean isChecked() { return mIsChecked; }
    }

    private class RowData {
        private boolean mIsChecked = false;
        private int[] mCodes = null;
        private String[] mData = null;

        RowData(String csv_line) {
            mIsChecked = false;
            mCodes = null;
            mData = csv_line.split(",");
        }
        RowData(String[] pData, boolean isChecked) {
            mIsChecked = isChecked;
            mCodes = null;
            mData = new String[pData.length];
            for (int i=0; i<pData.length; i++) {
                mData[i] = pData[i];
            }
        }
        private boolean isValidCol(int col) {
            try {
                int data = Integer.parseInt(mData[col-1]);
                switch (col-1) {
                    case 0:
                        if (data >= 0 && data <= 99) return true;
                        else return false;
                    case 1:
                        if (data >= 1 && data <= 8) return true;
                        else return false;
                    case 2:
                        if ((data >= 1 && data <= 12) || (data >= 99 && data <= 255)) return true;
                        else return false;
                    case 3:
                        if (data >= 0) return true;
                        else return false;
                    case 4:
                        if (data >= 0 && data <= 255) return true;
                        else return false;
                    case 5:
                        if (data >= 0 && data <= 255) return true;
                        else return false;
                    default:
                        return true;
                }
            } catch (NumberFormatException e) {
                return false;
            }
        }
        public boolean validate() {
            mCodes = null;
            int[] bits = new int[FIXED_BITCOUNT];
            for (int i=0; i<FIXED_BITCOUNT; i++) {
                try {
                    bits[i] = Integer.parseInt(mData[i]);
                    if ( !isValidCol(i+1) ) return false;
                } catch (NumberFormatException e) {
                    return false;
                }
            }
            mCodes = new int[FIXED_BITCOUNT];
            for (int i=0; i<FIXED_BITCOUNT; i++) {
                mCodes[i] = bits[i];
            }
            return true;
        }


        boolean isValid() { return !(mCodes==null); }
        String getColData(int col) {
            return (col==0)?" ":mData[col-1];
        }

        void setIsChecked(boolean val) { mIsChecked = val; }
        boolean isChecked() { return mIsChecked; }
    }

    //public String getProgramMessages() {
    public ArrayList<int[]> getProgramMessages() {
        ArrayList<int[]> programMessages = new ArrayList<int[]>();
        int i;
        for (i=0; i<mDataList.size(); i++) {
            if ( mDataList.get(i).isValid() ) {
                programMessages.add(mDataList.get(i).mCodes);
            }
        }
        return programMessages;
    }

    public TableFragment() {

        mRowCnt = 0;
    }

    /**
     * Returns a new instance of this fragment for the given section
     * number.
     */
    public static TableFragment newInstance() {
        TableFragment fragment = new TableFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAdapter = new TableAdapter();

        Log.e(Consts.LOGMESSAGE, "TableFragment : onCreate");
    }

//    @Override
//    public void onStop() {
//        super.onStop();
//        Log.e("onSaveInstanceState", "onStop");
//    }
//
//    @Override
//    public void onStart() {
//        super.onStart();
//        Log.e("onSaveInstanceState", "onStart");
//    }
//
//    @Override
//    public void onResume() {
//        super.onResume();
//        Log.e("onSaveInstanceState", "onResume");
//    }
//
//    @Override
//    public void onPause() {
//        super.onPause();
//        Log.e("onSaveInstanceState", "onPause");
//    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_table, container, false);
        mHeaderView = (LinearLayout)rootView.findViewById(R.id.header);
        mGridView = (ListView)rootView.findViewById(R.id.mysheet);
        mGridView.setAdapter(mAdapter);
        return rootView;
    }

    @Override
    public void onSaveInstanceState(Bundle outState)
    {
        super.onSaveInstanceState(outState);
        Log.e(Consts.LOGMESSAGE, "TableFragment : onSaveInstanceState");
        if (mHeader == null) return;
        boolean[] checks = new boolean[mDataList.size() + 1];
        int i;

        checks[0] = mHeader.mIsChecked;
        outState.putStringArray("HeaderData", mHeader.mData);

        for (i=0; i<mDataList.size(); i++) {
            checks[i+1] = mDataList.get(i).mIsChecked;
            outState.putStringArray(String.format("RowData%d", i), mDataList.get(i).mData);
        }
        outState.putBooleanArray("CheckVals", checks);
    }


    @Override
    public void onViewStateRestored(Bundle inState){
        super.onViewStateRestored(inState);
        Log.e(Consts.LOGMESSAGE, "TableFragment : onViewStateRestored");

        boolean[] checks;
        try {
            checks = inState.getBooleanArray("CheckVals");
            if (checks == null) {
                return;
            }
        } catch(NullPointerException e) {
            return;
        }

        mHeader = new HeaderData(inState.getStringArray("HeaderData"), checks[0]);
        mDataList.clear();
        int i;
        for (i=0; i<checks.length-1; i++) {
            RowData newData = new RowData(inState.getStringArray(String.format("RowData%d", i)), checks[i+1]);
            newData.validate();
            mDataList.add(newData);
        }
        showHeader();
        mRowCnt = mDataList.size();
        mAdapter.notifyDataSetChanged();
    }

    private LinearLayout.LayoutParams getSpecLayoutParam(int weightWidth, int weightHeight) {
        final float scale = getContext().getResources().getDisplayMetrics().density;
        int pixelWidth = (int) (weightWidth*mUnitCol * scale + 0.5f);
        int pixelHeight = (int) (weightHeight*mUnitRow * scale + 0.5f);
        if (weightHeight == -1)
            pixelHeight = LinearLayout.LayoutParams.MATCH_PARENT;
        //LinearLayout.LayoutParams param =new LinearLayout.LayoutParams(pixelWidth, LinearLayout.LayoutParams.MATCH_PARENT);
        LinearLayout.LayoutParams param =new LinearLayout.LayoutParams(pixelWidth, pixelHeight);
        param.gravity = Gravity.CENTER_VERTICAL;
        return param;
    }

    public void showHeader() {
        int i;
        mHeaderView.removeAllViewsInLayout();

        for (i=0; i<mHeader.getColumnCount(); i++) {
            View v;
            if (i==0) {
                CheckBox chk = new CheckBox(getActivity());
                chk.setChecked(mHeader.isChecked());
                chk.setOnClickListener( new CheckBox.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        CheckBox chk = (CheckBox)v;
                        mHeader.mIsChecked = chk.isChecked();
                        for (int j=0; j<mRowCnt; j++) {
                            mDataList.get(j).setIsChecked(mHeader.mIsChecked);
                        }

                        mAdapter.notifyDataSetChanged();
                    }
                } );
                v = chk;
            } else {
                FilterButton fltbtn = new FilterButton(getActivity());
                fltbtn.setColumnHeader(mHeader.getColumnHeader(i));
                v = fltbtn;
            }
            LinearLayout.LayoutParams params =getSpecLayoutParam(mHeader.getColumnWidth(i), 1);
            params.bottomMargin = params.topMargin = 0;
            v.setLayoutParams(params);
            mHeaderView.addView(v, i);
        }
    }

    public void onClick(View v) {
        int row;
        try {
            row = Integer.parseInt((String) v.getTag());
        } catch (NumberFormatException e) {
            return;
        }
        boolean val = ((CheckBox)v).isChecked();
        mDataList.get(row).mIsChecked = val;
        if (!val && mHeader.isChecked()) mHeader.setIsChecked(false);
        showHeader();
        mAdapter.notifyDataSetChanged();
    }

    public void onFocusChange(View v, boolean hasFocus) {
        int row, col;
        try {
            String[] temp = ((String) v.getTag()).split(",");
            row = Integer.parseInt(temp[0]);
            col = Integer.parseInt(temp[1]);
        } catch (NumberFormatException e) {
            return;
        }
        if (hasFocus) return;
        mDataList.get(row).mData[col-1] = ((EditText)v).getText().toString();
        mDataList.get(row).validate();
        mAdapter.notifyDataSetChanged();
    }

    String getTagForCheckBox(int row) {
        return String.format("%d", row);
    }

    String getTagForEditText(int row, int col) {
        return String.format("%d,%d", row, col);
    }

    private class TableAdapter extends BaseAdapter {
        @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN)
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if (mHeader == null || mHeader.getColumnCount() == 0) return convertView;
            int i;
            LinearLayout li;
            RowData rowData = mDataList.get(position);

            if (convertView == null) {
                li = new LinearLayout(getActivity());
                li.setOrientation(LinearLayout.HORIZONTAL);
            } else {
                li = (LinearLayout) convertView;
            }
            if (convertView == null) {
                //Datas
                for (i=0; i<mHeader.getColumnCount(); i++) {
                    View v;
                    if (i==0) {
                        CheckBox chk = new CheckBox(getActivity());
                        chk.setChecked(rowData.isChecked());
                        if (rowData.isValid()) chk.setEnabled(true);
                        else chk.setEnabled(false);

                        chk.setTag(getTagForCheckBox(position));
                        chk.setOnClickListener(TableFragment.this);

                        v = chk;
                    } else {
                        EditText edt = new EditText(getActivity());
                        edt.setText(rowData.getColData(i));

                        if (rowData.isValidCol(i)) edt.setBackground(getResources().getDrawable(R.drawable.validbackedittext));
                        else edt.setBackground(getResources().getDrawable(R.drawable.invalidbackedittext));

                        edt.setTag(getTagForEditText(position, i));
                        edt.setOnFocusChangeListener(TableFragment.this);
                        v = edt;
                    }
                    v.setLayoutParams(getSpecLayoutParam(mHeader.getColumnWidth(i), -1));
                    li.addView(v, i);
                }
                convertView = li;
            } else {

                for (i=0; i<mHeader.getColumnCount(); i++) {
                    if (i==0) {
                        CheckBox chk;
                        try {
                            chk = (CheckBox) li.getChildAt(i);
                        } catch (NullPointerException e) {
                            chk = new CheckBox(getActivity());
                        }
                        if (chk == null) chk = new CheckBox(getActivity());
                        chk.setChecked(rowData.isChecked());

                        if (rowData.isValid()) chk.setEnabled(true);
                        else chk.setEnabled(false);

                        chk.setTag(getTagForCheckBox(position));
                        chk.setOnClickListener(TableFragment.this);
                    } else {
                        EditText edt;
                        try {
                            edt = (EditText) li.getChildAt(i);
                        } catch(NullPointerException e) {
                            edt = new EditText(getActivity());
                        }
                        if (edt == null) edt = new EditText(getActivity());

                        if (rowData.isValidCol(i)) edt.setBackground(getResources().getDrawable(R.drawable.validbackedittext));
                        else edt.setBackground(getResources().getDrawable(R.drawable.invalidbackedittext));
                        edt.setText(rowData.getColData(i));

                        edt.setTag(getTagForEditText(position, i));
                        edt.setOnFocusChangeListener(TableFragment.this);
                    }
                }
            }
            return convertView;
        }

        @Override
        public int getCount() {
            //Snackbar.make(mGridView, String.format("You have %d invalid datas. So Please fix them.", mValidIndex), Snackbar.LENGTH_LONG).show();
            return mRowCnt;
        }

        @Override
        public String getItem(int position) {
            return String.format("%d", position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }
    }

    public void setData(Uri uri) {
        mRowCnt = 0;
        mDataList.clear();
        new LoadFilesTask().execute(uri);
    }

    public void onLoadFinished() {
        if (mValidIndex == 0) {
            Snackbar.make(mGridView, String.format("You have successfully loaded %d programming messages to send.", mDataList.size()), Snackbar.LENGTH_LONG).show();
        } else {
            Snackbar.make(mGridView, String.format("You have loaded %d invalid messages.\nSo Please fix them to send.", mValidIndex), Snackbar.LENGTH_LONG).show();
        }
    }

    private class LoadFilesTask extends AsyncTask<Uri, Long, Long> {

        protected Long doInBackground(Uri... uris) {
            try {
                InputStream csvStream = getActivity().getContentResolver().openInputStream(uris[0]);
                InputStreamReader csvStreamReader = new InputStreamReader(csvStream);
                BufferedReader reader = new BufferedReader(csvStreamReader);
                String csv_line;
                int i;
                try {
                    csv_line = reader.readLine();
                    mHeader = new HeaderData(csv_line);

                    mRowCnt = 0;
                    mValidIndex = 0 ;

                    publishProgress((long)0);

                    int kk = 0;
                    while ((csv_line = reader.readLine()) != null) {
                        RowData rowData = new RowData(csv_line);

                        mRowCnt++;
                        rowData.validate();
//                        Log.e("doInBackground", String.format("RowCnt : %d / %d %d", mRowCnt, rowData.validate()?1:0, rowData.isValid()?1:0));
                        if (!rowData.isValid()) {
                            RowData temp = mDataList.get(mValidIndex);
                            mDataList.set(mValidIndex, rowData);
                            mDataList.add(temp);
                            mValidIndex++;
                        } else {
                            mDataList.add(rowData);
                        }

                        for (i=0; i<mHeader.getColumnCount(); i++) {
                            mHeader.checkColumnWidth(i, rowData.getColData(i).length());
                        }

                        kk++;
                        if(kk == 5) {
                            publishProgress((long)mRowCnt);
                            kk = 0;
                        }
                    }
                    publishProgress((long)mRowCnt);

                    reader.close();
                    csvStreamReader.close();;
                    csvStream.close();

                } catch (IOException e) {
                    e.printStackTrace();
                }
                return Long.valueOf( mDataList.size() );
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            return Long.valueOf(0);
        }
        protected void onProgressUpdate(Long... progress) {
            if (progress[0] == 0)
                showHeader();
            else
                mAdapter.notifyDataSetChanged();
        }

        protected void onPostExecute(Long result) {
            Log.e(Consts.LOGMESSAGE, "Async Loader : Loading Finished");
            onLoadFinished();
        }
    }
}
