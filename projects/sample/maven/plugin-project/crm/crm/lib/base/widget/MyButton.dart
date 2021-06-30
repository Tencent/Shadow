

import 'package:flutter/material.dart';
import '../utils/CstColors.dart';
import '../widget/TextView.dart';

class MyButton extends FlatButton{

  final int type;
  final String text;
  final VoidCallback onPressed;
  final double minWidth;
  final double radius;
  final double height;
  final double size;
  final bool isCircle;
  final bool isLoading;
  final Widget textChild;
  final EdgeInsetsGeometry padding;

  MyButton({Key key,this.minWidth: 90,this.text,this.type:0,this.onPressed,
    this.height:34,
    this.size:12,
    this.textChild,
    this.isCircle:false,
    this.isLoading:false,
    this.padding: const EdgeInsets.symmetric(horizontal: 10,vertical: 0.0),
    this.radius: 3}): super(key: key,
    child: isLoading? Container(
      height: 25,
      width: 25,
      child: CircularProgressIndicator(
        backgroundColor: Colors.transparent,
        valueColor: AlwaysStoppedAnimation<Color>(Colors.white),
      ),
    )
        : (textChild ??TextView(text,size: size,color:  CstColors.white,)),
    color: Colors.transparent,
    height: height,
    padding:padding,
    // shape: isCircle ?
    // StadiumBorder(
    //   side: BorderSide(width: 1, color: type == 0 ? CstColors.cl_0FB36E :  Colors.transparent),
    // ):
    // ContinuousRectangleBorder(
    //     side: BorderSide(width: 0.5, color: type == 0 ? CstColors.cl_0FB36E :  Colors.transparent),
    //     borderRadius: BorderRadius.circular(radius)),
    minWidth: minWidth,
    onPressed: onPressed,
  );

}
