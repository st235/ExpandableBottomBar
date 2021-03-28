<img src="/images/video.gif" width="600" height="205">

# ExpandableBottomBar

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/com.github.st235/expandablebottombar/badge.svg)](https://maven-badges.herokuapp.com/maven-central/com.github.st235/expandablebottombar)
![GitHub code size in bytes](https://img.shields.io/github/languages/code-size/st235/ExpandableBottomBar.svg)
[![CircleCI](https://circleci.com/gh/st235/ExpandableBottomBar.svg?style=svg)](https://circleci.com/gh/st235/ExpandableBottomBar)

A new way to improve navigation in your app

Its really easy integrate to your project

## take it, faster, faster

__Important: library was migrated from JCenter to MavenCentral__ 

It means that it may be necessary to add __mavenCentral__ repository to your repositories list

```groovy
allprojects {
    repositories {
        // your repositories

        mavenCentral()
    }
}
```

- Maven

```text
<dependency>
  <groupId>com.github.st235</groupId>
  <artifactId>expandablebottombar</artifactId>
  <version>X.X</version>
  <type>pom</type>
</dependency>
```

- Gradle

```text
implementation 'com.github.st235:expandablebottombar:X.X'
```

- Ivy

```text
<dependency org='com.github.st235' name='expandablebottombar' rev='X.X'>
  <artifact name='expandablebottombar' ext='pom' ></artifact>
</dependency>
```

P.S.: Check out latest version code in badge at the top of this page.

## Usage

Really simple as I wrote earlier

Firstly, you should declare your view in xml file

```xml
    <github.com.st235.lib_expandablebottombar.ExpandableBottomBar
        android:id="@+id/expandable_bottom_bar"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_margin="20dp"
        app:exb_backgroundCornerRadius="25dp"
        app:exb_backgroundColor="#2e2e2e"
        app:exb_itemInactiveColor="#fff"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />
```

Then you should add menu items to your navigation component

```kotlin
        val bottomBar: ExpandableBottomBar = findViewById(R.id.expandable_bottom_bar)

        bottomBar.addItems(
                MenuItemDescriptor.Builder(context = this)
                        .addItem(R.id.icon_home, R.drawable.ic_bug, R.string.text, Color.GRAY)
                        .addItem(R.id.icon_go, R.drawable.ic_gift, R.string.text2, 0xFFFF77A9)
                        .addItem(R.id.icon_left, R.drawable.ic_one, R.string.text3, 0xFF58A5F0)
                        .addItem(R.id.icon_right, R.drawable.ic_two, R.string.text4, 0xFFBE9C91)
                        .build()
        )

        bottomBar.onItemSelectedListener = { view, menuItem ->
            /**
             * handle menu item clicks here,
             * but clicks on already selected item will not affect this callback
             */
        }
        
        bottomBar.onItemReselectedListener = { view, menuItem ->
            /**
             * handle here all the click in already selected items
             */
        }
```

## Xml menu declaration

If your menu is constantly, you may specify it from xml

Firstly, you should declare menu items in xml

```xml
<?xml version="1.0" encoding="utf-8"?>
<menu xmlns:app="http://schemas.android.com/apk/res-auto" xmlns:android="http://schemas.android.com/apk/res/android">
    <item
        android:id="@+id/home"
        android:title="@string/text"
        android:icon="@drawable/ic_home"
        app:exb_color="#FF8888" />

    <item
        android:id="@+id/settings"
        android:title="@string/text4"
        android:icon="@drawable/ic_settings"
        app:exb_color="@color/colorSettings" />

    <item
        android:id="@+id/bookmarks"
        android:title="@string/text3"
        android:icon="@drawable/ic_bookmarks"
        app:exb_color="#fa2" />
</menu>
```

each item tag has following attributes:

| property | type | description |
| ----- | ----- | ----- |
| **id** | reference | an id of menu item |
| **exb_color** | reference/color | color of element, it may be color reference or color |
| **icon** | reference | icon reference (vector drawables supported) |
| **title** | reference/text | item title |
| **exb_badgeColor** | color | notification badge background color. **It will override the color from layout attribute** |
| **exb_badgeTextColor** | color | notification badge text color. **It will override the color from layout attribute** |

Just like any Android menu 😉

Then you should reference this xml file at the view attributes

```xml
    <github.com.st235.lib_expandablebottombar.ExpandableBottomBar
        android:id="@+id/expandable_bottom_bar"
        android:layout_width="0dp"
        android:layout_height="wrap_content"
        android:layout_margin="20dp"
        app:exb_backgroundCornerRadius="25dp"
        app:exb_itemInactiveColor="#fff"
        app:exb_items="@menu/bottom_bar"
        app:layout_constraintBottom_toBottomOf="parent"
        app:layout_constraintEnd_toEndOf="parent"
        app:layout_constraintStart_toStartOf="parent" />
```

## Xml attributes

| property | type | description |
| ----- | ----- | ----- |
| **exb_elevation** | dimen | component elevation (important: api 21+) |
| **exb_backgroundColor** | color | bottom bar background color |
| **exb_transitionDuration** | integer | time between one item collapsed and another item expanded |
| **exb_backgroundCornerRadius** | dimen | bottom bar background corners radius |
| **exb_itemInactiveColor** | color | item menu color, when its inactive |
| **exb_itemBackgroundCornerRadius** | dimen | item background corner radius |
| **exb_itemStyle** | enum: normal, outline, stroke | controls the style of items. **normal** = items are filled with solid color; **outline** = no fill, only border; **stroke** = fill + border |
| **exb_itemBackgroundOpacity** | float | item background opacity (important: final color alpha calculates by next formulae alpha = opacity * 255) |
| **exb_item_vertical_margin** | dimen | top & bottom item margins |
| **exb_item_horizontal_margin** | dimen | left & right item margins |
| **exb_item_vertical_padding** | dimen | top & bottom item padding |
| **exb_item_horizontal_padding** | dimen | left & right item padding |
| **exb_items** | reference | xml supported menu format |
| **exb_notificationBadgeBackgroundColor** | color | notification badge background color. **Will be applied to all menu items** |
| **exb_notificationBadgeTextColor** | color | notification badge text color. **Will be applied to all menu items** |

## Notification badges

```kotlin
    /**
     * Returns notification object
     */
    val notification = bottomBar.getMenuItemFor(i.itemId).notification() // itemId is R.id.action_id

   notification.show() // shows simple dot-notification
   notification.show("string literal") // shows notification with counter. Counter could not exceed the 4 symbols length

  notification.clear() // removes notification badge from menu item
```

## Navigation Components support

Usually Drawer or BottomNavigationView attached to navigation controller with `NavigationUI.setupWithNavController(view, navController)`, but this system is not applicable
to customview components. That's why ExpandableBottomBar offers it's own `ExpandableBottomBarNavigationUI`

To attach ExpandableBottomBar to your navigation components you should use the same approach

```kotlin
    ExpandableBottomBarNavigationUI.setupWithNavController(bottomNavigation, navigationController)
```

## Coordinator Layout support

Do you waiting for Coordinator Layout support - and it is already here! Fabs and Snackbars aligned by bottom bar! Hooray 🎉

Available without registration and SMS, starting from **0.8** version. Seriously, everything is already working out of the box - nothing needs to be done.

But... if you need to support hiding the menu by list/grid scroll - then you are really lucky!

This functionality is very simple to implement. You need to redeclare custom `Coordinator Layout Behavoir` to `ExpandableBottomBarScrollableBehavior`.

```xml
    <github.com.st235.lib_expandablebottombar.ExpandableBottomBar
            android:id="@+id/expandable_bottom_bar"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_gravity="bottom"
            app:layout_behavior="github.com.st235.lib_expandablebottombar.behavior.ExpandableBottomBarScrollableBehavior"
            app:items="@menu/bottom_bar" />
```

Really easy ;D 

After integration this behavior should looks like:

<img src="/images/coordinator_layout.gif" width="270" height="480">

## Migration guide

You may found all necessary info about migration from old versions [here](https://github.com/st235/ExpandableBottomBar/wiki/Migration)

## Screens

<img src="/images/ordinary.png" width="270" height="480"> <img src="/images/small_rounded.png" width="270" height="480"> <img src="/images/hard_rounded.png" width="270" height="480"> <img src="/images/night_like.png" width="270" height="480"> <img src="/images/styles.png" width="270" height="480">


### License

```text
MIT License

Copyright (c) 2019 - present, Alexander Dadukin

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
