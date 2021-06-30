
import 'package:flutter/material.dart';
import '../controller/BaseController.dart';

import 'package:dio/dio.dart';
import '../rxbus/rx.dart';
import '../http/HttpsProvider.dart';

import '../http/Params.dart';
import '../http/ReqCallBack.dart';



abstract class BaseWidgetState<T extends StatefulWidget,K extends BaseWidgetController> extends State<T>  with AutomaticKeepAliveClientMixin{


  K controller;

  bool autoDispose(){
    return true;
  }


  @override
  void initState() {
    controller = getController();
    controller.setNotifyWidget((){
      setState(() {
      });
    });
    super.initState();
  }


  @override
  void dispose() {
    if(autoDispose())
      controller?.dispose();
    super.dispose();
  }


  K getController();



  @override
  bool get wantKeepAlive => false;


  void registerRxBus<T>(int tag,Function(T data) dataCallback){
    controller?.register<T>(tag,dataCallback);
  }

  void sendMessage<T>(int tag,T data){
    controller?.post<T>(tag,data);
  }
}

class BaseWidgetController with BaseController {


  List<CancelToken> cancelTokenList;

  CancelToken get cancelToken {
    var cancelToken = CancelToken();
    if (null == cancelTokenList)
      cancelTokenList = [];
    cancelTokenList.add(cancelToken);
    return cancelToken;
  }


  Future<dynamic> postP(String url, Params params, ReqCallBack reqCallBack) {
    return HttpUtils.instance().postP(
        url, params, cancelToken: cancelToken, callBack: reqCallBack);
  }

  Future<dynamic> postJ(String url, Params params, ReqCallBack reqCallBack) {
    return HttpUtils.instance().postJ(
        url, params, cancelToken: cancelToken, callBack: reqCallBack);
  }

  Future<dynamic> getP(String url, Params params, ReqCallBack reqCallBack) {
    return HttpUtils.instance().getP(
        url, params, cancelToken: cancelToken, callBack: reqCallBack);
  }

  Future<dynamic> getJ(String url, Params params, ReqCallBack reqCallBack) {
    return HttpUtils.instance().getJ(
        url, params, cancelToken: cancelToken, callBack: reqCallBack);
  }

  Future<dynamic> get(String url, Params params) {
    return HttpUtils.instance().get(
        url, data: params.map, token: cancelToken);
  }

  Future<dynamic> getByMap(String url, Map<String, dynamic> map) {
    return HttpUtils.instance().get(
        url, data: map, token: cancelToken);
  }

  Future<dynamic> postFile(String url, Params params,ReqCallBack reqCallBack) {
    return HttpUtils.instance().postFile(url,params,callBack: reqCallBack, cancelToken: cancelToken);
  }


  //通信
  RxBusUtils rxBusUtils;

  void register<T>(int tag, Function(T data) dataCallback) {
    if (null == rxBusUtils)
      rxBusUtils = RxBusUtils();
    rxBusUtils.register<T>(tag, dataCallback);
  }

  void post<T>(int tag, T data) {
    if (null == rxBusUtils)
      rxBusUtils = RxBusUtils();
    rxBusUtils.post<T>(tag, data);
  }


  @override
  void dispose() {
    rxBusUtils?.dispose();

    cancelTokenList?.forEach((element) {
      element?.cancel();
    });
    cancelTokenList?.clear();
    cancelTokenList = null;
    super.dispose();
  }
}





