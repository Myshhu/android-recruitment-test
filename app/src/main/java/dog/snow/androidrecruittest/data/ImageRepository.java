package dog.snow.androidrecruittest.data;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class ImageRepository {

    private static final String TAG = ImageRepository.class.getName();

    ImageRepository() {}

    public static void saveBitmapToCache(Context context, Bitmap pic, String name) {
        File cacheDir = context.getCacheDir();
        File pictureFile = new File(cacheDir, name);

        try (FileOutputStream outputStream = new FileOutputStream(pictureFile)) {
            pic.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
        } catch (Exception e) {
            Log.e(TAG, "saveToCache failed", e);
            pictureFile.delete();
        }
    }

    public static Bitmap getBitmapFromCache(Context context, String name) {
        File cacheDir = context.getCacheDir();
        Bitmap bitmap;

        //Firstly check if passed name is null, then check if file with given name exists
        if (name != null && new File(cacheDir, name).exists()) {
            bitmap = getBitmapFromFile(new File(cacheDir, name));
        } else {
            return null;
        }

        return bitmap;
    }

    private static Bitmap getBitmapFromFile(File pictureFile) {
        Log.d(TAG, "Getting Bitmap From File " + pictureFile);
        try {
            FileInputStream fileInputStream = new FileInputStream(pictureFile);
            return BitmapFactory.decodeStream(fileInputStream);
        } catch (FileNotFoundException e) {
            Log.e(TAG, "getBitmapFromFile error", e);
        }
        return null;
    }

    public static boolean isBitmapInCache(Context context, String name) {
        File cacheDir = context.getCacheDir();
        File pictureFile = new File(cacheDir, name);
        return pictureFile.exists();
    }

    public static Bitmap getBitmapFromUrl(String imageUrl) {
        Log.d(TAG, "Getting Bitmap From URL " + imageUrl);

        try {
            URL url = new URL(imageUrl);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            return BitmapFactory.decodeStream(input);
        } catch (Exception e) {
            Log.e(TAG, "getBitmapFromUrl error", e);
            return null;
        }
    }
}
