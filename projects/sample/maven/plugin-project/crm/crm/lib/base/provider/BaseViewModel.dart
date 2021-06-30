import 'package:crm/base/widget/ptr/PtrWidget.dart';
import 'package:dio/dio.dart';
import 'package:flutter/material.dart';
import '../rxbus/rx.dart';
import '../http/HttpsProvider.dart';

import '../http/Params.dart';
import '../http/ReqCallBack.dart';

class BaseViewModel with ChangeNotifier{


  PtrController refreshController;

  notifyPtr({bool needMove: true}) async{
   await refreshController?.requestRefresh(needMove: needMove);
  }

  List<CancelToken> cancelTokenList;

  CancelToken get cancelToken {
    var cancelToken = CancelToken();
    if (null == cancelTokenList)
      cancelTokenList = [];
    cancelTokenList.add(cancelToken);
    return cancelToken;
  }


  bool init = true;

  bool _disposed = false;

  Future<dynamic> postP(String url,Params params,ReqCallBack reqCallBack) {
    return  HttpUtils.instance().postP(url, params,cancelToken: cancelToken,callBack: reqCallBack);
  }

  Future<dynamic> postJ(String url,Params params,ReqCallBack reqCallBack) {
    return  HttpUtils.instance().postJ(url, params,cancelToken: cancelToken,callBack: reqCallBack);
  }

  Future<dynamic> getP(String url,Params params,ReqCallBack reqCallBack) {
    return   HttpUtils.instance().getP(url, params,cancelToken: cancelToken,callBack: reqCallBack);
  }

  Future<dynamic> getJ(String url,Params params,ReqCallBack reqCallBack) {
    return  HttpUtils.instance().getJ(url, params,cancelToken: cancelToken,callBack: reqCallBack);
  }


  bool requestTrue = true;

  void notifyUI(){
    requestTrue = true;
    notifyListeners();
  }


  //通信
  RxBusUtils rxBusUtils;

  void register<T>(int tag,Function(T data) dataCallback){
    if(null == rxBusUtils)
      rxBusUtils = RxBusUtils();
    rxBusUtils.register<T>(tag,dataCallback);
  }

  void sendMessage<T>(int tag,T data){
    if(null == rxBusUtils)
      rxBusUtils = RxBusUtils();
    rxBusUtils.post<T>(tag, data);
  }


  @override
  void notifyListeners() {
    if (!_disposed) {
      super.notifyListeners();
    }
  }

  @override
  void dispose() {
    rxBusUtils?.dispose();

    cancelTokenList?.forEach((element) {
      element?.cancel();
    });
    cancelTokenList?.clear();
    cancelTokenList = null;
    _disposed = true;
//    debugPrint('view_state_model dispose -->$runtimeType');
    super.dispose();
  }


  int companyIdParam;
  int orgIdParam;
  int employeeIdParam;


  bool isPartCheck = false;

  void notifyPartCheck(bool isPartCheck){
    this.isPartCheck = isPartCheck;
    if(!isPartCheck){
      companyIdParam = null;
      orgIdParam = null;
      employeeIdParam = null;
    }
    notifyListeners();
  }


}

/// [e]为错误类型 :可能为 Error , Exception ,String
/// [s]为堆栈信息
printErrorStack(e, s) {
}
