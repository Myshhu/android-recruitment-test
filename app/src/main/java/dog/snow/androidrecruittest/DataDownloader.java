package dog.snow.androidrecruittest;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class DataDownloader extends AsyncTask <Void, Integer, Void> {

    private static final String SERVER_URL = ServerURLContainer.SERVER_URL;
    private static final String SECOND_SERVER_URL = ServerURLContainer.SECOND_SERVER_URL;

    private static final String TAG = MainActivity.class.getName();
    private JSONArray downloadedJSONArray;
    private final WeakReference<Context> weakContext; //Avoid memory leak
    private DatabaseHelper databaseHelper;
    private RecyclerViewAdapter mAdapter;

    DataDownloader(Context context, RecyclerViewAdapter mAdapter) {
        this.weakContext = new WeakReference<>(context);
        this.mAdapter = mAdapter;
        databaseHelper = new DatabaseHelper(weakContext.get());

    }

    @Override
    protected Void doInBackground(Void... voids) {

        downloadedJSONArray = getJSONArrayFromURL(SERVER_URL);

        if(downloadedJSONArray == null) {
            Log.i(TAG, "First server connection failed, trying to connect to second server");
            makeToast("First server connection failed");
            getJSONArrayFromURL(SECOND_SERVER_URL);
        }

        if(downloadedJSONArray != null) {
            Log.d(TAG, "Data successfully downloaded, inserting array to database");
            makeToast("Data successfully downloaded");
            insertArrayToDatabase(downloadedJSONArray);
        } else {
            Log.e(TAG, "doInBackground - Data downloading failed");
            makeToast("Data downloading failed - check connection and restart app");
        }

        return null;
    }

    private void insertArrayToDatabase(JSONArray array) {
        databaseHelper.restartTable();
        Log.i(TAG, "Starting to insert array");

        for (int i = 0; i < array.length(); i++) {
            try {
                JSONObject object = array.getJSONObject(i);
                databaseHelper.addItem(object);
                onProgressUpdate(i);
            } catch (Exception e) {
                Log.e(TAG, "insertArrayToDatabase function failed", e);
            }
        }
    }

    @Override
    protected void onProgressUpdate(Integer... values) {
        super.onProgressUpdate(values);

        if(values[0] % 500 == 0) { //Refresh every 500 items added; app slows when list is refreshed after adding every item
            Log.i(TAG, "notifying Item Inserted item " + values[0]);
            mAdapter.notifyAdapterDataSetChanged();
        }
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Log.i(TAG, "Notifying adapter ");
        mAdapter.notifyAdapterDataSetChanged();
    }

    private JSONArray getJSONArrayFromURL(String parameterUrl) {
        try {
            URL url = new URL(parameterUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(3000);
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            downloadedJSONArray = createJSONArrayFromInputStream(inputStream);

            connection.disconnect();
        } catch (Exception e) {
            Log.e(TAG, "Data downloading error", e);
            downloadedJSONArray = null;
        }
        return downloadedJSONArray;
    }

    private JSONArray createJSONArrayFromInputStream(InputStream inputStream) throws IOException, JSONException {
        BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(inputStream));
        StringBuilder result = new StringBuilder();
        String line;
        while((line = bufferedReader.readLine()) != null) {
            result.append(line).append("\n");
        }
        return new JSONArray(result.toString());
    }

    private void printItemsFromDatabase() {
        Cursor items = databaseHelper.getItems();
        while (items.moveToNext()) {
            String result = items.getInt(0) + " " + // Id
                    items.getString(1) + " " +  // Name
                    items.getString(2) + " " +  // Description
                    items.getString(3) + " " +  // Icon
                    items.getLong(4) + " " +    // Timestamp
                    items.getString(5) + " ";   // Url
            Log.i(TAG, result);
        }
    }

    private void makeToast(String text) {
        ((MainActivity)weakContext.get()).runOnUiThread(() -> Toast.makeText(weakContext.get(), text, Toast.LENGTH_SHORT).show());
    }
}
