import 'package:crm/base/http/Params.dart';
import 'package:crm/base/http/ReqCallBack.dart';
import 'package:crm/base/model/UserModel.dart';
import 'package:crm/base/provider/BaseViewModel.dart';
import 'package:crm/base/utils/LoginUtils.dart';
import 'package:crm/base/utils/ToastUtil.dart';
import 'package:crm/base/widget/EditLayout.dart';


class LoginVM extends BaseViewModel{


  var accountCtr = EditController(text: "wyw");
  // var accountCtr = EditController(text: "bc");
  var pwdCtr = EditController(text: "123456");

  void login(Function(UserModel model) callback) async{
    if(accountCtr.isEmpty()){
      ToastUtil.showToast("账号不能为空");
      return;
    }
    if(pwdCtr.isEmpty()){
      ToastUtil.showToast("密码不能为空");
      return;
    }
    ToastUtil.showDialog();
    var params  = Params(type: 1);
    params.put("userName", accountCtr.text);
    params.put("password", pwdCtr.text);
    var req = ReqCallBack(
      onSuccess: (result){
        var model = UserModel.fromJson(result);
        LoginUtils.instance().intoLogin(model);
        callback?.call(model);
      },
    );
    await postJ("account/login", params,req);
  }



}
