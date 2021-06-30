

import 'package:bot_toast/bot_toast.dart';
import 'package:flutter/material.dart';
import '../painting/MyDecoration.dart';
import '../utils/ImageHelper.dart';
import '../utils/CstColors.dart';
import '../widget/LinearWidget.dart';
import '../widget/TextView.dart';


class ToastUtil{



  static void showToast(String text){
    if(null == text || text.length == 0)
      return;
    BotToast.showText(text: text,
        textStyle: const TextStyle(fontSize: 15, color: Colors.white,height: 1.5));
    // BotToast.showCustomNotification(
    //     enableSlideOff:false,
    //     toastBuilder: (value){
    //       var stroke = error? CstColors.cl_D48B8B : CstColors.cl_8BD4B5;
    //       var solid = error? CstColors.cl_FFEEEE : CstColors.cl_EEFFF8;
    //       var color = error? CstColors.cl_F92E2E : CstColors.cl_019A5A;
    //       var name = error? "ic_toast_error.png" : "ic_toast_suc.png";
    //       return Container(
    //         margin: EdgeInsets.only(top: 80),
    //         alignment: Alignment.topCenter,
    //         child:LinearWidget(
    //           direction: Axis.horizontal,
    //           height: 34,
    //           padding: EdgeInsets.symmetric(horizontal: 20),
    //           crossAxisAlignment: CrossAxisAlignment.center,
    //           mainAxisAlignment: MainAxisAlignment.center,
    //           decoration: MyDecoration.buildShape(
    //               stroke: stroke,
    //               solid: solid
    //           ),
    //           children: [
    //             ImageHelper.buildImage(name,width: 16,height: 16),
    //             SizedBox(width: 6,),
    //             TextView(text,size: 14,color: color,)
    //           ],
    //         ),
    //       );
    //     });

  }


  static bool isToast = false;

  static showDialog({
      WrapAnimation wrapAnimation,
      WrapAnimation wrapToastAnimation,
      Alignment align = Alignment.center,
      BackButtonBehavior backButtonBehavior,
      bool crossPage = true,
      bool clickClose = false,
      bool allowClick = false,
      bool enableKeyboardSafeArea = true,
      VoidCallback onClose,
      Duration duration,
      Duration animationDuration,
      Duration animationReverseDuration,
      Color backgroundColor = Colors.black12,
    }) {

    isToast = true;
  BotToast.showLoading(backgroundColor:Colors.black26,);
//       BotToast.showCustomLoading(
//          wrapAnimation: wrapAnimation,
//          wrapToastAnimation: wrapToastAnimation,
//          align: align,
//          enableKeyboardSafeArea: enableKeyboardSafeArea,
//          backButtonBehavior: backButtonBehavior,
//          toastBuilder: (_) => Container(
//            padding: const EdgeInsets.all(15),
////            decoration: const BoxDecoration(
////                color: Colors.transparent,
////                borderRadius: BorderRadius.all(Radius.circular(8))),
//            child: const CircularProgressIndicator(
//              backgroundColor: Colors.white,
//            ),
//          ),
//          clickClose: clickClose,
//          allowClick: allowClick,
//          crossPage: crossPage,
//          ignoreContentClick: true,
//          onClose: onClose,
//          duration: duration,
//          animationDuration: animationDuration,
//          animationReverseDuration: animationReverseDuration,
//          backgroundColor: backgroundColor);
    }


  static dismiss(){
    isToast = false;
    BotToast.closeAllLoading();
  }


}