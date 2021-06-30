
import 'package:crm/base/painting/MyDecoration.dart';
import 'package:crm/base/utils/ImageHelper.dart';
import 'package:crm/base/view/BaseWidget.dart';
import 'package:crm/base/widget/LinearWidget.dart';
import 'package:crm/base/widget/TextView.dart';
import 'package:crm/base/widget/date/DateDialog.dart';
import 'package:crm/base/widget/dialog/BottomDialog.dart';
import 'package:crm/common/MyColors.dart';
import 'package:flutter/material.dart';

import '../../base/extension/WidgetExt.dart';

enum SelectedType{
  date,
  bottom,
  other,
}

class ItemSelectedWidget extends StatefulWidget {

  final bool isNotNull;
  final bool isHide;
  final String title;
  final String content;
  final String hint;
  final SelectedType type;
  final ItemSelectedController controller;
  final Function(String value,int index) callback;
  final List<String> data;
  final List<String> Function() dataBuilder;

  const ItemSelectedWidget({Key key,
    this.isNotNull:true,
    this.isHide:false,
    this.title,
    this.data,
    this.dataBuilder,
    this.callback,
    this.type: SelectedType.date,
    this.content,
    this.hint:"请选择",
    this.controller}) : super(key: key);

  @override
  _ItemSelectedWidgetState createState() => _ItemSelectedWidgetState();
}

class _ItemSelectedWidgetState extends BaseWidgetState<ItemSelectedWidget,ItemSelectedController> {

  @override
  ItemSelectedController getController() =>  widget.controller??ItemSelectedController();

  BottomDialogController bottomController;
  DateDialogController dateController;

  List<String> data;
  @override
  void initState() {
    // TODO: implement initState
    super.initState();

    controller.title = widget.title;
    controller.isHide = widget.isHide;
    if(null != widget.content)
      controller.setValue(widget.content);

    if(widget.type == SelectedType.other){
      return;
    }
    data =  widget.data??widget.dataBuilder?.call();
    var index = data?.indexOf(controller.value)??0;
    controller.setIndex(index);
  }

  @override
  Widget build(BuildContext context) {


    var notNullWidget = widget.isNotNull? TextView("*",size: 16,color: MyColors.cl_FF0000,): SizedBox.shrink();


    if(controller.isHide)
      return SizedBox.shrink();

    return LinearWidget(
      crossAxisAlignment: CrossAxisAlignment.center,
      height: 60,
      padding: EdgeInsets.only(left: 16,right: 16),
      direction: Axis.horizontal,
      children: [
        notNullWidget,
        TextView(widget.title,size: 16,color: MyColors.cl_161722,weight: FontWeight.bold,),
        TextView(controller.isEmpty()? widget.hint : controller.value,size: 16,
            textAlign: TextAlign.end,
            color:controller.isEmpty()? MyColors.cl_B4B9C2 : MyColors.cl_7B8290).buildExpanded(),

        ImageHelper.buildImage("ic_enter.png",width: 16,height: 16)
      ],
    ).buildInk(decoration : MyDecoration.buildShape(solid: Colors.white),onTap:(){
      if(widget.type == SelectedType.date){
        if(null == bottomController)
          dateController = DateDialogController();
        DateDialog.show(context,controller: dateController,
            title: widget.title,
            dateCallback: (value){
              controller.setValue(value);
              controller.notifyUI();
              widget.callback?.call(value,0);
            });
      }
      else if(widget.type == SelectedType.bottom){
        if(null == bottomController)
          bottomController = BottomDialogController(data: data,title: widget.title,selectedItem: widget.content);
        bottomController.data = data;

        BottomDialog.show(context,controller: bottomController,
            callback: (value,index){
              controller.setValue(value);
              controller.setIndex(index);
              widget.callback?.call(value,index);
            });
      }
      else{
        widget.callback?.call("",0);
      }
    });
  }

}

class ItemSelectedController extends BaseWidgetController{


  String title;
  bool _isHide;

  bool get isHide => _isHide;

  set isHide(bool value) {
    _isHide = value;
    notifyUI();
  }
}


