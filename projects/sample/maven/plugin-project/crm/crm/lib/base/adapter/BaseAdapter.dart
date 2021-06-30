
import '../utils/CstColors.dart';
import 'package:flutter/material.dart';

class BaseAdapter<T>{

  List<T> data;

  Widget header;

  bool isClick = true;
  double divider;

  int count;
  Color dividerColor = CstColors.cl_A5AAB4;

  Function(BuildContext context,int index,T model) onItemClick;

  Widget Function(BuildContext context,int index, T model) builder;

  BaseAdapter({
    this.data,
    this.isClick = true,
    this.onItemClick,
    this.builder,
    this.count,
    this.header,
    this.divider = 0,
    this.dividerColor,
  });


  void setOnItemCallback(Function(BuildContext context,int index,T model) onItemClick){
    this.onItemClick = onItemClick;
  }


  BaseAdapter<T> setData(List<T> dat){
    this.data = data;

    return this;
  }

  List<T> getData()=> data;

  int getHeaderCount(){

    return (null == header ? 0 : 1);
  }

  int getItemCount(){
    int counts = (null == data ? 0 : data.length) + getHeaderCount();
    return count??counts;
  }

  Widget onBindViewHolderHeader(BuildContext context, int index){

    return header;
  }

  Widget onCreateViewHolder(BuildContext context, int index) {
    if(index < getHeaderCount()){
      return onBindViewHolderHeader(context,index);
    }

    var newIndex = index - getHeaderCount();
    T model = newIndex < (data?.length??0) ? data[newIndex] : null;

    var function = null == builder ? onBindViewHolder(context,index,model) : builder(context,index,model);

    return !isClick? function :InkWell(
      child: function,
      onTap: (){
        if(null != onItemClick)
          onItemClick(context,index,model);
        else
          onItemClicked(context,index,model);
      },
    );


  }

  //      customBorder:ContinuousRectangleBorder(
//        side: BorderSide(
//          color: Colors.white,
//          style: BorderStyle.solid,
//        )
//      ),

  Widget onBindViewHolder(BuildContext context,int index, T model){
    return null;
  }
  void onItemClicked(BuildContext context, int index,T model){

  }



}