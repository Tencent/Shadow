

import 'package:flutter/material.dart';
import '../utils/CstColors.dart';

enum BorderSideType{
  top,
  left,
  right,
  bottom,
  all,
}

class MyDecoration {

  static BoxDecoration buildShapeByRadius({
    Color solid: Colors.transparent,
    Color stroke:Colors.transparent,
    double strokeWidth:1.0,
    double topRightRadius = 0.0,
    double bottomRightRadius = 0.0,
    double topLeftRadius = 0.0,
    double bottomLeftRadius = 0.0,

  }){
    return BoxDecoration(
        color: solid,
        border: Border.all(
            color: stroke,
            width: strokeWidth
        ),
        borderRadius: BorderRadius.only(
          topLeft: Radius.circular(topLeftRadius),
          topRight: Radius.circular(topRightRadius),
          bottomLeft: Radius.circular(bottomLeftRadius),
          bottomRight: Radius.circular(bottomRightRadius),
        )
    );
  }

  static BoxDecoration buildShapeByBorder({
    Color solid: Colors.transparent,
    Color stroke:Colors.transparent,
    double strokeWidth:1.0,
    bool topRight = false,
    bool bottomRight = false,
    bool topLeft = false,
    bool bottomLeft = false,
    double radius = 0.0,
    bool topBorder:false,
    bool bottomBorder:false,
    bool rightBorder:false,
    bool leftBorder:false,

  }){
    final BorderSide side = BorderSide(color: stroke, width: strokeWidth, style: BorderStyle.solid);
    return BoxDecoration(
        color: solid,
        border: Border(
            top: topBorder? BorderSide.none: side,
            bottom: bottomBorder? BorderSide.none: side,
            right: rightBorder? BorderSide.none: side,
            left: leftBorder? BorderSide.none: side,
        ),
        borderRadius: BorderRadius.only(
          topLeft: topLeft ? Radius.zero : Radius.circular(radius),
          topRight: topRight ? Radius.zero : Radius.circular(radius),
          bottomLeft: bottomLeft ? Radius.zero : Radius.circular(radius),
          bottomRight: bottomRight ? Radius.zero : Radius.circular(radius),
        )
    );
  }


  static BoxDecoration buildShape({
    Color solid: Colors.transparent,
    Color stroke:Colors.transparent,
    double strokeWidth: 1.0,
    List<BoxShadow> boxShadow,
    double radius = 0.0,
    DecorationImage image
  }){
    return BoxDecoration(
      boxShadow: boxShadow,
        image: image,
        color: solid,
        border: Border.all(
            color: stroke,
            width: strokeWidth
        ),
        borderRadius: BorderRadius.circular(radius)
    );
  }


  static BoxDecoration buildShapeBorder({
    Color solid: Colors.transparent,
    Color stroke:Colors.transparent,
    DecorationImage image,
    List<BorderSideType> types: const [BorderSideType.all]
  }){

    var borderSide = BorderSide(color: stroke, width: 1.0, style: BorderStyle.solid);

    var left = types.contains(BorderSideType.left) ? BorderSide.none : borderSide;
    var right = types.contains(BorderSideType.right) ? BorderSide.none : borderSide;
    var bottom = types.contains(BorderSideType.bottom) ? BorderSide.none : borderSide;
    var top = types.contains(BorderSideType.top) ? BorderSide.none : borderSide;

    return BoxDecoration(
      image: image,
      color: solid,
      border: Border(left: left,right: right,bottom: bottom,top: top),
    );
  }

  static Border buildBorder({
    Color solid: Colors.transparent,
    Color stroke:Colors.transparent,
    List<BorderSideType> types: const [BorderSideType.all]
  }){

    var borderSide = BorderSide(color: stroke, width: 1.0, style: BorderStyle.solid);

    var left = types.contains(BorderSideType.left) ? BorderSide.none : borderSide;
    var right = types.contains(BorderSideType.right) ? BorderSide.none : borderSide;
    var bottom = types.contains(BorderSideType.bottom) ? BorderSide.none : borderSide;
    var top = types.contains(BorderSideType.top) ? BorderSide.none : borderSide;

    return  Border(left: left,right: right,bottom: bottom,top: top);
  }


}
