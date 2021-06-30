
import 'package:crm/base/view/BaseWidget.dart';
import 'package:crm/base/widget/TextView.dart';
import 'package:crm/common/MyColors.dart';
import 'package:flutter/material.dart';

import '../../../base/utils/CstColors.dart';
import '../../../base/widget/tab/indicator/MD2IndicatorSize.dart';



class TabBarWidget extends StatefulWidget {

  final TabBarController controller;
  final bool isScrollable;
  final double height;
  final double width;
  final Decoration indicator;
  final Decoration decoration;
  final List<String> titles;
  final EdgeInsetsGeometry padding;
  final EdgeInsetsGeometry margin;
  final ValueChanged<int> onTap;
  final Color bgColor;
  final bool autoDispose;
  final double tabSize;


  const TabBarWidget({Key key,this.onTap, this.controller,this.titles,
    this.isScrollable:false,this.height:45,this.width,this.indicator,
    this.bgColor: Colors.white,
    this.padding,this.margin, this.decoration,this.autoDispose:true,
  this.tabSize : 16}) : super(key: key);


  @override
  _TabBarWidgetState createState() => _TabBarWidgetState();
}

class _TabBarWidgetState extends BaseWidgetState<TabBarWidget,TabBarController> with SingleTickerProviderStateMixin{

  @override
  bool autoDispose() {
    // TODO: implement autoDispose
    return widget.autoDispose;
  }
  @override
  TabBarController getController() =>  widget.controller??TabBarController(tabTitles: widget.titles,vsync: this);

  @override
  Widget build(BuildContext context) {
    return Container(
      color: widget.decoration == null ? widget.bgColor : null,
      decoration: widget.decoration,
      padding: widget.padding,
      margin: widget.margin,
      height: widget.height,
      width: widget.width,
      child: TabBar(
        controller: controller.tabCtr,
        tabs: List.generate(controller.tabTitles.length, (index) =>
            TextView(controller.tabTitles[index],size: widget.tabSize,color: null)),
        indicator: widget.indicator ??MD2Indicator(indicatorColor: MyColors.cl_01C6AC),
        isScrollable: widget.isScrollable,
        labelColor: CstColors.cl_01C6AC,
        unselectedLabelColor: CstColors.cl_7B8290,
        onTap: widget.onTap,
      ),
    );
  }

}

class TabBarController extends BaseWidgetController {

  List<String> tabTitles;
  final int initialIndex;

  TabController tabCtr;

  TabBarController({this.tabTitles,this.initialIndex: 0,TickerProvider vsync}){
    tabCtr = TabController(length: tabTitles.length,initialIndex: initialIndex,
        vsync: vsync);
  }


  setTabTitles(List<String> tabTitles){
    this.tabTitles = tabTitles;
  }

  @override
  void dispose() {
    tabCtr?.dispose();
    super.dispose();
  }

}



