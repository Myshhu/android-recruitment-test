package dog.snow.androidrecruittest;

import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerViewAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private DataDownloader dataDownloader;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);
        RecyclerView recyclerView = (RecyclerView) findViewById(R.id.items_rv);

        RecyclerView.LayoutManager layoutManager = new LinearLayoutManager(this);
        recyclerView.setLayoutManager(layoutManager);

        mAdapter = new RecyclerViewAdapter(this);
        recyclerView.setAdapter(mAdapter);

        resetDatabase();
        downloadData();
    }

    private void downloadData() {
        dataDownloader = new DataDownloader(this, mAdapter, mSwipeRefreshLayout);
        dataDownloader.execute();
    }

    private void resetDatabase() {
        new DatabaseHelper(this).restartTable();
    }

    @Override
    public void onRefresh() {
        if (dataDownloader != null) {
            dataDownloader.cancel(true);
            Toast.makeText(this, "Refreshing..", Toast.LENGTH_SHORT).show();
            dataDownloader = new DataDownloader(this, mAdapter, mSwipeRefreshLayout);
            dataDownloader.execute();
        } else {
            mSwipeRefreshLayout.setRefreshing(false);
        }
    }
}
