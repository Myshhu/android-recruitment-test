package dog.snow.androidrecruittest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        
        downloadData();
    }

    private void downloadData() {
        DataDownloader dataDownloader = new DataDownloader(this);
        dataDownloader.execute();
    }
}
