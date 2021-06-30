

import 'dart:async';

import 'package:flutter/material.dart';
import '../utils/CstColors.dart';
import '../widget/TextView.dart';

class VerifyCodeWidget extends StatefulWidget {


  final Decoration decoration;
  final Decoration disabledDecoration;
  final Size size;
  final double radius;
  final double fontSize;
  final Color color;
  final bool start;
  final Color disabledColor;
  final bool isRecycler;
  final Future<dynamic> Function() requestCallBack;

  const VerifyCodeWidget({Key key,
    this.decoration,
    this.requestCallBack,
    this.isRecycler:false,
    this.start:false,
    this.disabledDecoration,
    this.size: const Size(110,50),
    this.radius: 0,
    this.fontSize:14,
    this.color : CstColors.white,
    this.disabledColor : CstColors.black
  }) : super(key: key);



  @override
  _VerityCodeWidgetState createState() => _VerityCodeWidgetState();
}

class _VerityCodeWidgetState extends State<VerifyCodeWidget> {

  bool enable = true;

  @override
  void initState() {
    // TODO: implement initState
    if(widget.start){
      widget.requestCallBack?.call()?.then((value){
        if(value == 1)
          reGetCountdown();
      }
      );
    }
    super.initState();
  }

  @override
  Widget build(BuildContext context) {

    if(!enable){
      return  Container(
        alignment: Alignment.center,
        decoration: widget.disabledDecoration,
        width: widget.size.width,
        height: widget.size.height,
        child: TextView(_codeCountdownStr,size: widget.fontSize,color: widget.disabledColor,),

      );
    }

    return Container(
      width: widget.size.width,
      height: widget.size.height,
      alignment: Alignment.center,
      decoration: widget.decoration,
      child: FlatButton(
          minWidth: widget.size.width,
          height: widget.size.height,
          shape:  StadiumBorder(
            side: BorderSide(width: 1, color:  Colors.transparent),
          ),
          color: Colors.transparent,
          child: TextView(_codeCountdownStr,size: widget.fontSize,color: widget.color,),
          onPressed: () {
            if (enable) {
              widget.requestCallBack?.call()?.then((value){
                if(value == 1)
                  reGetCountdown();
              });
            }
          }
      ),

    );
//      Ink()
//      InkWell(
//      child: FlatButton(
//        child: TextView(_codeCountdownStr,size: 14,color: textColor,),
//        color:  Colors.transparent,
//        height: 50,
//        minWidth: 110,
//        padding: EdgeInsets.symmetric(horizontal: 10),
//        shape: StadiumBorder(
//          side: BorderSide(width: 1, color: CstColors.cl_0FB36E),
//        ),
//      ),
//      onTap: (){
//        if(enable){
//          reGetCountdown();
//        }
//      },
//    );
  }

  Timer _countdownTimer;
  String _codeCountdownStr = '获取验证码';
  int _countdownNum = 59;

  void reGetCountdown() {
    setState(() {
      if (_countdownTimer != null) {
        return;
      }
      // Timer的第一秒倒计时是有一点延迟的，为了立刻显示效果可以添加下一行。
      _codeCountdownStr = '${_countdownNum--}s';
      enable = false;
      _countdownTimer = new Timer.periodic(new Duration(seconds: 1), (timer) {
        setState(() {
          if (_countdownNum > 0) {
            _codeCountdownStr = '${_countdownNum--}s';
          } else {
            enable = true;
            _codeCountdownStr = '获取验证码';
            _countdownNum = 59;
            _countdownTimer.cancel();
            _countdownTimer = null;
          }
        });
      });
    });
  }

  // 不要忘记在这里释放掉Timer
  @override
  void dispose() {
    enable = true;
    _countdownTimer?.cancel();
    _countdownTimer = null;
    super.dispose();
  }

}
