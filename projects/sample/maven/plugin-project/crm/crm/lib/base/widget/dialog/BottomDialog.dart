
import 'package:crm/base/adapter/BaseAdapter.dart';
import 'package:crm/base/painting/MyDecoration.dart';
import 'package:crm/base/utils/WidgetUtils.dart';
import 'package:crm/base/widget/LinearWidget.dart';
import 'package:crm/base/widget/StackWidget.dart';
import 'package:crm/base/widget/SuperTextView.dart';
import 'package:crm/base/widget/TextView.dart';
import 'package:crm/common/MyColors.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import '../../../base/view/BaseWidget.dart';
import '../../../base/extension/WidgetExt.dart';
import '../../../base/extension/ListExt.dart';

class BottomDialog extends StatefulWidget {
  const BottomDialog({Key key, this.controller, this.callback,this.data,this.title}) : super(key: key);



  static void show(BuildContext context,{
    BottomDialogController controller,
    List<String> data,
    String title,
    Function(String value,int index) callback,
  }){

    showModalBottomSheet(
        isScrollControlled:true,
        context: context,
        backgroundColor: Colors.transparent,
        builder: (context) {
          return BottomDialog(controller: controller,callback: callback,data: data,title: title,);
        });
  }

  final BottomDialogController controller;
  final Function(String value,int index) callback;
  final List<String> data;
  final String title;


  @override
  _BottomDialogState createState() => _BottomDialogState();
}

class _BottomDialogState extends BaseWidgetState<BottomDialog,BottomDialogController> {

  @override
  BottomDialogController getController() =>  widget.controller??BottomDialogController();

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    if(null != widget.data)
      controller.data = widget.data;
    if(null != widget.title)
      controller.title = widget.title;
  }

  @override
  Widget build(BuildContext context) {
    return SafeArea(child: Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        LinearWidget(
          margin: EdgeInsets.only(left: 10,right: 10),
          decoration: MyDecoration.buildShape(solid: Colors.white,radius: 10),
          children: [
            Container(
              alignment: Alignment.center,
              height: 60,
              child: TextView(controller.title,size: 14,color: MyColors.cl_7B8290,),
            ),
            Container(
                alignment: Alignment.center,
                height: (controller.data?.length??0) < 6 ? (controller.data?.length??0) *60.0 : 6*60.0,
                child: WidgetUtils.buildList(
                  itemExtent: 60,
                  adapter: BaseAdapter<String>(
                    data: controller.data,
                    onItemClick: (context,index,model){
                      controller.setValue(model);
                      controller.setIndex(index);
                      Navigator.of(context).pop();
                      widget.callback?.call(model,index);
                    },
                    builder: (context,index,model){
                      var selectedColor = index == controller.index? MyColors.cl_01C6AC : MyColors.cl_161722;
                      return Stack(
                        children: [
                          Container(
                            padding: EdgeInsets.symmetric(horizontal: 16),
                            alignment: Alignment.center,
                            child: TextView(model,size: 18,color: selectedColor,),
                          ),
                          Divider(
                            height: 2,
                            color: MyColors.cl_DEE4EA,
                            thickness:1,
                          ),
                        ],
                      );
                    },
                  ),
                )
            )
          ],
        ),
        SizedBox(height: 10,),
        Container(
          margin: EdgeInsets.only(left: 10,right: 10),
          child:  SuperTextView("取消",size: 18,color: MyColors.cl_161722,
            margin: EdgeInsets.only(left: 10,right: 10),
            solid: Colors.white,radius: 10,
            alignment: Alignment.center,
            height: 60,
            onTap: (){
              Navigator.of(context).pop();
            },),
        ),
        SizedBox(height: 10,),
      ],
    ));
  }
}

class BottomDialogController extends BaseWidgetController{

  List<String> data;
  String title;
  String selectedItem;


  BottomDialogController({
    this.data, this.title, this.selectedItem
  }){
    index = data?.indexOf(selectedItem)??-1;
  }

}
