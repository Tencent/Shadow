import 'package:crm/base/widget/SuperTextView.dart';
import 'package:crm/common/MyColors.dart';

import '../router/RouterHelper.dart';
import '../utils/ImageHelper.dart';
import '../utils/CstColors.dart';
import '../widget/LinearWidget.dart';
import '../widget/MyButton.dart';

import '../widget/TextView.dart';
import 'package:flutter/material.dart';



/// 加载中
class ViewStateBusyWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Container(
      color: Colors.white,
      padding: EdgeInsets.only(top: 200),
      child: Align(
        alignment: Alignment.topCenter,
        child: CircularProgressIndicator(),
      ),
    );
  }
}


/// 网络请求失败页面
class ViewStateErrorWidget extends StatelessWidget {

  final VoidCallback onPressed;
  final VoidCallback leftCallback;
  final bool isBack;
  ViewStateErrorWidget({
    this.onPressed,
    this.isBack:false, this.leftCallback
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
      appBar: isBack? AppBar(
        shadowColor: Colors.transparent,
        leadingWidth: 44,
        leading: Container(
            margin: EdgeInsets.only(left: 16),
            child: InkWell(
              child: ImageHelper.buildImage("ic_close.png",width: 28,height: 28,type: 1,fit: BoxFit.fitWidth),
              onTap: (){
                if(null == leftCallback)
                  RouterHelper.pop(context);
                else
                  leftCallback?.call();
              },
            )
        ),
      ) : null,
      body: LinearWidget(
        bgColor: Colors.white,
        alignment: Alignment.center,
        crossAxisAlignment: CrossAxisAlignment.center,
        mainAxisAlignment: MainAxisAlignment.center,
        children: [
          ImageHelper.buildImage("ic_net_error.png",width: 300,height: 224,type: 1),
          SizedBox(height: 12,),
          TextView("无法连接到网络",color: CstColors.cl_161722,size: 18,),
          SizedBox(height: 10,),
          TextView("请检查网络设置或稍后重试",color: CstColors.cl_7B8290,size: 14,),
          SizedBox(height: 15,),
          SuperTextView("确定",solid: MyColors.cl_01C6AC,size: 16,
              height: 40,
              width: 110,
              alignment: Alignment.center,
              margin: EdgeInsets.only(left: 8,right: 8),
              color: Colors.white, radius: 8.0,onTap: onPressed),
        ],
      ),
    );


  }
}

/// 网络请求失败页面
class ViewStateFailedWidget extends StatelessWidget {

  final VoidCallback onPressed;
  final String text;
  ViewStateFailedWidget({
    this.onPressed,
    this.text
  });

  @override
  Widget build(BuildContext context) {
    return Scaffold(
        backgroundColor: Colors.white,
        body: Center(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            children: [
              ImageHelper.buildImage("ic_net_error.png",width: 300,height: 224,type: 1),
              SuperTextView("重新加载",solid: MyColors.cl_01C6AC,size: 16,
                height: 40,
                width: 110,
                alignment: Alignment.center,
                color: Colors.white, radius: 4.0,onTap: onPressed)
            ],
          ),
        ));
  }
}

/// 页面无数据
class ViewStateEmptyWidget extends StatelessWidget {

  final String title;
  final VoidCallback leftCallback;
  final bool isBack;

  const ViewStateEmptyWidget({Key key, this.title, this.leftCallback, this.isBack:false}) : super(key: key);


  @override
  Widget build(BuildContext context) {
    return Scaffold(
      backgroundColor: Colors.white,
      appBar: isBack? AppBar(
        shadowColor: Colors.transparent,
        leadingWidth: 44,
        leading: Container(
            margin: EdgeInsets.only(left: 16),
            child: InkWell(
              child: ImageHelper.buildImage("ic_close.png",width: 28,height: 28,type: 1,fit: BoxFit.fitWidth),
              onTap: (){
                if(null == leftCallback)
                  RouterHelper.pop(context);
                else
                  leftCallback?.call();
              },
            )
        ),
      ) : null,
      body: Center(
      child: Column(
        mainAxisSize: MainAxisSize.min,
        children: [
          ImageHelper.buildImage("ic_nodata.png",width: 80,height: 70,type: 1),
          TextView(title??"暂无数据",size: 14,color: MyColors.cl_A0A4AB,)
        ],
      ),
    ));
  }
}

/// 页面未授权登录
class ViewStateUnAuthWidget extends StatelessWidget {
  @override
  Widget build(BuildContext context) {
    return Center(
      child: TextView("页面未授权",size: 18, color: Colors.black ),
    );
  }
}





