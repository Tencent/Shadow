package com.tencent.shadow.runtime;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.view.InflateException;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

public class XmlPullParserUtil {

    public static String getLayoutStartTagName(Resources res, int layoutResID) {
        XmlResourceParser parser;
        String name;
        try {
            int type;
            parser = res.getLayout(layoutResID);
            while ((type = parser.next()) != XmlPullParser.START_TAG &&
                    type != XmlPullParser.END_DOCUMENT) {
                // Empty
            }

            if (type != XmlPullParser.START_TAG) {
                throw new InflateException(parser.getPositionDescription()
                        + ": No start tag found!");
            }
            name = parser.getName();
        } catch (XmlPullParserException e) {
            final InflateException ie = new InflateException(e.getMessage(), e);
            ie.setStackTrace(new StackTraceElement[0]);
            throw ie;
        } catch (Exception e) {
            final InflateException ie = new InflateException(e.getMessage(), e);
            ie.setStackTrace(new StackTraceElement[0]);
            ie.printStackTrace();
            throw ie;
        }
        return name;
    }
}
