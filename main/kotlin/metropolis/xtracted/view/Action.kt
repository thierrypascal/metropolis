package metropolis.xtracted.view

import java.awt.Cursor
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.Button
import androidx.compose.material.Icon
import androidx.compose.material.IconButton
import androidx.compose.material.MaterialTheme
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import metropolis.xtracted.controller.Action


@Composable
fun <A : Action> ActionIconStrip(trigger: (A) -> Unit, vararg actionGroup: List<A>) {
    Row(modifier          = Modifier.height(IntrinsicSize.Max),
        verticalAlignment = Alignment.CenterVertically,
        content           = { for(actionList in actionGroup) {
                               for(action in actionList){
                                   ActionIcon(trigger = trigger, action = action) }
                               VerticalDivider()
                               }
                            })
}

@Composable
fun <A: Action> ActionButtonStrip(trigger: (A) -> Unit, actionGroup: List<A>, modifier: Modifier = Modifier) {
    Row(modifier              = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment     = Alignment.CenterVertically,
        content               = { for(action in actionGroup) { ActionButton(trigger = trigger, action = action) } })
}

@Composable
fun <A: Action> ActionClickableText(trigger: (A) -> Unit, action: A){
    Text(text     = action.name,
         color    = Color.Blue,
         style    = MaterialTheme.typography.body2,
         modifier = Modifier.padding(8.dp)
                            .cursor(if(action.enabled) Cursor.HAND_CURSOR else Cursor.DEFAULT_CURSOR)
                            .clickable(onClick = { trigger(action) })
        )
}

@Composable
fun <A: Action> ActionButton(trigger: (A) -> Unit, action: A){
    Button(modifier = Modifier.cursor(if(action.enabled) Cursor.HAND_CURSOR else Cursor.DEFAULT_CURSOR),
           onClick  = { trigger(action) },
           enabled  = action.enabled,
           content  = { Text(action.name) })
}

@Composable
fun <A: Action> ActionIcon(trigger: (A) -> Unit, action: A){
    withTooltip(control = { IconButton(modifier = Modifier.cursor(if(action.enabled) Cursor.HAND_CURSOR else Cursor.DEFAULT_CURSOR),
                                       onClick  = { trigger(action) },
                                       enabled  = action.enabled,
                                       content  = { if(action.icon != null) Icon(action.icon!!, action.name) else Text(action.name) }) },
                tooltip = { Text(text       = action.name,
                                 fontSize   = 12.sp,
                                 fontWeight = FontWeight.Light,
                                 modifier   = Modifier.padding(5.dp)
                                ) }
               )
}
