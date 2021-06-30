

class Config{


  //是否是开发
  static  bool DEVELOP = false;
  //是否是测试
  static  bool TEST = false;
  //是否是预发布环境
  static  bool PRE = false;

  /**
   * 开发环境
   */
  static final String JAVA_URL_DEV = "http://dev.crm.zcabc.com/crm/api/";
  static final String PHP_URL_DEV = "http://dev.crm.zcabc.com/crm/web/";//php
  static final String DEV_UPLOAD = "http://pre-crm.leadertable.com/crm/web/";//文件


  // public static final String HOST_DOWNLOAD =HOST + "/Attach/download";//下载
  // public static final String HOST_UPLOADING =HOST + "/Attach/upload";//上传

  /**
   * 测试环境
   **/
  static final String JAVA_URL_TEST = "http://pre-crm.leadertable.com/crm/api/";//java
  static final String PHP_URL_TEST = "http://pre-crm.leadertable.com/crm/web/";//php
  static final String TEST_UPLOAD = "http://pre-crm.leadertable.com/crm/web/";//文件

  /**
   * 预发布环境
   **/
  static final String JAVA_URL_PRE = "https://crmpre.zhongcaicloud.com/crm/api/";//java
  static final String PHP_URL_PRE  = "https://crmpre.zhongcaicloud.com/crm/web/";//php
  static final String UPLOAD_PRE  = "https://crmpre.zhongcaicloud.com/crm/web/";//文件

  /**
   * 正式环境
   **/
  static final String JAVA_URL = "https://crm.zhongcaicloud.com/crm/api/";//java
  static final String PHP_URL = "https://crm.zhongcaicloud.com/crm/web/";//php
  static final String UPLOAD = "https://crm.zhongcaicloud.com/crm/web/";//文件



  //是否是预发布环境
  static  bool isLogin = false;
  static  String  token = "";
}