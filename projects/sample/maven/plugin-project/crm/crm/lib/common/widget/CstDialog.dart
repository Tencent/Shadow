
import 'package:crm/base/painting/MyDecoration.dart';
import 'package:crm/base/widget/EditWidget.dart';
import 'package:crm/base/widget/LinearWidget.dart';
import 'package:crm/base/widget/SuperTextView.dart';
import 'package:crm/base/widget/TextView.dart';
import 'package:crm/base/widget/dialog/AnimDialog.dart';
import 'package:crm/common/MyColors.dart';
import 'package:flutter/material.dart';
import 'package:flutter_screenutil/flutter_screenutil.dart';
import '../../base/extension/ListExt.dart';


class CstDialog extends StatelessWidget {



  static void showCstDialog(BuildContext context,{
    int type:0,String title,Function(String value) rightCallBack,
    Function() leftCallBack
  }){

    showAnimationDialog(context: context,
        builder: (context){
          return CstDialog(type: type,title: title,rightCallBack: rightCallBack,leftCallBack: leftCallBack,);
        }
    );

  }


  final int type;
  final String title;
  final String rightText;
  final Function(String value) rightCallBack;
  final String leftText;
  final Function leftCallBack;

  String value ="";

   CstDialog({Key key, this.type, this.title,
    this.leftText: "取消", this.rightCallBack,
    this.rightText: "确定",
    this.leftCallBack}) : super(key: key);

  @override
  Widget build(BuildContext context) {


    return Material(
      color: Colors.transparent,
      child: Center(
        child: LinearWidget(
          crossAxisAlignment: CrossAxisAlignment.center,
          mainAxisSize: MainAxisSize.min,
          width: 296,
          padding: EdgeInsets.all(20),
          decoration: MyDecoration.buildShape(solid: Colors.white,radius: 14),
          children: [
            ...typeWidget(),
            Row(
              mainAxisAlignment: MainAxisAlignment.start,
              children: [
                Expanded(child: SuperTextView(leftText,size: 16,
                  height: 40,
                  width: double.infinity,
                  alignment: Alignment.center,
                  stroke: MyColors.cl_DEE4EA,
                  color: MyColors.cl_161722,
                  radius: 4.0,onTap: (){
                    leftCallBack?.call();
                    Navigator.of(context).pop();
                  },),),
                SizedBox(width: 12,),
                Expanded(child:SuperTextView(rightText,solid: MyColors.cl_01C6AC,size: 16,
                  height: 40,
                  width: double.infinity,
                  alignment: Alignment.center,
                  color: Colors.white, radius: 4.0,onTap: (){
                    rightCallBack?.call(value);
                    Navigator.of(context).pop();
                  },)),
              ],
            ),

          ],
        ),
      ),
    );
  }


  List<Widget> typeWidget(){
    if(type == 1){
      return [
        TextView(title??"",size: 16,color: MyColors.cl_1F2736,weight: FontWeight.bold,),
        SizedBox(height: 30,),
        Container(
          constraints: BoxConstraints(
              minHeight: 80
          ),
          decoration:  MyDecoration.buildShape(radius: 3,solid: MyColors.cl_F4F4F6),
          child: EditWidget(
            textAlign: TextAlign.left,
            padding: EdgeInsets.all(12),
            size: 16,
            hintColor: MyColors.cl_7B8290,
            hint: "请输入意见",
            color: MyColors.cl_161722,
            onChanged: (value){
              this.value = value;
            },
          ),
        ),
        SizedBox(height: 30,),
      ];
    }
    return [
      Container(
        alignment: Alignment.center,
        constraints: BoxConstraints(
            minHeight: 106
        ),
        child: TextView(title??"",size: 16,color: MyColors.cl_1F2736,weight: FontWeight.bold,),
      )
    ];
  }

}

