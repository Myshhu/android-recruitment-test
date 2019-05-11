package dog.snow.androidrecruittest;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.lang.ref.WeakReference;

public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.MyViewHolder> {
    private DatabaseHelper databaseHelper;
    private Cursor dbCursor;
    private final WeakReference<Context> weakContext; //Avoid memory leak

    private static final String TAG = RecyclerViewAdapter.class.getName();


    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class MyViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView tvName;
        private TextView tvDescription;
        ImageView imageViewIcon;
        public MyViewHolder(View v) {
            super(v);
            tvName = (TextView) v.findViewById(R.id.name_tv);
            tvDescription = (TextView) v.findViewById(R.id.description_tv);
            imageViewIcon = (ImageView) v.findViewById(R.id.icon_ic);
        }
    }

    public RecyclerViewAdapter(Context context) {
        weakContext = new WeakReference<>(context);
        databaseHelper = new DatabaseHelper(context);
        dbCursor = databaseHelper.getItems();
    }

    public void notifyAdapterDataSetChanged() {
        dbCursor = databaseHelper.getItems();
        this.notifyDataSetChanged();
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerViewAdapter.MyViewHolder onCreateViewHolder(ViewGroup parent,
                                                     int viewType) {

        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item, parent, false);
        MyViewHolder vh = new MyViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(MyViewHolder holder, int position) {
        String name = getNameFromCursor(position);
        String description = getDescriptionFromCursor(position);

        holder.tvName.setText(name);
        holder.tvDescription.setText(description);

        setHolderImage(holder, position, name);


    }

    private void setHolderImage(MyViewHolder holder, int holderPosition, String imageName) {
        new Thread(() -> {
            Bitmap bitmap;

            if (ImageRepository.isBitmapInCache(weakContext.get(), imageName)) {
                bitmap = ImageRepository.getBitmapFromCache(weakContext.get(), imageName);
            } else {
                String url = getIconURLFromCursor(holderPosition);
                bitmap = ImageRepository.getBitmapFromUrl(url);

                //Save image to cache if downloaded from url
                ImageRepository.saveBitmapToCache(weakContext.get(), bitmap, imageName);
            }

            Bitmap finalBitmap = bitmap;
            ((MainActivity)weakContext.get()).runOnUiThread(() -> holder.imageViewIcon.setImageBitmap(finalBitmap));

        }).start();
    }

    @Override
    public int getItemCount() {
        return dbCursor.getCount();
    }

    private String getNameFromCursor(int position) {
        dbCursor.moveToPosition(position);
        int nameIndex = dbCursor.getColumnIndex(DatabaseHelper.NAME_COLUMN);
        return dbCursor.getString(nameIndex);
    }

    private String getDescriptionFromCursor(int position) {
        dbCursor.moveToPosition(position);
        int descriptionIndex = dbCursor.getColumnIndex(DatabaseHelper.DESCRIPTION_COLUMN);
        return dbCursor.getString(descriptionIndex);
    }

    private String getIconURLFromCursor(int position) {
        dbCursor.moveToPosition(position);
        int columnIndex = dbCursor.getColumnIndex(DatabaseHelper.URL_COLUMN);
        return dbCursor.getString(columnIndex);
    }
}
