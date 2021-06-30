import 'package:crm/base/Config.dart';
import 'package:crm/base/http/Params.dart';
import 'package:crm/base/http/ReqCallBack.dart';
import 'package:crm/base/model/UserModel.dart';
import 'package:crm/base/utils/LoginUtils.dart';
import 'package:crm/base/widget/tab/BottomBarWidget.dart';

import '../../../base/Config.dart';
import '../../../base/widget/MyIndexStack.dart';

import '../../../base/provider/BaseViewModel.dart';

class MainVM extends BaseViewModel{

  var indexStackController = IndexStackController();
  var bottomCtr = BottomBarController();

  UserModel model;


  Future reqUserInfo(String userId,String platForm) {
    if(!requestTrue && Config.isLogin){
      return Future.value(1);
    }
    requestTrue = false;
    var params  = Params(type: 1);
    params.put("userId", userId??LoginUtils.instance().getUser()?.userId);
    params.put("sourcePath","oa");
    var req = ReqCallBack(
      isToast: false,
      onSuccess: (result){
        var model = UserModel.fromJson(result);
        LoginUtils.instance().intoLogin(model);
      },
    );
    return postJ("account/info", params,req);
    // return postJ("account/login", params,req);
  }


  @override
  void dispose() {
    indexStackController?.dispose();
    super.dispose();
  }
}
