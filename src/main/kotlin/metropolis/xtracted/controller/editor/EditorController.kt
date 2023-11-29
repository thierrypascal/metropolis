package metropolis.xtracted.controller.editor

import java.util.*
import metropolis.xtracted.controller.ControllerBase
import metropolis.xtracted.controller.Scheduler
import metropolis.xtracted.controller.undo.Snapshot
import metropolis.xtracted.controller.undo.UndoController
import metropolis.xtracted.model.Attribute
import metropolis.xtracted.model.AttributeId
import metropolis.xtracted.model.EditorState
import metropolis.xtracted.model.Translatable
import metropolis.xtracted.model.UndoState
import metropolis.xtracted.model.ValidationResult
import metropolis.xtracted.repository.CrudRepository
import metropolis.xtracted.repository.Identifiable

val ch = Locale("de", "CH")


class EditorController<D: Identifiable>(val id: Int,
                                        val repository      : CrudRepository<D>,
                                        val asData          : (List<Attribute<*>>) -> D,
                                        val asAttributeList : (D) -> List<Attribute<*>>,
                                        val onSave          : (Int) -> Unit,
                                        val onAdd           : (Int) -> Unit,
                                        val onDelete        : (Int) -> Unit,
                                        title               : Translatable,
                                        locale              : Locale) :
        ControllerBase<EditorState<D>, EditorAction>(initialState = EditorState(title      = title,
                                                                                locale     = locale,
                                                                                attributes = asAttributeList(repository.read(id)!!))
        ) {

    private val undoController = UndoController<EditorState<D>>()

    private val undoStackScheduler = Scheduler(200L)
    private var debounceStart = state

    override fun executeAction(action: EditorAction): EditorState<D> = when (action) {
        is EditorAction.Update    -> update(action.attribute, action.value)

        is EditorAction.Reload    -> reload()
        is EditorAction.Save      -> save()

        is EditorAction.Undo      -> undo()
        is EditorAction.Redo      -> redo()

        is EditorAction.SetLocale -> setLocale(action.locale)

        is EditorAction.Add       -> add()
        is EditorAction.Delete    -> delete()
    }

    private fun add() : EditorState<D> {
        onAdd(id)
        return state
    }

    private fun delete() : EditorState<D> {
        onDelete(id)
        return state
    }

    private fun update(attribute: Attribute<*>, valueAsText: String) : EditorState<D> {
        val nextEditorState = state.copy(attributes = state.attributes.replaceAll(setValueAsText(attribute, valueAsText)))
        scheduleUndoStackUpdate(nextEditorState)

        return nextEditorState
    }

    private fun reload() =
        state.copy(attributes = asAttributeList(repository.read(id)!!), undoState = UndoState())

    private fun save() : EditorState<D> {
        repository.update(asData(state.attributes))
        val updatedAttributes = buildList {
            for(attribute in state.attributes){
                add(attribute.copy(persistedValue = attribute.value))
            }

        }

        onSave(id)

        return state.copy(attributes =  updatedAttributes)
    }

    private fun undo() : EditorState<D> {
        val oldState = undoController.undo()
        val nextEditorState = oldState?.copy(undoState = undoController.undoState) ?: state
        debounceStart = nextEditorState
        return nextEditorState
    }

    private fun redo() : EditorState<D> {
        val newState = undoController.redo()
        val nextEditorState = newState?.copy(undoState = undoController.undoState) ?: state
        debounceStart = nextEditorState
        return nextEditorState
    }

    private fun setLocale(locale: Locale) : EditorState<D> =
        state.copy(locale = locale)

    private fun<T : Any> setValueAsText(attribute: Attribute<T>, valueAsText: String): List<Attribute<*>> {
        if(attribute.readOnly){
            return listOf( attribute)
        }
        else {
            if(attribute.required && valueAsText.isBlank()){
                return listOf( attribute.copy (valueAsText = valueAsText, validationResult = ValidationResult(false,
                    ErrorMessage.REQUIRED
                )
                ))
            }

            var validationResult = attribute.syntaxValidator(valueAsText)
            if(validationResult.valid){
                validationResult = attribute.semanticValidator(attribute.converter(valueAsText))
            }

            return when {
                validationResult.valid -> {
                    val newValue = attribute.converter(valueAsText)
                    buildList{
                    val updatedAttribute =  attribute.copy(valueAsText            = attribute.formatter(newValue, valueAsText),
                                                           value                  = newValue,
                                                           validationResult       = validationResult)
                        add(updatedAttribute)
                        attribute.dependentAttributes.forEach{ entry ->
                            val dependentAttribute = state.attributes.first { it.id == entry.key }
                            var updatedDependentAttribute = entry.value(updatedAttribute, dependentAttribute)
                            updatedDependentAttribute = if(updatedDependentAttribute.required && updatedDependentAttribute.value == null){
                                updatedDependentAttribute.copy(validationResult = ValidationResult(false,
                                    ErrorMessage.REQUIRED
                                )
                                )
                            } else {
                                updatedDependentAttribute.copy(validationResult = updatedDependentAttribute.syntaxValidator(updatedDependentAttribute.valueAsText))
                            }

                            add(updatedDependentAttribute)
                        }
                    }

                }

                else                   -> { listOf(attribute.copy(valueAsText      = valueAsText,
                                                                  validationResult = validationResult)
                                                  )
                                           }
            }

        }
    }

    private fun List<Attribute<*>>.replaceAll(attributes: List<Attribute<*>>) : List<Attribute<*>>{
        return toMutableList()
            .apply {
                for(attribute in attributes){
                    val idx = indexOfFirst { attribute.id == it.id }

                    removeAt(idx)
                    add(idx, attribute)
                }
            }
    }

    /**
     *  to enable a "bulk-undo" we have to wait until there is no new action triggered
     */
    private fun scheduleUndoStackUpdate(newState: EditorState<D>) {
        undoStackScheduler.scheduleTask {
            undoController.pushOnUndoStack(Snapshot(debounceStart, newState))
            state = state.copy(undoState = undoController.undoState)
            debounceStart = state
        }
    }
}

operator fun<T> List<Attribute<*>>.get(attributeId: AttributeId) : T = first{it.id == attributeId}.value as T


private enum class ErrorMessage(override val german: String, override val english: String) : Translatable {
    REQUIRED("obligatorisches Feld", "required field")
}