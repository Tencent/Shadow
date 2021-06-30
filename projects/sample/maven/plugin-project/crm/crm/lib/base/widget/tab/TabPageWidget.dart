
import '../../../base/widget/LinearWidget.dart';
import 'package:flutter/material.dart';

import '../../../base/view/BaseWidget.dart';
import 'BarPreferredSizeWidget.dart';
import 'TabBarWidget.dart';

class TabPageWidget extends StatefulWidget {

  final List<String> tabTitles;
  final List<Widget> pages;
  final double barHeight;
  final TabPageController controller;

  final Decoration indicator;
  final Color bgColor;
  final Decoration decoration;
  final EdgeInsetsGeometry padding;
  final EdgeInsetsGeometry margin;
  final ScrollPhysics barPhysics;
  final PreferredSizeWidget appBar;
  final List<Widget> centerChildren;
  final ValueChanged<int> onTap;


  const TabPageWidget({Key key,this.tabTitles,this.barPhysics,
    this.barHeight:54, this.controller,this.pages,
    this.centerChildren,
    this.bgColor,
    this.indicator, this.decoration,
    this.padding,
    this.margin,
    this.appBar,
    this.onTap
  }) : super(key: key);
  @override
  _TabPageWidgetState createState() => _TabPageWidgetState();
}

class _TabPageWidgetState extends BaseWidgetState<TabPageWidget,TabPageController> with SingleTickerProviderStateMixin {
  
  @override
  TabPageController getController() =>  widget.controller??TabPageController();

  TabBarController _tabBarController;

  @override
  void initState() {
    super.initState();
    _tabBarController = TabBarController(tabTitles: widget.tabTitles,vsync: this);
    controller.controller = _tabBarController;
  }



  @override
  Widget build(BuildContext context) {




    if(null == widget.bgColor)
      return contentWidget();
    return Scaffold(
      appBar: widget.appBar,
      backgroundColor: widget.bgColor,
      body: contentWidget(),
    );
  }

  Widget contentWidget(){
    return Column(
      children: [
        TabBarWidget(
          indicator:widget.indicator,
          decoration:widget.decoration,
          padding:widget.padding,
          margin:widget.margin,
          height: widget.barHeight,
          onTap: widget.onTap,
          controller: controller.controller,
        ),
        ...widget.centerChildren??[],
        Expanded(child: TabBarView(
          physics: widget.barPhysics,
          controller: controller.controller.tabCtr,
          children: widget.pages,
        ))
      ],
    );
  }
}


class TabPageController extends BaseWidgetController{

   TabBarController controller ;


   @override
  void dispose() {
    super.dispose();
  }

}