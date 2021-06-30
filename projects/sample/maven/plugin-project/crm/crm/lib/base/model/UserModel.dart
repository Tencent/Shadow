/// id : 33
/// delete_tag : 0
/// createTime : "2020-09-15 19:12:31"
/// updateTime : "2021-02-05 10:37:41"
/// idStr : "33"
/// userName : "wyw"
/// sName : "wyw"
/// name : "吴怡文"
/// password : "e10adc3949ba59abbe56e057f20f883e"
/// userId : 203
/// postName : "科员"
/// orgId : 118
/// companyId : 118
/// gjId : 81
/// gjName : "杭州广角"
/// roleId : 3
/// perId : 0
/// orgIds : null
/// headImgId : 90
/// deviceId : null
/// clientId : null
/// telephone : "17376561620"
/// lastLogin : "2021-02-05 10:37:41"
/// token : ""
/// report : null
/// status : 0
/// sign : "个性签名"
/// headImg : {"id":null,"path":"http://pre-crm.leadertable.com/crm/Public/Uploads/2018-01-24/_word_head_be83ee5bd20d969631ab62b11dad2f9d.jpg","bigPath":"http://pre-crm.leadertable.com/crm/Public/Uploads/2018-01-24/_word_head_be83ee5bd20d969631ab62b11dad2f9d.jpg","middlePath":"http://pre-crm.leadertable.com/crm/Public/Uploads/2018-01-24/_word_head_be83ee5bd20d969631ab62b11dad2f9d.jpg"}
/// orgName : "浙江中财担保有限公司"
/// companyName : "浙江中财担保有限公司"
/// roleName : "普通用户"

class UserModel {
  int _id;
  int _deleteTag;
  String _createTime;
  String _updateTime;
  String _idStr;
  String _userName;
  String _sName;
  String _name;
  String _password;
  int _userId;
  String _postName;
  int _orgId;
  int _companyId;
  int _gjId;
  String _gjName;
  int _roleId;
  int _perId;
  dynamic _orgIds;
  int _headImgId;
  dynamic _deviceId;
  dynamic _clientId;
  String _telephone;
  String _lastLogin;
  String _token;
  dynamic _report;
  int _status;
  String _sign;
  HeadImg _headImg;
  String _orgName;
  String _companyName;
  String _roleName;

  int get id => _id;
  int get deleteTag => _deleteTag;
  String get createTime => _createTime;
  String get updateTime => _updateTime;
  String get idStr => _idStr;
  String get userName => _userName;
  String get sName => _sName;
  String get name => _name;
  String get password => _password;
  int get userId => _userId;
  String get postName => _postName;
  int get orgId => _orgId;
  int get companyId => _companyId;
  int get gjId => _gjId;
  String get gjName => _gjName;
  int get roleId => _roleId;
  int get perId => _perId;
  dynamic get orgIds => _orgIds;
  int get headImgId => _headImgId;
  dynamic get deviceId => _deviceId;
  dynamic get clientId => _clientId;
  String get telephone => _telephone;
  String get lastLogin => _lastLogin;
  String get token => _token;
  dynamic get report => _report;
  int get status => _status;
  String get sign => _sign;
  HeadImg get headImg => _headImg;
  String get orgName => _orgName;
  String get companyName => _companyName;
  String get roleName => _roleName;


  UserModel({
      int id,
      int deleteTag,
      String createTime,
      String updateTime,
      String idStr,
      String userName,
      String sName,
      String name,
      String password,
      int userId,
      String postName,
      int orgId,
      int companyId,
      int gjId,
      String gjName,
      int roleId,
      int perId,
      dynamic orgIds,
      int headImgId,
      dynamic deviceId,
      dynamic clientId,
      String telephone,
      String lastLogin,
      String token,
      dynamic report,
      int status,
      String sign,
      HeadImg headImg,
      String orgName,
      String companyName,
      String roleName}){
    _id = id;
    _deleteTag = deleteTag;
    _createTime = createTime;
    _updateTime = updateTime;
    _idStr = idStr;
    _userName = userName;
    _sName = sName;
    _name = name;
    _password = password;
    _userId = userId;
    _postName = postName;
    _orgId = orgId;
    _companyId = companyId;
    _gjId = gjId;
    _gjName = gjName;
    _roleId = roleId;
    _perId = perId;
    _orgIds = orgIds;
    _headImgId = headImgId;
    _deviceId = deviceId;
    _clientId = clientId;
    _telephone = telephone;
    _lastLogin = lastLogin;
    _token = token;
    _report = report;
    _status = status;
    _sign = sign;
    _headImg = headImg;
    _orgName = orgName;
    _companyName = companyName;
    _roleName = roleName;
}

  UserModel.fromJson(dynamic json) {
    _id = json["id"];
    _deleteTag = json["delete_tag"];
    _createTime = json["createTime"];
    _updateTime = json["updateTime"];
    _idStr = json["idStr"];
    _userName = json["userName"];
    _sName = json["sName"];
    _name = json["name"];
    _password = json["password"];
    _userId = json["userId"];
    _postName = json["postName"];
    _orgId = json["orgId"];
    _companyId = json["companyId"];
    _gjId = json["gjId"];
    _gjName = json["gjName"];
    // _roleId = json["roleId"];
    _roleId = json["realRoleId"];
    _perId = json["perId"];
    _orgIds = json["orgIds"];
    _headImgId = json["headImgId"];
    _deviceId = json["deviceId"];
    _clientId = json["clientId"];
    _telephone = json["telephone"];
    _lastLogin = json["lastLogin"];
    _token = json["token"];
    _report = json["report"];
    _status = json["status"];
    _sign = json["sign"];
    _headImg = json["headImg"] != null ? HeadImg.fromJson(json["headImg"]) : null;
    _orgName = json["orgName"];
    _companyName = json["companyName"];
    _roleName = json["roleName"];
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    map["id"] = _id;
    map["delete_tag"] = _deleteTag;
    map["createTime"] = _createTime;
    map["updateTime"] = _updateTime;
    map["idStr"] = _idStr;
    map["userName"] = _userName;
    map["sName"] = _sName;
    map["name"] = _name;
    map["password"] = _password;
    map["userId"] = _userId;
    map["postName"] = _postName;
    map["orgId"] = _orgId;
    map["companyId"] = _companyId;
    map["gjId"] = _gjId;
    map["gjName"] = _gjName;
    map["roleId"] = _roleId;
    map["perId"] = _perId;
    map["orgIds"] = _orgIds;
    map["headImgId"] = _headImgId;
    map["deviceId"] = _deviceId;
    map["clientId"] = _clientId;
    map["telephone"] = _telephone;
    map["lastLogin"] = _lastLogin;
    map["token"] = _token;
    map["report"] = _report;
    map["status"] = _status;
    map["sign"] = _sign;
    if (_headImg != null) {
      map["headImg"] = _headImg.toJson();
    }
    map["orgName"] = _orgName;
    map["companyName"] = _companyName;
    map["roleName"] = _roleName;
    return map;
  }

}

/// id : null
/// path : "http://pre-crm.leadertable.com/crm/Public/Uploads/2018-01-24/_word_head_be83ee5bd20d969631ab62b11dad2f9d.jpg"
/// bigPath : "http://pre-crm.leadertable.com/crm/Public/Uploads/2018-01-24/_word_head_be83ee5bd20d969631ab62b11dad2f9d.jpg"
/// middlePath : "http://pre-crm.leadertable.com/crm/Public/Uploads/2018-01-24/_word_head_be83ee5bd20d969631ab62b11dad2f9d.jpg"

class HeadImg {
  dynamic _id;
  String _path;
  String _bigPath;
  String _middlePath;

  dynamic get id => _id;
  String get path => _path;
  String get bigPath => _bigPath;
  String get middlePath => _middlePath;

  HeadImg({
      dynamic id, 
      String path, 
      String bigPath, 
      String middlePath}){
    _id = id;
    _path = path;
    _bigPath = bigPath;
    _middlePath = middlePath;
}

  HeadImg.fromJson(dynamic json) {
    _id = json["id"];
    _path = json["path"];
    _bigPath = json["bigPath"];
    _middlePath = json["middlePath"];
  }

  Map<String, dynamic> toJson() {
    var map = <String, dynamic>{};
    map["id"] = _id;
    map["path"] = _path;
    map["bigPath"] = _bigPath;
    map["middlePath"] = _middlePath;
    return map;
  }

}