package tech.rkanelabs.marblelab.ui

import androidx.compose.foundation.background
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import tech.rkanelabs.marblelab.data.TileType
import tech.rkanelabs.marblelab.data.WallMask

fun Modifier.border(
    color: Color,
    sides: WallMask = WallMask.All,
    strokeWidth: Dp = 2.dp,
) = drawWithContent {
    drawContent()

    val strokeWidthPx = strokeWidth.toPx()
    val halfStroke = strokeWidthPx / 2f
    if (sides.has(WallMask.Up)) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(size.width - halfStroke, 0f),
            strokeWidth = strokeWidthPx,
            cap = StrokeCap.Square
        )
    }
    if (sides.has(WallMask.Right)) {
        drawLine(
            color = color,
            start = Offset(size.width - halfStroke, 0f),
            end = Offset(size.width - halfStroke, size.height),
            strokeWidth = strokeWidthPx,
            cap = StrokeCap.Square
        )
    }
    if (sides.has(WallMask.Down)) {
        drawLine(
            color = color,
            start = Offset(0f, size.height - halfStroke),
            end = Offset(size.width, size.height - halfStroke),
            strokeWidth = strokeWidthPx,
            cap = StrokeCap.Square
        )
    }
    if (sides.has(WallMask.Left)) {
        drawLine(
            color = color,
            start = Offset(0f, 0f),
            end = Offset(0f, size.height),
            strokeWidth = strokeWidthPx,
            cap = StrokeCap.Square
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
