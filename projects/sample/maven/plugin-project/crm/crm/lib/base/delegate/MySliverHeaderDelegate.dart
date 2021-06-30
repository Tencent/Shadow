import 'package:flutter/material.dart';

class MySliverHeaderDelegate  extends SliverPersistentHeaderDelegate{


  final Widget child;
  final double height;

  const MySliverHeaderDelegate(this.child,this.height);


  @override
  Widget build(BuildContext context, double shrinkOffset, bool overlapsContent) {
    return Material(
      child: child,
    );
  }

  @override
  double get maxExtent => height;

  @override
  double get minExtent => height;

  @override
  bool shouldRebuild(covariant SliverPersistentHeaderDelegate oldDelegate) {
    return true;
  }
}
