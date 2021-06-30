

import 'package:flutter/material.dart';
import '../../provider/StateWidget.dart';
import '../../provider/ListViewModel.dart';
import 'PtrWidget.dart';

import '../../adapter/BaseAdapter.dart';
import '../../utils/WidgetUtils.dart';

class PtrListWidget extends PtrWidget {


  final EdgeInsetsGeometry padding;
  final bool shrinkWrap;
  final BaseAdapter adapter;
  final double itemExtent;
  final ListViewModel viewModel;
  final bool enablePullUp;
  final Future Function() future;
  final bool enablePullDown;
  final bool initFuture;


  PtrListWidget({Key key,
    this.padding,
    this.itemExtent,
    this.future,
    this.viewModel,
    this.initFuture:true,
    this.adapter,
    this.shrinkWrap:false,
    this.enablePullUp:true,
    this.enablePullDown:true
  }) : super(key: key,
    initFuture: initFuture,
    enablePullDown: enablePullDown,
    enablePullUp: enablePullUp,
    controller: viewModel.refreshController,
    onRefresh: (){
      return viewModel.refresh(future);
    },
    onLoading: (){
      return viewModel.loadMore(future);
    },
    builder: (context){
      if(null == viewModel.list || viewModel.list.isEmpty){
        return ViewStateEmptyWidget();
      }
      return WidgetUtils.buildList(
          adapter: adapter,
          padding:padding,
          itemExtent: itemExtent,
          shrinkWrap: shrinkWrap
      );
    },

  );

}


