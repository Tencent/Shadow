
import 'package:flutter/material.dart';
import 'package:provider/provider.dart';

mixin BaseMixin {


  Widget getView(BuildContext context);



  Map _argumentsMap;
  Map argumentOf(BuildContext context) {
    if(null == _argumentsMap)
      _argumentsMap = ModalRoute.of(context).settings.arguments;
    if(null == _argumentsMap){
      _argumentsMap = {};
    }
    if(_argumentsMap.isNotEmpty)
      print("页面参数传递： " + _argumentsMap.toString());
    return _argumentsMap;
  }

  // Map argumentOf(BuildContext context) {
  //   var argumentMap = ModalRoute.of(context).settings.arguments;
  //   return argumentMap;
  // }



  //  show({Future<dynamic> reqCallback}){
//    BotToast.showCustomNotification(
//      toastBuilder: (value){
//        reqCallback.then((t) => value);
//
//      return NetLoadingDialog(
//        outsideDismiss: false,
//      );
//    },
//    );
//  }
//
    T providerOf<T>(BuildContext context){
    return Provider.of<T>(context,listen: false);
  }


}
