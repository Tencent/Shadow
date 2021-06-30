import 'package:crm/base/painting/MyDecoration.dart';
import 'package:crm/base/widget/SuperTextView.dart';
import 'package:crm/base/widget/TextView.dart';
import 'package:crm/common/MyColors.dart';
import 'package:flutter/material.dart';

class AnimDialog {


  static void showCustomer(BuildContext context,{
    String content,String leftText:"取消",String rightText:"确定",
    Function() leftCallback,Function(Function() f) rightCallback
  }) async{

    await showAnimationDialog(context: context,
        barrierDismissible:true,
        transitionType: TransitionDialogType.inFromRight,
        builder: (context){
          return Material(
              color: Colors.transparent,
              child:Center(
                child: Container(
                  decoration: MyDecoration.buildShape(
                      solid: MyColors.cl_white,
                      radius: 14
                  ),
                  width: 296,
                  constraints: BoxConstraints(
                      minHeight: 168
                  ),
                  child: Material(
                      color: Colors.transparent,
                    child: Column(
                      mainAxisSize: MainAxisSize.min,
                      children: [
                        Container(
                          padding: EdgeInsets.only(top: 50,bottom: 40),
                          child:  TextView(content,size: 16,color:MyColors.cl_1F2736,weight: FontWeight.bold,),),
                        Row(
                          children: [
                            Spacer(),
                            SuperTextView(leftText,size: 16,
                              height: 40,
                              width: 120,
                              alignment: Alignment.center,
                              stroke: MyColors.cl_DEE4EA,
                              color: MyColors.cl_161722,
                              radius: 4.0,onTap: (){
                                leftCallback?.call();
                                Navigator.of(context).pop();
                              },),
                            SizedBox(width: 12,),
                            SuperTextView(rightText,solid: MyColors.cl_01C6AC,size: 16,
                              height: 40,
                              width: 120,
                              alignment: Alignment.center,
                              color: Colors.white, radius: 4.0,onTap: (){
                                rightCallback?.call((){
                                  Navigator.of(context).pop();
                                });
                              },),
                            Spacer(),
                          ],
                        ),
                        SizedBox(height: 20,)
                      ],
                    ),
                  )
                ),
              ));
        });
  }
}




enum TransitionDialogType {
  inFromLeft,
  inFromRight,
  inFromTop,
  inFromBottom,
  scale,
  fade,
  rotation,
  size,
}

Future showAnimationDialog({
  @required
  BuildContext context,
  bool barrierDismissible = false,
  WidgetBuilder builder,
  bool useRootNavigator = true,
  Alignment alignment = Alignment.topRight,
  RouteSettings routeSettings,
  TransitionDialogType transitionType,
}) {
  assert(useRootNavigator != null);
  assert(debugCheckHasMaterialLocalizations(context));

  final ThemeData theme = Theme.of(context);
  return showGeneralDialog(
    context: context,
    pageBuilder: (BuildContext buildContext, Animation<double> animation, Animation<double> secondaryAnimation) {
      final Widget pageChild = Builder(builder: builder);
      // return SafeArea(
      //   child: Builder(builder: (BuildContext context) {
      //     return theme != null ? Theme(data: theme, child: pageChild) : pageChild;
      //   }),
      // );
      return Align(
          alignment: alignment,
          child: pageChild
      );
    },
    barrierDismissible: barrierDismissible,
    barrierLabel: MaterialLocalizations.of(context).modalBarrierDismissLabel,
    barrierColor: Colors.black54,
    transitionDuration: const Duration(milliseconds: 200),
    transitionBuilder: (context, animation1, animation2, child) {
      return _buildDialogTransitions(context, animation1, animation2, child, transitionType);
    },
    useRootNavigator: useRootNavigator,
    routeSettings: routeSettings,
  );
}

Widget _buildDialogTransitions(
    BuildContext context, Animation<double> animaton1, Animation<double> secondaryAnimation, Widget child, TransitionDialogType type) {
  if (type == TransitionDialogType.fade) {
    // 渐变效果
    return FadeTransition(
      // 从0开始到1
      opacity: Tween(begin: 0.0, end: 1.0).animate(CurvedAnimation(
        // 传入设置的动画
        parent: animaton1,
        // 设置效果，快进漫出   这里有很多内置的效果
        curve: Curves.fastOutSlowIn,
      )),
      child: child,
    );
  } else if (type == TransitionDialogType.scale) {
    return ScaleTransition(
      scale: Tween(begin: 0.0, end: 1.0).animate(CurvedAnimation(parent: animaton1, curve: Curves.fastOutSlowIn)),
      child: child,
    );
  } else if (type == TransitionDialogType.rotation) {
    // 旋转加缩放动画效果
    return RotationTransition(
      turns: Tween(begin: 0.0, end: 1.0).animate(CurvedAnimation(
        parent: animaton1,
        curve: Curves.fastOutSlowIn,
      )),
      child: ScaleTransition(
        scale: Tween(begin: 0.0, end: 1.0).animate(CurvedAnimation(parent: animaton1, curve: Curves.fastOutSlowIn)),
        child: child,
      ),
    );
  } else if (type == TransitionDialogType.inFromLeft) {
    // 左右滑动动画效果
    return SlideTransition(
      position: Tween<Offset>(begin: Offset(-1.0, 0.0), end: Offset(0.0, 0.0))
          .animate(CurvedAnimation(parent: animaton1, curve: Curves.fastOutSlowIn)),
      child: child,
    );
  } else if (type == TransitionDialogType.inFromRight) {
    return SlideTransition(
      position: Tween<Offset>(begin: Offset(1.0, 0.0), end: Offset(0.0, 0.0))
          .animate(CurvedAnimation(parent: animaton1, curve: Curves.fastOutSlowIn)),
      child: child,
    );
  } else if (type == TransitionDialogType.inFromTop) {
    return SlideTransition(
      position: Tween<Offset>(begin: Offset(0.0, -1.0), end: Offset(0.0, 0.0))
          .animate(CurvedAnimation(parent: animaton1, curve: Curves.fastOutSlowIn)),
      child: child,
    );
  } else if (type == TransitionDialogType.inFromBottom) {
    return SlideTransition(
      position: Tween<Offset>(begin: Offset(0.0, 1.0), end: Offset(0.0, 0.0))
          .animate(CurvedAnimation(parent: animaton1, curve: Curves.fastOutSlowIn)),
      child: child,
    );
  } else if (type == TransitionDialogType.size) {
    return SizeTransition(
      child: child,
      sizeFactor: Tween<double>(begin: 0.1, end: 1.0).animate(CurvedAnimation(parent: animaton1, curve: Curves.linear)),
    );
  } else {
    return child;
  }
}
