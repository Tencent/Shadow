import 'package:flutter/material.dart';

class MyCustomPainter extends CustomPainter {
  @override
  void paint(Canvas canvas, Size size) {
    _drawFlower(canvas,size);
  }

  _drawFlower(Canvas canvas, Size size) {
    //将花变为红色
    // if (flowerPaths.length >= RoseData.flowerPoints.length) {
    //   var path = Path();
    //   for (int i = 0; i < flowerPaths.length; i++) {
    //     if (i == 0) {
    //       path.moveTo(flowerPaths[i].dx, flowerPaths[i].dy);
    //     } else {
    //       path.lineTo(flowerPaths[i].dx, flowerPaths[i].dy);
    //     }
    //   }
    //   _paint.style = PaintingStyle.fill;
    //   _paint.color = _flowerColor;
    //   canvas.drawPath(path, _paint);
    // }
    // //绘制线
    // _paint.style = PaintingStyle.stroke;
    // _paint.color = _strokeColor;
    // //去掉最后2个点，最后2个点为了绘制红色
    // var points = flowerPaths.sublist(0, max(0, flowerPaths.length - 2));
    // canvas.drawPoints(PointMode.polygon, points, _paint);
  }

  @override
  bool shouldRepaint(covariant CustomPainter oldDelegate) {
    return this != oldDelegate;
  }
}
