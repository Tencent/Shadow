
import 'dart:convert';

import '../Config.dart';
import '../cache/Cahes.dart';
import '../model/UserModel.dart';
import '../utils/BaseUtils.dart';

class LoginUtils {
  static LoginUtils _inst;

  static LoginUtils instance(){
    if(null == _inst)
      _inst = LoginUtils();

    return _inst;
  }

  UserModel mUserModel;

  void init() {
    var userInfo = Caches.getVar().getString(Caches.USER);
    if(BaseUtils.isEmpty(userInfo)){
      return;
    }
    mUserModel = UserModel.fromJson(json.decode(userInfo));
    Config.isLogin = true;
    Config.token = mUserModel?.token;
  }


  UserModel getUser(){
    var t =  mUserModel;
    return t;
  }

  bool isPart(){
    return mUserModel?.roleId != 3;
  }

  //是否有下属跑动记录   1.我的下属记录 2.我的下属记录 和我的跑动纪律 3.我的跑动记录
  bool isRunningNext(){
    return mUserModel?.roleId == 2;
  }

  bool isCEO(){ //
    return mUserModel?.roleId == 1;
  }

  //是否可以新增编辑 客户联系人
  bool isAddOrEditContact(){ //
    return !(mUserModel?.companyId == 257 || mUserModel?.companyId == 1);
  }

  void logout(){
    Caches.getVar().remove(Caches.USER);
    mUserModel = null;
    Config.isLogin = false;
    Config.token = "";

  }



  void intoLogin(UserModel model) {

    mUserModel = model;
    Config.isLogin = true;
    Config.token = model?.token;

    var value = json.encode(model.toJson());
    Caches.getVar().put(Caches.USER, value);
  }


}
