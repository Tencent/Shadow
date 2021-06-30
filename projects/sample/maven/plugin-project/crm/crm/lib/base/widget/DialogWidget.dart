import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../utils/CstColors.dart';

class DialogWidget extends StatelessWidget {


  final Widget child;
  final Widget Function(DialogController controller) builder;
  final DialogController controller;

  const DialogWidget({Key key,
    @required this.child,
    @required this.builder,
    this.controller,
  }) : super(key: key);



  @override
  Widget build(BuildContext context) {
    return WillPopScope(
      onWillPop: () async{
        if(!controller.isBack()) {
          controller?.dismiss();
          return false;
        }
        return true;
      },
      child: InkWell(
        child: child,
        onTap: (){
          showDialog(context);
        },
      ),
    );
  }



  void showDialog(BuildContext context){
    final OverlayState overlayState = Overlay.of(context);

    var overlayEntry = createSelectPopupWindow();

    overlayState.insert(overlayEntry);

    controller.overlayEntryList.add(overlayEntry);

  }

  OverlayEntry createSelectPopupWindow() {

    OverlayEntry overlayEntry = new OverlayEntry(
        builder: (context) {

          return ChangeNotifierProvider.value(
              value: controller,
              child: Consumer<DialogController>(
                builder: (context,model,child){
                  return Material(
                    color: CstColors.bgColor,
                    child: Center(
                      child: builder(controller),
                    ),
                  );
                },
                child: null,
              ),
            );

        });
    return overlayEntry;
  }

}

class DialogController extends ChangeNotifier{

  List<OverlayEntry> overlayEntryList = [];

  bool isBack() => overlayEntryList.isEmpty;


  dismiss(){
    overlayEntryList?.forEach((element) {
      element.remove();
    });
    overlayEntryList?.clear();
  }

}

