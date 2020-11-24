package com.example.before;

import android.Manifest;
import android.app.Activity;
import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;
import androidx.core.os.EnvironmentCompat;

import com.example.before.rmi.PictureRmi;
import com.example.before.rmi.RmiClient;
import com.example.before.trans.FileTransTask;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import lipermi.net.Client;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

public class AcceptActivity extends Activity {

    private ImageView ivCamera;
    private ImageView ivPhoto;

    // 拍照的requestCode
    private static final int CAMERA_REQUEST_CODE = 0x00000010;
    // 申请相机权限的requestCode
    private static final int PERMISSION_CAMERA_REQUEST_CODE = 0x00000012;
    /**
     * 用于保存拍照图片的uri
     */
    private Uri mCameraUri;

    /**
     * 用于保存图片的文件路径，Android 10以下使用图片路径访问图片
     */
    private String mCameraImagePath;

    /**
     *  是否是Android 10以上手机
     */
    private boolean isAndroidQ = Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.Q;
    private File picture;
    private static long lastClickTime = 0;
    private static long curClickTime = 0;



    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("接受intent", getIntent().getDataString());
        setContentView(R.layout.photo);
        ivCamera = (ImageView) findViewById(R.id.ivCamera);
        ivPhoto = (ImageView) findViewById(R.id.ivPhoto);
        ivCamera.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                checkPermissionAndCamera();
            }
        });

        Button button = findViewById(R.id.submit);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                  curClickTime = System.currentTimeMillis();
                if (AcceptActivity.this.picture == null) {
                    Toast.makeText(AcceptActivity.this, "请先拍照",
                            Toast.LENGTH_LONG).show();
                } else if (curClickTime - lastClickTime < 5000) {
                    Toast.makeText(AcceptActivity.this, "图片已上传，请稍等,不要频繁点击",
                            Toast.LENGTH_LONG).show();
                    Log.d("连续点击提交图片", "=============");
                } else {
                    lastClickTime = curClickTime;
                    Toast.makeText(AcceptActivity.this, "图片已上传，等待跳转",
                            Toast.LENGTH_LONG).show();
                    Log.d("图片信息", picture.getName() + "  " + picture.getParent() + "   " + picture.length());
                   //rUploadFile(picture);
                    String fileName = picture.getName();
                    CommontUtils.WEB_FILE_PATH = getString(R.string.hostAndPort) + File.separator + fileName;
                    FileTransTask fileTransTask = new FileTransTask(fileName, picture, 1);
                    Future<String> future = CommontUtils.executor.submit(fileTransTask);
                    try {

                        boolean isNull = (future == null);
                        Log.d("future是否为空", "=================" + isNull);
                        String res = future.get();
                        Log.d("返回结果", " =================" + res);
                        startActivity(new Intent(AcceptActivity.this, WebViewTestActivity.class));
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }


                }
                lastClickTime = curClickTime;

            }
        });


    }

    /**
     * 检查权限并拍照。
     * 调用相机前先检查权限。
     */
    private void checkPermissionAndCamera() {
        int hasCameraPermission = ContextCompat.checkSelfPermission(getApplication(),
                Manifest.permission.CAMERA);
        if (hasCameraPermission == PackageManager.PERMISSION_GRANTED) {
            //有权限，调起相机拍照。
            openCamera();
        } else {
            //没有权限，申请权限。
            ActivityCompat.requestPermissions(this,new String[]{Manifest.permission.CAMERA},
                    PERMISSION_CAMERA_REQUEST_CODE);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                if (isAndroidQ) {
                    // Android 10 使用图片uri加载
                    ivPhoto.setImageURI(mCameraUri);
                    Log.d("安卓10拍照完毕", "============  " + mCameraUri);
                    String path = getRealFilePath(getApplicationContext(), mCameraUri);
                    Log.d("真实路径，基于安卓10 ", path);
                    picture = new File(path);
                } else {
                    Bitmap bitmap = BitmapFactory.decodeFile(mCameraImagePath);
                    // 使用图片路径加载
                    ivPhoto.setImageBitmap(bitmap);

                    Log.d("安卓8拍照完毕", " ================" + mCameraImagePath);
                   // String name = mCameraImagePath.substring(mCameraImagePath.lastIndexOf("/") + 1);
                     picture = savePNG_After(bitmap, mCameraImagePath + ".jpg");
                    Log.d("图片大小为", picture.length() + "");
                }

            } else {
                Toast.makeText(this,"取消",Toast.LENGTH_LONG).show();
            }
        }
    }
   /* public void rUploadFile(File file) {
        OkHttpClient okHttpClient = new OkHttpClient();
        Log.d("图片数据", "大小为" + file.length());
        String filePath = file.getPath();
        String fileName2 = filePath.substring(filePath.lastIndexOf("/") + 1);
        CommontUtils.WEB_FILE_PATH = getString(R.string.hostAndPort) + File.separator + fileName2;
        Log.d("上传后的图片访问路径是","===   " + CommontUtils.WEB_FILE_PATH);
        String url = getString(R.string.url);
        okHttpClient.newCall(new Request.Builder().url(url).post(new MultipartBody.Builder().setType(MultipartBody.FORM).addFormDataPart("originalData", fileName2, RequestBody.create(MediaType.parse("application/octet-stream"), file)).build()).build()).enqueue(new Callback() {
            public void onFailure(Call call, IOException e) {
            }

            public void onResponse(Call call, Response response) throws IOException {
                String string = response.body().string();
                Log.d("success........", "成功");
                AcceptActivity.this.startActivity(new Intent(AcceptActivity.this, WebViewTestActivity.class));
            }
        });
    }*/
   public void uploadFile(File file) {



       MultipartBody.Builder builder = new MultipartBody.Builder();
       //设置类型
       builder.setType(MultipartBody.FORM);
       //追加参数

       //builder.addFormDataPart(key, object.toString());
       builder.addFormDataPart("originalData", "1.jpg", RequestBody.create(null, file));
       RequestBody body = builder.build();



       final Request request = new Request.Builder().url("http://192.168.1.129:8080/file2tomcat").post(body).build();
       //单独设置参数 比如读取超时时间
       OkHttpClient okHttpClient = new OkHttpClient();
       final Call call = okHttpClient.newBuilder().writeTimeout(50, TimeUnit.SECONDS).build().newCall(request);
       call.enqueue(new Callback() {
           @Override
           public void onFailure(Call call, IOException e) {
               Log.e("双层失败", e.toString());

           }

           @Override
           public void onResponse(Call call, Response response) throws IOException {
               if (response.isSuccessful()) {
                   String string = response.body().string();
                   Log.e("成功上chuan", "response ----->" + string);

               }
           }
       });
   }



       public String getRealFilePath(Context context, Uri uri) {
        Cursor cursor;
        int index;
        if (uri == null) {
            return null;
        }
        String scheme = uri.getScheme();
        String data = null;
        if (scheme == null) {
            return uri.getPath();
        }
        if ("file".equals(scheme)) {
            return uri.getPath();
        }
        if (!"content".equals(scheme) || (cursor = context.getContentResolver().query(uri, new String[]{"_data"}, (String) null, (String[]) null, (String) null)) == null) {
            return null;
        }
        if (cursor.moveToFirst() && (index = cursor.getColumnIndex("_data")) > -1) {
            data = cursor.getString(index);
        }
        cursor.close();
        return data;
    }

    public  File savePNG_After(Bitmap bitmap, String name) {
        File file = new File(name);
        try {
            FileOutputStream out = new FileOutputStream(file);
            if (bitmap.compress(Bitmap.CompressFormat.JPEG, 100, out)) {
                out.flush();
                out.close();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return file;
    }

    /**
     * 处理权限申请的回调。
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSION_CAMERA_REQUEST_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //允许权限，有调起相机拍照。
                openCamera();
            } else {
                //拒绝权限，弹出提示框。
                Toast.makeText(this,"拍照权限被拒绝",Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * 调起相机拍照
     */
    private void openCamera() {
        Intent captureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        // 判断是否有相机
        if (captureIntent.resolveActivity(getPackageManager()) != null) {
            File photoFile = null;
            Uri photoUri = null;

            if (isAndroidQ) {
                // 适配android 10
                photoUri = createImageUri();
            } else {
                try {
                    photoFile = createImageFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }

                if (photoFile != null) {
                    mCameraImagePath = photoFile.getAbsolutePath();
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        //适配Android 7.0文件权限，通过FileProvider创建一个content类型的Uri
                        photoUri = FileProvider.getUriForFile(this, getPackageName() + ".fileprovider", photoFile);
                    } else {
                        photoUri = Uri.fromFile(photoFile);
                    }
                }
            }

            mCameraUri = photoUri;
            if (photoUri != null) {
                captureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoUri);
                captureIntent.addFlags(Intent.FLAG_GRANT_WRITE_URI_PERMISSION);
                startActivityForResult(captureIntent, CAMERA_REQUEST_CODE);
            }
        }
    }

    /**
     * 创建图片地址uri,用于保存拍照后的照片 Android 10以后使用这种方法
     *
     * @return 图片的uri
     */
    private Uri createImageUri() {
        String status = Environment.getExternalStorageState();
        // 判断是否有SD卡,优先使用SD卡存储,当没有SD卡时使用手机存储
        if (status.equals(Environment.MEDIA_MOUNTED)) {
            return getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, new ContentValues());
        } else {
            return getContentResolver().insert(MediaStore.Images.Media.INTERNAL_CONTENT_URI, new ContentValues());
        }
    }

    /**
     * 创建保存图片的文件
     * @return
     * @throws IOException
     */
    private File createImageFile() throws IOException {
        String imageName = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
        if (!storageDir.exists()) {
            storageDir.mkdir();
        }
        File tempFile = new File(storageDir, imageName);
        if (!Environment.MEDIA_MOUNTED.equals(EnvironmentCompat.getStorageState(tempFile))) {
            return null;
        }
        return tempFile;
    }

}
