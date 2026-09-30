package com.paymentsave.paymentsave.coreapp.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.res.AssetFileDescriptor;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.os.Handler;
import android.provider.DocumentsContract;
import android.provider.MediaStore;
import android.provider.OpenableColumns;
import android.util.Log;
import android.webkit.MimeTypeMap;

import androidx.annotation.RequiresApi;
import androidx.documentfile.provider.DocumentFile;

import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.bumptech.glide.request.RequestOptions;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Comparator;
import java.util.Date;
import java.util.Objects;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class FileUtils {
    public static ExecutorService executorService = Executors.newFixedThreadPool(4);

    public static final String MIME_TYPE_AUDIO = "audio/*";
    public static final String MIME_TYPE_TEXT = "text/*";
    public static final String MIME_TYPE_IMAGE = "image/*";
    public static final String MIME_TYPE_VIDEO = "video/*";
    public static final String MIME_TYPE_APP = "application/*";
    public static final String HIDDEN_PREFIX = ".";
    /**
     * TAG for log messages.
     */
    static final String TAG = "FileUtils";
    private static final boolean DEBUG = false; // Set to true to enable logging
    /**
     * File and folder comparator. TODO Expose sorting option method
     *
     * @author paulburke
     */
    public static Comparator<File> sComparator = new Comparator<File>() {
        @Override
        public int compare(File f1, File f2) {
            // Sort alphabetically by lower case, which is much cleaner
            return f1.getName().toLowerCase().compareTo(
                    f2.getName().toLowerCase());
        }
    };
    /**
     * File (not directories) filter.
     *
     * @author paulburke
     */
    public static FileFilter sFileFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            final String fileName = file.getName();
            // Return files only (not directories) and skip hidden files
            return file.isFile() && !fileName.startsWith(HIDDEN_PREFIX);
        }
    };
    /**
     * Folder (directories) filter.
     *
     * @author paulburke
     */
    public static FileFilter sDirFilter = new FileFilter() {
        @Override
        public boolean accept(File file) {
            final String fileName = file.getName();
            // Return directories only and skip hidden directories
            return file.isDirectory() && !fileName.startsWith(HIDDEN_PREFIX);
        }
    };

    private FileUtils() {
    } //private constructor to enforce Singleton pattern

    /**
     * Gets the extension of a file name, like ".png" or ".jpg".
     *
     * @param uri
     * @return Extension including the dot("."); "" if there is no extension;
     * null if uri was null.
     */
    public static String getExtension(String uri) {
        if (uri == null) {
            return null;
        }

        int dot = uri.lastIndexOf(".");
        if (dot >= 0) {
            return uri.substring(dot);
        } else {
            // No extension.
            return "";
        }
    }

    /**
     * @return Whether the URI is a local one.
     */
    public static boolean isLocal(String url) {
        if (url != null && !url.startsWith("http://") && !url.startsWith("https://")) {
            return true;
        }
        return false;
    }

    /**
     * @return True if Uri is a MediaStore Uri.
     * @author paulburke
     */
    public static boolean isMediaUri(Uri uri) {
        return "media".equalsIgnoreCase(uri.getAuthority());
    }

    /**
     * Convert File into Uri.
     *
     * @param file
     * @return uri
     */
    public static Uri getUri(File file) {
        if (file != null) {
            return Uri.fromFile(file);
        }
        return null;
    }

    /**
     * Returns the path only (without file name).
     *
     * @param file
     * @return
     */
    public static File getPathWithoutFilename(File file) {
        if (file != null) {
            if (file.isDirectory()) {
                // no file to be split off. Return everything
                return file;
            } else {
                String filename = file.getName();
                String filepath = file.getAbsolutePath();

                // Construct path without file name.
                String pathwithoutname = filepath.substring(0,
                        filepath.length() - filename.length());
                if (pathwithoutname.endsWith("/")) {
                    pathwithoutname = pathwithoutname.substring(0, pathwithoutname.length() - 1);
                }
                return new File(pathwithoutname);
            }
        }
        return null;
    }

    /**
     * @return The MIME type for the given file.
     */
    public static String getMimeType(File file) {

        String extension = getExtension(file.getName());

        if (extension.length() > 0)
            try {
                return MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension.substring(1));
            } catch (Exception e) {
                e.printStackTrace();
            }
        return "application/octet-stream";
    }

    public static boolean isVideoFile(Context context, Uri uri) throws IOException {
        String mimeType = getMimeType(context, uri);
        return mimeType != null && mimeType.startsWith("video");
    }


    public static long getFileSize(Context context, Uri fileUri) {

        Cursor returnCursor = context.getContentResolver().
                query(fileUri, null, null, null, null);

        if (returnCursor != null && (returnCursor.moveToFirst())) {
            int sizeIndex = returnCursor.getColumnIndex(OpenableColumns.SIZE);
            returnCursor.moveToFirst();

            long size = returnCursor.getLong(sizeIndex);
            returnCursor.close();

            // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
            long fileSizeInKB = size / 1024;
            // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
            long fileSizeInMB = fileSizeInKB / 1024;
            Log.e(TAG, "getFileSize: " + fileSizeInMB);
            return fileSizeInMB;
        } else {
            AssetFileDescriptor fileDescriptor = null;
            try {
                fileDescriptor = context.getContentResolver().openAssetFileDescriptor(fileUri, "r");
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            long fileSize = fileDescriptor.getLength();
            // Convert the bytes to Kilobytes (1 KB = 1024 Bytes)
            long fileSizeInKB = fileSize / 1024;
            // Convert the KB to MegaBytes (1 MB = 1024 KBytes)
            long fileSizeInMB = fileSizeInKB / 1024;
            Log.e(TAG, "getFileSize: " + fileSizeInMB);
            return fileSizeInMB;
        }
    }


    public static Uri getImageUri(Context inContext, Bitmap inImage) {
        //        ByteArrayOutputStream bytes = new ByteArrayOutputStream();
        //        inImage.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
        String path = MediaStore.Images.Media.insertImage(inContext.getContentResolver(), inImage, "IMG_" + Calendar.getInstance().getTime(), null);
        return Uri.parse(path);
    }

    /**
     * @return The MIME type for the give Uri.
     */
    public static String getMimeType(Context context, Uri uri) throws IOException {
        Log.e(TAG, "getMimeType: " + uri.getPath());

        File file;
        try {
            String path = getPath(context, uri);
            file = new File(Objects.requireNonNull(path));
        } catch (Exception e) {
            Log.e(TAG, "getMimeType:R " + getMimeType(Objects.requireNonNull(getFileFromExternalStorage(context, uri))));
            getFileFromExternalStorage(context, uri);
            String path = getPathFromUri(context, uri);
            file = new File(path);
        }
        return getMimeType(file);
    }

    private static File getFileFromExternalStorage(Context context, Uri uri) throws IOException {
        // Get the ContentResolver
        ContentResolver contentResolver = context.getContentResolver();

        // Query the MediaStore for the file details
        Cursor cursor = contentResolver.query(uri, null, null, null, null);

        // Get the file path from the details
        String filePath = null;
        if (cursor != null && cursor.moveToFirst()) {
            int column_index = cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME);
            String fileName = cursor.getString(column_index);
            String dirPath = Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "dir_name";
            File dir = new File(dirPath);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            filePath = dir.getAbsolutePath() + File.separator + fileName;
            File outFile = new File(filePath);
            OutputStream outputStream = new FileOutputStream(outFile);
            InputStream inputStream = contentResolver.openInputStream(uri);
            byte[] buffer = new byte[4 * 1024];
            int read;
            while ((read = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, read);
            }
            outputStream.flush();
            outputStream.close();
            inputStream.close();
        }

        // Check if the file path is on external storage
        if (filePath != null && filePath.startsWith(Environment.getExternalStorageDirectory().getPath())) {
            // Convert the file path to a File object
            File file = new File(filePath);
            // Do something with the file
            return file;
        } else {
            // File path is not on external storage
            return null;
        }
    }


    public static String getPathFromUri(Context context, Uri uri) {
        if (uri == null) {
            return null;
        }

        String path = null;
        if ("content".equals(uri.getScheme())) {
            // For content URIs, try to resolve to a file path using the DocumentFile API
            DocumentFile documentFile = DocumentFile.fromSingleUri(context, uri);
            if (documentFile != null) {
                path = documentFile.getUri().getPath();
            }
        } else if ("file".equals(uri.getScheme())) {
            // For file URIs, simply get the path from the URI
            path = uri.getPath();
        }

        return path;
    }

    public static String getMimeTypeTwo(File file) throws IOException {
        String mimeType = null;
        String fileName = file.getName();
        String fileExtension = fileName.substring(fileName.lastIndexOf(".") + 1);

        URL url = file.toURI().toURL();
        URLConnection connection = url.openConnection();
        String contentType = connection.getContentType();

        if (contentType != null) {
            mimeType = contentType.split(";")[0];
        } else {
            mimeType = URLConnection.guessContentTypeFromName(fileName);
            if (mimeType == null) {
                mimeType = URLConnection.guessContentTypeFromStream(new FileInputStream(file));
            }
        }

        if (mimeType == null) {
            mimeType = MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.toLowerCase());
        }

        return mimeType;
    }


    public static RequestOptions getGlideRequestOptions(Context context, File file, long maxFileSize) {
        // Calculate the size of the file in pixels
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(file.getAbsolutePath(), options);
        int imageWidth = options.outWidth;
        int imageHeight = options.outHeight;

        // Calculate the appropriate width and height based on the file size
        int sizeMultiplier = (int) Math.ceil(Math.sqrt((double) file.length() / maxFileSize));
        int targetWidth = imageWidth / sizeMultiplier;
        int targetHeight = imageHeight / sizeMultiplier;

        // Set the RequestOptions object with the appropriate size
        return new RequestOptions()
                .override(targetWidth, targetHeight)
                .diskCacheStrategy(DiskCacheStrategy.DATA);
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is {@link LocalStorageProvider}.
     * @author paulburke
     */
    public static boolean isLocalStorageDocument(Uri uri) {
        return LocalStorageProvider.AUTHORITY.equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     * @author paulburke
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     * @author paulburke
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
     * @author paulburke
     */
    public static boolean isMediaDocument(Uri uri) {
        return "com.android.providers.media.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is Google Photos.
     */
    public static boolean isGooglePhotosUri(Uri uri) {
        return "com.google.android.apps.photos.content".equals(uri.getAuthority());
    }

    /**
     * Get the value of the data column for this Uri. This is useful for
     * MediaStore Uris, and other file-based ContentProviders.
     *
     * @param context       The context.
     * @param uri           The Uri to query.
     * @param selection     (Optional) Filter used in the query.
     * @param selectionArgs (Optional) Selection arguments used in the query.
     * @return The value of the _data column, which is typically a file path.
     * @author paulburke
     */
    public static String getDataColumn(Context context, Uri uri, String selection,
                                       String[] selectionArgs) {

        Cursor cursor = null;
        final String column = "_data";
        final String[] projection = {
                column
        };

        try {
            cursor = context.getContentResolver().query(uri, projection, selection, selectionArgs,
                    null);
            if (cursor != null && cursor.moveToFirst()) {
                if (DEBUG)
                    DatabaseUtils.dumpCursor(cursor);

                final int column_index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(column_index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }

    /**
     * Get a file path from a Uri. This will get the the path for Storage Access
     * Framework Documents, as well as the _data field for the MediaStore and
     * other file-based ContentProviders.<br>
     * <br>
     * Callers should check whether the path is local before assuming it
     * represents a local file.
     *
     * @param context The context.
     * @param uri     The Uri to query.
     * @author paulburke
     * @see #isLocal(String)
     * @see #getFile(Context, Uri)
     */


    public static String getPath(final Context context, final Uri uri) {

        if (DEBUG)
            Log.d(TAG + " File -",
                    "Authority: " + uri.getAuthority() +
                            ", Fragment: " + uri.getFragment() +
                            ", Port: " + uri.getPort() +
                            ", Query: " + uri.getQuery() +
                            ", Scheme: " + uri.getScheme() +
                            ", Host: " + uri.getHost() +
                            ", Segments: " + uri.getPathSegments().toString()
            );


        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            Log.e(TAG, "getPath: DocumentUri");

            // LocalStorageProvider
            if (isLocalStorageDocument(uri)) {
                // The path is the id
                return DocumentsContract.getDocumentId(uri);
            }
            // ExternalStorageProvider
            else if (isExternalStorageDocument(uri)) {
                Log.e(TAG, "IsExternalStorageDocument: " + isExternalStorageDocument(uri));
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
                    Log.e(TAG, "IsExternalStorageDocument: " + Environment.getExternalStorageDirectory() + "/" + split[1]);
                    return Environment.getExternalStorageDirectory() + "/" + split[1];
                }

                // TODO handle non-primary volumes
            }
            // DownloadsProvider
            else if (isDownloadsDocument(uri)) {

                final String id = DocumentsContract.getDocumentId(uri);
                final Uri contentUri = ContentUris.withAppendedId(
                        Uri.parse("content://downloads/public_downloads"), Long.valueOf(id));

                return getDataColumn(context, contentUri, null, null);
            }
            // MediaProvider
            else if (isMediaDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                Uri contentUri = null;
                if ("image".equals(type)) {
                    contentUri = MediaStore.Images.Media.EXTERNAL_CONTENT_URI;
                } else if ("video".equals(type)) {
                    contentUri = MediaStore.Video.Media.EXTERNAL_CONTENT_URI;
                } else if ("audio".equals(type)) {
                    contentUri = MediaStore.Audio.Media.EXTERNAL_CONTENT_URI;
                }

                final String selection = "_id=?";
                final String[] selectionArgs = new String[]{
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {
            Log.e(TAG, "getPath: ContentUri");
            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            Log.e(TAG, "getPath: FileUri");
            return uri.getPath();
        }

        return null;
    }

    /**
     * Convert Uri into File, if possible.
     *
     * @return file A local file that the Uri was pointing to, or null if the
     * Uri is unsupported or pointed to a remote resource.
     * @author paulburke
     * @see #getPath(Context, Uri)
     */

    public static File getFile(Context context, Uri uri) {
        if (uri != null) {
            String path = getPath(context, uri);
            Log.e(TAG, "getFile:S " + path);
            if (isLocal(path)) {
                return new File(path);
            }
        }
        return null;
    }


    /**
     * Get the file size in a human-readable string.
     *
     * @param size
     * @return
     * @author paulburke
     */
    public static String getReadableFileSize(int size) {
        final int BYTES_IN_KILOBYTES = 1024;
        final DecimalFormat dec = new DecimalFormat("###.#");
        final String KILOBYTES = " KB";
        final String MEGABYTES = " MB";
        final String GIGABYTES = " GB";
        float fileSize = 0;
        String suffix = KILOBYTES;

        if (size > BYTES_IN_KILOBYTES) {
            fileSize = size / BYTES_IN_KILOBYTES;
            if (fileSize > BYTES_IN_KILOBYTES) {
                fileSize = fileSize / BYTES_IN_KILOBYTES;
                if (fileSize > BYTES_IN_KILOBYTES) {
                    fileSize = fileSize / BYTES_IN_KILOBYTES;
                    suffix = GIGABYTES;
                } else {
                    suffix = MEGABYTES;
                }
            }
        }
        return String.valueOf(dec.format(fileSize) + suffix);
    }

    /**
     * Attempt to retrieve the thumbnail of given File from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @param context
     * @param file
     * @return
     * @author paulburke
     */
    public static Bitmap getThumbnail(Context context, File file) {
        return getThumbnail(context, getUri(file), getMimeType(file));
    }

    /**
     * Attempt to retrieve the thumbnail of given Uri from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @param context
     * @param uri
     * @return
     * @author paulburke
     */
    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public static Bitmap getThumbnail(Context context, Uri uri) throws IOException {
        return getThumbnail(context, uri, getMimeType(context, uri));
    }

    /**
     * Attempt to retrieve the thumbnail of given Uri from the MediaStore. This
     * should not be called on the UI thread.
     *
     * @param context
     * @param uri
     * @param mimeType
     * @return
     * @author paulburke
     */
    public static Bitmap getThumbnail(Context context, Uri uri, String mimeType) {
        if (DEBUG)
            Log.d(TAG, "Attempting to get thumbnail");

        if (!isMediaUri(uri)) {
            Log.e(TAG, "You can only retrieve thumbnails for images and videos.");
            return null;
        }

        Bitmap bm = null;
        if (uri != null) {
            final ContentResolver resolver = context.getContentResolver();
            Cursor cursor = null;
            try {
                cursor = resolver.query(uri, null, null, null, null);
                if (cursor.moveToFirst()) {
                    final int id = cursor.getInt(0);
                    if (DEBUG)
                        Log.d(TAG, "Got thumb ID: " + id);

                    if (mimeType.contains("video")) {
                        bm = MediaStore.Video.Thumbnails.getThumbnail(
                                resolver,
                                id,
                                MediaStore.Video.Thumbnails.MINI_KIND,
                                null);
                    } else if (mimeType.contains(FileUtils.MIME_TYPE_IMAGE)) {
                        bm = MediaStore.Images.Thumbnails.getThumbnail(
                                resolver,
                                id,
                                MediaStore.Images.Thumbnails.MINI_KIND,
                                null);
                    }
                }
            } catch (Exception e) {
                if (DEBUG)
                    Log.e(TAG, "getThumbnail", e);
            } finally {
                if (cursor != null)
                    cursor.close();
            }
        }
        return bm;
    }

    /**
     * Get the Intent for selecting content to be used in an Intent Chooser.
     *
     * @return The intent for opening a file with Intent.createChooser()
     * @author paulburke
     */
    public static Intent createGetContentIntent() {
        // Implicitly allow the user to select a particular kind of data
        final Intent intent = new Intent(Intent.ACTION_GET_CONTENT);
        // The MIME data type filter
        intent.setType("*/*");
        // Only return URIs that can be opened with ContentResolver
        intent.addCategory(Intent.CATEGORY_OPENABLE);
        return intent;
    }

//    public static void setGlideImage(Context context,ImageView imageView, String url) {
//        Glide.with(context)
//                .load(url)
//                .diskCacheStrategy(DiskCacheStrategy.DATA)
//                .error(R.drawable.ic_add_image_bg)
//                .dontAnimate()
//                .into(imageView);
//    }


//    public static Intent chooseImageWithPermissionCheck(Context context, Activity activity) {
//        Intent intent = null;
//        if (AppConstants.hasAllPermissions(context)) {
//            intent = chooseImageWithVariation();
//        } else {
//            AppConstants.requestAllPermission(activity);
//        }
//        return intent;
//    }

    public static Intent chooseImageWithVariation() {
        Intent camIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        Intent galIntent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        galIntent.setType("image/*");

        Intent chooserIntent = Intent.createChooser(camIntent, "Select Image");
        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[]{galIntent});

        return chooserIntent;
    }


    public static Uri bitmapToUri(Context context, Bitmap bitmap, String displayName) {
        // Save the bitmap to a temporary file
        File file = new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), displayName + ".jpg");
        try {
            FileOutputStream fos = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.flush();
            fos.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // Insert the image into the MediaStore
        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.DISPLAY_NAME, displayName);
        values.put(MediaStore.Images.Media.MIME_TYPE, "image/jpeg");
        values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis());
        values.put(MediaStore.Images.Media.DATA, file.getAbsolutePath());

        ContentResolver resolver = context.getContentResolver();
        Uri uri = resolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        return uri;
    }


    public static Uri getUriFromData(Context context, Intent data) {

        Uri uri = null;
        Bitmap bitmap = null;
        try {
            bitmap = (Bitmap) data.getExtras().get("data");
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (bitmap == null) {
            uri = data.getData();
        } else {
            try {
                uri = FileUtils.getImageUri(context, bitmap);
            } catch (Exception e) {
                Log.e(TAG, "getUriFromData: 2" + e.getMessage());
            }
        }
        return uri;
    }

    public String getRealPathFromURI(Activity activity, Uri uri) {
        Cursor cursor = activity.getContentResolver().query(uri, null, null, null, null);
        cursor.moveToFirst();
        int idx = cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATA);
        return cursor.getString(idx);
    }
//
//    public static MultipartBody.Part convertUriToMultipart(Context context, String part_name, Uri uri) {
//        MultipartBody.Part partList = null;
//        String partName = part_name;
//        partList = (MultipartEncoder.prepareFilePart(context, partName, uri));
//        return partList;
//    }


    public static Bitmap getBitmapFromURLCustom(String src) {
        try {
            URL url = new URL(src);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setDoInput(true);
            connection.connect();
            InputStream input = connection.getInputStream();
            Bitmap myBitmap = BitmapFactory.decodeStream(input);
            return myBitmap;
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }


    public static Bitmap downloadImage(Context context, String url) {
        Bitmap bitmap = null;
        InputStream stream = null;
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        try {
            Log.e(TAG, "downloadImage: " + getHttpConnection(context, url));
//            stream = getHttpConnection(context, url);
            bitmap = BitmapFactory.decodeStream(stream, null, bmOptions);
//            stream.close();
        } catch (IOException e1) {
            e1.printStackTrace();
            System.out.println("downloadImage" + e1.toString());
        }
        return bitmap;
    }


    // Makes HttpURLConnection and returns Bitmap
    public static Bitmap getHttpConnection(Context context, String urlString) throws IOException {
        final Bitmap[] bitmap = {null};
        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inSampleSize = 1;

        final InputStream[] stream = {null};
        String secureProtocol = "https://";
        String urlWithOutProtocol = urlString.split("//")[1];
        String urlS = secureProtocol + urlWithOutProtocol;
        URL url = new URL(urlS);
        URLConnection connection = url.openConnection();

        if (!(connection instanceof HttpURLConnection))
            throw new IOException("Not an HTTP connection");


        executorService.execute(new Runnable() {
            @Override
            public void run() {
                try {
                    HttpURLConnection httpConnection = (HttpURLConnection) connection;
                    httpConnection.setAllowUserInteraction(false);
                    httpConnection.setInstanceFollowRedirects(true);
                    httpConnection.setRequestMethod("GET");
//                    httpConnection.setConnectTimeout(8000);
                    httpConnection.connect();

                    Log.e(TAG, "getHttpConnection: " + httpConnection.getResponseCode());
                    if (httpConnection.getResponseCode() == HttpURLConnection.HTTP_OK) {
                        stream[0] = httpConnection.getInputStream();
                    }
                } catch (Exception ex) {
                    Log.e(TAG, "getHttpConnection: Error " + ex);
                    ex.printStackTrace();
                    System.out.println("downloadImage" + ex.toString());
                }
            }
        });


        new Handler().postDelayed(new Runnable() {
            @Override
            public void run() {
                bitmap[0] = BitmapFactory.decodeStream(stream[0], null, bmOptions);
                executorService.execute(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            stream[0].close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }, 2000);
//        // Create an executor that executes tasks in the main thread.
//        Executor mainExecutor = ContextCompat.getMainExecutor(context);
//        // Execute a task in the main thread
//        mainExecutor.execute(new Runnable() {
//            @Override
//            public void run() {
//                // You code logic goes here.
//            }
//        });
        return bitmap[0];
    }


//    public static void chooseImageType(int code,Context context,Activity activity){
//        final CharSequence[] options = {"Take Photo", "Choose from Gallery", "Cancel"};
//        AlertDialog.Builder builder = new AlertDialog.Builder(context);
//        builder.setTitle("Add Photo!");
//        builder.setItems(options, new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int item) {
//                if (options[item].equals("Take Photo")) {
//                    Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//                    activity.startActivityForResult(intent, code);
//                } else if (options[item].equals("Choose from Gallery")) {
//                    Intent getIntent = new Intent(Intent.ACTION_GET_CONTENT);
//                    getIntent.setType("image/*");
//
//                    Intent pickIntent = new Intent(Intent.ACTION_PICK, android.provider.MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
//                    pickIntent.setType("image/*");
//
//                    Intent chooserIntent = Intent.createChooser(getIntent, "Select Image");
//                    chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, new Intent[] {pickIntent});
//
//                    activity.startActivityForResult(chooserIntent, code);
//                } else if (options[item].equals("Cancel")) {
//                    dialog.dismiss();
//                }
//            }
//        });
//        builder.show();
//    }

    @SuppressLint("SimpleDateFormat")
    public static String getTimeByString(String created_at) {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd hh:mma");
        Date date = null;
        try {
            date = parser.parse(created_at);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String onlyDate = formatter.format(date);
        return onlyDate;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateToString(String created_at) {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd, hh:mma");
        Date date = null;
        try {
            date = parser.parse(created_at);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String onlyDate = formatter.format(date);
        return onlyDate;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateToTimeOnly(String created_at) {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("hh:mma");
        Date date = null;
        try {
            date = parser.parse(created_at);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String onlyDate = formatter.format(date);
        return onlyDate;
    }

    @SuppressLint("SimpleDateFormat")
    public static String getDateFromString(String created_at) {
        SimpleDateFormat parser = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss");
        SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd");
        Date date = null;
        try {
            date = parser.parse(created_at);
        } catch (Exception e) {
            e.printStackTrace();
        }
        String onlyDate = formatter.format(date);
        return onlyDate;
    }


//    public static String copyFileToInternal(Context context, Uri fileUri) {
//        Cursor cursor = context.getContentResolver().query(fileUri, new String[]{OpenableColumns.DISPLAY_NAME, OpenableColumns.SIZE}, null, null);
//        cursor.moveToFirst();
//
//        @SuppressLint("Range") String displayName = cursor.getString(cursor.getColumnIndex(OpenableColumns.DISPLAY_NAME));
//        @SuppressLint("Range") long size = cursor.getLong(cursor.getColumnIndex(OpenableColumns.SIZE));
//
//        File file = new File(context.getFilesDir() + "/" + displayName);
//        try {
//            FileOutputStream fileOutputStream = new FileOutputStream(file);
//            InputStream inputStream = context.getContentResolver().openInputStream(fileUri);
//            byte buffers[] = new byte[1024];
//            int read;
//            while ((read = inputStream.read(buffers)) != -1) {
//                fileOutputStream.write(buffers, 0, read);
//            }
//            inputStream.close();
//            fileOutputStream.close();
//            return file.getPath();
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
//        return null;
//    }

}
