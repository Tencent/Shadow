

import 'package:flutter/material.dart';
import '../utils/BaseUtils.dart';

class SelectableTextWidget extends StatelessWidget {

  final EdgeInsetsGeometry margin;
  final EdgeInsetsGeometry padding;
  final Alignment alignment;
  final Decoration decoration;
  final Color bgColor;
  final MainAxisSize mainAxisSize;
  final double height;
  final double width;
  final BoxConstraints constraints;
  final String text;
  final double size;
  final Color color;

  const SelectableTextWidget({Key key, this.margin, this.padding, this.alignment, this.decoration,
    this.bgColor, this.mainAxisSize, this.height, this.width, this.constraints, this.text,
    this.size : 14,
    this.color : Colors.black
  }) : super(key: key);


  @override
  Widget build(BuildContext context) {
    return Container(
      alignment: alignment,
      height: height,
      width: width,
      margin: margin,
      decoration:decoration,
      color: bgColor,
      padding: padding,
      child: SelectableText(
          BaseUtils.isEmpty(text) ? "" : text,
          enableInteractiveSelection:false,
          scrollPhysics:const NeverScrollableScrollPhysics(),
        style: TextStyle(color: color,fontSize: size),
      ),
    );
  }
}

