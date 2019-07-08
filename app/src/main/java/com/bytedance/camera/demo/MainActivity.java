package com.bytedance.camera.demo;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.bumptech.glide.Glide;
import com.bytedance.camera.demo.bean.Feed;
import com.bytedance.camera.demo.bean.FeedResponse;
import com.bytedance.camera.demo.network.VideoService;

import java.io.IOException;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private RecyclerView mRv;
    private List<Feed> mFeeds = new ArrayList();
    private Button addBtn;
    private Button refreshBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        addBtn = findViewById(R.id.add);
        refreshBtn = findViewById(R.id.refresh);
        addBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(MainActivity.this,PostVideo.class));
            }
        });


        initRecycler();
    }

    private void initRecycler() {
        mRv = findViewById(R.id.rv);
        mRv.setLayoutManager(new LinearLayoutManager(this));
        mRv.setAdapter(new RecyclerView.Adapter() {
            @NonNull
            @Override
            public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
                ImageView imageView = new ImageView(parent.getContext());
                imageView.setLayoutParams(new ViewGroup.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
                imageView.setAdjustViewBounds(true);
                return new MyViewHolder(imageView);
            }

            @Override
            public void onBindViewHolder(@NonNull RecyclerView.ViewHolder holder, int position) {
                ImageView iv = (ImageView) holder.itemView;
                iv.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        /*跳转播放*/
                    }
                });
                String url = mFeeds.get(position).getUrl();

                Glide.with((iv.getContext())).load(url).into(iv);
            }

            @Override
            public int getItemCount() {
                return mFeeds.size();
            }
        });
    }

    public static class MyViewHolder extends RecyclerView.ViewHolder {
        public MyViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }

    public void fetchFeed(View view) throws ExecutionException, InterruptedException {

        FetchFeedTask fetchFeedTask = new FetchFeedTask();
        fetchFeedTask.execute();
        if (fetchFeedTask.get() != null) {
            mFeeds = fetchFeedTask.get().getFeeds();
            mRv.getAdapter().notifyDataSetChanged();
        }
    }

    class FetchFeedTask extends AsyncTask<String,Integer, FeedResponse> {

        @Override
        protected FeedResponse doInBackground(String... strings) {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl("http://test.androidcamp.bytedance.com/")
                    .addConverterFactory(GsonConverterFactory.create())
                    .build();

            Response<FeedResponse> response = null;
            try {
                response = retrofit.create(VideoService.class)
                        .getFeed().execute();
            } catch (IOException e) {
                e.printStackTrace();
            }
            //System.out.println(response);
            return response == null?null:response.body();
        }
    }
}
