
import 'package:flutter/material.dart';
import '../../provider/StateWidget.dart';
import '../../provider/ListViewModel.dart';
import 'PtrWidget.dart';

import '../../adapter/BaseAdapter.dart';
import '../../utils/WidgetUtils.dart';





class PtrSliverListWidget extends PtrWidget {

  final EdgeInsetsGeometry padding;
  final bool shrinkWrap = false;
  final BaseAdapter adapter;
  final double itemExtent;
  final ListViewModel viewModel;
  final bool enablePullUp;
  final List<Widget> slivers;
  final Future Function() future;
  final bool initFuture;


  PtrSliverListWidget({Key key,
    this.initFuture:true,
    this.padding,
    this.itemExtent,
    this.viewModel,
    this.adapter,
    this.slivers,
    this.enablePullUp:true,
    this.future
  }) : super(key: key,
    initFuture: initFuture,
    enablePullUp: enablePullUp,
    controller: viewModel.refreshController,
    onRefresh: (){
      return viewModel.refresh(future);
    },
    onLoading: (){
      return viewModel.loadMore(future);
    },
    builder: (context){
      Widget child;
      if(null == viewModel.list || viewModel.list.isEmpty){
        child =  WidgetUtils.buildSliverFillRemaining(
          child: ViewStateEmptyWidget()
        );
      }
      else
        child = WidgetUtils.buildSliverList(
            itemExtent: itemExtent,
            adapter: adapter
        );
      return CustomScrollView(
        slivers: [
          ...slivers??[],
          child
        ],
      );
    },

  );

}

