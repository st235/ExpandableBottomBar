package github.com.st235.expandablebottombar;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;
import github.com.st235.lib_expandablebottombar.ExpandableBottomBar;
import github.com.st235.lib_expandablebottombar.ExpandableBottomBarMenuItem;

public class JavaActivity extends AppCompatActivity {

    private static final String TAG = JavaActivity.class.getSimpleName();

    private ExpandableBottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java);

        bottomBar = findViewById(R.id.expandable_bottom_bar);

        bottomBar.addItems(
                new ExpandableBottomBarMenuItem.Builder(this)
                        .addItem(R.id.icon_home, R.drawable.ic_home, R.string.text, Color.GRAY)
                        .addItem(R.id.icon_likes, R.drawable.ic_likes, R.string.text2, 0xffff77a9)
                        .addItem(R.id.icon_bookmarks, R.drawable.ic_bookmarks, R.string.text3, 0xff58a5f0)
                        .addItem(R.id.icon_settings, R.drawable.ic_settings, R.string.text4, 0xffbe9c91)
                        .build()
        );

        bottomBar.setOnItemSelectedListener((view, item) -> {
            Log.d(TAG, "selected: " + item.toString());
            return null;
        });

        bottomBar.setOnItemReselectedListener((view, item) -> {
            Log.d(TAG, "reselected: " + item.toString());
            return null;
        });
    }
}
