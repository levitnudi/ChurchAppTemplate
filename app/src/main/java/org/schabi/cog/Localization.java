package org.schabi.cog;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.preference.PreferenceManager;

import org.schabi.newpipe.R;

import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

/*
 * Copyright 2015 Levit Nudi
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

public class Localization {

    private Localization() {
    }

    public static Locale getPreferredLocale(Context context) {
        SharedPreferences sp = PreferenceManager.getDefaultSharedPreferences(context);

        String languageCode = sp.getString(String.valueOf(R.string.search_language_key),
                context.getString(R.string.default_language_value));

        if(languageCode.length() == 2) {
            return new Locale(languageCode);
        }
        else if(languageCode.contains("_")) {
            String country = languageCode
                    .substring(languageCode.indexOf("_"), languageCode.length());
            return new Locale(languageCode.substring(0, 2), country);
        }
        return Locale.getDefault();
    }

    public static String localizeViewCount(long viewCount, Context context) {
        Locale locale = getPreferredLocale(context);

        Resources res = context.getResources();
        String viewsString = res.getString(R.string.view_count_text);

        NumberFormat nf = NumberFormat.getInstance(locale);
        String formattedViewCount = nf.format(viewCount);
        return String.format(viewsString, formattedViewCount);
    }

    public static String localizeNumber(long number, Context context) {
        Locale locale = getPreferredLocale(context);
        NumberFormat nf = NumberFormat.getInstance(locale);
        return nf.format(number);
    }

    private static String formatDate(String date, Context context) {
        Locale locale = getPreferredLocale(context);
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date datum = null;
        try {
            datum = formatter.parse(date);
        } catch (ParseException e) {
            e.printStackTrace();
        }

        DateFormat df = DateFormat.getDateInstance(DateFormat.MEDIUM, locale);

        return df.format(datum);
    }

    public static String localizeDate(String date, Context context) {
        Resources res = context.getResources();
        String dateString = res.getString(R.string.upload_date_text);

        String formattedDate = formatDate(date, context);
        return String.format(dateString, formattedDate);
    }
}
