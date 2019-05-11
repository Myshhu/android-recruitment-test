package dog.snow.androidrecruittest;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

public class DataDownloader extends AsyncTask <Void, Void, Void> {

    private static final String SERVER_URL = ServerURLContainer.SERVER_URL;
    private static final String TAG = MainActivity.class.getName();
    private JSONArray downloadedJSONArray;
    private final WeakReference<Context> weakContext; //Avoid memory leak
    private DatabaseHelper databaseHelper;
    private RecyclerViewAdapter mAdapter;

    DataDownloader(Context context, RecyclerViewAdapter mAdapter) {
        this.weakContext = new WeakReference<>(context);
        this.mAdapter = mAdapter;
        downloadedJSONArray = new JSONArray();
    }

    @Override
    protected Void doInBackground(Void... voids) {
        try {
            URL url = new URL(SERVER_URL);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.connect();

            InputStream inputStream = connection.getInputStream();
            downloadedJSONArray = createJSONArrayFromInputStream(inputStream);

            connection.disconnect();
        } catch (Exception e) {
            Log.e(TAG, "Data downloading error", e);
        }

        Log.d(TAG, downloadedJSONArray.toString());

        databaseHelper = new DatabaseHelper(weakContext.get());
        databaseHelper.insertArray(downloadedJSONArray);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        hideEmptyListTextView();
        Log.i(TAG, "Notifying adapter ");
        mAdapter.notifyAdapterDataSetChanged();
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

    private Bitmap getBitmapFromUrl(String imageUrl) {
        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (IOException e) {
            Log.e(TAG, "getBitmapFromUrl error", e);
            return null;
        }
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

    private void hideEmptyListTextView() {
        if (databaseHelper.getItems().getCount() > 0) {
            ((MainActivity)weakContext.get()).findViewById(R.id.empty_list_tv).setVisibility(View.GONE);
        }
    }
}
