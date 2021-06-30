
import 'package:crm/base/utils/CstColors.dart';
import 'package:crm/base/widget/TextView.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import '../../../base/view/BaseWidget.dart';
import '../../../base/extension/WidgetExt.dart';
import '../../../base/extension/ListExt.dart';

class CstTabBarWidget extends StatefulWidget {

  final CstTabBarController controller;
  final double height;
  final Decoration indicator;
  final Decoration decoration;
  final List<String> tabTitles;
  final EdgeInsetsGeometry padding;
  final EdgeInsetsGeometry margin;
  final ValueChanged<int> onTap;
  final Color bgColor;
  final int index;

  const CstTabBarWidget({Key key,
    this.controller,
    this.height:45,
    this.indicator,
    this.decoration,
    this.tabTitles,
    this.padding,
    this.margin,
    this.onTap,
    this.bgColor,
    this.index:0
  }) : super(key: key);

  @override
  _CstTabBarWidgetState createState() => _CstTabBarWidgetState();
}

class _CstTabBarWidgetState extends BaseWidgetState<CstTabBarWidget,CstTabBarController> {

  @override
  CstTabBarController getController() =>  widget.controller??CstTabBarController();


  @override
  void initState() {
    super.initState();
    controller.tabTitles = widget.tabTitles;
    controller.index = widget.index;
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      color: widget.decoration == null ? widget.bgColor : null,
      decoration: widget.decoration,
      padding: widget.padding,
      margin: widget.margin,
      height: widget.height,
      child: Row(
        children: List.generate(controller?.tabTitles?.length??0, (index){
          var title = controller?.tabTitles[index];
          return  Expanded(child: Container(
            decoration: controller.index == index ? widget.indicator : null,
            alignment: Alignment.center,
            child: TextView(title,size: 15,color: controller.index == index ? CstColors.cl_01C6AC: CstColors.cl_7B8290,),
          ).buildInkWell((){
            if(controller.index == index)
              return;
            controller.setIndex(index);
            widget.onTap?.call(index);
          })
          );
        })
      ),
    );
  }
}

class  CstTabBarController extends BaseWidgetController{

  List<String> tabTitles;

  setTabTitles(List<String> tabTitles){
    this.tabTitles = tabTitles;
  }

  @override
  void dispose() {
    super.dispose();
  }

}

