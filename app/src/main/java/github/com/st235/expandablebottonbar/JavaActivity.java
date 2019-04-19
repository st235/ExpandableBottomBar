package github.com.st235.expandablebottonbar;

import android.graphics.Color;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import github.com.st235.lib_expandablebottombar.ExpandableBottomBar;
import github.com.st235.lib_expandablebottombar.ExpandableBottomBarMenuItem;

public class JavaActivity extends AppCompatActivity {

    private ExpandableBottomBar bottomBar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_java);

        bottomBar = findViewById(R.id.expandable_bottom_bar);

        bottomBar.addItems(
                new ExpandableBottomBarMenuItem.Builder()
                        .addItem(R.id.icon_home, R.drawable.ic_home, R.string.text, Color.GRAY)
                        .addItem(R.id.icon_likes, R.drawable.ic_likes, R.string.text2, Color.parseColor("#ff77a9"))
                        .addItem(R.id.icon_bookmarks, R.drawable.ic_bookmarks, R.string.text3, Color.parseColor("#58a5f0"))
                        .addItem(R.id.icon_settings, R.drawable.ic_settings, R.string.text4, Color.parseColor("#be9c91"))
                        .build()
        );
    }
}
