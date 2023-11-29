package metropolis.xtracted.view.editor

import androidx.compose.material.Text
import java.util.*
import androidx.compose.runtime.Composable
import metropolis.xtracted.controller.editor.EditorAction
import metropolis.xtracted.model.EditorState
import metropolis.xtracted.view.ActionIconStrip
import metropolis.xtracted.view.AlignLeftRight
import metropolis.xtracted.view.CH
import metropolis.xtracted.view.Toolbar

@Composable
fun EditorBar(state: EditorState<*>, trigger : (EditorAction) -> Unit) {
    Toolbar {
        AlignLeftRight{
            ActionIconStrip(trigger,
                            listOf(
                                EditorAction.Save(state.changed && state.valid),
                                EditorAction.Reload),
                            listOf(
                                EditorAction.Undo(state.undoState.undoAvailable),
                                EditorAction.Redo(state.undoState.redoAvailable)),
                            listOf(
                                EditorAction.Add(),
                                EditorAction.Delete())
            )
            Text(if (state.locale == CH) state.title.german else state.title.english)
            ActionIconStrip(trigger,
                            listOf(
                                EditorAction.SetLocale(CH, state.locale != CH),
                                   EditorAction.SetLocale(Locale.ENGLISH, state.locale != Locale.ENGLISH))
                           )
        }
    }
}