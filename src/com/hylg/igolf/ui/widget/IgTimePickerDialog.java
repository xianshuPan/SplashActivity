/*
 * Copyright (c) 2010-2011, The MiCode Open Source Community (www.micode.net)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hylg.igolf.ui.widget;

import java.util.Calendar;
import java.util.Date;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnClickListener;
import android.widget.Toast;

import com.hylg.igolf.R;
import com.hylg.igolf.ui.widget.IgTimePicker.OnIgTimeChangedListener;

public class IgTimePickerDialog extends AlertDialog implements OnClickListener {

    private Calendar mDate = Calendar.getInstance();
    private OnIgTimeSetListener mOnIgTimeSetListener;
    private IgTimePicker mIgTimePicker;

    private Context mContext;

    public IgTimePickerDialog(Context context, long date, boolean is24HourView) {
        super(context);
        mIgTimePicker = new IgTimePicker(context, date, is24HourView);
        setView(mIgTimePicker);
        mIgTimePicker.setOnIgTimeChangedListener(new OnIgTimeChangedListener() {
            @Override
            public void onIgTimeChanged(IgTimePicker view, int hourOfDay,
                                        int minute) {
                mDate.set(Calendar.HOUR_OF_DAY, hourOfDay);
                mDate.set(Calendar.MINUTE, minute);
            }

        });

        mContext = context;
        mDate.setTimeInMillis(date);
        Calendar cal = mIgTimePicker.getResetCalendar();
        mDate.set(Calendar.SECOND, 0);
        mDate.set(Calendar.MINUTE, cal.get(Calendar.MINUTE));
        mDate.set(Calendar.HOUR_OF_DAY, cal.get(Calendar.HOUR_OF_DAY));
        setButton(BUTTON_POSITIVE, context.getString(android.R.string.ok), this);
        setButton(BUTTON_NEGATIVE, context.getString(android.R.string.cancel), (OnClickListener)null);
        setTitle(context.getString(R.string.str_dialog_set_title_tee_time));
    }

    public void setOnIgTimeSetListener(OnIgTimeSetListener callBack) {
    	mOnIgTimeSetListener = callBack;
    }

    public void onClick(DialogInterface arg0, int arg1) {
        if (BUTTON_POSITIVE == arg1 && mOnIgTimeSetListener != null) {


            mOnIgTimeSetListener.OnIgTimeSet(this, mDate.getTimeInMillis());



        }
    }
    
    public interface OnIgTimeSetListener {
        void OnIgTimeSet(AlertDialog dialog, long date);
    }

}