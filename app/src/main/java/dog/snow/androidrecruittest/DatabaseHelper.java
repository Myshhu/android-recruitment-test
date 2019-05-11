package dog.snow.androidrecruittest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONObject;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "JSON_Items";
    private static final String COLUMN1 = "id";
    private static final String COLUMN2 = "name";
    private static final String COLUMN3 = "description";
    private static final String COLUMN4 = "icon";
    private static final String COLUMN5 = "timestamp";
    private static final String COLUMN6 = "url";

    DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 103);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate called");
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN1 + " INTEGER, " +
                  COLUMN2 + " TEXT, " + COLUMN3 + " TEXT, " + COLUMN4 + " TEXT, " + COLUMN5 + " LONG, " + COLUMN6 + " TEXT)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(TAG, "onUpgrade called");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    public void dropTable() {
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    public void createTable() {
        SQLiteDatabase database = this.getWritableDatabase();
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" + COLUMN1 + " INTEGER, " +
                COLUMN2 + " TEXT, " + COLUMN3 + " TEXT, " + COLUMN4 + " TEXT, " + COLUMN5 + " LONG, " + COLUMN6 + " TEXT)";
        database.execSQL(createTable);
    }

    public void restartTable() {
        dropTable();
        createTable();
        Log.i(TAG, "Database version: " + getWritableDatabase().getVersion());
    }

    public boolean addItem(JSONObject object) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();
        try {
            contentValues.put(COLUMN2, object.getString(COLUMN2));
            contentValues.put(COLUMN3, object.getString(COLUMN3));
            contentValues.put(COLUMN4, object.getString(COLUMN4));
            contentValues.put(COLUMN5, object.getLong(COLUMN5));
            contentValues.put(COLUMN6, object.getString(COLUMN6));

            long result = database.insert(TABLE_NAME, null, contentValues);

            return (result != -1); //Return false when inserting failed
        } catch (Exception e) {
            Log.e(TAG, "Adding to database failed", e);
            return false;
        }
    }

    boolean insertArray(JSONArray array) {
        restartTable();

        boolean result = true;

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject object = array.getJSONObject(i);
                result = addItem(object);
            } catch (Exception e) {
                Log.e(TAG, "insertArray function failed", e);
            }
        }
        return result; //Return true if all objects were successfully added
    }

    Cursor getItems() {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return database.rawQuery(query, null);
    }
}
