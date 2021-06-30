
import 'package:flutter/gestures.dart';
import 'package:flutter/material.dart';
import 'package:pull_to_refresh/pull_to_refresh.dart';
import '../../controller/BaseController.dart';
import '../../provider/StateWidget.dart';



class PtrWidget extends StatefulWidget {

  final bool enablePullUp;
  final bool enablePullDown;
  final bool enableTwoLevel;
  final bool initFuture;
  final Future Function() onRefresh;
  final Future Function() onLoading;
  final Widget Function(BuildContext context) builder;
  final PtrController controller;


  const PtrWidget({Key key,
    this.controller,
    this.onRefresh,
    this.onLoading,
    this.enablePullUp:false,
    this.enablePullDown:true,
    this.enableTwoLevel:false,
    this.initFuture:true,
    this.builder
  }
      ) : super(key: key);


  @override
  _PtrWidgetState createState() => _PtrWidgetState();
}

class _PtrWidgetState extends State<PtrWidget> {

   PtrController controller;

  @override
  void initState() {
    controller = widget.controller??PtrController();
    controller?.setNotifyWidget((){
      if(mounted){
        setState(() {

        });
      }
    });
    if(widget.initFuture)
    initData();
    super.initState();
  }

  void initData() async{
    controller.init = true;
    var code = await widget.onRefresh?.call();
    controller.state = code;
  }

  @override
  Widget build(BuildContext context) {

    return SmartRefresher(
      // physics: AlwaysScrollableScrollPhysics(),
      enablePullDown: widget.enablePullDown,
      // dragStartBehavior: DragStartBehavior.down,
      // primary:true,
      enableTwoLevel:widget.enableTwoLevel,
      enablePullUp: widget.enablePullUp,
      controller: controller,
      onRefresh: () async{
        controller.init = false;
        var code = await widget.onRefresh?.call();
        controller.state = code;
      },
      onLoading: () async{
        var code = await widget.onLoading?.call();
        controller.state = code;
      },
      child:child,

    );
  }

  Widget get child {
    if(!controller.init){
      return widget.builder(context);
    }
    switch(controller.state){
      case 0:
        return ViewStateBusyWidget();
      case 1:{
          controller.init = false;
          return widget.builder(context);
        }
      case -1:
        return ViewStateErrorWidget(onPressed:initData);
    }
    return ViewStateFailedWidget(onPressed: initData);

  }

  @override
  void dispose() {
    controller?.dispose();
    super.dispose();
  }
}

class PtrController extends RefreshController with BaseController{

  bool init = true;

  PtrController({bool initialRefresh: false}):super(initialRefresh: initialRefresh);

  static final int waiting = 0;
  static final int done = 1;
  static final int hasError = -1;

  int _state = waiting;

  int get state => _state;

  set state(int value) {
    _state = value;
    notifyWidget?.call();
  }
}

