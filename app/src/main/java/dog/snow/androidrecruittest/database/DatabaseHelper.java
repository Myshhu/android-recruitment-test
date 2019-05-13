package dog.snow.androidrecruittest.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONException;
import org.json.JSONObject;

import java.lang.ref.WeakReference;

import dog.snow.androidrecruittest.app.MainActivity;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "DatabaseHelper";
    private final WeakReference<Context> weakContext; //Avoid memory leak


    private static final String TABLE_NAME = "JSON_Items";
    private static final String ID_COLUMN = "id";
    public static final String NAME_COLUMN = "name";
    public static final String DESCRIPTION_COLUMN = "description";
    public static final String ICON_COLUMN = "icon";
    private static final String TIMESTAMP_COLUMN = "timestamp";
    private static final String URL_COLUMN = "url";

    public DatabaseHelper(Context context) {
        super(context, TABLE_NAME, null, 103);
        weakContext = new WeakReference<>(context);
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

    public void restartTable() {
        dropTable();
        createTable();
        Log.i(TAG, "Database version: " + getWritableDatabase().getVersion());
    }

    public void addItem(JSONObject object) {
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

    public Cursor getItems() {
        SQLiteDatabase database = this.getWritableDatabase();
        String query = "SELECT * FROM " + TABLE_NAME;
        return database.rawQuery(query, null);
    }

    public Cursor getFilteredItems(String searchedText) {
        if (searchedText.equals("") || searchedText.trim().length() == 0) { //Check if searchedText is only whitespaces
            return getItems();
        }
        try {
            SQLiteDatabase database = this.getWritableDatabase();
            String query = "SELECT * FROM " + TABLE_NAME + " WHERE " + NAME_COLUMN + " LIKE " + "'%" + searchedText + "%'" + " OR " + DESCRIPTION_COLUMN + " LIKE " + "'%" + searchedText + "%'";
            Log.i(TAG, "Performing query: " + query);
            return database.rawQuery(query, null);
        } catch (Exception e) {
            Log.e(TAG, "getFilteredItems error", e);
            makeToast("Filtering error");
            return getItems();
        }
    }

    private void makeToast(String text) {
        ((MainActivity) weakContext.get()).runOnUiThread(() -> Toast.makeText(weakContext.get(), text, Toast.LENGTH_SHORT).show());
    }
}
