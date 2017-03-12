package com.reversecoder.javamail.androidstudio.k9.mailstore.migrations;


import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import timber.log.Timber;

import com.reversecoder.javamail.androidstudio.k9.K9;


class MigrationTo37 {
    public static void addAttachmentsContentDispositionColumn(SQLiteDatabase db) {
        try {
            db.execSQL("ALTER TABLE attachments ADD content_disposition TEXT");
        } catch (SQLiteException e) {
            Timber.e("Unable to add content_disposition column to attachments");
        }
    }
}
