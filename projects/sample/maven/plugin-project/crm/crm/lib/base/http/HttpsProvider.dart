

import 'dart:developer';

import 'package:common_utils/common_utils.dart';
import 'package:dio/dio.dart';
import 'package:flutter/cupertino.dart';
import '../http/ReqCallBack.dart';

import '../Config.dart';
import '../http/Params.dart';
import '../http/ValueUtil.dart';
import '../utils/BaseUtils.dart';

class HttpUtils {

  static String getJavaUrl() {
    if (Config.DEVELOP)
      return Config.JAVA_URL_DEV;
    if (Config.TEST)
      return Config.JAVA_URL_TEST;
    if (Config.PRE)
      return Config.JAVA_URL_PRE;
    return Config.JAVA_URL;
  }


  static String getPhpUrl() {
    if (Config.DEVELOP)
      return Config.PHP_URL_DEV;
    if (Config.TEST)
      return Config.PHP_URL_TEST;
    if (Config.PRE)
      return Config.PHP_URL_PRE;
    return Config.PHP_URL;
  }

  static String getUploadUrl() {
    if (Config.DEVELOP)
      return Config.DEV_UPLOAD;
    if (Config.TEST)
      return Config.TEST_UPLOAD;
    if (Config.PRE)
      return Config.UPLOAD_PRE;
    return Config.UPLOAD;
  }


  static final int CONNECT_TIMEOUT = 30000;

  Dio _dio;
  static HttpUtils _inst;

  HttpUtils._internal() {
    _dio = new Dio(BaseOptions(
//      baseUrl: getPhpUrl(),
      connectTimeout: CONNECT_TIMEOUT,
      receiveTimeout: CONNECT_TIMEOUT,
      sendTimeout: CONNECT_TIMEOUT,
      // 5s
      contentType: Headers.jsonContentType,
      responseType: ResponseType.json,
    ));

    // _dio.interceptors.clear();
    // if (Config.TEST || Config.DEVELOP || Config.PRE)
    //   _dio.interceptors.add(LogInterceptor(responseBody: true)); //开启请求日志
    // else
    //   _dio.interceptors.add(LogInterceptor(responseBody: false)); //开启请求日志

   // _dio.interceptors.add(DioInterceptors()); //开启请求日志

  }


  // 获取对象
  static HttpUtils init() {
    if (_inst == null) {
      // 使用私有的构造方法来创建对象
      _inst = HttpUtils._internal();
    }
    return _inst;
  }

  static HttpUtils instance() {
    if (_inst == null) {
      // 使用私有的构造方法来创建对象
      _inst = HttpUtils._internal();
    }
    return _inst;
  }

  GlobalKey<NavigatorState> _navigatorKey;
  GlobalKey<NavigatorState> get navigatorKey{
    if(_navigatorKey == null){
      _navigatorKey = GlobalKey<NavigatorState>();
    }
    return _navigatorKey;
  }


  static const String _POST = 'post';
  static const String _GET = 'get';


  request(String url, {Map<String,dynamic> data,String method, CancelToken cancelToken,
    String  baseUrl,
    ReqCallBack callBack,
  }) async {
    data = data ?? {};
    method = method ?? _POST;

    _dio.options.method = method;
    _dio.options.baseUrl = baseUrl?? getJavaUrl();
    _dio.options.contentType = Headers.jsonContentType;
    if(!BaseUtils.isEmpty(Config.token)) {
      _dio.options.headers.clear();
      _dio.options.headers.addAll({"access-token": Config.token});
    }
    // if (Config.TEST || Config.DEVELOP || Config.PRE) {
      print('请求url');
      print('${baseUrl}$url');
      print('请求参数');
      print('${BaseUtils.toJson(data)}');
    // }
    try {
      Response response = method == _POST ? await _dio.post(
          url, data: (method == _POST ? data : null),
          queryParameters: (method == _POST ? null : data), cancelToken: cancelToken):
      await _dio.get(url,queryParameters: data,cancelToken: cancelToken);

      // if (Config.TEST || Config.DEVELOP || Config.PRE) {
        print('----------data---------');
        log(response.toString());
      // }

      return ValueUtil.fromJson(response, callBack);
    }
    catch(e,s){
      if(s != null)
        debugPrint('error--->\n' + s.toString());
      callBack?.onReqError(e);
      callBack?.onReqCompleted();
      return -1;
    }

  }


  Future<dynamic> postFile(String url,Params data,{method,
    CancelToken cancelToken,
    ReqCallBack callBack,
    ProgressCallback onSendProgress,
    ProgressCallback onReceiveProgress,
  }) async {
    data = data ?? {};
    method = method ?? _POST;

    _dio.options.baseUrl = getUploadUrl();
    _dio.options.method = method;
    _dio.options.contentType = "multipart/form-data";

    if(!BaseUtils.isEmpty(Config.token)) {
      _dio.options.headers.clear();
      _dio.options.headers.addAll({"access-token": Config.token});
    }

    if (Config.TEST || Config.DEVELOP || Config.PRE) {
      print('请求url');
      print('${getUploadUrl()}$url');
      print('请求参数');
      // print('${BaseUtils.toJson(newMap)}');
    }
    FormData formData = data.map == null ? null : FormData.fromMap(data.map);

    try {
      Response response = await _dio.request(
        url, data: formData, cancelToken: cancelToken,
        onReceiveProgress: onReceiveProgress,
        onSendProgress: onSendProgress,);

      if (Config.TEST || Config.DEVELOP || Config.PRE) {
        print('----------data---------');
        log(response.toString());
      }

      return ValueUtil.fromJson(response, callBack);
    }
    catch(e){
      callBack?.onReqError(e);
      return -1;
    }


//    onSendProgress: (int sent, int total) {
////          print("上传进度：${sent / total * 100} %"); //取精度，如：56.45%
//    },);
//    return false;
  }



  get(String url,{Map<String, dynamic> data,options,CancelToken token}) async{
    print('get request path ------${url}-------请求参数${data}');
    print('------------');
    Response response;
    try{
      response = await _dio.get(url,queryParameters: data,options: options,cancelToken: token);
    }on DioError catch (e){
      print('请求失败---错误类型${e.type}--错误信息${e.message}');
    }

    return response.data.toString();
  }





  Future<dynamic> postP(String url,Params param,{ReqCallBack callBack,CancelToken cancelToken})  {
    return  request(url, data: param.map, cancelToken: cancelToken,callBack: callBack,
        baseUrl: getPhpUrl()
    );
  }

  Future<dynamic> postJ(String url,Params param,{ReqCallBack callBack,CancelToken cancelToken})  {
    return  request(url, data: param.map, cancelToken: cancelToken,callBack: callBack,
        baseUrl: getJavaUrl()
    );
  }


  Future<dynamic> getP(String url,Params param, {ReqCallBack callBack,CancelToken cancelToken})   {
    return  request(url, data: param.map, cancelToken: cancelToken,callBack: callBack, method: _GET,
        baseUrl: getPhpUrl()
    );
  }


  Future<dynamic> getJ(String url,Params param, {ReqCallBack callBack,CancelToken cancelToken})  {
    return  request(url, data: param.map, cancelToken: cancelToken,callBack: callBack, method: _GET,
        baseUrl: getJavaUrl()
    );
  }



}

class DioInterceptors extends InterceptorsWrapper {



  @override
  void onError(DioError err,
      ErrorInterceptorHandler handler) {
    print("ERROR[${err?.response?.statusCode}] => PATH: ${err?.requestOptions?.path}");
    super.onError(err,handler);
  }
}


