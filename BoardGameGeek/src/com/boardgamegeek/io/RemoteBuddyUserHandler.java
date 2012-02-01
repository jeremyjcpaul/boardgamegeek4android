package com.boardgamegeek.io;

import static org.xmlpull.v1.XmlPullParser.END_DOCUMENT;
import static org.xmlpull.v1.XmlPullParser.END_TAG;
import static org.xmlpull.v1.XmlPullParser.START_TAG;

import java.io.IOException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;

import com.boardgamegeek.BggApplication;
import com.boardgamegeek.provider.BggContract;
import com.boardgamegeek.provider.BggContract.Buddies;
import com.boardgamegeek.util.StringUtils;

public class RemoteBuddyUserHandler extends XmlHandler {
	private static final String TAG = "RemoteBuddyUserHandler";

	public RemoteBuddyUserHandler() {
		super(BggContract.CONTENT_AUTHORITY);
	}

	@Override
	public boolean parse(XmlPullParser parser, ContentResolver resolver, String authority)
			throws XmlPullParserException, IOException {

		String[] projection = { Buddies.BUDDY_ID };

		int type;
		while ((type = parser.next()) != END_DOCUMENT) {
			if (type == START_TAG && Tags.USER.equals(parser.getName())) {
				int id = StringUtils.parseInt(parser.getAttributeValue(null, Tags.ID));
				String name = parser.getAttributeValue(null, Tags.NAME);
				ContentValues values = parseUser(parser, resolver);

				Uri uri = Buddies.buildBuddyUri(id);
				Cursor cursor = resolver.query(uri, projection, null, null, null);
				if (!cursor.moveToFirst()) {
					cursor.close();
					if (name.equals(BggApplication.getInstance().getUserName())) {
						resolver.update(Buddies.CONTENT_URI, values, Buddies.BUDDY_NAME + "=?", new String[] { name });
					} else {
						Log.w(TAG, "Tried to parse user, but ID not in database: " + id);
					}
				} else {
					resolver.update(uri, values, null, null);
				}

				cursor.close();
			}
		}

		return false;
	}

	private ContentValues parseUser(XmlPullParser parser, ContentResolver resolver) throws XmlPullParserException,
			IOException {

		final int depth = parser.getDepth();
		ContentValues values = new ContentValues();

		int type;
		while (((type = parser.next()) != END_TAG || parser.getDepth() > depth) && type != END_DOCUMENT) {
			if (type == START_TAG) {
				final String tag = parser.getName();
				final String attribute = parser.getAttributeValue(null, Tags.VALUE);
				if (Tags.FIRSTNAME.equals(tag)) {
					values.put(Buddies.BUDDY_FIRSTNAME, attribute);
				} else if (Tags.LASTNAME.equals(tag)) {
					values.put(Buddies.BUDDY_LASTNAME, attribute);
				} else if (Tags.AVATAR.equals(tag)) {
					values.put(Buddies.AVATAR_URL, attribute);
				}
			}
		}

		values.put(Buddies.UPDATED, System.currentTimeMillis());
		return values;
	}

	private interface Tags {
		String USER = "user";
		String ID = "id";
		String NAME = "name";
		String FIRSTNAME = "firstname";
		String LASTNAME = "lastname";
		String AVATAR = "avatarlink";
		String VALUE = "value";
	}
}