
import 'package:crm/base/view/BaseWidget.dart';
import 'package:crm/base/widget/EditLayout.dart';
import 'package:crm/base/widget/EditWidget.dart';
import 'package:crm/base/widget/LinearWidget.dart';
import 'package:crm/base/widget/TextView.dart';
import 'package:crm/base/widget/ptr/PtrWidget.dart';
import 'package:crm/common/MyColors.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import '../../base/view/BaseWidget.dart';
import '../../base/extension/WidgetExt.dart';
import '../../base/extension/ListExt.dart';

class ItemTitleInputWidget extends StatefulWidget {


  final ItemTitleInputController controller;
  final bool isNotNull;
  final String title;
  final String content;
final InputType inputType;

  const ItemTitleInputWidget({Key key,this.inputType, this.controller, this.isNotNull:true, this.title, this.content}) : super(key: key);

  @override
  _ItemTitleInputWidgetState createState() => _ItemTitleInputWidgetState();
}

class _ItemTitleInputWidgetState extends BaseWidgetState<ItemTitleInputWidget,ItemTitleInputController> {

  @override
  ItemTitleInputController getController() => widget.controller?? ItemTitleInputController();

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
      padding: EdgeInsets.only(left: 16,right: 16,top: 20,bottom: 10),
      children: [
        Row(
          children: [
            notNullWidget,
            TextView(widget.title,size: 16,color: MyColors.cl_161722,weight: FontWeight.bold,),
          ],
        ),
        Container(
          constraints: BoxConstraints(
              minHeight: 74
          ),
          child: EditWidget(
            text: controller.value,
            inputType: widget.inputType,
            textAlign: TextAlign.left,
            controller: controller.contentController,
            hint: "请输入",
            size: 16,
            onChanged: (value){
              controller.setValue(value);
            },
            hintColor: MyColors.cl_B4B9C2,
            color: MyColors.cl_7B8290,
          ),
        )
      ],
    );
  }
}

class ItemTitleInputController extends BaseWidgetController{

  var contentController = TextEditingController();

  String title;

}

