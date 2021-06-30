


import 'package:flutter/material.dart';
import 'package:provider/provider.dart';
import '../adapter/BaseAdapter.dart';
import '../delegate/MySliverHeaderDelegate.dart';


class WidgetUtils {



  static Widget buildList({
    EdgeInsetsGeometry padding,
    ScrollController controller,
    bool shrinkWrap = false,
    BaseAdapter adapter,
    double itemExtent,
    Axis scrollDirection = Axis.vertical,
  })
  {

    if(adapter.divider == 0){
      return ListView.builder(
        padding: padding,
        controller: controller,
        shrinkWrap: shrinkWrap,
        itemExtent: itemExtent,
        scrollDirection: scrollDirection,
        itemCount: adapter.getItemCount(),
        itemBuilder: (BuildContext context, int index) {
          return adapter.onCreateViewHolder(context, index);
        },
      );
    }

    return ListView.separated(
        padding: padding,
        controller: controller,
        shrinkWrap: shrinkWrap,
        scrollDirection: scrollDirection,
        itemCount: adapter.getItemCount(),
        itemBuilder: (BuildContext context, int index) {
          return adapter.onCreateViewHolder(context, index);
        },
        separatorBuilder: (BuildContext context, int index) {
          return  Divider(
              height:  adapter.divider,
              color: adapter.dividerColor
          );
        }
    );

  }



  static PageView buildPageView({BaseAdapter adapter,
    Axis scrollDirection = Axis.horizontal,
    bool pageSnapping:true,
  PageController controller,
    Function(int  value) onPageChanged,
  }){


    return PageView.builder(
      pageSnapping: pageSnapping,
      controller: controller,
      onPageChanged: onPageChanged,
      scrollDirection: scrollDirection,
      itemCount: adapter.getItemCount(),
      itemBuilder: (context, index) {
        return adapter.onCreateViewHolder(context, index);
      },
    );
  }


  static Widget buildGrid({
    BaseAdapter adapter,
    int crossAxisCount: 2,
    double crossAxisSpacing:0,
    double mainAxisSpacing : 0,
    EdgeInsetsGeometry padding,
    double childAspectRatio:1,
    ScrollPhysics physics,
    bool shrinkWrap:true,

  }){
    if(adapter.getItemCount() == 0)
      return SizedBox.shrink();

    return GridView.builder(
      padding:padding,
      shrinkWrap: shrinkWrap,
      physics: physics,
      gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
          crossAxisCount: crossAxisCount,

          childAspectRatio: childAspectRatio,
          crossAxisSpacing: crossAxisSpacing,
          mainAxisSpacing: mainAxisSpacing
      ),
      itemBuilder: (context, index) {
        return adapter.onCreateViewHolder(context, index);
      },
      itemCount: adapter.getItemCount(),
    );
  }




  static SliverGrid buildSliverGrid({
    BaseAdapter adapter,
    double crossAxisSpacing:0,
    double mainAxisSpacing : 0,
    int crossAxisCount:2,
    double childAspectRatio:1.0,
  }) {

    return  SliverGrid(
        gridDelegate: SliverGridDelegateWithFixedCrossAxisCount(
            childAspectRatio:childAspectRatio,
            crossAxisCount: crossAxisCount, crossAxisSpacing: crossAxisSpacing, mainAxisSpacing: mainAxisSpacing),
        delegate: SliverChildBuilderDelegate((BuildContext context, int index) {
          return adapter.onCreateViewHolder(context, index);
        }, childCount: adapter.getItemCount()));
  }

  static SliverMultiBoxAdaptorWidget buildSliverList({
    BaseAdapter adapter,
    double itemExtent
  }){

    if(null != itemExtent)
      return SliverFixedExtentList(
        itemExtent: itemExtent,
        delegate: SliverChildBuilderDelegate((context, index) {
          return adapter.onCreateViewHolder(context, index);
        },
          //设置显示个数
          childCount: adapter.getItemCount(),
        ),
      );
    return SliverList(
      delegate: SliverChildBuilderDelegate((context, index) {
        return adapter.onCreateViewHolder(context, index);
      }, childCount: adapter.getItemCount()),
    );

  }


  static SliverPersistentHeader buildSliverHeader({bool floating: true,bool pinned: true,
    @required Widget child,
    @required double height,
  }){

    return SliverPersistentHeader(
      floating:floating,
      pinned:pinned,
      delegate: MySliverHeaderDelegate(child,height),
    );
  }


  static SliverPadding buildSliverPadding({Widget child,
    EdgeInsetsGeometry padding: const EdgeInsets.all(0.0)
  }){
    return SliverPadding(
      padding: padding,
      sliver: SliverToBoxAdapter(
        child: child,
      ),
    );
  }

  static SliverToBoxAdapter buildSliverToBoxAdapter({Widget child,
  }){
    return SliverToBoxAdapter(
      child: child,
    );
  }

  static SliverVisibility buildSliverVisibility({Widget child,
    bool visible:true
  }){
    return SliverVisibility(
      visible: visible,
      sliver: SliverToBoxAdapter(
        child: child,
      ),
    );
  }

  //此组件充满视口剩余空间，通常用于最后一个sliver组件，以便于没有任何剩余控件。
  static SliverFillRemaining buildSliverFillRemaining({Widget child,bool hasScrollBody:false,
  }){
    return SliverFillRemaining(
      hasScrollBody: hasScrollBody,
      child: child,
    );
  }

  static ChangeNotifierProvider buildProvider<T extends ChangeNotifier>({Widget Function(BuildContext context,T model) builder, T model,
  }){
    return ChangeNotifierProvider<T>.value(
      value: model,
      child: Consumer<T>(
        builder: (context,model,child){
          return builder(context,model);
        },
      ),
    );
  }

}
