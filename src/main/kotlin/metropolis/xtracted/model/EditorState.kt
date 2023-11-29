package metropolis.xtracted.model

import java.util.*
import metropolis.xtracted.view.CH

data class EditorState<D>(val title      : Translatable,
                          val locale     : Locale             = CH,
                          val undoState  : UndoState = UndoState(),
                          val attributes : List<Attribute<*>> = emptyList()) {
    val changed : Boolean
        get() = attributes.any{ it.changed }

    val valid : Boolean
        get() = attributes.all{ it.valid }
}

operator fun<T: Any> EditorState<*>.get(id : AttributeId) : Attribute<T> = attributes.find{it.id == id} as Attribute<T>
