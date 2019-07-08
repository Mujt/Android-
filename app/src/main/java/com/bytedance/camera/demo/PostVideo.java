package com.bytedance.camera.demo;

import android.Manifest;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.media.MediaMetadataRetriever;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.service.autofill.CustomDescription;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;
import android.widget.VideoView;

import com.bytedance.camera.demo.bean.PostVideoResponse;
import com.bytedance.camera.demo.network.VideoService;
import com.bytedance.camera.demo.utils.ResourceUtils;
import com.bytedance.camera.demo.utils.Utils;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.ExecutionException;

import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class PostVideo extends AppCompatActivity {

    private static final int PICK_IMAGE = 1;
    private static final int PICK_VIDEO = 2;
    private static final int RECORD = 3;
    private static final String TAG = "PostVideoActivity";
    private Button mBtn;
    private ImageView img;
    private VideoView video;

    public Uri mSelectedImage;
    private Uri mSelectedVideo;
    private Uri recordVideo;

    private Button record;

    private File imgFile;
    private File videoFile;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.postvideo);
        img = findViewById(R.id.choose_img);
        video = findViewById(R.id.choose_video);
        mBtn = findViewById(R.id.choose);
        record = findViewById(R.id.record);
        img.setVisibility(View.INVISIBLE);
        video.setVisibility(View.INVISIBLE);

        record.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (getString(R.string.record).equals(record.getText())) {
                    Intent intent = new Intent(PostVideo.this, CustomCameraActivity.class);
                    startActivityForResult(intent, RECORD);
                    record.setText("POST IT");
                } else {
                    //record.setEnabled(true);
                    try {
                        postVideo(2);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    //record.setText(getString(R.string.record));
                    //record.setEnabled(false);
                }
            }
        });



        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String s = mBtn.getText().toString();
                if (getString(R.string.select_an_image).equals(s)) {
                    chooseImage();
                    /*Todo:设置img*/

                } else if (getString(R.string.select_a_video).equals(s)) {
                    chooseVideo();
                    /*Todo:设置video*/

                } else if (getString(R.string.post_it).equals(s)) {
                    try {
                        postVideo(1);
                    } catch (ExecutionException e) {
                        e.printStackTrace();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    private void setPic() {
        //todo 根据imageView裁剪
        //todo 根据缩放比例读取文件，生成Bitmap

        //todo 如果存在预览方向改变，进行图片旋转

        //todo 如果存在预览方向改变，进行图片旋转

        int targetW = img.getWidth();
        int targetH = img.getHeight();

        BitmapFactory.Options bmOptions = new BitmapFactory.Options();
        bmOptions.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imgFile.getAbsolutePath(),bmOptions);
        int photoW = bmOptions.outWidth;
        int photoH = bmOptions.outHeight;
        int scaleFactor = Math.min(photoW/targetW,photoH/targetH);
        bmOptions.inJustDecodeBounds = false;
        bmOptions.inSampleSize = scaleFactor;
        bmOptions.inPurgeable = true;
        Bitmap bmp = BitmapFactory.decodeFile(imgFile.getAbsolutePath(),bmOptions);
        img.setImageBitmap(bmp);
    }

    public void chooseImage() {
        // TODO-C2 (4) Start Activity to select an image
        Intent intent = new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent,"Select Picture"),PICK_IMAGE);
    }


    public void chooseVideo() {
        Intent intent = new Intent();
        intent.setType("video/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent.createChooser(intent,"Select Video"),PICK_VIDEO);
        // TODO-C2 (5) Start Activity to select a video
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.d(TAG, "onActivityResult() called with: requestCode = [" + requestCode + "], resultCode = [" + resultCode + "], data = [" + data + "]");

        if (resultCode == RESULT_OK && null != data) {

            if (requestCode == PICK_IMAGE) {
                mSelectedImage = data.getData();
                Log.d(TAG, "selectedImage = " + mSelectedImage);
                mBtn.setText(R.string.select_a_video);
                img.setVisibility(View.VISIBLE);
                //video.setVisibility(View.INVISIBLE);
                imgFile = new File(ResourceUtils.getRealPath(PostVideo.this, mSelectedImage));
                setPic();
            } else if (requestCode == PICK_VIDEO) {
                mSelectedVideo = data.getData();
                Log.d(TAG, "mSelectedVideo = " + mSelectedVideo);
                mBtn.setText(R.string.post_it);
                //img.setVisibility(View.INVISIBLE);
                video.setVisibility(View.VISIBLE);
                video.setVideoURI(mSelectedVideo);
                video.start();
            } else if (requestCode == RECORD) {
                recordVideo = Uri.parse(data.getStringExtra("data"));
                video.setVisibility(View.VISIBLE);
                video.setVideoURI(recordVideo);
                video.start();
                MediaMetadataRetriever rev = new MediaMetadataRetriever();
                rev.setDataSource(this,recordVideo);
                Bitmap bitmap = rev.getFrameAtTime(0);
                img.setVisibility(View.VISIBLE);
                img.setImageBitmap(bitmap);
                File file = Utils.getOutputMediaFile(Utils.MEDIA_TYPE_IMAGE);
                mSelectedVideo = recordVideo;
                //mSelectedImage =
            }
        }
    }

    private MultipartBody.Part getMultipartFromUri(String name, Uri uri) {
        // if NullPointerException thrown, try to allow storage permission in system settings
        File f = new File(ResourceUtils.getRealPath(PostVideo.this, uri));
        RequestBody requestFile = RequestBody.create(MediaType.parse("multipart/form-data"), f);
        return MultipartBody.Part.createFormData(name, f.getName(), requestFile);
    }

    private void postVideo(int i) throws ExecutionException, InterruptedException {
        if (i == 1) {
            mBtn.setText("POSTING...");
            mBtn.setEnabled(false);
        } else {
            record.setText("POSTING...");
            record.setEnabled(false);
        }

        ActivityCompat.requestPermissions(PostVideo.this,
                new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                        Manifest.permission.READ_EXTERNAL_STORAGE},
                1);

        PostTask post = new PostTask();
        post.execute();
        if (post.get().isSuccess()) {
            Toast.makeText(this,"Post succeed!",Toast.LENGTH_LONG).show();
        } else {
            Toast.makeText(this,"Post failedï¼",Toast.LENGTH_LONG).show();
        }
        if (i == 1) {
            mBtn.setText(R.string.select_an_image);
            mBtn.setEnabled(true);
        } else {
            record.setText(getString(R.string.record));
            record.setEnabled(true);
        }
        img.setVisibility(View.INVISIBLE);
        video.setVisibility(View.INVISIBLE);
        // TODO-C2 (6) Send Request to post a video with its cover image
        // if success, make a text Toast and show
    }

    class PostTask extends AsyncTask<String,Integer, PostVideoResponse> {

        @Override
        protected PostVideoResponse doInBackground(String... strings) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://test.androidcamp.bytedance.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Response<PostVideoResponse> response = null;
            try {
                response = retrofit.create(VideoService.class)
                        .createVideo("16231173","Mujt",getMultipartFromUri("cover_image",mSelectedImage),getMultipartFromUri("video",mSelectedVideo)).execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println(response);
            return response == null?null:response.body();
        }
    }
}
