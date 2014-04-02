package com.boardgamegeek.pref;

import com.boardgamegeek.R;

import android.content.Context;
import android.content.Intent;
import android.preference.Preference;
import android.util.AttributeSet;

public class ContactUsPreference extends Preference {
	public ContactUsPreference(Context context) {
		super(context);
		init();
	}

	public ContactUsPreference(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ContactUsPreference(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	private void init() {
		Intent emailIntent = new Intent(Intent.ACTION_SEND);
		emailIntent.setType("text/email");
		emailIntent.putExtra(Intent.EXTRA_EMAIL,
			new String[] { getContext().getString(R.string.pref_about_contact_us_summary) });
		emailIntent.putExtra(Intent.EXTRA_SUBJECT, R.string.pref_feedback_title);
		setIntent(emailIntent);
	}
}
