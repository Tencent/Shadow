
import 'package:flutter/material.dart';
import '../../../base/utils/ImageHelper.dart';
import '../../../base/utils/CstColors.dart';
import '../../../base/widget/TextView.dart';
import '../../../base/model/ItemModel.dart';
import '../../../base/view/BaseWidget.dart';

class BottomBarWidget extends StatefulWidget {

  final BottomBarController controller;
  final Function(int index) callback;

  const BottomBarWidget({Key key, this.controller,this.callback}) : super(key: key);

  @override
  _BottomBarWidgetState createState() => _BottomBarWidgetState();
}

class _BottomBarWidgetState extends BaseWidgetState<BottomBarWidget,BottomBarController> {

  @override
  BottomBarController getController() =>  widget.controller??BottomBarController();

  @override
  void initState() {
    // TODO: implement initState
    super.initState();
    controller.setCallback(widget.callback);
  }

  @override
  Widget build(BuildContext context) {
    return Container(
        decoration: BoxDecoration(
            color: Colors.white,
            boxShadow:[
              BoxShadow(
                  color: CstColors.cl_7DD1D1D1,
                  offset: Offset(0.0, -3.0), //阴影xy轴偏移量
                  blurRadius: 15.0, //阴影模糊程度
                  spreadRadius: 6.0 //阴影扩散程度
              )
            ]
        ),
        height: 52,
        child: Row(
          children: List.generate(controller.barList.length, (index) {
            var model = controller.barList[index];
            return  Expanded(child:
            InkWell(child: Column(
              mainAxisAlignment: MainAxisAlignment.center,
              children: [
                AnimatedCrossFade(
                  duration: Duration(milliseconds: 300),
                  crossFadeState:
                  controller.index == index ? CrossFadeState.showFirst : CrossFadeState.showSecond,
                  firstChild:  ImageHelper.buildImage(model.iconSelected,width: 26,height: 26,type: 1,fit: BoxFit.fitHeight),
                  secondChild:  ImageHelper.buildImage(model.icon,width: 26,height: 26,type: 1,fit: BoxFit.fitHeight),
                ),

                SizedBox(height: 3,),
                AnimatedCrossFade(
                  duration: Duration(milliseconds: 300),
                  crossFadeState:
                  controller.index == index ? CrossFadeState.showFirst : CrossFadeState.showSecond,
                  firstChild:  TextView(model.title,color: CstColors.cl_1F2736,size: 11,),
                  secondChild:  TextView(model.title,color: CstColors.cl_B9BEC3,size: 11,),
                ),

              ],
            ),
              highlightColor: Colors.transparent,
              splashColor: Colors.transparent,
              onTap: () async{
                if(index ==  controller.index)
                  return;
                controller.setIndex(index);
              },
            )
            );
          }
          ),
        )
    );
  }
}

class BottomBarController extends BaseWidgetController{
  List<String> titles = ["跑动","客户","分析","公示","联系人"];
  List<String> resourceSel = ["ic_running_selector.png","ic_customer_selector.png","ic_analysis_selector.png",
    "ic_publicity_selector.png","ic_contract_selector.png"];
  List<String> resource = ["ic_running.png","ic_customer.png","ic_analysis.png","ic_publicity.png","ic_contract.png"];

  List<ItemModel> barList;


  void setLimit(bool publicity){
    barList = [];
    List.generate(titles.length,(index){
      var model = ItemModel(
          title: titles[index],
          icon: resource[index],
          iconSelected: resourceSel[index]);
      if(publicity || index != 3)
        barList.add(model);

    });
  }


  BottomBarController(){
    // barList = List.generate(titles.length, (index) => ItemModel(
    //     title: titles[index],
    //     icon: resource[index],
    //     iconSelected: resourceSel[index]
    //
    // )).toList();
  }

  Function(int index) callback;

  setCallback(Function(int index) callback){
    this.callback = callback;
  }


  @override
  void setIndex(int index, {bool isNotify = true}) {
    // TODO: implement setIndex
    super.setIndex(index);
    callback?.call(index);
  }
}


