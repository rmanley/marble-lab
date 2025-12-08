package tech.rkanelabs.marblelab.ui

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import tech.rkanelabs.marblelab.data.TileType
import tech.rkanelabs.marblelab.data.WallMask

fun Modifier.border(
    color: Color,
    sides: WallMask = WallMask.All,
    strokeWidth: Dp = 2.dp,
) = drawBehind {
    val strokeWidthPx = strokeWidth.toPx()
    if (sides.has(WallMask.Up)) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width, 0f),
            strokeWidth = strokeWidthPx
        )
    }
    if (sides.has(WallMask.Right)) {
        drawLine(
            color = color,
            start = Offset(size.width, 0f),
            end = Offset(0f, size.height),
            strokeWidth = strokeWidthPx
        )
    }
    if (sides.has(WallMask.Down)) {
        drawLine(
            color = color,
            start = Offset(0f, size.height),
            end = Offset(size.width, size.height),
            strokeWidth = strokeWidthPx
        )
    }
    if (sides.has(WallMask.Left)) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(0f, size.height),
            strokeWidth = strokeWidthPx
        )
    }
}

fun Modifier.background(
    tileType: TileType,
    shape: Shape = RectangleShape
) = background(
    color = when (tileType) {
        TileType.Empty -> Color.White
        TileType.Floor -> Color.Gray
        TileType.Marble -> Color.Magenta
        TileType.Goal -> Color.Green
        TileType.Hole -> Color.Red
    },
    shape = shape
)