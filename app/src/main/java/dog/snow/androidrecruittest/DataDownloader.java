package dog.snow.androidrecruittest;

import android.content.Context;
import android.database.Cursor;
import android.os.AsyncTask;
import android.util.Log;

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

    private static final String SERVER_URL = "http://774bac8f.ngrok.io/api/items";
    private static final String TAG = MainActivity.class.getName();
    private JSONArray downloadedJSONArray;
    private final WeakReference<Context> weakContext; //Avoid memory leak
    private DBHelper dbHelper;

    DataDownloader(Context context) {
        this.weakContext = new WeakReference<>(context);
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
        } catch (Exception e) {
            Log.e(TAG, "Data downloading error", e);
        }

        Log.d(TAG, downloadedJSONArray.toString());

        dbHelper = new DBHelper(weakContext.get());
        dbHelper.insertArray(downloadedJSONArray);

        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        super.onPostExecute(aVoid);

        Cursor items = dbHelper.getItems();
        while (items.moveToNext()) {
            String result = items.getInt(0) + " " +
                    items.getString(1) + " " +
                    items.getString(2) + " " +
                    items.getString(3) + " " +
                    items.getLong(4) + " " +
                    items.getString(5) + " ";
                    Log.i(TAG, result);
        }
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
}
