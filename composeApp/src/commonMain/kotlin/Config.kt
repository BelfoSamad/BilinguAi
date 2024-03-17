import com.samadtch.bilinguai.models.pojo.inputs.BaseInput
import com.samadtch.bilinguai.models.pojo.inputs.OptionsInput
import com.samadtch.bilinguai.models.pojo.inputs.TextInput
import com.samadtch.bilinguai.ui.screens.boarding.HypeMessage

fun getInputs(): List<List<BaseInput>> {
    return listOf(
        listOf(
            TextInput(
                key = "topic",
                label = "Topic",
                lines = null,
                hint = "What to talk about...",
                defaultValue = null
            ),
            OptionsInput(
                key = "language",
                label = "Languages",
                options = listOf(
                    "English",
                    "French",
                    "Italian",
                    "Spanish"
                ),
                hint = "",
                maxSelection = 1,
                minSelection = 1,
                multiSelection = false
            )
        )
    )
}

fun getHypes(): List<HypeMessage> {
    return listOf(
        HypeMessage(
            start = "Generate a conversation about ",
            word = listOf("Sports", "Art", "Food"),
            end = ""
        ),
        HypeMessage(
            start = "Make the conversation in ",
            word = listOf("English", "French", "Spanish"),
            end = ""
        ),
        HypeMessage(
            start = "Make the conversation ",
            word = listOf("Conservative", "Normal", "Creative"),
            end = ""
        )
    )
}