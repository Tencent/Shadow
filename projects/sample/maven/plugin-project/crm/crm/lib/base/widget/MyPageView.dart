

import 'package:flutter/material.dart';
import '../controller/BaseController.dart';
import '../controller/BaseController.dart';
import '../provider/ProviderWidget.dart';

class MyPageView extends StatelessWidget {

  final MyPageViewController controller;
  final List<Widget> children;

  const MyPageView({Key key,
    @required this.controller,
    @required this.children
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {

    var _controller = controller??MyPageViewController();
    return ProviderWidget<MyPageViewController>(
        builder: (context,model,child){
          return children[model.index];
        },
        model: _controller);
  }
}

class MyPageViewController  with BaseController,ChangeNotifier{


}

