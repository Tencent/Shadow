


import 'package:flutter/material.dart';

class WrapWidget extends StatelessWidget {

  final EdgeInsetsGeometry margin;
  final EdgeInsetsGeometry padding;
  final Axis direction;
  final List<Widget> children;
  final WrapAlignment alignment;
  final Decoration decoration;
  final Color bgColor;
  final MainAxisSize mainAxisSize;
  final double height;
  final double width;

  WrapWidget({
    Key key,
    this.margin,
    this.padding,
    this.direction : Axis.horizontal,
    this.children,
    this.alignment: WrapAlignment.start,
    this.decoration,
    this.bgColor,
    this.mainAxisSize:MainAxisSize.min,
    this.height,
    this.width,

  }):super(key: key);

  @override
  Widget build(BuildContext context) {
    return Container(
      height: height,
      width: width,
      margin: margin,
      padding: padding,
      decoration:decoration,
      color: bgColor,
      child: Wrap(
        direction: direction,
        children: children,
        alignment: alignment,
      ),
    );
  }
}

