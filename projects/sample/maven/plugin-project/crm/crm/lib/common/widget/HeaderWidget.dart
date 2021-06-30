
import 'package:crm/base/router/RouterHelper.dart';
import 'package:crm/base/utils/ImageHelper.dart';
import 'package:crm/base/widget/LinearWidget.dart';
import 'package:crm/base/widget/TextView.dart';
import 'package:crm/common/MyColors.dart';
import 'package:flutter/material.dart';
import '../../base/view/BaseWidget.dart';
import '../../base/extension/WidgetExt.dart';

class HeaderWidget extends StatefulWidget with PreferredSizeWidget{

  final String backIcon;
  final Function() leftCallback;
  final Function() rightCallback;
  final String title;
  final String rightText;
  final Widget child;
  final HeaderController controller;
  final AlignmentGeometry alignment;

  const HeaderWidget({Key key, this.backIcon: "ic_back.png",
    this.leftCallback,
    this.rightCallback,
    this.alignment:Alignment.centerRight,
    this.child,
    this.controller,
    this.title:"",
    this.rightText:""}) : super(key: key);

  @override
  _HeaderWidgetState createState() => _HeaderWidgetState();


  @override
  Size get preferredSize => Size.fromHeight(42);
}

class _HeaderWidgetState extends BaseWidgetState<HeaderWidget,HeaderController> {

  @override
  HeaderController getController() =>  widget.controller??HeaderController();


  @override
  void initState() {
    super.initState();
    if(widget.title.isNotEmpty)
      controller.title = widget.title;
  }

  @override
  Widget build(BuildContext context) {

    return SafeArea(
      child: Stack(
        alignment: widget.alignment,
        children: [
          LinearWidget(
            bgColor: Colors.white,
            mainAxisSize:MainAxisSize.max,
            crossAxisAlignment:CrossAxisAlignment.center,
            direction: Axis.horizontal,
            padding: EdgeInsets.only(left: 12,right: 16),
            mainAxisAlignment: MainAxisAlignment.spaceBetween,
            height: 42,
            children: [
              ImageHelper.buildImage(widget.backIcon,width: 28,height: 28,type: 1).buildInkWell(() {
                if(null == widget.leftCallback)
                  RouterHelper.pop(context);
                else
                  widget.leftCallback?.call();
              }),
              TextView(controller.title,size: 18, weight: FontWeight.bold, color: MyColors.cl_1F2736,),
              TextView(widget.rightText,size: 16, color: MyColors.cl_00AC96,).buildInkWell(() {
                widget.rightCallback?.call();
              }),
            ],
          ),
          widget.child??SizedBox.shrink()
        ],
      ),
    );
  }
}

class HeaderController extends BaseWidgetController{

  String _title;

  String get title => _title;

  set title(String value) {
    _title = value;
    notifyWidget?.call();
  }
}
