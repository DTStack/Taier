/* eslint-disable new-cap */
/* eslint-disable no-redeclare */

const MyEdgeStyle = function (Mx: any) {
    const {
        mxConstants,
        mxEdgeStyle,
        mxPoint,
        mxUtils,
        mxStyleRegistry,
        mxRectangle
    } = Mx;

    function outputEdgeInfo (state: any, source: any, target: any, points: any, result: any) {
        console.log('state:', state);
        console.log('source:', source);
        console.log('target:', target);
        console.log('points:', points);
        console.log('result:', result);
    };

    return {
        /**
         * Function: OrthConnector
         *
         * Implements a local orthogonal router between the given
         * cells.
         *
         * Parameters:
         *
         * state - <mxCellState> that represents the edge to be updated.
         * source - <mxCellState> that represents the source terminal.
         * target - <mxCellState> that represents the target terminal.
         * points - List of relative control points.
         * result - Array of <mxPoints> that represent the actual points of the
         * edge.
         *
         */
        OrthConnector: function (state: any, source: any, target: any, points: any, result: any) {
            var graph = state.view.graph;
            var sourceEdge =
                source == null ? false : graph.getModel().isEdge(source.cell);
            var targetEdge =
                target == null ? false : graph.getModel().isEdge(target.cell);

            var pts = state.absolutePoints;
            var p0 = pts[0];
            var pe = pts[pts.length - 1];

            var sourceX = source != null ? source.x : p0.x;
            var sourceY = source != null ? source.y : p0.y;
            var sourceWidth = source != null ? source.width : 0;
            var sourceHeight = source != null ? source.height : 0;

            var targetX = target != null ? target.x : pe.x;
            var targetY = target != null ? target.y : pe.y;
            var targetWidth = target != null ? target.width : 0;
            var targetHeight = target != null ? target.height : 0;

            var scaledSourceBuffer =
                state.view.scale *
                mxEdgeStyle.getJettySize(state, source, target, points, true);
            var scaledTargetBuffer =
                state.view.scale *
                mxEdgeStyle.getJettySize(state, source, target, points, false);

            // Workaround for loop routing within buffer zone
            if (source != null && target == source) {
                scaledTargetBuffer = Math.max(
                    scaledSourceBuffer,
                    scaledTargetBuffer
                );
                scaledSourceBuffer = scaledTargetBuffer;
            }

            var totalBuffer = scaledTargetBuffer + scaledSourceBuffer;
            var tooShort = false;

            // Checks minimum distance for fixed points and falls back to segment connector
            if (p0 != null && pe != null) {
                var dx = pe.x - p0.x;
                var dy = pe.y - p0.y;

                tooShort = dx * dx + dy * dy < totalBuffer * totalBuffer;
            }

            if (
                tooShort ||
                (mxEdgeStyle.orthPointsFallback &&
                    (points != null && points.length > 0)) ||
                sourceEdge ||
                targetEdge
            ) {
                mxEdgeStyle.SegmentConnector(state, source, target, points, result);

                return;
            }

            // Determine the side(s) of the source and target vertices
            // that the edge may connect to
            // portConstraint [source, target]
            var portConstraint: any = [
                mxConstants.DIRECTION_MASK_ALL,
                mxConstants.DIRECTION_MASK_ALL
            ];
            var rotation = 0;

            if (source != null) {
                portConstraint[0] = mxUtils.getPortConstraints(
                    source,
                    state,
                    true,
                    mxConstants.DIRECTION_MASK_ALL
                );
                rotation = mxUtils.getValue(
                    source.style,
                    mxConstants.STYLE_ROTATION,
                    0
                );

                if (rotation != 0) {
                    var newRect = mxUtils.getBoundingBox(
                        new mxRectangle(
                            sourceX,
                            sourceY,
                            sourceWidth,
                            sourceHeight
                        ),
                        rotation
                    );
                    sourceX = newRect.x;
                    sourceY = newRect.y;
                    sourceWidth = newRect.width;
                    sourceHeight = newRect.height;
                }
            }

            if (target != null) {
                portConstraint[1] = mxUtils.getPortConstraints(
                    target,
                    state,
                    false,
                    mxConstants.DIRECTION_MASK_ALL
                );
                rotation = mxUtils.getValue(
                    target.style,
                    mxConstants.STYLE_ROTATION,
                    0
                );

                if (rotation != 0) {
                    var newRect = mxUtils.getBoundingBox(
                        new mxRectangle(
                            targetX,
                            targetY,
                            targetWidth,
                            targetHeight
                        ),
                        rotation
                    );
                    targetX = newRect.x;
                    targetY = newRect.y;
                    targetWidth = newRect.width;
                    targetHeight = newRect.height;
                }
            }

            // Avoids floating point number errors
            sourceX = Math.round(sourceX * 10) / 10;
            sourceY = Math.round(sourceY * 10) / 10;
            sourceWidth = Math.round(sourceWidth * 10) / 10;
            sourceHeight = Math.round(sourceHeight * 10) / 10;

            targetX = Math.round(targetX * 10) / 10;
            targetY = Math.round(targetY * 10) / 10;
            targetWidth = Math.round(targetWidth * 10) / 10;
            targetHeight = Math.round(targetHeight * 10) / 10;

            var dir: any = [0, 0];

            // Work out which faces of the vertices present against each other
            // in a way that would allow a 3-segment connection if port constraints
            // permitted.
            // geo -> [source, target] [x, y, width, height]
            var geo: any = [
                [sourceX, sourceY, sourceWidth, sourceHeight],
                [targetX, targetY, targetWidth, targetHeight]
            ];
            var buffer: any = [scaledSourceBuffer, scaledTargetBuffer];

            for (var i = 0; i < 2; i++) {
                mxEdgeStyle.limits[i][1] = geo[i][0] - buffer[i];
                mxEdgeStyle.limits[i][2] = geo[i][1] - buffer[i];
                mxEdgeStyle.limits[i][4] = geo[i][0] + geo[i][2] + buffer[i];
                mxEdgeStyle.limits[i][8] = geo[i][1] + geo[i][3] + buffer[i];
            }

            // Work out which quad the target is in
            var sourceCenX = geo[0][0] + geo[0][2] / 2.0;
            var sourceCenY = geo[0][1] + geo[0][3] / 2.0;
            var targetCenX = geo[1][0] + geo[1][2] / 2.0;
            var targetCenY = geo[1][1] + geo[1][3] / 2.0;

            var dx = sourceCenX - targetCenX;
            var dy = sourceCenY - targetCenY;

            var quad = 0;

            if (dx < 0) {
                if (dy < 0) {
                    quad = 2;
                } else {
                    quad = 1;
                }
            } else {
                if (dy <= 0) {
                    quad = 3;

                    // Special case on x = 0 and negative y
                    if (dx == 0) {
                        quad = 2;
                    }
                }
            }

            // Check for connection constraints
            var currentTerm = null;

            if (source != null) {
                currentTerm = p0;
            }

            var constraint: any = [[0.5, 0.5], [0.5, 0.5]];

            for (var i = 0; i < 2; i++) {
                if (currentTerm != null) {
                    constraint[i][0] = (currentTerm.x - geo[i][0]) / geo[i][2];

                    if (Math.abs(currentTerm.x - geo[i][0]) <= 1) {
                        dir[i] = mxConstants.DIRECTION_MASK_WEST;
                    } else if (
                        Math.abs(currentTerm.x - geo[i][0] - geo[i][2]) <= 1
                    ) {
                        dir[i] = mxConstants.DIRECTION_MASK_EAST;
                    }

                    constraint[i][1] = (currentTerm.y - geo[i][1]) / geo[i][3];

                    if (Math.abs(currentTerm.y - geo[i][1]) <= 1) {
                        dir[i] = mxConstants.DIRECTION_MASK_NORTH;
                    } else if (
                        Math.abs(currentTerm.y - geo[i][1] - geo[i][3]) <= 1
                    ) {
                        dir[i] = mxConstants.DIRECTION_MASK_SOUTH;
                    }
                }

                currentTerm = null;

                if (target != null) {
                    currentTerm = pe;
                }
            }

            var sourceTopDist = geo[0][1] - (geo[1][1] + geo[1][3]);
            var sourceLeftDist = geo[0][0] - (geo[1][0] + geo[1][2]);
            var sourceBottomDist = geo[1][1] - (geo[0][1] + geo[0][3]);
            var sourceRightDist = geo[1][0] - (geo[0][0] + geo[0][2]);

            mxEdgeStyle.vertexSeperations[1] = Math.max(
                sourceLeftDist - totalBuffer,
                0
            );
            mxEdgeStyle.vertexSeperations[2] = Math.max(
                sourceTopDist - totalBuffer,
                0
            );
            mxEdgeStyle.vertexSeperations[4] = Math.max(
                sourceBottomDist - totalBuffer,
                0
            );
            mxEdgeStyle.vertexSeperations[3] = Math.max(
                sourceRightDist - totalBuffer,
                0
            );

            // ==============================================================
            // Start of source and target direction determination

            // Work through the preferred orientations by relative positioning
            // of the vertices and list them in preferred and available order

            var dirPref: any = [];
            var horPref: any = [];
            var vertPref: any = [];

            horPref[0] =
                sourceLeftDist >= sourceRightDist
                    ? mxConstants.DIRECTION_MASK_WEST
                    : mxConstants.DIRECTION_MASK_EAST;
            vertPref[0] =
                sourceTopDist >= sourceBottomDist
                    ? mxConstants.DIRECTION_MASK_NORTH
                    : mxConstants.DIRECTION_MASK_SOUTH;

            horPref[1] = mxUtils.reversePortConstraints(horPref[0]);
            vertPref[1] = mxUtils.reversePortConstraints(vertPref[0]);

            var preferredHorizDist =
                sourceLeftDist >= sourceRightDist
                    ? sourceLeftDist
                    : sourceRightDist;
            var preferredVertDist =
                sourceTopDist >= sourceBottomDist
                    ? sourceTopDist
                    : sourceBottomDist;

            var prefOrdering: any = [[0, 0], [0, 0]];
            var preferredOrderSet = false;

            // If the preferred port isn't available, switch it
            for (var i = 0; i < 2; i++) {
                if (dir[i] != 0x0) {
                    continue;
                }

                if ((horPref[i] & portConstraint[i]) == 0) {
                    horPref[i] = mxUtils.reversePortConstraints(horPref[i]);
                }

                if ((vertPref[i] & portConstraint[i]) == 0) {
                    vertPref[i] = mxUtils.reversePortConstraints(vertPref[i]);
                }

                prefOrdering[i][0] = vertPref[i];
                prefOrdering[i][1] = horPref[i];
            }

            if (preferredVertDist > 0 && preferredHorizDist > 0) {
                // Possibility of two segment edge connection
                if (
                    (horPref[0] & portConstraint[0]) > 0 &&
                    (vertPref[1] & portConstraint[1]) > 0
                ) {
                    prefOrdering[0][0] = horPref[0];
                    prefOrdering[0][1] = vertPref[0];
                    prefOrdering[1][0] = vertPref[1];
                    prefOrdering[1][1] = horPref[1];
                    preferredOrderSet = true;
                } else if (
                    (vertPref[0] & portConstraint[0]) > 0 &&
                    (horPref[1] & portConstraint[1]) > 0
                ) {
                    prefOrdering[0][0] = vertPref[0];
                    prefOrdering[0][1] = horPref[0];
                    prefOrdering[1][0] = horPref[1];
                    prefOrdering[1][1] = vertPref[1];
                    preferredOrderSet = true;
                }
            }

            if (preferredVertDist > 0 && !preferredOrderSet) {
                prefOrdering[0][0] = vertPref[0];
                prefOrdering[0][1] = horPref[0];
                prefOrdering[1][0] = vertPref[1];
                prefOrdering[1][1] = horPref[1];
                preferredOrderSet = true;
            }

            if (preferredHorizDist > 0 && !preferredOrderSet) {
                prefOrdering[0][0] = horPref[0];
                prefOrdering[0][1] = vertPref[0];
                prefOrdering[1][0] = horPref[1];
                prefOrdering[1][1] = vertPref[1];
                preferredOrderSet = true;
            }

            // The source and target prefs are now an ordered list of
            // the preferred port selections
            // It the list can contain gaps, compact it

            for (var i = 0; i < 2; i++) {
                if (dir[i] != 0x0) {
                    continue;
                }

                if ((prefOrdering[i][0] & portConstraint[i]) == 0) {
                    prefOrdering[i][0] = prefOrdering[i][1];
                }

                dirPref[i] = prefOrdering[i][0] & portConstraint[i];
                dirPref[i] |= (prefOrdering[i][1] & portConstraint[i]) << 8;
                dirPref[i] |= (prefOrdering[1 - i][i] & portConstraint[i]) << 16;
                dirPref[i] |=
                    (prefOrdering[1 - i][1 - i] & portConstraint[i]) << 24;

                if ((dirPref[i] & 0xf) == 0) {
                    dirPref[i] = dirPref[i] << 8;
                }

                if ((dirPref[i] & 0xf00) == 0) {
                    dirPref[i] = (dirPref[i] & 0xf) | (dirPref[i] >> 8);
                }

                if ((dirPref[i] & 0xf0000) == 0) {
                    dirPref[i] =
                        (dirPref[i] & 0xffff) | ((dirPref[i] & 0xf000000) >> 8);
                }

                dir[i] = dirPref[i] & 0xf;

                if (
                    portConstraint[i] == mxConstants.DIRECTION_MASK_WEST ||
                    portConstraint[i] == mxConstants.DIRECTION_MASK_NORTH ||
                    portConstraint[i] == mxConstants.DIRECTION_MASK_EAST ||
                    portConstraint[i] == mxConstants.DIRECTION_MASK_SOUTH
                ) {
                    dir[i] = portConstraint[i];
                }
            }

            // ==============================================================
            // End of source and target direction determination

            var sourceIndex =
                dir[0] == mxConstants.DIRECTION_MASK_EAST ? 3 : dir[0];
            var targetIndex =
                dir[1] == mxConstants.DIRECTION_MASK_EAST ? 3 : dir[1];

            sourceIndex -= quad;
            targetIndex -= quad;

            if (sourceIndex < 1) {
                sourceIndex += 4;
            }

            if (targetIndex < 1) {
                targetIndex += 4;
            }

            var routePattern =
                mxEdgeStyle.routePatterns[sourceIndex - 1][targetIndex - 1];

            mxEdgeStyle.wayPoints1[0][0] = geo[0][0];
            mxEdgeStyle.wayPoints1[0][1] = geo[0][1];

            switch (dir[0]) {
                case mxConstants.DIRECTION_MASK_WEST:
                    mxEdgeStyle.wayPoints1[0][0] -= scaledSourceBuffer;
                    mxEdgeStyle.wayPoints1[0][1] += constraint[0][1] * geo[0][3];
                    break;
                case mxConstants.DIRECTION_MASK_SOUTH:
                    mxEdgeStyle.wayPoints1[0][0] += constraint[0][0] * geo[0][2];
                    mxEdgeStyle.wayPoints1[0][1] += geo[0][3] + scaledSourceBuffer;
                    break;
                case mxConstants.DIRECTION_MASK_EAST:
                    mxEdgeStyle.wayPoints1[0][0] += geo[0][2] + scaledSourceBuffer;
                    mxEdgeStyle.wayPoints1[0][1] += constraint[0][1] * geo[0][3];
                    break;
                case mxConstants.DIRECTION_MASK_NORTH:
                    mxEdgeStyle.wayPoints1[0][0] += constraint[0][0] * geo[0][2];
                    mxEdgeStyle.wayPoints1[0][1] -= scaledSourceBuffer;
                    break;
            }

            var currentIndex = 0;

            // Orientation, 0 horizontal, 1 vertical
            var lastOrientation =
                (dir[0] &
                    (mxConstants.DIRECTION_MASK_EAST |
                        mxConstants.DIRECTION_MASK_WEST)) >
                0
                    ? 0
                    : 1;
            var initialOrientation = lastOrientation;
            var currentOrientation = 0;

            for (var i = 0; i < routePattern.length; i++) {
                var nextDirection = routePattern[i] & 0xf;

                // Rotate the index of this direction by the quad
                // to get the real direction
                var directionIndex =
                    nextDirection == mxConstants.DIRECTION_MASK_EAST
                        ? 3
                        : nextDirection;

                directionIndex += quad;

                if (directionIndex > 4) {
                    directionIndex -= 4;
                }

                var direction = mxEdgeStyle.dirVectors[directionIndex - 1];

                currentOrientation = directionIndex % 2 > 0 ? 0 : 1;
                // Only update the current index if the point moved
                // in the direction of the current segment move,
                // otherwise the same point is moved until there is
                // a segment direction change
                if (currentOrientation != lastOrientation) {
                    currentIndex++;
                    // Copy the previous way point into the new one
                    // We can't base the new position on index - 1
                    // because sometime elbows turn out not to exist,
                    // then we'd have to rewind.
                    mxEdgeStyle.wayPoints1[currentIndex][0] =
                        mxEdgeStyle.wayPoints1[currentIndex - 1][0];
                    mxEdgeStyle.wayPoints1[currentIndex][1] =
                        mxEdgeStyle.wayPoints1[currentIndex - 1][1];
                }

                var tar = (routePattern[i] & mxEdgeStyle.TARGET_MASK) > 0;
                var sou = (routePattern[i] & mxEdgeStyle.SOURCE_MASK) > 0;
                var side = (routePattern[i] & mxEdgeStyle.SIDE_MASK) >> 5;
                side = side << quad;

                if (side > 0xf) {
                    side = side >> 4;
                }

                var center = (routePattern[i] & mxEdgeStyle.CENTER_MASK) > 0;

                if ((sou || tar) && side < 9) {
                    var limit = 0;
                    var souTar = sou ? 0 : 1;

                    if (center && currentOrientation == 0) {
                        limit =
                            geo[souTar][0] + constraint[souTar][0] * geo[souTar][2];
                    } else if (center) {
                        limit =
                            geo[souTar][1] + constraint[souTar][1] * geo[souTar][3];
                    } else {
                        limit = mxEdgeStyle.limits[souTar][side];
                    }

                    if (currentOrientation == 0) {
                        var lastX = mxEdgeStyle.wayPoints1[currentIndex][0];
                        var deltaX = (limit - lastX) * direction[0];

                        if (deltaX > 0) {
                            mxEdgeStyle.wayPoints1[currentIndex][0] +=
                                direction[0] * deltaX;
                        }
                    } else {
                        var lastY = mxEdgeStyle.wayPoints1[currentIndex][1];
                        var deltaY = (limit - lastY) * direction[1];

                        if (deltaY > 0) {
                            mxEdgeStyle.wayPoints1[currentIndex][1] +=
                                direction[1] * deltaY;
                        }
                    }
                } else if (center) {
                    // Which center we're travelling to depend on the current direction
                    mxEdgeStyle.wayPoints1[currentIndex][0] +=
                        direction[0] *
                        Math.abs(mxEdgeStyle.vertexSeperations[directionIndex] / 2);
                    mxEdgeStyle.wayPoints1[currentIndex][1] +=
                        direction[1] *
                        Math.abs(mxEdgeStyle.vertexSeperations[directionIndex] / 2);
                }

                if (
                    currentIndex > 0 &&
                    mxEdgeStyle.wayPoints1[currentIndex][currentOrientation] ==
                        mxEdgeStyle.wayPoints1[currentIndex - 1][currentOrientation]
                ) {
                    currentIndex--;
                } else {
                    lastOrientation = currentOrientation;
                }
            }

            for (var i = 0; i <= currentIndex; i++) {
                if (i == currentIndex) {
                    // Last point can cause last segment to be in
                    // same direction as jetty/approach. If so,
                    // check the number of points is consistent
                    // with the relative orientation of source and target
                    // jx. Same orientation requires an even
                    // number of turns (points), different requires
                    // odd.
                    var targetOrientation =
                        (dir[1] &
                            (mxConstants.DIRECTION_MASK_EAST |
                                mxConstants.DIRECTION_MASK_WEST)) >
                        0
                            ? 0
                            : 1;
                    var sameOrient =
                        targetOrientation == initialOrientation ? 0 : 1;

                    // (currentIndex + 1) % 2 is 0 for even number of points,
                    // 1 for odd
                    if (sameOrient != (currentIndex + 1) % 2) {
                        // The last point isn't required
                        break;
                    }
                }

                result.push(
                    new mxPoint(
                        Math.round(mxEdgeStyle.wayPoints1[i][0]),
                        Math.round(mxEdgeStyle.wayPoints1[i][1])
                    )
                );
            }

            // Removes duplicates
            var index = 1;

            while (index < result.length) {
                if (
                    result[index - 1] == null ||
                    result[index] == null ||
                    result[index - 1].x != result[index].x ||
                    result[index - 1].y != result[index].y
                ) {
                    index++;
                } else {
                    result.splice(index, 1);
                }
            }

            // Special hand first and last pointX;
            // 源代码的算法还没有完全搞清楚，这里只是细微的调整了一下坐标值
            const resultLen = result.length;
            if (resultLen > 2) {
                let firstPoint = result[1].y;
                let lastSecondPointY = result[resultLen - 2].y;
                const differenceValue = (target ? target.y : NaN) - source.y;
                // console.log('differenceValue:', target.y, source.y, differenceValue)
                if (differenceValue < 60) {
                    result[1].y = firstPoint + 20;
                    result[resultLen - 1].y = lastSecondPointY - 20;
                }
            }
            // outputEdgeInfo(state, source, target, points, result);
        },

        myStyle: function (state: any, source: any, target: any, points: any, result: any) {
            if (source != null && target != null) {
                var pt = new mxPoint(target.getCenterX(), source.getCenterY());

                if (mxUtils.contains(source, pt.x, pt.y)) {
                    pt.y = source.y + source.height;
                }
                result.push(pt);
            }
            outputEdgeInfo(state, source, target, points, result);
            mxStyleRegistry.putValue('myEdgeStyle', mxEdgeStyle.MyStyle);
        }
    };
}

export default MyEdgeStyle;
