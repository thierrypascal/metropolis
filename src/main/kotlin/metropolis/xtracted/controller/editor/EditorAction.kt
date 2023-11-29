package metropolis.xtracted.controller.editor

import java.util.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*
import androidx.compose.ui.graphics.vector.ImageVector
import metropolis.xtracted.controller.Action
import metropolis.xtracted.model.Attribute

sealed class EditorAction(
        override val name   : String,
        override val icon   : ImageVector? = null,
        override val enabled: Boolean) : Action {

    class Update(val attribute: Attribute<*>, val value: String) : EditorAction("Update ${attribute.id.translate(Locale.ENGLISH)}", null, !attribute.readOnly)

    object Reload                                   : EditorAction("Reload", Icons.Outlined.Cached, true)
    class Save(enabled: Boolean)                    : EditorAction("Save", Icons.Outlined.Save, enabled)

    class SetLocale(val locale: Locale, enabled: Boolean) : EditorAction(locale.isO3Language, null, enabled)

    class Undo(enabled: Boolean = true)             : EditorAction("Undo", Icons.Outlined.Undo, enabled)
    class Redo(enabled: Boolean = true)             : EditorAction("Redo", Icons.Outlined.Redo, enabled)

    class Add(enabled: Boolean = true)              : EditorAction("Add"    , Icons.Outlined.Add   , enabled)
    class Delete(enabled: Boolean = true)           : EditorAction("Delete" , Icons.Outlined.Delete, enabled)

}