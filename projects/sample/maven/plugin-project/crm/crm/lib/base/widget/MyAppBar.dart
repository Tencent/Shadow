import 'package:crm/base/utils/WidgetUtils.dart';
import 'package:crm/base/widget/TextView.dart';
import 'package:crm/common/MyColors.dart';

import '../../base/router/RouterHelper.dart';
import '../../base/utils/ImageHelper.dart';
import 'package:flutter/material.dart';
import '../../base/extension/WidgetExt.dart';
import '../../base/extension/ListExt.dart';

class MyAppBar extends AppBar{

  final AppBarController controller;

  MyAppBar({Key key,this.controller}):super(key: key,
      backgroundColor: controller.bgColor,
      shadowColor: Colors.transparent,
      centerTitle: true,
      leadingWidth: controller.ivLeftIcon.isEmpty ? 0 : 44,
      leading: controller.ivLeftIcon.isEmpty ? null : Container(
          margin: EdgeInsets.only(left: 16),
          child: ImageHelper.buildImage(controller.ivLeftIcon,width: 28,height: 28,type: 1,fit: BoxFit.fitWidth)
              .buildInkWell((){
            controller.leftFunc?.call();
          })
      ),
      title: WidgetUtils.buildProvider<AppBarController>(
          model: controller,
          builder: (context,model){
            return TextView(model.title,size: 18, weight: FontWeight.bold, color: MyColors.cl_1F2736,);
          }
      ),
      actions: [
        WidgetUtils.buildProvider<AppBarController>(
            model: controller,
            builder: (context,model){
              return Row(
                children: [
                  ...controller.children??[],
                  controller.tvRightText.isEmpty ? SizedBox.shrink() :
                  Container(
                    margin: EdgeInsets.only(left: 4),
                    child: TextView(controller.tvRightText,size: 16, color: MyColors.cl_00AC96,).buildInkWell(() {
                      controller.rightFun?.call();
                    }),
                  ),
                  SizedBox(width: 12,)
                ],
              );
            }
        )
      ]
  );

}

class AppBarController with ChangeNotifier{

  String title;
  String ivLeftIcon;
  Function() leftFunc;

  Color bgColor;
  String tvRightText;
  Function() rightFun;

  List<Widget> children;

  AppBarController({this.title, this.ivLeftIcon:"ic_back.png",
    this.leftFunc, this.bgColor, this.tvRightText:"", this.rightFun, this.children});



  void setTitle(String title,{Color bgColor:Colors.white,
    String ivLeftIcon:"ic_back.png",
    String tvRightText:"",
    Function() rightFun,

    List<Widget> children,
  }){
    this.title = title;
    this.bgColor = bgColor;
    this.ivLeftIcon = ivLeftIcon;
    this.tvRightText = tvRightText;
    this.rightFun = rightFun;
    this.children = children;
  }

}



