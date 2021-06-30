
import 'package:crm/base/widget/EditLayout.dart';
import 'package:crm/base/widget/EditWidget.dart';
import 'package:crm/base/widget/LinearWidget.dart';
import 'package:crm/base/widget/TextView.dart';
import 'package:crm/common/MyColors.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import '../../base/view/BaseWidget.dart';
import '../../base/extension/WidgetExt.dart';
import '../../base/extension/ListExt.dart';

class ItemInputWidget extends StatefulWidget {

  final bool isNotNull;
  final bool isHide;
  final String title;
  final String content;
  final String hint;
  final InputType inputType;
  final ItemInputController controller;

  const ItemInputWidget({Key key,this.inputType, this.isNotNull:true, this.isHide, this.title, this.content, this.hint:"请输入", this.controller}) : super(key: key);

  @override
  _ItemInputWidgetState createState() => _ItemInputWidgetState();
}

class _ItemInputWidgetState extends BaseWidgetState<ItemInputWidget,ItemInputController> {

  @override
  ItemInputController getController() =>  widget.controller??ItemInputController();

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    controller.title = widget.title;
    controller.setValue(widget.content);
  }

  @override
  Widget build(BuildContext context) {


    var notNullWidget = widget.isNotNull? TextView("*",size: 16,color: MyColors.cl_FF0000,): SizedBox.shrink();


    return LinearWidget(
      bgColor: Colors.white,
      crossAxisAlignment: CrossAxisAlignment.center,
      constraints: BoxConstraints(
        minHeight: 60,
      ),
      padding: EdgeInsets.only(left: 16,right: 16),
      direction: Axis.horizontal,
      children: [
        notNullWidget,
        TextView(widget.title,size: 16,color: MyColors.cl_161722,weight: FontWeight.bold,),
        Expanded(child: EditWidget(
          text: widget.content,
          hint: widget.hint,
          inputType: widget.inputType,
          controller: controller.inputCtr,
          hintColor: MyColors.cl_B4B9C2,
          color: MyColors.cl_7B8290,
          size: 16,
          textAlign: TextAlign.end,
        ))
      ],
    );
  }
}

class ItemInputController extends BaseWidgetController{
  var inputCtr = TextEditingController();

  String title;
  @override
  String get value => inputCtr.text;

  @override
  void dispose() {
    inputCtr?.dispose();
    super.dispose();
  }
}

