package com.harrycha.mapreminder;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteStatement;

public class RemindersHelper extends SQLiteOpenHelper {

    private static final String TABLE_NAME = "Reminders";
    private static final String idCol = "ID";
    private static final String createdDateTimeCol = "CreatedDateTime";
    private static final String latitudeCol = "Latitude";
    private static final String longitudeCol = "Longitude";
    private static final String titleCol = "Title";
    private static final String descriptionCol = "Description";
    private static final String hasSundayCol = "HasSunday";
    private static final String hasMondayCol = "HasMonday";
    private static final String hasTuesdayCol = "HasTuesday";
    private static final String hasWednesdayCol = "HasWednesday";
    private static final String hasThursdayCol = "HasThursday";
    private static final String hasFridayCol = "HasFriday";
    private static final String hasSaturdayCol = "HasSaturday";
    private static final String hasEnterCol = "HasEnter";
    private static final String hasLeaveCol = "HasLeave";
    private static final String radiusCol = "Radius";
    private static final String isInRadiusCol = "IsInRadius";

    public RemindersHelper(Context context) {
        super(context, TABLE_NAME, null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String createTable = "CREATE TABLE " + TABLE_NAME + " (" + idCol + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                createdDateTimeCol + " TEXT, " + latitudeCol + " DOUBLE, " + longitudeCol + " DOUBLE, " +
                titleCol + " TEXT, " + descriptionCol + " TEXT, " +
                hasSundayCol + " INT, " + hasMondayCol + " INT, " + hasTuesdayCol + " INT, " + hasWednesdayCol + " INT, " +
                hasThursdayCol + " INT, " + hasFridayCol + " INT, " + hasSaturdayCol + " INT, " +
                hasEnterCol + " INT, " + hasLeaveCol + " INT, " + radiusCol + " INT, " + isInRadiusCol + " INT)";
        db.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int i, int i1) {
        db.execSQL("DROP IF TABLE EXISTS " + TABLE_NAME);
        onCreate(db);
    }

    public Cursor getAll() {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public Cursor get(long createdDateTime) {
        SQLiteDatabase db = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + createdDateTimeCol + " = " + createdDateTime;
        Cursor data = db.rawQuery(query, null);
        return data;
    }

    public void add(long createdDateTime, double latitude, double longitude, String title, String description,
                    long hasSunday, long hasMonday, long hasTuesday, long hasWednesday,
                    long hasThursday, long hasFriday, long hasSaturday,
                    long hasEnter, long hasLeave, long radius) {
        String query = "INSERT INTO " + TABLE_NAME + " (" +
                createdDateTimeCol + ", " +
                latitudeCol + ", " +
                longitudeCol + ", " +
                titleCol + ", " +
                descriptionCol + ", " +
                hasSaturdayCol + ", " +
                hasMondayCol + ", " +
                hasTuesdayCol + ", " +
                hasWednesdayCol + ", " +
                hasThursdayCol + ", " +
                hasFridayCol + ", " +
                hasSundayCol + ", " +
                hasEnterCol + ", " +
                hasLeaveCol + ", " +
                radiusCol + ", " +
                isInRadiusCol +
                ") VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, 0)";

        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement stmt = db.compileStatement(query);
        stmt.bindLong(1, createdDateTime);
        stmt.bindDouble(2, latitude);
        stmt.bindDouble(3, longitude);
        stmt.bindString(4, title);
        stmt.bindString(5, description);
        stmt.bindLong(6, hasSunday);
        stmt.bindLong(7, hasMonday);
        stmt.bindLong(8, hasTuesday);
        stmt.bindLong(9, hasWednesday);
        stmt.bindLong(10, hasThursday);
        stmt.bindLong(11, hasFriday);
        stmt.bindLong(12, hasSaturday);
        stmt.bindLong(13, hasEnter);
        stmt.bindLong(14, hasLeave);
        stmt.bindLong(15, radius);
        stmt.executeInsert();
    }

    public void edit(long createdDateTime, String title, String description,
                     long hasSunday, long hasMonday, long hasTuesday, long hasWednesday,
                     long hasThursday, long hasFriday, long hasSaturday,
                     long hasEnter, long hasLeave, long radius) {
        String query = "UPDATE " + TABLE_NAME + " SET " +
                titleCol + " = ?, " +
                descriptionCol + " = ?, " +
                hasSundayCol + " = ?, " +
                hasMondayCol + " = ?, " +
                hasTuesdayCol + " = ?, " +
                hasWednesdayCol + " = ?, " +
                hasThursdayCol + " = ?, " +
                hasFridayCol + " = ?, " +
                hasSaturdayCol + " = ?, " +
                hasEnterCol + " = ?, " +
                hasLeaveCol + " = ?, " +
                radiusCol + " = ? WHERE " +
                createdDateTimeCol + " = ?";

        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement stmt = db.compileStatement(query);
        stmt.bindString(1, title);
        stmt.bindString(2, description);
        stmt.bindLong(3, hasSunday);
        stmt.bindLong(4, hasMonday);
        stmt.bindLong(5, hasTuesday);
        stmt.bindLong(6, hasWednesday);
        stmt.bindLong(7, hasThursday);
        stmt.bindLong(8, hasFriday);
        stmt.bindLong(9, hasSaturday);
        stmt.bindLong(10, hasEnter);
        stmt.bindLong(11, hasLeave);
        stmt.bindLong(12, radius);
        stmt.bindLong(13, createdDateTime);
        stmt.executeUpdateDelete();
    }

    public void updateLocation(long createdDateTime, long isInRadius) {
        String query = "UPDATE " + TABLE_NAME + " SET " +
                isInRadiusCol + " = ? WHERE " +
                createdDateTimeCol + " = ?";

        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement stmt = db.compileStatement(query);
        stmt.bindLong(1, isInRadius);
        stmt.bindLong(2, createdDateTime);
        stmt.executeUpdateDelete();
    }

    public void delete(long createdDateTime) {
        String query = "DELETE FROM " + TABLE_NAME + " WHERE " + createdDateTimeCol + " = ?";

        SQLiteDatabase db = this.getWritableDatabase();
        SQLiteStatement stmt = db.compileStatement(query);
        stmt.bindLong(1, createdDateTime);
        stmt.executeUpdateDelete();
    }
}

