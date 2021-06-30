


import 'package:flutter/material.dart';

class AnimWidget extends StatefulWidget {
  @override
  _AnimatedWidgetState createState() => _AnimatedWidgetState();
}

class _AnimatedWidgetState extends State<AnimWidget> with SingleTickerProviderStateMixin {
  AnimationController _controller;

  Animation animation;

  @override
  void initState() {
    _controller = AnimationController(duration: Duration(milliseconds: 1000),vsync: this);

    animation = Tween(begin: 0.0,end: 3.0).animate(_controller);
    //开始动画
    super.initState();
  }

  @override
  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return AnimatedBuilder(
      animation: animation,
      builder: (BuildContext context, Widget child) {
        return Transform(
          transform:Matrix4.skew(animation.value,animation.value),
          // angle: animation.value,
          child: child,
        );
      },
      child: InkWell(
        child: FlutterLogo(size: 60,),
        onTap: (){
          setState(() {
            if(_controller.isCompleted){
              _controller.reverse();
            }
            else{
              _controller.forward();
            }
          });
        },
      ),
    );
  }
}

