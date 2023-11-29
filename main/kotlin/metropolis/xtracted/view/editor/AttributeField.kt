package metropolis.xtracted.view.editor

import java.util.*
import androidx.compose.foundation.border
import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.Card
import androidx.compose.material.Divider
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Error
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import metropolis.xtracted.controller.editor.EditorAction
import metropolis.xtracted.model.Attribute
import metropolis.xtracted.view.handCursor


@Composable
fun <T : Any> AttributeField(attribute: Attribute<T>,
                             locale   : Locale,
                             trigger  : (EditorAction) -> Unit,
                             modifier : Modifier = Modifier) {

    val focused = remember { mutableStateOf(false) }

    val focusedColor   = if (attribute.valid) FormColors.VALID.color else FormColors.ERROR.color
    val unfocusedColor = if (attribute.valid) FormColors.RIGHTTRACK.color else FormColors.ERROR.color
    val color          = if (focused.value) focusedColor else unfocusedColor

    Column(modifier = modifier.padding(vertical = 6.dp, horizontal = 5.dp)) {
        Label(attribute, color, focused.value, locale)
        InputElement(attribute, color, focused, trigger = trigger)
        ErrorMessage(attribute, locale)
    }
}

@Composable
private fun<T: Any> InputElement(attr   : Attribute<T>,
                                 color  : Color,
                                 focused: MutableState<Boolean>,
                                 trigger: (EditorAction) -> Unit){
    with(attr){
        Row(modifier = Modifier.fillMaxWidth()
                               .padding(start = 12.dp, end = 6.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp, Alignment.End)){

            BasicTextField(value         = valueAsText,
                           onValueChange = { trigger(EditorAction.Update(attr, it)) },
                           singleLine    = true,
                           readOnly      = readOnly,
                           textStyle     = TextStyle(color = FormColors.NORMALTEXT.color),
                           modifier      = Modifier.height(20.dp)
                                                   .weight(1.0f)
                                                   .onFocusChanged {  focused.value = it.isFocused}
                          )
            Unit(attr.unit)
        }

        Divider(color     = if (readOnly) Color.Transparent else color,
                thickness = if (focused.value) 2.dp else 1.dp)
    }

}

@Composable
private fun <T : Any> ErrorMessage(attr: Attribute<T>, locale   : Locale) {
    with(attr){
        val showErrorMsg = remember { mutableStateOf(false) }

        val errorIconWidth = 20.dp

        Row(horizontalArrangement = Arrangement.spacedBy(4.dp, Alignment.End),
            verticalAlignment     = Alignment.CenterVertically,
            modifier              = Modifier.fillMaxWidth()
                                            .height(20.dp)
                                            .padding(top = 2.dp, end = 6.dp)) {

            if (!valid && showErrorMsg.value) {
                BoxWithConstraints {
                    Row(verticalAlignment     = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.End,
                        modifier              = Modifier.width(maxWidth - errorIconWidth - 4.dp)) {
                        Card(backgroundColor = FormColors.ERROR.color,
                             modifier        = Modifier.border(0.dp, FormColors.ERROR.color, RoundedCornerShape(33))
                                                       .fillMaxHeight()) {
                            ErrorText(validationResult.message?.translate(locale))
                        }
                    }
                }
            }

            if (!valid) {
                ErrorIcon(showErrorMsg)
            }
        }
    }
}

@Composable
private fun Label(attr: Attribute<*>, color: Color, focused : Boolean, locale: Locale) {
    val labelAreaHeight = 32.dp
    Row(modifier = Modifier.height(labelAreaHeight)
                           .fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween) {
        Text(text       = "${attr.id.translate(locale)} ${if(attr.required) "*" else ""}" ,
             fontSize   = 12.sp,
             color      = color,
             modifier   = Modifier.align(Alignment.Top).padding(top = 8.dp),
             fontWeight = if (focused) FontWeight.Bold else FontWeight.Normal
            )
    }
}

@Composable
private fun ErrorIcon(showErrorMsg: MutableState<Boolean>) {
        IconButton(onClick  = { showErrorMsg.value = !showErrorMsg.value },
                   modifier = Modifier.clip(CircleShape)
                                      .size(20.dp)
                                      .handCursor()
                                      .focusable(false)) {

            Icon(imageVector        = Icons.Filled.Error,
                 contentDescription = "Error",
                 tint               = FormColors.ERROR.color,
                 modifier           = Modifier.focusable(false))
        }
}


@Composable
private fun ErrorText(message: String?) {
    message?.let {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Text(text       = it,
                 color      = FormColors.ERRORCONTRAST.color,
                 fontSize   = 10.sp,
                 fontWeight = FontWeight.Normal,
                 modifier   = Modifier.padding(start = 4.dp,
                                               end   = 4.dp,
                                               top   = 1.dp
                                              )
                )
        }
    }
}


@Composable
private fun Unit(unit: String) =
    Text(text = unit,
         color = FormColors.RIGHTTRACK.color)

enum class FormColors(val color : Color) {
    ERROR(Color(0xFF9F2C13)),
    ERRORCONTRAST(Color.White),
    VALID(Color(0xFF2e7d32)),
    RIGHTTRACK(Color.Gray),
    NORMALTEXT(Color.Black),

    BACKGROUND_COLOR_HEADER(Color(0xFF0E325E)),
    BACKGROUND_COLOR_GROUPS(Color(0xFF8698AE)),
    BACKGROUND_COLOR_LIGHT(Color(0xFFECEFF2)),
    FONT_ON_BACKGOUND(Color(0xFFE1E1E1)),
    DISABLED_ON_BACKGROUND(Color(0xFF787E85)),
    BODY_BACKGROUND(Color.White),
    LABEL(Color.DarkGray),
    SWITCH_THUMB(Color(0xFF0E325E)),
    UNCHECKEDTRACKCOLOR(Color.LightGray),
    BACKGROUND_COLOR_SELECTED_ELEMENT(Color(0xFFCFD4DA)),
}



