package com.example.app_ban_hang;

import android.net.Uri;
import android.os.Bundle;
import android.widget.VideoView;

import androidx.annotation.Nullable;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends BaseActivity {

    private VideoView videoIntro;
    private RecyclerView rvNews;
    private NewsAdapter newsAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // Toolbar
        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        if (getSupportActionBar() != null) getSupportActionBar().setTitle("Sneaker Hub");

        videoIntro = findViewById(R.id.videoIntro);
        rvNews = findViewById(R.id.rvNews);

        setupVideoIntro();
        setupNews();

        // Bottom nav
        setupBottomNavigation(R.id.nav_home);
    }

    private void setupVideoIntro() {
        try {
            String videoPath = "file:///android_asset/sneaker_intro.mp4";
            Uri uri = Uri.parse(videoPath);
            videoIntro.setVideoURI(uri);

            // Có MediaController (nếu bạn muốn tua/pause)
            android.widget.MediaController controller = new android.widget.MediaController(this);
            controller.setAnchorView(videoIntro);
            videoIntro.setMediaController(controller);

            videoIntro.setOnPreparedListener(mp -> {
                mp.setLooping(true);
                videoIntro.start();
            });

            videoIntro.setOnErrorListener((mp, what, extra) -> {
                android.widget.Toast.makeText(this, "Không phát được video", android.widget.Toast.LENGTH_SHORT).show();
                return true;
            });

        } catch (Exception e) {
            e.printStackTrace();
            android.widget.Toast.makeText(this, "Lỗi video: " + e.getMessage(), android.widget.Toast.LENGTH_SHORT).show();
        }
    }

    private void setupNews() {
        List<NewsItem> newsList = new ArrayList<>();
        newsList.add(new NewsItem(
                "Nike Air Max Day 2024",
                "Các mẫu Air Max mới nhất sẽ ra mắt vào tháng tới",
                "https://example.com/news1.jpg"
        ));
        newsList.add(new NewsItem(
                "Adidas Samba trở lại",
                "Mẫu giày cổ điển đang tạo cơn sốt trong giới trẻ",
                "https://example.com/news2.jpg"
        ));
        newsList.add(new NewsItem(
                "Limited Edition Jordan",
                "Jordan 1 Retro High OG 'Lost & Found' phiên bản giới hạn",
                "https://example.com/news3.jpg"
        ));

        newsAdapter = new NewsAdapter(newsList);
        rvNews.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false));
        rvNews.setAdapter(newsAdapter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (videoIntro != null && !videoIntro.isPlaying()) videoIntro.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        if (videoIntro != null && videoIntro.isPlaying()) videoIntro.pause();
    }

    @Override
    protected void onBottomTabReselected(int itemId) { /* no-op */ }
}
