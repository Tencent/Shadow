

import 'package:flutter/material.dart';
import '../widget/LinearWidget.dart';
import '../controller/BaseController.dart';
import '../utils/CstColors.dart';
import '../widget/TextView.dart';

class TabWidget extends StatefulWidget {

  final List<String> data;
  final List<String> Function(BuildContext context) dataBuilder;

  final double minWidth;
  final double height;
  final Function(int index,String value) onTop;
  final MyTabController controller;

  const TabWidget({Key key,
     this.data,
    this.minWidth: 34,
    this.height: 34,
    this.onTop,
    this.dataBuilder,
    this.controller,
  }) : super(key: key);


  @override
  _TabWidgetState createState() => _TabWidgetState();
}

class _TabWidgetState extends State<TabWidget> {


  MyTabController _controller;
  @override
  void initState() {
    _controller = widget.controller??MyTabController();

    _controller.setNotifyWidget((){
      setState(() {

      });
    });
    super.initState();
  }

  @override
  Widget build(BuildContext context) {

    var data  = widget.data??widget.dataBuilder(context);
    return LinearWidget(
      height: widget.height,
      direction: Axis.horizontal,
      children: data.map((model){
        var index = data.indexOf(model);
        var isCheck = _controller.index == index;
        var color = isCheck ? CstColors.white : CstColors.cl_161722;
        var bgColor = isCheck ? CstColors.cl_01C6AC : CstColors.white;

        var shape = isCheck ? null :
        BeveledRectangleBorder(
            side: BorderSide(width: 0.5, color: CstColors.cl_E6EAEE),
            borderRadius: BorderRadius.circular(0));

        return FlatButton(
          padding: EdgeInsets.symmetric(horizontal: 10),
          child: TextView(model,size: 12,color: color,),
          color: bgColor,
          height: widget.height,
          minWidth: widget.minWidth,
          shape: shape,
          onPressed: (){
            _controller.setIndex(index);
            _controller.setValue(model);
            widget.onTop?.call(index,model);

          },
        );

      }).toList()
      ,
    );

  }
}


class MyTabController with BaseController{

  int index = 0;

  Function() notifyWidget;

  void setNotifyWidget(Function() notifyWidget){
    this.notifyWidget = notifyWidget;
  }



  @override
  void setValue(String value) {
    super.setValue(value);
  }

}
