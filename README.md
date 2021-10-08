        安卓自定义的大图加载控件（分块加载和内存复用）
* 大致思路
* 1.用options获取圆图片宽高
* 2.用matrix进行比例压缩（按照控件宽度）
* 3.用rect确定加载区域，传入decode解码
* 4.用canvas绘制

* 效果图：https://github.com/fenchen31/picture/blob/master/LineChartView.jpg
* 或 app/src/main/res/drawable/bigimageview.jpg
