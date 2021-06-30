
import 'package:crm/base/utils/CstColors.dart';
import 'package:crm/base/utils/ImageHelper.dart';
import 'package:crm/base/widget/LinearWidget.dart';
import 'package:crm/base/widget/SuperTextView.dart';
import 'package:crm/base/widget/TextView.dart';
import 'package:crm/common/MyColors.dart';
import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import '../../../base/view/BaseWidget.dart';
import '../../../base/extension/WidgetExt.dart';
import '../../../base/extension/ListExt.dart';

class DateDialog extends StatefulWidget {


  static void show(BuildContext context,{
    String title:"跑动时间",
    int type:0,
    DateDialogController controller,
    Function(String date) dateCallback

  }){

    showModalBottomSheet(
        context: context,
        builder: (context) {
          return DateDialog(controller: controller,dateCallback:dateCallback,title: title,type: type);
        });
  }

  final DateDialogController controller;
  final Function(String date) dateCallback;
  final String title;
  final int type;

  const DateDialog({Key key, this.controller,this.dateCallback,this.title,this.type}) : super(key: key);

  @override
  _DateDialogState createState() => _DateDialogState();
}

class _DateDialogState extends BaseWidgetState<DateDialog,DateDialogController> {


  DateTime _dateTime = DateTime.now();

  @override
  DateDialogController getController() => widget.controller?? DateDialogController();

  @override
  void initState() {
    super.initState();
  }

  @override
  Widget build(BuildContext context) {

    return LinearWidget(
      height: 364,
      padding: EdgeInsets.only(left: 16,right: 16,top: 14,bottom: 6),
      children: [
        Row(
          mainAxisAlignment: MainAxisAlignment.spaceBetween,
          children: [
            TextView(widget.title,size: 16,color: MyColors.cl_161722,weight: FontWeight.bold,),
            ImageHelper.buildImage("ic_close.png",type: 1,width: 28,height: 28).buildInkWell(() =>
                Navigator.of(context).pop()
            )
          ],
        ),
        // widget.type == 0 ?
        Expanded(child: CupertinoDatePicker(
            mode: CupertinoDatePickerMode.date,
            initialDateTime: DateTime.now(),
            onDateTimeChanged: (date) {
              _dateTime = date;
            }),),
        // Expanded(
        //     child: Stack(
        //       children: [
        //         Container(
        //           margin: EdgeInsets.only(left: 30),
        //           child: CupertinoDatePicker(
        //               mode: CupertinoDatePickerMode.date,
        //               initialDateTime: DateTime.now(),
        //               onDateTimeChanged: (date) {
        //                 _dateTime = date;
        //               }),
        //         ),
        //         Row(
        //           children: [
        //             Expanded(
        //                 flex: 3,
        //                 child: Container(
        //               color: CstColors.bgColor,
        //             )),
        //             Spacer(flex: 5,),
        //             Expanded(
        //                 flex: 3,
        //                 child: Container(
        //                   color: CstColors.bgColor,
        //                 )),
        //           ],
        //         )
        //       ],
        //     )
        // ),

        SuperTextView("确定",solid: MyColors.cl_01C6AC,size: 16,
          height: 46,
          alignment: Alignment.center,
          margin: EdgeInsets.only(left: 8,right: 8),
          color: Colors.white, radius: 8.0,onTap: (){
            Navigator.of(context).pop();
            if(widget.type == 0){
              controller.setValue("${_dateTime.year}-${_change(_dateTime.month)}-${_change(_dateTime.day)}");
              widget.dateCallback?.call(controller.value);
            }
            else{
              controller.setValue("${_dateTime.year}-${_change(_dateTime.month)}");
              widget.dateCallback?.call(controller.value);
            }
          },)
      ],
    );

  }

  String _change(int value){
    if(value < 10)
      return "0${value}";
    return value.toString();
  }
}

class DateDialogController extends BaseWidgetController{

}

