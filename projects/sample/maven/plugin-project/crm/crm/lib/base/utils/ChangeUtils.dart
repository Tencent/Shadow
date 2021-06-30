import 'package:flutter/cupertino.dart';
import 'package:flutter/material.dart';
import '../Config.dart';
import '../cache/Cahes.dart';
import '../utils/CstColors.dart';
import '../widget/MyButton.dart';
import '../widget/TextView.dart';

class ChangeUtils{



   static Widget show() {
     var datas = ["开发", "测试", "预发布","正式"];

     List<PopupMenuEntry<String>> list = [];

     datas?.forEach((element) {
       var entry = PopupMenuItem<String>(
         value: '$element',
         child: TextView('$element',color: CstColors.cl_7B8290,size: 16,),
       );

       list.add(entry);
     });


     return PopupMenuButton<String>(
       tooltip: "切换环境",
       child:   MyButton(text: "切换环境",),
       offset: Offset(0.5,0.5),
       onSelected: (value){
         switch (value) {
           case "开发": {
             Config.DEVELOP = true;
             Config.TEST = false;
             Config.PRE = false;
             Caches.getVar().put("change", "");
           }
           break;
           case "测试": {
             Config.DEVELOP = false;
             Config.TEST = true;
             Config.PRE = false;
             Caches.getVar().put("change", "1");
           }
           break;
           case "预发布": {
             Config.DEVELOP = false;
             Config.TEST = false;
             Config.PRE = true;
             Caches.getVar().put("change", "2");
           }
           break;
           case "正式": {
             Config.DEVELOP = false;
             Config.TEST = false;
             Config.PRE = false;
             Caches.getVar().put("change", "3");
           }
           break;
         }
       },
       itemBuilder: (context) {
         return list;
       },
     );

  }

   static void init(){
    String cache = Caches.getVar().getString("change");
    switch(cache){
      case "1": {
        Config.DEVELOP = false;
        Config.TEST = true;
        Config.PRE = false;
      }
      break;
      case "2": {
        Config.DEVELOP = false;
        Config.TEST = false;
        Config.PRE = true;
      }
      break;
      case "3": {
        Config.DEVELOP = false;
        Config.TEST = false;
        Config.PRE = false;
      }
      break;
    }
  }

  /**
   * 能否切换环境
   * @return
   */
//   static boolean isCanChange(){
//    int state = CacheHelper.getVal().getInt("env_switch", 0);
//    if(Config.DEVELOP || Config.TEST || state == 1){
//      CacheHelper.getVal().putInt("env_switch", 1);
//      return true;
//    }
//    return false;
//  }

}
