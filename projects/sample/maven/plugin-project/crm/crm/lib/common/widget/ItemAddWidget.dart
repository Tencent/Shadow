
import 'package:crm/base/utils/ImageHelper.dart';
import 'package:crm/base/view/BaseWidget.dart';
import 'package:crm/base/widget/LinearWidget.dart';
import 'package:crm/base/widget/TextView.dart';
import 'package:crm/common/MyColors.dart';
import 'package:flutter/material.dart';

import '../../base/extension/WidgetExt.dart';


class ItemAddWidget extends StatefulWidget {

  final bool isNotNull;
  final String title;
  final ItemAddController controller;
  final bool isAdd;
  final Function() callback;

  const ItemAddWidget({Key key, this.isNotNull:true, this.title, this.callback, this.controller,this.isAdd: true}) : super(key: key);

  @override
  _ItemAddWidgetState createState() => _ItemAddWidgetState();
}

class _ItemAddWidgetState extends BaseWidgetState<ItemAddWidget,ItemAddController> {

  @override
  ItemAddController getController() =>  widget.controller??ItemAddController();

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    controller.title = widget.title;
    controller.isAdd = widget.isAdd;
    controller.isNotNull = widget.isNotNull;
  }

  @override
  Widget build(BuildContext context) {
    var notNullWidget = controller.isNotNull? TextView("*",size: 16,color: MyColors.cl_FF0000,): SizedBox.shrink();
    return LinearWidget(
      bgColor: Colors.white,
      crossAxisAlignment: CrossAxisAlignment.center,
      height: 60,
      padding: EdgeInsets.only(left: 16,right: 16),
      direction: Axis.horizontal,
      children: [
        notNullWidget,
        TextView(widget.title,size: 16,color: MyColors.cl_161722,weight: FontWeight.bold,),
        Spacer(),
        controller.isAdd ? ImageHelper.buildImage("ic_add_file.png",width: 22,height: 22).buildInkWell((){
          widget.callback?.call();
        }): SizedBox.shrink()
      ],
    );
  }
}

class ItemAddController extends BaseWidgetController{
  String title;
  bool _isNotNull;
  bool isAdd;

  bool get isNotNull => _isNotNull;


  set isNotNull(bool value) {
    _isNotNull = value;
  }

  setNotNull(bool value) {
    _isNotNull = value;
    notifyWidget?.call();
  }
}


