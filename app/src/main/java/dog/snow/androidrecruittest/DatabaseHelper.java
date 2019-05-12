package dog.snow.androidrecruittest;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";

    private static final String TABLE_NAME = "JSON_Items";
    private static final String ID_COLUMN = "id";
    static final String NAME_COLUMN = "name";
    static final String DESCRIPTION_COLUMN = "description";
    static final String ICON_COLUMN = "icon";
    private static final String TIMESTAMP_COLUMN = "timestamp";
    static final String URL_COLUMN = "url";

    DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 103);
    }


    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        Log.d(TAG, "onCreate called");
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" + ID_COLUMN + " INTEGER, " +
                NAME_COLUMN + " TEXT, " + DESCRIPTION_COLUMN + " TEXT, " + ICON_COLUMN + " TEXT, " + TIMESTAMP_COLUMN + " LONG, " + URL_COLUMN + " TEXT)";
        sqLiteDatabase.execSQL(createTable);
    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        Log.d(TAG, "onUpgrade called");
        sqLiteDatabase.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
        onCreate(sqLiteDatabase);
    }

    private void dropTable() {
        Log.d(TAG, "Dropping table");
        SQLiteDatabase database = this.getWritableDatabase();
        database.execSQL("DROP TABLE IF EXISTS " + TABLE_NAME);
    }

    private void createTable() {
        SQLiteDatabase database = this.getWritableDatabase();
        String createTable = "CREATE TABLE " + TABLE_NAME + "(" + ID_COLUMN + " INTEGER, " +
                NAME_COLUMN + " TEXT, " + DESCRIPTION_COLUMN + " TEXT, " + ICON_COLUMN + " TEXT, " + TIMESTAMP_COLUMN + " LONG, " + URL_COLUMN + " TEXT)";
        database.execSQL(createTable);
    }

    void restartTable() {
        dropTable();
        createTable();
        Log.i(TAG, "Database version: " + getWritableDatabase().getVersion());
    }

    void addItem(JSONObject object) {
        SQLiteDatabase database = this.getWritableDatabase();
        ContentValues contentValues = createContentValues(object);
        long result = database.insert(TABLE_NAME, null, contentValues);
        if (result == -1) {
            Log.e(TAG, "Inserting to database failed");
        }
    }

    private ContentValues createContentValues(JSONObject object) {
        ContentValues contentValues = new ContentValues();

        //Default values
        contentValues.put(ID_COLUMN, "");
        contentValues.put(NAME_COLUMN, "");
        contentValues.put(DESCRIPTION_COLUMN, "");
        contentValues.put(ICON_COLUMN, "");
        contentValues.put(TIMESTAMP_COLUMN, "");
        contentValues.put(URL_COLUMN, "");

        try {

            if (object.has(ID_COLUMN)) {
                contentValues.put(ID_COLUMN, object.getString(ID_COLUMN));
            }

            if (object.has(NAME_COLUMN)) {
                contentValues.put(NAME_COLUMN, object.getString(NAME_COLUMN));
            } else {
                if (object.has("title")) {
                    contentValues.put(NAME_COLUMN, object.getString("title"));
                } else {
                    contentValues.put(NAME_COLUMN, "No name provided");
                }
            }

            if (object.has(DESCRIPTION_COLUMN)) {
                contentValues.put(DESCRIPTION_COLUMN, object.getString(DESCRIPTION_COLUMN));
            } else {
                contentValues.put(DESCRIPTION_COLUMN, "No description provided");
            }

            if (object.has(ICON_COLUMN)) {
                contentValues.put(ICON_COLUMN, object.getString(ICON_COLUMN));
            } else {
                if (object.has(URL_COLUMN)) {
                    contentValues.put(ICON_COLUMN, object.getString(URL_COLUMN));
                }
            }

            if (object.has(TIMESTAMP_COLUMN)) {
                contentValues.put(TIMESTAMP_COLUMN, object.getLong(TIMESTAMP_COLUMN));
            }

            if (object.has(URL_COLUMN)) {
                contentValues.put(URL_COLUMN, object.getString(URL_COLUMN));
            }

        } catch (JSONException e) {
            Log.e(TAG, "createContentValues error", e);
        }
        return contentValues;
    }

    void insertArray(JSONArray array) {
        restartTable();
        Log.i(TAG, "Starting to insert array");

        for (int i = 0; i < array.length(); i++) {
            try {
                Log.i(TAG, "Inserting item " + i);
                JSONObject object = array.getJSONObject(i);
                addItem(object);
            } catch (Exception e) {
                Log.e(TAG, "insertArray function failed", e);
            }
        }
    }

    Cursor getItems() {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return database.rawQuery(query, null);
    }
}
