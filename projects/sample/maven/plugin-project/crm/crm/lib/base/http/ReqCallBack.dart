
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import '../utils/ToastUtil.dart';


class ReqCallBack {

  final Function(dynamic data) onSuccess;
  final Function() onError;
  final Function(int code) onFailed;
  final VoidCallback onCompleted;

  String key;
  bool isPaging;
  bool isToast;



  ReqCallBack({
    this.onSuccess,
    this.onError,
    this.onFailed,
    this.isToast:true,
    this.onCompleted,
    this.key:"records",
    this.isPaging:false,
  });



  void onReqError(e) {
    if(e != null)
      debugPrint('error--->\n' + e.toString());
    if(null != onError)
      onError();

    if(isToast){
//      ToastUtil.showToast("网络错误");
    }
  }

  void onReqCompleted() {
    ToastUtil.dismiss();
    if(null != onCompleted)
      onCompleted();


  }

  void onReqFailed(int code,String msg) {
    if(null != onFailed)
      onFailed(code);
  }

  void onReqSuccess(result) {
    if(isPaging)
      onSuccess?.call(result[key]);
    else
      onSuccess?.call(result);
  }


}