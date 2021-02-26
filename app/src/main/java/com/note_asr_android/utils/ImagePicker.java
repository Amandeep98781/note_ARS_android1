package com.note_asr_android.utils;


import android.Manifest;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.os.Build;
import android.os.Environment;
import android.provider.DocumentsContract;
import android.provider.MediaStore;

import android.util.DisplayMetrics;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.note_asr_android.R;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public abstract class ImagePicker extends AppCompatActivity {
    private static final int REQUEST_CODE = 100;
    private static final int GALLERY_REQUEST_CODE = 101;
    private static final int CAMERA_REQUEST_CODE = 102;
    private static final String IMAGE = "image";
    private static final String VIDEO = "video";
    private static final String OTHERS = "others";
    private String camera = "Camera", gallery = "Gallery",picture = "Picture",video = "Video",gif = "Gif", optionLabel = "Choose an option...";
    private String alert = "Alert!!!", cancel = "Cancel", openSettings = "Open Settings", ok = "Ok";
    private String permissionRequired = "Required camera and storage permission to access this functionality.";
    String[] item0 = {camera, gallery};
    Bitmap bmpPic;
    String imageAbsolutePath = " ", uid;
    private Activity mActivity;
    String destPath;

    public ImagePicker() {
    }

    public void camera(final Activity activity) {
        mActivity = activity;
        if (!cameraPermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    checkPermissionDenied(new String[]{Manifest.permission.CAMERA});
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    checkPermissionDenied(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
                } else {
                    requestPermission();
                }
            }

        } else {
            CaptureImage();
        }
    }

    public void gallery(final Activity activity) {
        mActivity = activity;
        if (!cameraPermission(new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE})) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.CAMERA)) {
                    checkPermissionDenied(new String[]{Manifest.permission.CAMERA});
                } else if (shouldShowRequestPermissionRationale(Manifest.permission.WRITE_EXTERNAL_STORAGE)) {
                    checkPermissionDenied(new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE});
                } else {
                    requestPermission();
                }
            }

        } else {

            final CharSequence[] items = { "Take Photo", "Choose from Library","Cancel" };
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Choose Your Option");
        builder.setItems(items, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int item) {
              //  boolean result= Utility.checkPermission(ShareDeal.this);

                if (items[item].equals("Take Photo")) {

                    CaptureImage();
                } else if (items[item].equals("Choose from Library")) {
                    OpenGallery();
                } else if(items[item].equals("Cancel")){
                    dialog.dismiss();
                }
            }
        });
        builder.show();
//            AlertDialog.Builder mBuilder = new AlertDialog.Builder(activity);
//            mBuilder.setTitle(optionLabel).setItems(item0, new DialogInterface.OnClickListener() {
//                @Override
//                public void onClick(DialogInterface dialog, int which) {
//                    if (item0[which].equals(camera)) {
//                        CaptureImage();
//                    }
//                    if (item0[which].equals(gallery)) {
//                        OpenGallery();
//                    }
//
//                }
//            }).create().show();
//            OpenGallery();
        }
    }

    private boolean cameraPermission(String[] permissions) {
        return ContextCompat.checkSelfPermission(mActivity, permissions[0]) == PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(mActivity, permissions[1]) == PackageManager.PERMISSION_GRANTED;
    }

    void requestPermission() {
        ActivityCompat.requestPermissions(mActivity, new String[]{Manifest.permission.CAMERA, Manifest.permission.WRITE_EXTERNAL_STORAGE}, REQUEST_CODE);
    }


    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            try {
                DisplayMetrics displayMetrics = getResources().getDisplayMetrics();
                int display_Width = displayMetrics.widthPixels;
//                Log.e("displayWIdth", "" + display_Width);

                bmpPic = decodeSampledBitmapFromFile(imageAbsolutePath,
                        display_Width / 2, display_Width / 2);
                String path = saveImageToExternalStorage(bmpPic);
                selectedImage(path,"","");
            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (requestCode == GALLERY_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null) {
                Uri selectedImage = data.getData();
                if (isImage(selectedImage).equalsIgnoreCase("image"))
                {
                    imageAbsolutePath = getImagePath(selectedImage);
                    selectedImage(imageAbsolutePath,isImage(selectedImage),"");
                }

            } else {
                Toast.makeText(this, "error", Toast.LENGTH_SHORT).show();
            }
        }
    }

    public String getImagePath(Uri uri) {
        String[] projection = {MediaStore.Images.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Images.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }


    public String getVideoPath(Uri uri) {
        String[] projection = {MediaStore.Video.Media.DATA};
        Cursor cursor = getContentResolver().query(uri, projection, null, null, null);
        if (cursor != null) {
            // HERE YOU WILL GET A NULLPOINTER IF CURSOR IS NULL
            // THIS CAN BE, IF YOU USED OI FILE MANAGER FOR PICKING THE MEDIA
            int column_index = cursor
                    .getColumnIndexOrThrow(MediaStore.Video.Media.DATA);
            cursor.moveToFirst();
            return cursor.getString(column_index);
        } else
            return null;
    }


    private String isImage(Uri uri){
        ContentResolver cr = getContentResolver();
        String mime = cr.getType(uri);
        Log.i("in gallery", String.valueOf(uri));

        if (mime.toLowerCase().contains("image")) {

            return IMAGE;

        } else if (mime.toLowerCase().contains("video")){
//                    selectedVideo(CommonMethods.saveImageToExternalStorage(bitmap2), image_absolute_path);
//                    Log.i("TAG", "ImagePicker:: This is video");

            return VIDEO;

        }
        else {

            return OTHERS;

        }
    }

    private String getThumbnail(String path){
        Bitmap bitmap2 = ThumbnailUtils.createVideoThumbnail(path, MediaStore.Video.Thumbnails.MICRO_KIND);
//                    new CompreesVideo(mActivity,this,image_absolute_path,"",CommonMethods.saveImageToExternalStorage(bitmap2)).execute();\
        return saveBitmapToExternalStorage(bitmap2);

    }


    private Bitmap decodeSampledBitmapFromFile(String pathName, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;

        BitmapFactory.decodeFile(pathName, options);// decodeResource(res,
        // resId, options);
        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth,
                reqHeight);
        Log.i("Sample_Size", "" + options.inSampleSize);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return getbitmap(pathName, options);
    }


    public static Bitmap getbitmap(String image_Path, BitmapFactory.Options options) {
        Bitmap bm;
        Bitmap bitmap = null;
        try {
            bm = BitmapFactory.decodeStream(new FileInputStream(new File(
                    image_Path)), null, options);
            bitmap = bm;

            ExifInterface exif = new ExifInterface(image_Path);

            int orientation = exif.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 1);

            Matrix m = new Matrix();

            if ((orientation == ExifInterface.ORIENTATION_ROTATE_180)) {
                exif.setAttribute(ExifInterface.TAG_ORIENTATION, "3");
                exif.saveAttributes();

                m.postRotate(180);

                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);

                return bitmap;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_90) {
                exif.setAttribute(ExifInterface.TAG_ORIENTATION, "6");
                exif.saveAttributes();

                m.postRotate(90);

                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);

                return bitmap;
            } else if (orientation == ExifInterface.ORIENTATION_ROTATE_270) {
                exif.setAttribute(ExifInterface.TAG_ORIENTATION, "8");
                exif.saveAttributes();
                m.postRotate(270);

                bitmap = Bitmap.createBitmap(bm, 0, 0, bm.getWidth(),
                        bm.getHeight(), m, true);
                return bitmap;
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();

        } catch (IOException e) {
            Log.i("IOException:", e.toString());
            e.printStackTrace();
        }
        return bitmap;
    }


    private int calculateInSampleSize(BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.i("height", "" + height);
        Log.i("width", "" + width);
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and
            // keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }
        return inSampleSize;
    }

    public String saveImageToExternalStorage(Bitmap bitmap) {
        String ImageURi = null;
        try {
            File imageFile = new File(imageAbsolutePath);
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 60;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            ImageURi = imageFile.getAbsolutePath();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ImageURi;
    }

    public String saveBitmapToExternalStorage(Bitmap bitmap) {
        String ImageURi = null;
        try {
            File imageFile = createFileForImage();
            FileOutputStream outputStream = new FileOutputStream(imageFile);
            int quality = 100;
            bitmap.compress(Bitmap.CompressFormat.JPEG, quality, outputStream);
            outputStream.flush();
            outputStream.close();
            ImageURi = imageFile.getAbsolutePath();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        return ImageURi;
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                camera(mActivity);
            }
            else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                checkPermissionDenied(permissions);
            }
        }
        else if (requestCode == GALLERY_REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                camera(mActivity);
            }
            else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                checkPermissionDenied(permissions);
            }
        }
        else if (requestCode == REQUEST_CODE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                gallery(mActivity);
            }
            else if (grantResults[0] == PackageManager.PERMISSION_DENIED) {
                checkPermissionDenied(permissions);
            }
        }
    }

    private void checkPermissionDenied(String[] permissions) {
        boolean neverAskedChecked = false;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            for (String permission : permissions) {
                if (!shouldShowRequestPermissionRationale(permission)) {
                    neverAskedChecked = true;
                }
            }
            if (!neverAskedChecked) {
                android.app.AlertDialog.Builder mBuilder = new android.app.AlertDialog.Builder(mActivity);
                mBuilder.setTitle(alert).setMessage(permissionRequired).setPositiveButton(ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        requestPermission();
                    }
                }).setNegativeButton(cancel, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                }).create().show();
            } else {
                AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
                builder.setTitle(alert).setMessage(permissionRequired).setCancelable(false);
                builder.setPositiveButton(openSettings, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        finish();
                        Intent intent = new Intent(android.provider.Settings.ACTION_APPLICATION_DETAILS_SETTINGS,
                                Uri.fromParts("package", getPackageName(), null));
                        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }).create().show();
            }
        }
    }

    public void OpenGallery() {
        Intent pictureActionIntent;
        pictureActionIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pictureActionIntent.setType("image/* video/*");
        startActivityForResult(pictureActionIntent, GALLERY_REQUEST_CODE);
    }

    public void OpenImage() {
        Intent pictureActionIntent;
        pictureActionIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pictureActionIntent.setType("image/*");
        startActivityForResult(pictureActionIntent, GALLERY_REQUEST_CODE);
    }

    public void OpenVideo() {
        Intent pictureActionIntent;
        pictureActionIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pictureActionIntent.setType("video/*");
        startActivityForResult(pictureActionIntent, GALLERY_REQUEST_CODE);
    }

    public void OpenGif() {
        Intent pictureActionIntent;
        pictureActionIntent = new Intent(Intent.ACTION_PICK,
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        pictureActionIntent.setType("image/gif");
        startActivityForResult(pictureActionIntent, GALLERY_REQUEST_CODE);
    }

    public void CaptureImage() {
        try {
            Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            File photoFile = createFileForImage();
            imageAbsolutePath = photoFile.getAbsolutePath();
            intent.putExtra(MediaStore.EXTRA_OUTPUT, FileProvider.getUriForFile(getApplicationContext(), getApplicationContext().getPackageName() + ".provider", photoFile));
            startActivityForResult(intent, CAMERA_REQUEST_CODE);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public File createFileForImage() throws IOException {
        String imageFileName = "image" + System.currentTimeMillis() + ".jpg";
        File der = new File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES), mActivity.getString(R.string.app_name));
        File image = null;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            if (!der.exists()) {
                der.mkdirs();
            }
            image = new File(der, imageFileName);
            imageAbsolutePath = image.getAbsolutePath();
        }
        return image;
    }






    @RequiresApi(api = Build.VERSION_CODES.KITKAT)
    public String getPathFromUri(final Context context, final Uri uri) {

        final boolean isKitKat = Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT;

        // DocumentProvider
        if (isKitKat && DocumentsContract.isDocumentUri(context, uri)) {
            // ExternalStorageProvider
            if (isExternalStorageDocument(uri)) {
                final String docId = DocumentsContract.getDocumentId(uri);
                final String[] split = docId.split(":");
                final String type = split[0];

                if ("primary".equalsIgnoreCase(type)) {
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
                final String[] selectionArgs = new String[] {
                        split[1]
                };

                return getDataColumn(context, contentUri, selection, selectionArgs);
            }
        }
        // MediaStore (and general)
        else if ("content".equalsIgnoreCase(uri.getScheme())) {

            // Return the remote address
            if (isGooglePhotosUri(uri))
                return uri.getLastPathSegment();

            return getDataColumn(context, uri, null, null);
        }
        // File
        else if ("file".equalsIgnoreCase(uri.getScheme())) {
            return uri.getPath();
        }

        return null;
    }

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
                final int index = cursor.getColumnIndexOrThrow(column);
                return cursor.getString(index);
            }
        } finally {
            if (cursor != null)
                cursor.close();
        }
        return null;
    }


    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is ExternalStorageProvider.
     */
    public static boolean isExternalStorageDocument(Uri uri) {
        return "com.android.externalstorage.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is DownloadsProvider.
     */
    public static boolean isDownloadsDocument(Uri uri) {
        return "com.android.providers.downloads.documents".equals(uri.getAuthority());
    }

    /**
     * @param uri The Uri to check.
     * @return Whether the Uri authority is MediaProvider.
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


    public abstract void selectedImage(String imagePath, String type, String thumbnailPath);

}