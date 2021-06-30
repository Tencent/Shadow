import 'package:dio/dio.dart';
import '../http/BaseModel.dart';
import '../http/ReqCallBack.dart';
import '../utils/ToastUtil.dart';

class ValueUtil {


  static int fromJson(Response response, ReqCallBack callBack) {

    int code = 1;

    if(response.statusCode != 200){//服务器错误
      code = -1;
      if(callBack?.isToast == true)
        ToastUtil.showToast(response.statusMessage);
      callBack?.onReqError(response.statusMessage);

    }
    else{
      BaseModel model = BaseModel.fromJson(response.data);

      if(model.meta.code == 200){
        var returnCode = -2;
        if(model.data.returnCode != null && model.data.returnCode.isNotEmpty){
          returnCode = int.parse(model.data.returnCode);
        }
        if(returnCode == 0) {
          callBack?.onReqSuccess(model.data.result);
        }
        else{
          code = returnCode;
          //逻辑业务处理提示
          if(callBack?.isToast == true)
            ToastUtil.showToast(model.data.msg);
          callBack?.onReqFailed(returnCode,model.data.msg);
        }
      }
      else{
        //其他业务问题
        code = 2000;
        if(callBack?.isToast == true)
          ToastUtil.showToast(model.meta.msg);
        callBack?.onReqFailed(model.meta.code,model.meta.msg);
      }
    }
    callBack?.onReqCompleted();
    return code;
  }

}
