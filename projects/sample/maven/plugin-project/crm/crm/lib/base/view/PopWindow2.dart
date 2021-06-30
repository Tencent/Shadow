
import 'package:flutter/material.dart';
import '../utils/CstColors.dart';


class PopWindow2 extends StatefulWidget {

  final double width;
  final double height;
  final double popMaxHeight;
  final double popWidth;
  final Widget child;
  final Widget Function(GlobalKey key,Function()) build;
  final Decoration decoration;

  const PopWindow2({Key key, this.width, this.height,
    @required this.build,
    @required this.child,
     this.popMaxHeight,
     this.popWidth,
    this.decoration
  }) : super(key: key);

  @override
  _BasePopWindowState createState() => _BasePopWindowState();
}

class _BasePopWindowState extends State<PopWindow2> {

  var _globalKey = GlobalKey();

  @override
  Widget build(BuildContext context) {


    return Container(
      key: _globalKey,
      height: widget.height,
      width: widget.width,
      decoration: widget.decoration,
      child: InkWell(
        child: widget.child,
        onTap: showPop,
      ),
    );

  }


  var _popKey = GlobalKey();

  void showPop(){
    final RenderBox box = context.findRenderObject() as RenderBox;
    final RenderBox overlay = Overlay.of(context).context.findRenderObject() as RenderBox;


    var height = _globalKey.currentContext.size.height;
    Offset target = box.localToGlobal(
//        box.size.center(Offset.zero),
      box.size.center(Offset(0.0, height/2)),
      ancestor: overlay,
    );

    var child = widget.build(_popKey,dismiss);
    var screenHeight = MediaQuery.of(context).size.height;
    var preferBelow = (widget.popMaxHeight??0.0) < (screenHeight - target.dy);
    if(!preferBelow){
      target = box.localToGlobal(
//        box.size.center(Offset.zero),
        box.size.center(Offset(0.0, -height/2)),
        ancestor: overlay,
      );
    }

    Navigator.of(context, rootNavigator: false).push(
        PopRoute(child,
            target: target,
            size: Size(widget.popWidth??0,widget.popMaxHeight??0),
            preferBelow:preferBelow
        ));
  }




  void dismiss(){
    Navigator.of(context, rootNavigator: false).pop();
  }
}


class PopRoute extends PopupRoute<String>{

  final Offset target;
  final Widget child;
  final Size size;
  final bool preferBelow;

  PopRoute(this.child,{
    this.target,
    this.size,
    this.preferBelow,
  });

  @override
  Duration get transitionDuration => Duration(milliseconds: 300);

  @override
  bool get barrierDismissible => true;

  @override
  Color get barrierColor => null;

  double _kMenuCloseIntervalEnd = 2.0 / 3.0;

  @override
  String get barrierLabel => "";


  @override
  Widget buildPage(BuildContext context, Animation<double> animation, Animation<double> secondaryAnimation) {

//    var preferBelow = child.key;//context.size.height < target.dy;
    return MediaQuery.removePadding(
      context: context,
      removeTop: true,
      removeBottom: true,
      removeLeft: true,
      removeRight: true,
      child: Builder(
        builder: (BuildContext context) {
          return CustomSingleChildLayout(
              delegate: _PositionDelegate(
                  target: target,
                  preferBelow:preferBelow
              ),
              child: Material(
                child: Container(
                  width: size.width + 4,
                  height: size.height + 4,
                  padding: EdgeInsets.all(2.0),
                  decoration: BoxDecoration(
                      color: Colors.white,
                      boxShadow: [
                        BoxShadow(
                            color: CstColors.cl_33161722,
                            offset: Offset(0.0, 15.0), //阴影xy轴偏移量
                            blurRadius: 15.0, //阴影模糊程度
                            spreadRadius: 1.0 //阴影扩散程度
                        )
                      ]
                  ),
                  child: child,
                ),
              )
          );
        },
      ),
    );
  }

  @override
  Animation<double> createAnimation() {
    return CurvedAnimation(
      parent: super.createAnimation(),
      curve: Curves.linear,
      reverseCurve:  Interval(0.0, _kMenuCloseIntervalEnd),
    );
  }



}

class _PositionDelegate extends SingleChildLayoutDelegate {
  _PositionDelegate({
    @required this.target,
    this.verticalOffset:0.0,
    @required this.preferBelow,
  }) : assert(target != null),
        assert(verticalOffset != null),
        assert(preferBelow != null);

  final Offset target;

  final double verticalOffset;

  final bool preferBelow;

  @override
  BoxConstraints getConstraintsForChild(BoxConstraints constraints) => constraints.loosen();

  @override
  Offset getPositionForChild(Size size, Size childSize) {
    return positionDependentBox(
      size: size,
      childSize: childSize,
      target: target,
      verticalOffset: verticalOffset,
      preferBelow: preferBelow,
    );
  }

  @override
  bool shouldRelayout(_PositionDelegate oldDelegate) {
    return target != oldDelegate.target
        || verticalOffset != oldDelegate.verticalOffset
        || preferBelow != oldDelegate.preferBelow;
  }
}


