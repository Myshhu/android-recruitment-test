package dog.snow.androidrecruittest;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;

public class ImageRepository {

    private static final String TAG = ImageRepository.class.getName();

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

    public static Bitmap readBitmapFromCache(Context context, String name) {
        File cacheDir = context.getCacheDir();
        File pictureFile = new File(cacheDir, name);

        FileInputStream fileInputStream;
        Bitmap bitmap = null;
        try {
            fileInputStream = new FileInputStream(pictureFile);
            bitmap = BitmapFactory.decodeStream(fileInputStream);

        } catch (FileNotFoundException e) {
            Log.e(TAG, "saveToCache failed - File not found", e);
        }

        return bitmap;
    }

    public static boolean isBitmapInCache(Context context, String name) {
        File cacheDir = context.getCacheDir();
        File pictureFile = new File(cacheDir, name);
        return pictureFile.exists();
    }
}
