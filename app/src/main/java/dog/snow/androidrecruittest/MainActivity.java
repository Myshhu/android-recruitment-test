package dog.snow.androidrecruittest;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;

public class MainActivity extends AppCompatActivity {

    private RecyclerViewAdapter mAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.items_rv);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new RecyclerViewAdapter(this);
        recyclerView.setAdapter(mAdapter);

        resetDatabase();
        downloadData();
    }

    private void downloadData() {
        DataDownloader dataDownloader = new DataDownloader(this, mAdapter);
        dataDownloader.execute();
    }

    private void resetDatabase() {
        new DatabaseHelper(this).restartTable();
    }
}
