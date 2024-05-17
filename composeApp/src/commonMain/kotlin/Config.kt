import com.samadtch.bilinguai.ui.screens.boarding.HypeMessage

//TODO: Change Supported Languages
val languages = mapOf(
    "Arabic" to "ar",
    "English" to "en",
    "French" to "fr",
    "Italian" to "it",
    "Spanish" to "es",
)

//TODO: Change Hype Messages
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