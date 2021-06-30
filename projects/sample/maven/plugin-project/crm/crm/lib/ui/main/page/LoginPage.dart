
import 'package:crm/base/router/RouterHelper.dart';
import 'package:crm/base/widget/EditLayout.dart';
import 'package:crm/base/widget/EditWidget.dart';
import 'package:crm/base/widget/TextView.dart';
import 'package:crm/ui/main/viewmodel/LoginVM.dart';
import 'package:flutter/material.dart';

import '../../../base/view/BasePage.dart';


class LoginPage extends StatefulWidget {
  @override
  _LoginPageState createState() => _LoginPageState();
}

class _LoginPageState extends BasePageState<LoginPage,LoginVM> {

  @override
  LoginVM getViewModel() =>  LoginVM();

  @override
  Widget getView(BuildContext context) {

    return Center(
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          EditWidget(hint: "输入账号",
            padding: EdgeInsets.only(left: 20,right: 20),
            controller: viewModel.accountCtr,
          ),
          SizedBox(height: 10,),
          EditWidget(hint: "输入密码", padding: EdgeInsets.only(left: 20,right: 20),
            controller: viewModel.pwdCtr,
          ),
          FlatButton(
            height: 34,
              onPressed: (){
                viewModel.login((model){
                  RouterHelper.buildMain(context,model.userId?.toString()??"");
                });
                // RouterHelper.buildMain(context,"6307");
                // RouterHelper.buildMain(context,"7047");
                // RouterHelper.buildMain(context,"292");
                // RouterHelper.buildMain(context,"5812");
          }, child: TextView("登陆",size: 14,))
        ],
      ),
    );
  }



}

