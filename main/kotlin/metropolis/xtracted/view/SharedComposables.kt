package metropolis.xtracted.view

import java.awt.Cursor
import java.awt.Toolkit
import java.io.IOException
import java.io.InputStream
import java.net.URL
import java.util.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.Image
import androidx.compose.foundation.TooltipArea
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowDropDown
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.input.pointer.PointerIcon
import androidx.compose.ui.input.pointer.pointerHoverIcon
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.res.loadImageBitmap
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp


@Composable
fun VerticalDivider(){
    Box(modifier = Modifier.fillMaxHeight()
                           .padding(vertical = 14.dp)
                           .width(1.dp)
                           .background(MaterialTheme.colors.onPrimary.copy(alpha = 0.5f)))
}


@Composable
fun<T> SelectionBox(currentSelectionTitle: String, allItems: List<T>, listItem: @Composable (T) -> Unit, onClick: () -> Unit, onItemClick: (T) -> Unit) {
    var dropDownExpanded by remember { mutableStateOf(false) }

    Box(modifier = Modifier.width(150.dp)
                           .background(Color.Transparent)
                           .padding(5.dp)
                           .border(width = 0.5.dp, color = MaterialTheme.colors.onSurface.copy(alpha = 0.5f), shape = RoundedCornerShape(8.dp))
                           .background(Color.White, shape = RoundedCornerShape(8.dp))
                           .handCursor()
                           .clickable(indication        = null,
                                      interactionSource = remember { MutableInteractionSource() },
                                      onClick           = { onClick()
                                                            dropDownExpanded = !dropDownExpanded
                                                          })) {
        Text(text     = currentSelectionTitle,
             modifier = Modifier.align(Alignment.CenterStart)
                                .padding(start = 8.dp)
                                .matchParentSize())
        Icon(imageVector        = Icons.Filled.ArrowDropDown,
             contentDescription = "Arrow DropDown",
             modifier           = Modifier.padding(8.dp)
                                          .size(20.dp, 20.dp)
                                          .align(Alignment.CenterEnd),
             tint               = MaterialTheme.colors.onSurface)

        DropdownMenu(expanded         = dropDownExpanded,
                     onDismissRequest = { dropDownExpanded = false },
                     modifier         = Modifier.background(MaterialTheme.colors.surface)) {
            if (allItems.isEmpty()) {
                Text(text     = "No items yet",
                     modifier = Modifier.padding(10.dp))
            } else {
                allItems.forEach {
                    DropdownMenuItem(onClick = { onItemClick(it)
                                                 dropDownExpanded = false
                                                }) {
                        listItem(it)
                    }
                }
            }
        }
    }
}


@Composable
fun Toolbar(content: @Composable () -> Unit) {
    Row(modifier = Modifier.fillMaxWidth()
                           .shadow(2.dp)
                           .background(color = Color.LightGray)
                           .height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically) {
        content()
    }
}

@Composable
fun Link(url: String, text: String = "read more...") {
    val uriHandler = LocalUriHandler.current
    withTooltip(control = { Text(text     = text,
                                 color    = Color.Blue,
                                 style    = MaterialTheme.typography.body2,
                                 maxLines = 1,
                                 overflow = TextOverflow.Ellipsis,
                                 modifier = Modifier.padding(8.dp)
                                                    .cursor(Cursor.HAND_CURSOR)
                                                    .clickable(onClick = {
                                                        try {
                                                            uriHandler.openUri("$url")
                                                        } catch (e: IOException) {
                                                            Toolkit.getDefaultToolkit().beep()
                                                        }
                                                    })
                                )},
               tooltip = { Text(text       = url,
                                fontSize   = 12.sp,
                                fontWeight = FontWeight.Light,
                                modifier   = Modifier.padding(5.dp)
                               ) })

}

@Composable
fun AlignRight(modifier: Modifier = Modifier, content: @Composable () -> Unit) {
    Row(verticalAlignment = Alignment.CenterVertically,
        modifier          = modifier.fillMaxWidth()){
        Spacer(Modifier.weight(1.0f))
        content()
    }
}

@Composable
fun AlignLeftRight(content: @Composable () -> Unit){
    Row(modifier              = Modifier.fillMaxWidth(),
        verticalAlignment     = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween) {
        content()
    }
}

@Composable
fun ErrorMessage(message: String){
    Text(text  = message,
         color = Color.Red)
}

@Composable
fun VSpace(space: Dp) {
    Spacer(modifier = Modifier.height(space))
}

@Composable
fun VulcanSalute(size: TextUnit = 73.sp) {
    Text(text     = "\uD83D\uDD96",
         fontSize = 73.sp)
}

private val tooltipBackground = Color(255, 255, 210)

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun withTooltip(control : @Composable () -> Unit, tooltip : @Composable () -> Unit){
    // see: https://github.com/JetBrains/compose-jb/tree/master/tutorials/Desktop_Components#tooltips
    TooltipArea(tooltip = { // composable tooltip content
        Surface(modifier = Modifier.shadow(4.dp),
                color    = tooltipBackground,
                shape    = RoundedCornerShape(4.dp),
                content  = tooltip) },
                content =  control
               )
}

@Composable
fun PictureBox(url: String, description: String, caption: String, defaultImage: ImageBitmap) {
    val currentImage = remember{ mutableStateOf(defaultImage) }

    LaunchedEffect(url) {
        currentImage.value = defaultImage
        try {
            currentImage.value = getImageBitmapFromStream(withContext(Dispatchers.IO) {
                URL(url).openStream() })
        }catch (_: Exception){}
    }

    Column {
        Card(elevation = 2.dp,
             shape     = RoundedCornerShape(8.dp),
             modifier  = Modifier.size(300.dp, 200.dp)) {
            Image(bitmap             = currentImage.value,
                  contentDescription = "Image of $description",
                  contentScale       = ContentScale.Crop)
        }
        Text(text       = caption,
             fontWeight = FontWeight.ExtraLight,
             modifier   = Modifier.align(Alignment.CenterHorizontally))
    }
}

private fun getImageBitmapFromStream(stream : InputStream) =
    stream.buffered()                  // kleine Optimierung: auf einem BufferedInputStream arbeiten
        .use { loadImageBitmap(it) }  // liest ImageBitmap vom Stream


fun Modifier.cursor(cursorId: Int) : Modifier = pointerHoverIcon(PointerIcon(Cursor(cursorId)))

fun Modifier.handCursor() = cursor(Cursor.HAND_CURSOR)

val CH = Locale("de", "CH")

fun Double?.format(userInput: String, nullFormat: String = "?", locale: Locale = CH) : String =
    if (null == this) {
        nullFormat
    } else {
        val pattern = when {
                          userInput.endsWith(".") -> "%,.0f."
                          userInput.contains(".") -> "%,.${userInput.length - 1 - userInput.indexOf(".")}f"
                          else                    -> "%,.0f"
                      }
        pattern.format(locale, this)
    }

fun Number?.pp(pattern: String, nullFormat: String = ""): String {
    return if (null == this) nullFormat else pattern.format(CH, this)
}

fun String?.format(nullFormat : String = "") = this ?: nullFormat

