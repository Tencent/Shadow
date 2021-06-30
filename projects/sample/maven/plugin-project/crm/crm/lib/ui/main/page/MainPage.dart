
import 'package:crm/base/Config.dart';
import 'package:crm/base/provider/MyFutureBuilder.dart';
import 'package:crm/base/provider/StateWidget.dart';
import 'package:crm/base/rxbus/RxCodes.dart';
import 'package:crm/base/utils/BaseUtils.dart';
import 'package:crm/base/utils/ImageHelper.dart';
import 'package:crm/base/utils/LoginUtils.dart';
import 'package:crm/base/widget/TextView.dart';
import 'package:crm/common/MyColors.dart';
import 'package:flutter/material.dart';

import '../../../base/provider/StateWidget.dart';
import '../../../base/provider/StateWidget.dart';
import '../../../base/view/BasePage.dart';
import '../../../base/widget/MyIndexStack.dart';
import '../../../base/widget/tab/BottomBarWidget.dart';
import '../../../ui/main/viewmodel/MainVM.dart';

import '../../../base/extension/WidgetExt.dart';
import '../../../base/extension/ListExt.dart';

class MainPage extends StatefulWidget {


  final String userId;
  final String platform;

  const MainPage({Key key, this.userId,this.platform:"ios", }) : super(key: key);

  @override
  _MainPageState createState() => _MainPageState();
}

class _MainPageState extends BasePageState<MainPage,MainVM> {

  @override
  MainVM getViewModel() =>  MainVM();


  @override
  bool get isScaffold => true;

  List<String> pageList =[];
  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    // LoginUtils.instance().logout();
  }

  @override
  Widget getView(BuildContext context) {

        return ViewStateEmptyWidget(title: "用户暂无权限",isBack:true,leftCallback: (){
          onExit(widget.platform);
        });
  }

  @override
  Widget getBottomNavigationBar(BuildContext context) {
    viewModel?.bottomCtr?.setLimit(pageList.isNotEmpty);
    return SafeArea(
      child: BottomBarWidget(controller: viewModel?.bottomCtr,callback: (index){
        if(index == 2)
          sendMessage(RxCodes.code_7, 1);
        FocusManager.instance.primaryFocus.unfocus();
        viewModel.indexStackController.setIndex(index);

      },),

    );
  }

  @override
  void dispose() {
    // LoginUtils.instance().logout();
    super.dispose();
  }

}
