


import 'package:flutter/material.dart';

class LinearWidget extends StatelessWidget {

  final EdgeInsetsGeometry margin;
  final EdgeInsetsGeometry padding;
  final Axis direction;
  final List<Widget> children;
  final MainAxisAlignment mainAxisAlignment;
  final CrossAxisAlignment crossAxisAlignment;
  final Decoration decoration;
  final Color bgColor;
  final MainAxisSize mainAxisSize;
  final double height;
  final double width;
  final AlignmentGeometry alignment;
  final BoxConstraints constraints;

  LinearWidget({
    Key key,
    this.margin,
    this.padding,
    this.direction : Axis.vertical,
    this.children,
    this.crossAxisAlignment: CrossAxisAlignment.start,
    this.mainAxisAlignment: MainAxisAlignment.start,
    this.decoration,
    this.bgColor,
    this.mainAxisSize:MainAxisSize.min,
    this.height,
    this.width,
    this.constraints,
    this.alignment,

  }):super(key: key);

  @override
  Widget build(BuildContext context) {
    var flex = Flex(
      direction: direction,
      children: children,
      mainAxisSize: mainAxisSize,
      mainAxisAlignment: mainAxisAlignment,
      crossAxisAlignment: crossAxisAlignment,
    );

    var child = (null == bgColor && null == decoration) ? flex : Material(color: Colors.transparent,child: flex);
    return Container(
      alignment: alignment,
      constraints:constraints,
      height: height,
      width: width,
      margin: margin,
      padding: padding,
      decoration:decoration,
      color: bgColor,
      child: child,
    );
  }
}

