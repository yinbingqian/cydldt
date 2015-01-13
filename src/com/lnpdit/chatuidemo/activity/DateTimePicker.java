package com.lnpdit.chatuidemo.activity;

import java.util.Calendar;

import com.lnpdit.chatuidemo.R;

import android.content.Context;
import android.text.format.DateFormat;
import android.widget.FrameLayout;
import android.widget.NumberPicker;
import android.widget.NumberPicker.OnValueChangeListener;

public class DateTimePicker extends FrameLayout {
	private final NumberPicker mYearSpinner;
	private final NumberPicker mMonthSpinner;
	private final NumberPicker mDaySpinner;
	private Calendar mDate;
	// private int mHour,mMinute;
	private int mYear, mMonth, mDay;
	// private String[] mDateDisplayValues = new String[7];
	private OnDateTimeChangedListener mOnDateTimeChangedListener;

	public DateTimePicker(Context context) {
		super(context);
		mDate = Calendar.getInstance();

		mYear = mDate.get(Calendar.YEAR);
		mMonth = mDate.get(Calendar.MONTH);
		mDay = mDate.get(Calendar.DAY_OF_MONTH);
		// mHour=mDate.get(Calendar.HOUR_OF_DAY);
		// mMinute=mDate.get(Calendar.MINUTE);

		inflate(context, R.layout.datedialog, this);

		mYearSpinner = (NumberPicker) this.findViewById(R.id.np_year);
		mYearSpinner.setMinValue(1902);
		mYearSpinner.setMaxValue(2038);
		// updateDateControl();
		mYearSpinner.setValue(mYear);
		mYearSpinner.setOnValueChangedListener(mOnYearChangedListener);

		mMonthSpinner = (NumberPicker) this.findViewById(R.id.np_month);
		mMonthSpinner.setMaxValue(12);
		mMonthSpinner.setMinValue(1);
		mMonthSpinner.setValue(mMonth);
		mMonthSpinner.setOnValueChangedListener(mOnMonthChangedListener);

		mDaySpinner = (NumberPicker) this.findViewById(R.id.np_day);

		if (mMonthSpinner.getValue() == 1 || mMonthSpinner.getValue() == 3
				|| mMonthSpinner.getValue() == 5
				|| mMonthSpinner.getValue() == 7
				|| mMonthSpinner.getValue() == 8
				|| mMonthSpinner.getValue() == 10
				|| mMonthSpinner.getValue() == 12) {
			mDaySpinner.setMaxValue(31);
		} else if (mMonthSpinner.getValue() == 2) {
			if (mYearSpinner.getValue() % 4 == 0) {
				mDaySpinner.setMaxValue(29);
			} else {
				mDaySpinner.setMaxValue(28);
			}
		} else {
			mDaySpinner.setMaxValue(30);
		}
		mDaySpinner.setMinValue(1);
		mDaySpinner.setValue(mDay);
		mDaySpinner.setOnValueChangedListener(mOnDayChangedListener);
	}

	private NumberPicker.OnValueChangeListener mOnYearChangedListener = new OnValueChangeListener() {
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			// mDate.add(Calendar.DAY_OF_MONTH, newVal - oldVal);
			mYear = mYearSpinner.getValue();
			// updateDateControl();
			onDateTimeChanged();
		}
	};

	private NumberPicker.OnValueChangeListener mOnMonthChangedListener = new OnValueChangeListener() {
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			if (mMonthSpinner.getValue() == 1 || mMonthSpinner.getValue() == 3
					|| mMonthSpinner.getValue() == 5
					|| mMonthSpinner.getValue() == 7
					|| mMonthSpinner.getValue() == 8
					|| mMonthSpinner.getValue() == 10
					|| mMonthSpinner.getValue() == 12) {
				mDaySpinner.setMaxValue(31);
			} else if (mMonthSpinner.getValue() == 2) {
				if (mYearSpinner.getValue() % 4 == 0) {
					mDaySpinner.setMaxValue(29);
				} else {
					mDaySpinner.setMaxValue(28);
				}
			} else {
				mDaySpinner.setMaxValue(30);
			}
			mDaySpinner.setMinValue(1);
			mDaySpinner.setValue(mDay);
			mDaySpinner.setOnValueChangedListener(mOnDayChangedListener);
			
			mMonth = mMonthSpinner.getValue()-1;
			onDateTimeChanged();
		}
	};

	private NumberPicker.OnValueChangeListener mOnDayChangedListener = new OnValueChangeListener() {
		@Override
		public void onValueChange(NumberPicker picker, int oldVal, int newVal) {
			mDay = mDaySpinner.getValue();
			onDateTimeChanged();
		}
	};

	// private void updateDateControl()
	// {
	// Calendar cal = Calendar.getInstance();
	// cal.setTimeInMillis(mDate.getTimeInMillis());
	// cal.add(Calendar.DAY_OF_YEAR, -7 / 2 - 1);
	// mDateSpinner.setDisplayedValues(null);
	// for (int i = 0; i < 7; ++i)
	// {
	// cal.add(Calendar.DAY_OF_YEAR, 1);
	// mDateDisplayValues[i] = (String) DateFormat.format("MM.dd EEEE", cal);
	// }
	// mDateSpinner.setDisplayedValues(mDateDisplayValues);
	// mDateSpinner.setValue(7 / 2);
	// mDateSpinner.invalidate();
	// }

	public interface OnDateTimeChangedListener {
		// void onDateTimeChanged(DateTimePicker view, int year, int month, int
		// day,int hour,int minute);
		void onDateTimeChanged(DateTimePicker view, int year, int month, int day);
	}

	public void setOnDateTimeChangedListener(OnDateTimeChangedListener callback) {
		mOnDateTimeChangedListener = callback;
	}

	private void onDateTimeChanged() {
		if (mOnDateTimeChangedListener != null) {
			mOnDateTimeChangedListener.onDateTimeChanged(this, mYear, mMonth,
					mDay);
			// mDate.get(Calendar.YEAR), mDate.get(Calendar.MONTH),
			// mDate.get(Calendar.DAY_OF_MONTH));
			// mDate.get(Calendar.MONTH),
			// mDate.get(Calendar.DAY_OF_MONTH),mHour, mMinute);

		}
	}
}
