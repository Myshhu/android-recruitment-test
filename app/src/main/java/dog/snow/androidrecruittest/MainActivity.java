package dog.snow.androidrecruittest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.Toast;

import org.json.JSONArray;
import org.json.JSONException;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class MainActivity extends AppCompatActivity {

    //private final String serverURL = "http://192.168.0.18:8080/api/items";
    private static final String SERVER_URL = "http://1bbdf2f3.ngrok.io/api/items";
    private static final String TAG = MainActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        downloadData();
    }

    private void downloadData() {
        new Thread(() -> {
            JSONArray downloadedJSON = new JSONArray();
            try {
                URL url = new URL(SERVER_URL);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.connect();

                InputStream inputStream = connection.getInputStream();
                downloadedJSON = createJSONArrayFromInputStream(inputStream);

                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Data downloaded", Toast.LENGTH_SHORT).show());

            } catch (Exception e) {
                runOnUiThread(() -> Toast.makeText(getApplicationContext(), "Data downloading error", Toast.LENGTH_SHORT).show());
                Log.e(TAG, "Data downloading error", e);
            }

            Log.d(TAG, downloadedJSON.toString());
        }).start();
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
