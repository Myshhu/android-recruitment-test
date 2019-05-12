package dog.snow.androidrecruittest;

import android.database.Cursor;
import android.os.Bundle;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity implements SwipeRefreshLayout.OnRefreshListener {

    private RecyclerViewAdapter mAdapter;
    private SwipeRefreshLayout mSwipeRefreshLayout;
    private DataDownloader dataDownloader;
    private EditText etSearch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mSwipeRefreshLayout = (SwipeRefreshLayout) findViewById(R.id.swipeRefreshLayout);
        mSwipeRefreshLayout.setOnRefreshListener(this);

        etSearch = (EditText) findViewById(R.id.search_et);
        etSearch.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                new Thread(() -> {
                    DatabaseHelper databaseHelper = new DatabaseHelper(getApplicationContext());
                    Cursor filteredItems = databaseHelper.getFilteredItems(charSequence.toString());
                    mAdapter.setFilteredResults(filteredItems);
                }).start();
            }

            @Override
            public void afterTextChanged(Editable editable) {

            }
        });

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
