<img src="https://raw.githubusercontent.com/st235/ExpandableBottomBar/master/images/video.gif" width="600" height="205">

# ExpandableBottomBar

A new way to improve navigation in your app

Its really easy integrate to your project

## take it, faster, faster

- Maven

```text
<dependency>
  <groupId>com.github.st235</groupId>
  <artifactId>expandablebottombar</artifactId>
  <version>0.4</version>
  <type>pom</type>
</dependency>
```

- Gradle

```text
implementation 'com.github.st235:expandablebottombar:0.4'
```

- Ivy

```text
<dependency org='com.github.st235' name='expandablebottombar' rev='0.4'>
  <artifact name='expandablebottombar' ext='pom' ></artifact>
</dependency>
```

## Usage

Really simple as I wrote earlier

Firstly, you should declare your view in xml file

```xml
    <github.com.st235.lib_expandablebottombar.ExpandableBottomBar
        android:id="@+id/expandable_bottom_bar"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_margin="20dp"
        app:backgroundCornerRadius="25dp"
        app:backgroundColor="#2e2e2e"
        app:itemInactiveBackgroundColor="#fff"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />
```

Then you should add menu items to your navigation component

```kotlin
        val bottomBar: ExpandableBottomBar = findViewById(R.id.expandable_bottom_bar)

        bottomBar.addItems(
                ExpandableBottomBarMenuItem.Builder()
                        .addItem(R.id.icon_home, R.drawable.ic_bug, R.string.text, Color.GRAY)
                        .addItem(R.id.icon_go, R.drawable.ic_gift, R.string.text2, 0xFFFF77A9)
                        .addItem(R.id.icon_left, R.drawable.ic_one, R.string.text3, 0xFF58A5F0)
                        .addItem(R.id.icon_right, R.drawable.ic_two, R.string.text4, 0xFFBE9C91)
                        .build()
        )

        bottomBar.onItemClickListener = { view, menuItem ->
            // handle menu item clicks here
        }
```

## Xml supported properties

- **elevation** component elevation (important: api 21+)
- **backgroundColor** bottom bar background color
- **transitionDuration** time between one item collapsed and another item expanded
- **backgroundCornerRadius** bottom bar background corners radius
- **itemInactiveColor** item menu color, when its inactive
- **itemBackgroundCornerRadius** item background corner radius
- **itemBackgroundOpacity** item background opacity (important: final color alpha calculates by next formulae __alpha = opacity * 255__)
- **item_vertical_margin** top & bottom item margins
- **item_horizontal_margin** left & right item margins
- **item_vertical_padding** top & bottom item padding
- **item_horizontal_padding** left & right item padding

## Screens

<img src="https://raw.githubusercontent.com/st235/ExpandableBottomBar/master/images/ordinary.png" width="270" height="480"> <img src="https://raw.githubusercontent.com/st235/ExpandableBottomBar/master/images/small_rounded.png" width="270" height="480"> <img src="https://raw.githubusercontent.com/st235/ExpandableBottomBar/master/images/hard_rounded.png" width="270" height="480"> <img src="https://raw.githubusercontent.com/st235/ExpandableBottomBar/master/images/night_like.png" width="270" height="480">

### License

```text
MIT License

Copyright (c) 2019 Alexander Dadukin

Permission is hereby granted, free of charge, to any person obtaining a copy
of this software and associated documentation files (the "Software"), to deal
in the Software without restriction, including without limitation the rights
to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
copies of the Software, and to permit persons to whom the Software is
furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all
copies or substantial portions of the Software.

THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
SOFTWARE.
```
