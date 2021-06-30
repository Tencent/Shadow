
import 'package:crm/base/painting/MyDecoration.dart';
import 'package:flutter/material.dart';

import '../../base/extension/WidgetExt.dart';
import '../../base/widget/TextView.dart';

class SuperTextView extends StatelessWidget {

  final String data;
  final double size;
  final Color color;
  final Color solid;
  final Color stroke;
  final EdgeInsetsGeometry padding;
  final Function() onTap;
  final double radius;
  final double height;
  final double width;
  final EdgeInsetsGeometry margin;
  final EdgeInsetsGeometry paddingLeft;
  final AlignmentGeometry alignment;
  final Widget leftChild;



  const SuperTextView(this.data,{Key key, this.size, this.color,
    this.padding,
    this.paddingLeft: const EdgeInsets.only(left: 18),
    this.solid:Colors.transparent,
    this.stroke:Colors.transparent,
    this.onTap,
    this.radius:0.0,
    this.alignment,
    this.leftChild,
    this.margin, this.height, this.width}) : super(key: key);


  @override
  Widget build(BuildContext context) {

    var child = leftChild == null ? TextView(data,size: size,color: color) :
    Stack(
      alignment: Alignment.centerLeft,
      children: [
        leftChild,
        Container(
          margin: paddingLeft,
          child: TextView(data,size: size,color: color,softWrap:false),
        )
      ],
    );

    if(onTap != null){
      return Container(
        alignment: alignment,
        padding: padding,
        margin: margin,
        height: height,
        width: width,
        child: child,
      ).buildInk(radius: radius,
          decoration: MyDecoration.buildShape(radius: radius,solid: solid,stroke: stroke),
          onTap: onTap);
    }
    return Container(
      alignment: alignment,
      height: height,
      width: width,
      margin: margin,
      decoration: MyDecoration.buildShape(radius: radius,solid: solid,stroke: stroke),
      padding: padding,
      child: child,
    );
  }
}
