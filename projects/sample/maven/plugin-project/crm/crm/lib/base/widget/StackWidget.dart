

import 'package:flutter/material.dart';

class StackWidget extends StatelessWidget {

  final EdgeInsetsGeometry margin;
  final EdgeInsetsGeometry padding;
  final List<Widget> children;
  final Decoration decoration;
  final Color bgColor;
  final MainAxisSize mainAxisSize;
  final double height;
  final double width;
  final AlignmentGeometry alignment;
  final TextDirection textDirection;
  final StackFit fit;
  final BoxConstraints constraints;

  StackWidget({
    Key key,
    this.margin,
    this.padding,
    this.children,
    this.decoration,
    this.bgColor,
    this.mainAxisSize:MainAxisSize.max,
    this.alignment:AlignmentDirectional.topStart,
    this.height,
    this.width,
    this.textDirection,
    this.fit: StackFit.expand,
    this.constraints,

  }):super(key: key);

  @override
  Widget build(BuildContext context) {


    return Container(
      constraints: constraints,
      height: height,
      width: width,
      margin: margin,
      padding: padding,
      decoration:decoration,
      color: bgColor,
      child: (null == decoration && bgColor == null) ? Stack(
        fit: fit,
        alignment: alignment,
        children: children,
        textDirection: textDirection,
      ): Material(child: Stack(
        fit: fit,
        alignment: alignment,
        children: children,
        textDirection: textDirection,
      ), color: Colors.transparent,),
    );
  }
}

