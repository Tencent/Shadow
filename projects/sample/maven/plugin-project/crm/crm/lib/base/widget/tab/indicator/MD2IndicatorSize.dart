
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';

enum MD2IndicatorSize {
  tiny,
  normal,
  full,
}

class MD2Indicator extends Decoration {
  final double indicatorHeight;
  final double indicatorWidth;
  final Color indicatorColor;
  final MD2IndicatorSize indicatorSize;
  final double paddingTop;

  const MD2Indicator(
      { this.indicatorHeight: 3.0,
        this.indicatorWidth:30.0,
         this.indicatorColor: Colors.black,
         this.indicatorSize:MD2IndicatorSize.tiny,
      this.paddingTop:0.0});

  @override
  _MD2Painter createBoxPainter([VoidCallback onChanged]) {
    return new _MD2Painter(this, onChanged);
  }
}

class _MD2Painter extends BoxPainter {
  final MD2Indicator decoration;

  _MD2Painter(this.decoration, VoidCallback onChanged)
      : assert(decoration != null),
        super(onChanged);

  @override
  void paint(Canvas canvas, Offset offset, ImageConfiguration configuration) {
    assert(configuration != null);
    assert(configuration.size != null);

    Rect rect;
    if (decoration.indicatorSize == MD2IndicatorSize.full) {
      rect = Offset(offset.dx,
          (configuration.size.height - decoration.paddingTop - decoration.indicatorHeight ?? 3))
      & Size(configuration.size.width, decoration.indicatorHeight ?? 3);
    } else if (decoration.indicatorSize == MD2IndicatorSize.normal) {
      rect = Offset(offset.dx + 6,
          (configuration.size.height - decoration.paddingTop -decoration.indicatorHeight ?? 3))
      & Size(configuration.size.width - 12, decoration.indicatorHeight ?? 3);
    } else if (decoration.indicatorSize == MD2IndicatorSize.tiny) {
      rect = Offset(offset.dx + configuration.size.width / 2 - decoration.indicatorWidth/2,
          (configuration.size.height - decoration.paddingTop - decoration.indicatorHeight ?? 3))
      & Size(decoration.indicatorWidth, decoration.indicatorHeight ?? 3);
    }

    final Paint paint = Paint();
    paint.color = decoration.indicatorColor ?? Color(0xff1967d2);
    paint.style = PaintingStyle.fill;
    canvas.drawRRect(
        RRect.fromRectAndCorners(rect,
//            topRight: Radius.circular(0), topLeft: Radius.circular(8)
        ),
        paint);
  }
}
