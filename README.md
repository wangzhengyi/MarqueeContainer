# MarqueeContainer

借助ViewPager实现的无限循环展示Image的自定义控件.

## Features

- 支持本地图片的无限轮播和网络图片的无限轮播(集成了Volley)
- 支持继承,可以支持PhotoView等其他展示组件

## Example

![MarqueeContainer]()

# Gradle Dependency

目前没有上传控件到jcenter上，因此Gradle 依赖还是项目依赖的形式:

```gradle
dependencies {
    compile project(':marquee')
}
```

# Usage

## Custom Attrs

目前MarqueeContainer支持的自定义属性包括:

- defaultImageResId：默认的展示图片
- errorImageResId：加载失败时的展示图片
- indicatorEnableResId: 指示器资源(处于ViewPager当前页)
- indicatorDisableResId：指示器资源(非ViewPager当前页)

## Sample Usage

布局文件:
```xml
<?xml version="1.0" encoding="utf-8"?>
<RelativeLayout xmlns:android="http://schemas.android.com/apk/res/android"
    xmlns:mc="http://schemas.android.com/apk/res-auto"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <com.wzy.marquee.MarqueeContainer
        android:id="@+id/id_marquee_container"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        mc:defaultImageResId="@drawable/test2"
        mc:errorImageResId="@drawable/test3"/>

</RelativeLayout>
```

Activity：
```java
private void initView() {
    mMarqueeContainer = (MarqueeContainer) findViewById(R.id.id_marquee_container);
    mMarqueeContainer.setImageUrls(mImageUrls); // 加载本地图片资源
    mMarqueeContainer.setImageResIds(mResIds);  // 加载网络图片资源
}
```

# License

    Copyright 2011, 2012 Chris Banes

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.