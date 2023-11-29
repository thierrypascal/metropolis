package metropolis.xtracted.view

import androidx.compose.foundation.HorizontalScrollbar
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.VerticalScrollbar
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.focusable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.rememberScrollbarAdapter
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Icon
import androidx.compose.material.LocalTextStyle
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.KeyEventType
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.input.key.type
import androidx.compose.ui.input.pointer.PointerEventType
import androidx.compose.ui.input.pointer.onPointerEvent
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import metropolis.xtracted.controller.lazyloading.LazyTableAction
import metropolis.xtracted.data.SortDirection
import metropolis.xtracted.data.UNORDERED
import metropolis.xtracted.model.StringColumn
import metropolis.xtracted.model.TableColumn
import metropolis.xtracted.model.TableState


private val gutterWidth    = 55.dp
private val scrollBarWidth = 15.dp

private val tableBackground = Color.Transparent

private val cellBorderColor         = Color.Gray
private val tableCellBackground     = Color.White
private val darkTableCellBackground = Color(250, 250, 250)

private val headerCellBackground     = Color(230, 230, 230)
private val darkHeaderCellBackground = Color(210, 210, 210)
private val filterCellBackground     = Color(250, 250, 250)
private val darkFilterCellBackground = Color(240, 240, 240)

private val selectedItemBorder       = Color(80, 80, 80)


@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun <T> Table(tableState  : TableState<T>,
              itemProvider: (Int) -> T,
              idProvider  : (T) -> Int,
              trigger     : (LazyTableAction) -> Unit,
              modifier    : Modifier = Modifier) {

    with(tableState){
        Box(modifier = modifier.background(color = tableBackground)
                               .focusRequester(tableState.focusRequester)
                               .focusable()
                               .onPreviewKeyEvent {
                                   if (it.key == Key.DirectionUp && it.type == KeyEventType.KeyDown) {
                                       trigger(LazyTableAction.SelectPrevious)
                                       true
                                   }
                                   else if(it.key == Key.DirectionDown && it.type == KeyEventType.KeyDown){
                                       trigger(LazyTableAction.SelectNext)
                                       true
                                   }
                                   else false
                               }) {

            val horizontalScrollState = rememberScrollState()

            Column(Modifier.align(Alignment.CenterStart)
                           .padding(end = scrollBarWidth)) {

                HeaderRow(this@with, horizontalScrollState, trigger)

                if(filteredCount == 0){
                    NoItemsBox(Modifier.weight(1.0f, true))
                }
                else {
                    LazyColumn(modifier = Modifier.padding(bottom = scrollBarWidth)
                                                  .weight(1.0f)
                                                  .border(color = cellBorderColor, width = Dp.Hairline),
                               state    = lazyListState) {
                        itemsIndexed(items = allIds) { idx, id ->
                            TableRow(this@with, idx, itemProvider(id), idProvider, trigger, horizontalScrollState)
                        }
                    }
                }
            }

            VerticalScrollbar(modifier = Modifier.align(Alignment.CenterEnd)
                                                 .width(scrollBarWidth)
                                                 .background(tableBackground)
                                                 .padding(start = 3.dp, end = 3.dp, top = 60.dp, bottom = scrollBarWidth + 3.dp),
                              adapter = rememberScrollbarAdapter(scrollState = lazyListState)
                             )

            val indent = 60.dp.value + columns.filter { it.fixed && it.width != Dp.Unspecified }.sumOf { it.width.value.toDouble()}
            HorizontalScrollbar(modifier = Modifier.height(scrollBarWidth)
                                                   .fillMaxWidth()
                                                   .background(tableBackground)
                                                   .align(Alignment.BottomCenter)
                                                   .padding(start = indent.dp, end = scrollBarWidth, bottom = 3.dp, top = 3.dp),
                                adapter = rememberScrollbarAdapter(horizontalScrollState))

            Text(text     = "$filteredCount/$totalCount",
                 fontSize = 9.sp,
                 modifier = Modifier.align(Alignment.BottomStart)
                )
        }
    }
}

@Composable
private fun NoItemsBox(modifier: Modifier){
    Box(modifier = modifier.padding(bottom = scrollBarWidth)
                           .fillMaxWidth()
                           .background(tableCellBackground)
                           .border(color = cellBorderColor, width = Dp.Hairline),
        contentAlignment = Alignment.Center){
        Text("No items found")
    }
}


@Composable
private fun<T> HeaderRow(tableState: TableState<T>, scrollState: ScrollState, trigger: (LazyTableAction) -> Unit) {
    with(tableState){
        Row(Modifier.fillMaxWidth()
                    .height(IntrinsicSize.Max)){
            HeaderCell(tableState, StringColumn("", width = gutterWidth, valueProvider = {""}), background = darkHeaderCellBackground, filterBackground = darkFilterCellBackground, trigger = trigger)
            columns.filter { it.fixed }
                .forEach {
                    HeaderCell(tableState       = tableState,
                               column           = it,
                               trigger          = trigger,
                               background       = darkHeaderCellBackground,
                               filterBackground = darkFilterCellBackground
                    )
                }
            Spacer(Modifier.width(2.dp))
            Row(modifier = Modifier.height(IntrinsicSize.Max)
                                   .fillMaxWidth()
                                   .horizontalScroll(scrollState)) {
                columns.filter { !it.fixed }
                    .forEach { HeaderCell(tableState = tableState,
                                          column     = it,
                                          trigger    = trigger
                                         ) }

                HeaderCell(tableState, StringColumn(header = "", width  = Dp.Unspecified, valueProvider = { "" }), trigger = trigger)
            }
        }
    }
}


@Composable
private fun<T> RowScope.HeaderCell(tableState: TableState<T>, column: TableColumn<T, *>, trigger: (LazyTableAction) -> Unit, background: Color = headerCellBackground, filterBackground: Color = filterCellBackground, borderColor: Color = cellBorderColor){
    val modifier = if(Dp.Unspecified != column.width) Modifier.width(column.width) else Modifier.weight(1f)
    Column(modifier = modifier.border(color = borderColor, width = Dp.Hairline, shape = RectangleShape)
                              .fillMaxHeight()
                              .background(background)){
        Box{
            Text(text     = column.header,
                 modifier = Modifier.padding(bottom = 2.dp)
                                    .fillMaxWidth()
                                    .align(Alignment.Center)
                                    .padding(5.dp)
                                    .clickable(enabled = column.dbColumn != null) { trigger(LazyTableAction.ToggleSortOrder(column)) }
                                    .handCursor(),
                 textAlign = TextAlign.Center,
                 maxLines  = 1,
                 overflow  = TextOverflow.Ellipsis
            )
            if (tableState.currentSort != UNORDERED &&
                null != column.dbColumn &&
                tableState.currentSort.column == column.dbColumn) {
                val icon = if(tableState.currentSort.direction == SortDirection.ASC) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown
                Icon(imageVector        = icon,
                     contentDescription = "Sort",
                     modifier           = Modifier.padding(end = 8.dp).size(14.dp).align(Alignment.CenterEnd))
            }
        }

        if(null != column.dbColumn){
            BasicTextField(value         = column.filterAsText,
                           onValueChange = { trigger(LazyTableAction.SetFilter(column, it)) },
                           singleLine    = true,
                           textStyle     = LocalTextStyle.current.copy(textAlign = TextAlign.Center,
                                                                       color     = if(column.validFilterDescription()) Color.Black else Color.Red,
                                                                       fontSize  = 12.sp,
                                                                       fontStyle = FontStyle.Italic),
                           modifier      = modifier.padding(start = 6.dp, end = 6.dp, bottom = 4.dp)
                                                   .background(filterBackground, RoundedCornerShape(5.dp))
                                                   .padding(4.dp)
                                                   .fillMaxWidth()
            )
        }
    }
}


@Composable
private fun<T> TableRow(tableState: TableState<T>, idx: Int, item: T, idProvider: (T) -> Int, trigger: (LazyTableAction) -> Unit, scrollState: ScrollState){
    with(tableState){
        var modifier = Modifier.fillMaxWidth()
                               .height(IntrinsicSize.Max)
                               .clickable { trigger(LazyTableAction.Select(idProvider(item))) }

        if(tableState.selectedId == idProvider(item)){
            modifier = modifier.border(width = 2.dp, color = selectedItemBorder , shape = RoundedCornerShape(6.dp))
        }

        Row(modifier = modifier,
            verticalAlignment = Alignment.CenterVertically){

            GutterCell(text = "$idx")
            columns.filter { it.fixed }
                .forEach{
                    TableCell(text          = it.convert(it.valueProvider(item)),
                              textAlignment = it.alignment,
                              background    = darkTableCellBackground,
                              width         = it.width)
                }
            Spacer(Modifier.width(2.dp))
            Row(modifier          = Modifier.height(IntrinsicSize.Max)
                                            .fillMaxWidth()
                                            .horizontalScroll(scrollState),
                verticalAlignment = Alignment.CenterVertically) {

                columns.filter { !it.fixed }
                    .forEach {
                        TableCell(text          = it.convert(it.valueProvider(item)),
                                  textAlignment = it.alignment,
                                  width         = it.width)
                    }
                TableCell(text = "",
                          width = Dp.Unspecified)
            }
        }
    }

}

@Composable
private fun RowScope.GutterCell(text: String){
    TableCell(textAlignment = Alignment.CenterEnd, text = text, width = gutterWidth, fontSize = 9.sp, background = headerCellBackground)
}


@OptIn(ExperimentalComposeUiApi::class)
@Composable
private fun RowScope.TableCell(text: String, textAlignment: Alignment = Alignment.Center, background: Color =  tableCellBackground, borderColor: Color = cellBorderColor, width: Dp, fontSize: TextUnit = TextUnit.Unspecified) {
    val modifier = if(Dp.Unspecified != width) Modifier.width(width) else Modifier.weight(1f)
    Box(modifier = modifier.fillMaxHeight()
                           .background(color = background)
                           .border(color = borderColor, width = Dp.Hairline, shape = RectangleShape)
                           .padding(10.dp),
        contentAlignment = textAlignment
    ) {
        val clipboard = LocalClipboardManager.current
        Text(text     = text,
             maxLines = 1,
             overflow = TextOverflow.Ellipsis,
             fontSize = fontSize,
                                 modifier = Modifier.onPointerEvent(PointerEventType.Press){
                                     clipboard.setText(AnnotatedString(text))
                                 }
        )
    }
}

private fun<T, V> TableColumn<T, V>.convert(value: Any?): String =  formatter(value as V)
