import 'package:flutter/material.dart';

class PositionDelegate extends SingleChildLayoutDelegate {
  PositionDelegate({
    @required this.target,
    @required this.verticalOffset,
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
  bool shouldRelayout(PositionDelegate oldDelegate) {
    return target != oldDelegate.target
        || verticalOffset != oldDelegate.verticalOffset
        || preferBelow != oldDelegate.preferBelow;
  }
}
