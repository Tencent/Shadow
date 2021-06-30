


import 'package:crm/base/router/RouterHelper.dart';
import 'package:crm/base/utils/ChannelPluginUtils.dart';
import 'package:crm/base/utils/ImageHelper.dart';
import 'package:crm/base/utils/LaunchUtils.dart';
import 'package:crm/base/widget/MyAppBar.dart';
import 'package:crm/base/widget/TextView.dart';
import 'package:crm/common/MyColors.dart';
import 'package:crm/common/widget/HeaderWidget.dart';
import 'package:crm/common/widget/SuperCheckBox.dart';
import 'package:fluro/fluro.dart';
import 'package:flutter/material.dart';
import 'package:flutter/services.dart';
import 'package:lifecycle/lifecycle.dart';
import '../provider/BaseViewModel.dart';
import 'package:provider/provider.dart';

import '../utils/CstColors.dart';
import '../utils/LoginUtils.dart';
import '../view/BaseMixin.dart';
import '../../base/extension/WidgetExt.dart';

abstract class BasePageState<T extends StatefulWidget,K extends BaseViewModel> extends State<T> with BaseMixin ,AutomaticKeepAliveClientMixin{


  K viewModel;

//  @override
  Color get bgColor => null;
  bool get isScaffold => true;

  bool autoDispose(){
    return true;
  }


  @override
  void initState() {
    viewModel = getViewModel();
    super.initState();
  }


  Map get arguments => argumentOf(context);


  var isInit = true;

  void initValue(){

  }
  @override
  Widget build(BuildContext context) {
    if(isInit) {
      initValue();
      isInit = false;
    }


    var _provider = viewModel != null ? ChangeNotifierProvider<K>.value(
      value: viewModel,
      // builder: (context,child){
      //   Provider.of<K>(context);
      //   return getView(context);
      // },
      child: Consumer<K>(
        builder: (context,model,child){
          return getView(context);
        },
      ),
    ):  getView(context);


    if(!isScaffold)
      return _provider;

    return Scaffold(
      appBar: getAppBar(context),
      backgroundColor: bgColor,
      body: _provider,
      bottomNavigationBar: getBottomNavigationBar(context),
    );
  }


  @override
  void dispose() {
    if(autoDispose())
      viewModel?.dispose();
    _appBarCtr?.dispose();
    super.dispose();
  }


  K getViewModel();


  HeaderController topController;

  HeaderController getTopController(){
    if(null == topController)
      topController = HeaderController();
    return topController;
  }

  bool get isUserHeader => false;

  AppBarController _appBarCtr;


  AppBarController get appBarCtr {
    if(null == _appBarCtr)
      _appBarCtr = AppBarController(leftFunc: (){
        RouterHelper.pop(context);
      });
    return _appBarCtr;
  }



  Widget getAppBar(BuildContext context) {
    if(!isUserHeader){
      return null;
    }
    return MyAppBar(controller: appBarCtr,);
  }


  void onExit(String platform){
      ChannelPluginUtils.closedApp();
  }

  AppBar createHeaderWidget({String backIcon: "ic_back.png",Widget titleChild,
    Color backgroundColor,Function() leftCallback,String title,List<Widget> actions}){

    return AppBar(
      centerTitle: true,
      backgroundColor: backgroundColor,
      shadowColor: Colors.transparent,
      leadingWidth: backIcon.isEmpty ? 0 : 44,
      leading: backIcon.isEmpty ? null : Container(
        margin: EdgeInsets.only(left: 16),
        child: ImageHelper.buildImage(backIcon,width: 28,height: 28,type: 1,fit: BoxFit.fitWidth).buildInkWell(() {
          if(null == leftCallback)
            RouterHelper.pop(context);
          else
            leftCallback?.call();
        }),
      ),
      title: titleChild??TextView(title,size: 18, weight: FontWeight.bold, color: MyColors.cl_1F2736,),
      actions: actions,
    );
  }

  Widget getBottomNavigationBar(BuildContext context) {
    return null;
  }


  @override
  bool get wantKeepAlive => false;


  void registerRxBus<T>(int tag,Function(T data) dataCallback){
    viewModel?.register<T>(tag,dataCallback);
  }

  void sendMessage<T>(int tag,T data){
    viewModel?.sendMessage<T>(tag,data);
  }



}

abstract class LifecyclePageState<T extends StatefulWidget,K extends BaseViewModel> extends BasePageState<T,K> with LifecycleAware, LifecycleMixin{

  @override
  void onLifecycleEvent(LifecycleEvent event) {
    // print("------ Login  event ${event.toString()}");
    switch(event){
      case LifecycleEvent.push://第一次跳转界面调用
        onResume();
        break;
      case LifecycleEvent.visible:
        break;
      case LifecycleEvent.active://home 后再打开页面 active
        onResume();
        break;
      case LifecycleEvent.inactive: //home 键  先调用 inactive - > invisible
        inactive();
        break;
      case LifecycleEvent.invisible:
        onPause();
        break;
      case LifecycleEvent.pop:
        break;
    }
  }

  void inactive(){
    FocusManager.instance.primaryFocus.unfocus();
  }

  void onResume();

  void onPause();

}
