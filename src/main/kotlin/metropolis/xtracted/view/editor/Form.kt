package metropolis.xtracted.view.editor

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import metropolis.xtracted.controller.editor.EditorAction
import metropolis.xtracted.model.EditorState

@Composable
fun <T> Form(state: EditorState<T>, trigger: (EditorAction) -> Unit) {
    LazyVerticalGrid(columns               = GridCells.Fixed(2),
        contentPadding        = PaddingValues(20.dp),
        horizontalArrangement = Arrangement.spacedBy(30.dp),
        verticalArrangement   = Arrangement.spacedBy(15.dp)) {
        with(state) {
            attributes.forEach {
                item(span    = { GridItemSpan(it.span) },
                    content = { AttributeField(attribute = it, locale = locale, trigger = trigger) })
            }
        }
    }
}