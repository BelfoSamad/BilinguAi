package com.samadtch.bilinguai.data.repositories.fake

import com.samadtch.bilinguai.data.repositories.base.DataRepository
import com.samadtch.bilinguai.models.Data
import kotlinx.coroutines.delay

class FakeDataRepository : DataRepository {

    //Success
    override suspend fun generateData(inputs: Map<String, Any>, temperature: Float): Result<Data> {
        delay(2000)
        return Result.success(
            Data(
                "d120",
                language = "English",
                topic = "Test adding convo",
                conversation = listOf(
                    "Hey, how's it going?",
                    "Not bad, just chilling. You?",
                    "Just finished a book, any recommendations?",
                    "Oh nice! What genre are you into?",
                    "Mostly mystery and thriller.",
                    "Have you read 'Gone Girl'? It's fantastic!",
                    "Yes! Loved it. Looking for something similar.",
                    "Try 'The Girl on the Train', it's gripping!",
                    "Sounds intriguing, I'll check it out. Thanks!",
                    "No problem, hope you enjoy it!",
                    "Definitely will. Anyway, gotta run now. Talk later!",
                    "Sure thing, catch you later!"
                ),
                vocabulary = mapOf(
                    "Chilling" to "Relaxing or hanging out casually",
                    "Recommendations" to "Suggestions or advice given to someone",
                    "Genre" to "A category or type, especially in literature or art",
                    "Intriguing" to "Fascinating or arousing curiosity",
                    "Gripping" to "Engrossing or compelling, holding one's attention tightly"
                ),
                createdAt = 1L
            )
        )
    }

    //Success
    override suspend fun getData(): Result<List<Data>> {
        delay(3000)
        return Result.success(listOf(
            Data(
                "d1",
                language = "English",
                topic = "Angry Things Mega first testing the long title and the deletion process",
                conversation = listOf(
                    "Hey, how's it going?",
                    "Not bad, just chilling. You?",
                    "Just finished a book, any recommendations?",
                    "Oh nice! What genre are you into?",
                    "Mostly mystery and thriller.",
                    "Have you read 'Gone Girl'? It's fantastic!",
                    "Yes! Loved it. Looking for something similar.",
                    "Try 'The Girl on the Train', it's gripping!",
                    "Sounds intriguing, I'll check it out. Thanks!",
                    "No problem, hope you enjoy it!",
                    "Definitely will. Anyway, gotta run now. Talk later!",
                    "Sure thing, catch you later!"
                ),
                vocabulary = mapOf(
                    "Chilling" to "Relaxing or hanging out casually",
                    "Recommendations" to "Suggestions or advice given to someone",
                    "Genre" to "A category or type, especially in literature or art",
                    "Intriguing" to "Fascinating or arousing curiosity",
                    "Gripping" to "Engrossing or compelling, holding one's attention tightly"
                ),
                createdAt = 1L
            ),
            Data(
                "d2",
                language = "English",
                topic = "Angry Things",
                conversation = listOf(
                    "Hey, how's it going?",
                    "Not bad, just chilling. You?",
                    "Just finished a book, any recommendations?",
                    "Oh nice! What genre are you into?",
                    "Mostly mystery and thriller.",
                    "Have you read 'Gone Girl'? It's fantastic!",
                    "Yes! Loved it. Looking for something similar.",
                    "Try 'The Girl on the Train', it's gripping!",
                    "Sounds intriguing, I'll check it out. Thanks!",
                    "No problem, hope you enjoy it!",
                    "Definitely will. Anyway, gotta run now. Talk later!",
                    "Sure thing, catch you later!"
                ),
                vocabulary = mapOf(
                    "Chilling" to "Relaxing or hanging out casually",
                    "Recommendations" to "Suggestions or advice given to someone",
                    "Genre" to "A category or type, especially in literature or art",
                    "Intriguing" to "Fascinating or arousing curiosity",
                    "Gripping" to "Engrossing or compelling, holding one's attention tightly"
                ),
                createdAt = 1L
            ),
            Data(
                "d3",
                language = "English",
                topic = "Angry Things",
                conversation = listOf(
                    "Hey, how's it going?",
                    "Not bad, just chilling. You?",
                    "Just finished a book, any recommendations?",
                    "Oh nice! What genre are you into?",
                    "Mostly mystery and thriller.",
                    "Have you read 'Gone Girl'? It's fantastic!",
                    "Yes! Loved it. Looking for something similar.",
                    "Try 'The Girl on the Train', it's gripping!",
                    "Sounds intriguing, I'll check it out. Thanks!",
                    "No problem, hope you enjoy it!",
                    "Definitely will. Anyway, gotta run now. Talk later!",
                    "Sure thing, catch you later!"
                ),
                vocabulary = mapOf(
                    "Chilling" to "Relaxing or hanging out casually",
                    "Recommendations" to "Suggestions or advice given to someone",
                    "Genre" to "A category or type, especially in literature or art",
                    "Intriguing" to "Fascinating or arousing curiosity",
                    "Gripping" to "Engrossing or compelling, holding one's attention tightly"
                ),
                createdAt = 1L
            ),
            Data(
                "d4",
                language = "English",
                topic = "Angry Things",
                conversation = listOf(
                    "Hey, how's it going?",
                    "Not bad, just chilling. You?",
                    "Just finished a book, any recommendations?",
                    "Oh nice! What genre are you into?",
                    "Mostly mystery and thriller.",
                    "Have you read 'Gone Girl'? It's fantastic!",
                    "Yes! Loved it. Looking for something similar.",
                    "Try 'The Girl on the Train', it's gripping!",
                    "Sounds intriguing, I'll check it out. Thanks!",
                    "No problem, hope you enjoy it!",
                    "Definitely will. Anyway, gotta run now. Talk later!",
                    "Sure thing, catch you later!"
                ),
                vocabulary = mapOf(
                    "Chilling" to "Relaxing or hanging out casually",
                    "Recommendations" to "Suggestions or advice given to someone",
                    "Genre" to "A category or type, especially in literature or art",
                    "Intriguing" to "Fascinating or arousing curiosity",
                    "Gripping" to "Engrossing or compelling, holding one's attention tightly"
                ),
                createdAt = 1L
            ),
            Data(
                "d5",
                language = "English",
                topic = "Angry Things",
                conversation = listOf(
                    "Hey, how's it going?",
                    "Not bad, just chilling. You?",
                    "Just finished a book, any recommendations?",
                    "Oh nice! What genre are you into?",
                    "Mostly mystery and thriller.",
                    "Have you read 'Gone Girl'? It's fantastic!",
                    "Yes! Loved it. Looking for something similar.",
                    "Try 'The Girl on the Train', it's gripping!",
                    "Sounds intriguing, I'll check it out. Thanks!",
                    "No problem, hope you enjoy it!",
                    "Definitely will. Anyway, gotta run now. Talk later!",
                    "Sure thing, catch you later!"
                ),
                vocabulary = mapOf(
                    "Chilling" to "Relaxing or hanging out casually",
                    "Recommendations" to "Suggestions or advice given to someone",
                    "Genre" to "A category or type, especially in literature or art",
                    "Intriguing" to "Fascinating or arousing curiosity",
                    "Gripping" to "Engrossing or compelling, holding one's attention tightly"
                ),
                createdAt = 1L
            ),
            Data(
                "d6",
                language = "English",
                topic = "Angry Things",
                conversation = listOf(
                    "Hey, how's it going?",
                    "Not bad, just chilling. You?",
                    "Just finished a book, any recommendations?",
                    "Oh nice! What genre are you into?",
                    "Mostly mystery and thriller.",
                    "Have you read 'Gone Girl'? It's fantastic!",
                    "Yes! Loved it. Looking for something similar.",
                    "Try 'The Girl on the Train', it's gripping!",
                    "Sounds intriguing, I'll check it out. Thanks!",
                    "No problem, hope you enjoy it!",
                    "Definitely will. Anyway, gotta run now. Talk later!",
                    "Sure thing, catch you later!"
                ),
                vocabulary = mapOf(
                    "Chilling" to "Relaxing or hanging out casually",
                    "Recommendations" to "Suggestions or advice given to someone",
                    "Genre" to "A category or type, especially in literature or art",
                    "Intriguing" to "Fascinating or arousing curiosity",
                    "Gripping" to "Engrossing or compelling, holding one's attention tightly"
                ),
                createdAt = 1L
            ),
            Data(
                "d7",
                language = "English",
                topic = "Angry Things",
                conversation = listOf(
                    "Hey, how's it going?",
                    "Not bad, just chilling. You?",
                    "Just finished a book, any recommendations?",
                    "Oh nice! What genre are you into?",
                    "Mostly mystery and thriller.",
                    "Have you read 'Gone Girl'? It's fantastic!",
                    "Yes! Loved it. Looking for something similar.",
                    "Try 'The Girl on the Train', it's gripping!",
                    "Sounds intriguing, I'll check it out. Thanks!",
                    "No problem, hope you enjoy it!",
                    "Definitely will. Anyway, gotta run now. Talk later!",
                    "Sure thing, catch you later!"
                ),
                vocabulary = mapOf(
                    "Chilling" to "Relaxing or hanging out casually",
                    "Recommendations" to "Suggestions or advice given to someone",
                    "Genre" to "A category or type, especially in literature or art",
                    "Intriguing" to "Fascinating or arousing curiosity",
                    "Gripping" to "Engrossing or compelling, holding one's attention tightly"
                ),
                createdAt = 1L
            ),
            Data(
                "d21",
                language = "English",
                topic = "Angry Things",
                conversation = listOf(
                    "Hey, how's it going?",
                    "Not bad, just chilling. You?",
                    "Just finished a book, any recommendations?",
                    "Oh nice! What genre are you into?",
                    "Mostly mystery and thriller.",
                    "Have you read 'Gone Girl'? It's fantastic!",
                    "Yes! Loved it. Looking for something similar.",
                    "Try 'The Girl on the Train', it's gripping!",
                    "Sounds intriguing, I'll check it out. Thanks!",
                    "No problem, hope you enjoy it!",
                    "Definitely will. Anyway, gotta run now. Talk later!",
                    "Sure thing, catch you later!"
                ),
                vocabulary = mapOf(
                    "Chilling" to "Relaxing or hanging out casually",
                    "Recommendations" to "Suggestions or advice given to someone",
                    "Genre" to "A category or type, especially in literature or art",
                    "Intriguing" to "Fascinating or arousing curiosity",
                    "Gripping" to "Engrossing or compelling, holding one's attention tightly"
                ),
                createdAt = 1L
            ),
            Data(
                "d8",
                language = "English",
                topic = "Angry Things",
                conversation = listOf(
                    "Hey, how's it going?",
                    "Not bad, just chilling. You?",
                    "Just finished a book, any recommendations?",
                    "Oh nice! What genre are you into?",
                    "Mostly mystery and thriller.",
                    "Have you read 'Gone Girl'? It's fantastic!",
                    "Yes! Loved it. Looking for something similar.",
                    "Try 'The Girl on the Train', it's gripping!",
                    "Sounds intriguing, I'll check it out. Thanks!",
                    "No problem, hope you enjoy it!",
                    "Definitely will. Anyway, gotta run now. Talk later!",
                    "Sure thing, catch you later!"
                ),
                vocabulary = mapOf(
                    "Chilling" to "Relaxing or hanging out casually",
                    "Recommendations" to "Suggestions or advice given to someone",
                    "Genre" to "A category or type, especially in literature or art",
                    "Intriguing" to "Fascinating or arousing curiosity",
                    "Gripping" to "Engrossing or compelling, holding one's attention tightly"
                ),
                createdAt = 1L
            ),
            Data(
                "d9",
                language = "English",
                topic = "Angry Things",
                conversation = listOf(
                    "Hey, how's it going?",
                    "Not bad, just chilling. You?",
                    "Just finished a book, any recommendations?",
                    "Oh nice! What genre are you into?",
                    "Mostly mystery and thriller.",
                    "Have you read 'Gone Girl'? It's fantastic!",
                    "Yes! Loved it. Looking for something similar.",
                    "Try 'The Girl on the Train', it's gripping!",
                    "Sounds intriguing, I'll check it out. Thanks!",
                    "No problem, hope you enjoy it!",
                    "Definitely will. Anyway, gotta run now. Talk later!",
                    "Sure thing, catch you later!"
                ),
                vocabulary = mapOf(
                    "Chilling" to "Relaxing or hanging out casually",
                    "Recommendations" to "Suggestions or advice given to someone",
                    "Genre" to "A category or type, especially in literature or art",
                    "Intriguing" to "Fascinating or arousing curiosity",
                    "Gripping" to "Engrossing or compelling, holding one's attention tightly"
                ),
                createdAt = 1L
            ),
            Data(
                "d10",
                language = "English",
                topic = "Angry Things",
                conversation = listOf(
                    "Hey, how's it going?",
                    "Not bad, just chilling. You?",
                    "Just finished a book, any recommendations?",
                    "Oh nice! What genre are you into?",
                    "Mostly mystery and thriller.",
                    "Have you read 'Gone Girl'? It's fantastic!",
                    "Yes! Loved it. Looking for something similar.",
                    "Try 'The Girl on the Train', it's gripping!",
                    "Sounds intriguing, I'll check it out. Thanks!",
                    "No problem, hope you enjoy it!",
                    "Definitely will. Anyway, gotta run now. Talk later!",
                    "Sure thing, catch you later!"
                ),
                vocabulary = mapOf(
                    "Chilling" to "Relaxing or hanging out casually",
                    "Recommendations" to "Suggestions or advice given to someone",
                    "Genre" to "A category or type, especially in literature or art",
                    "Intriguing" to "Fascinating or arousing curiosity",
                    "Gripping" to "Engrossing or compelling, holding one's attention tightly"
                ),
                createdAt = 1L
            ),
            Data(
                "d11",
                language = "English",
                topic = "Angry Things",
                conversation = listOf(
                    "Hey, how's it going?",
                    "Not bad, just chilling. You?",
                    "Just finished a book, any recommendations?",
                    "Oh nice! What genre are you into?",
                    "Mostly mystery and thriller.",
                    "Have you read 'Gone Girl'? It's fantastic!",
                    "Yes! Loved it. Looking for something similar.",
                    "Try 'The Girl on the Train', it's gripping!",
                    "Sounds intriguing, I'll check it out. Thanks!",
                    "No problem, hope you enjoy it!",
                    "Definitely will. Anyway, gotta run now. Talk later!",
                    "Sure thing, catch you later!"
                ),
                vocabulary = mapOf(
                    "Chilling" to "Relaxing or hanging out casually",
                    "Recommendations" to "Suggestions or advice given to someone",
                    "Genre" to "A category or type, especially in literature or art",
                    "Intriguing" to "Fascinating or arousing curiosity",
                    "Gripping" to "Engrossing or compelling, holding one's attention tightly"
                ),
                createdAt = 1L
            ),
            Data(
                "d12",
                language = "English",
                topic = "Angry Things",
                conversation = listOf(
                    "Hey, how's it going?",
                    "Not bad, just chilling. You?",
                    "Just finished a book, any recommendations?",
                    "Oh nice! What genre are you into?",
                    "Mostly mystery and thriller.",
                    "Have you read 'Gone Girl'? It's fantastic!",
                    "Yes! Loved it. Looking for something similar.",
                    "Try 'The Girl on the Train', it's gripping!",
                    "Sounds intriguing, I'll check it out. Thanks!",
                    "No problem, hope you enjoy it!",
                    "Definitely will. Anyway, gotta run now. Talk later!",
                    "Sure thing, catch you later!"
                ),
                vocabulary = mapOf(
                    "Chilling" to "Relaxing or hanging out casually",
                    "Recommendations" to "Suggestions or advice given to someone",
                    "Genre" to "A category or type, especially in literature or art",
                    "Intriguing" to "Fascinating or arousing curiosity",
                    "Gripping" to "Engrossing or compelling, holding one's attention tightly"
                ),
                createdAt = 1L
            ),
            Data(
                "d13",
                language = "English",
                topic = "Angry Things",
                conversation = listOf(
                    "Hey, how's it going?",
                    "Not bad, just chilling. You?",
                    "Just finished a book, any recommendations?",
                    "Oh nice! What genre are you into?",
                    "Mostly mystery and thriller.",
                    "Have you read 'Gone Girl'? It's fantastic!",
                    "Yes! Loved it. Looking for something similar.",
                    "Try 'The Girl on the Train', it's gripping!",
                    "Sounds intriguing, I'll check it out. Thanks!",
                    "No problem, hope you enjoy it!",
                    "Definitely will. Anyway, gotta run now. Talk later!",
                    "Sure thing, catch you later!"
                ),
                vocabulary = mapOf(
                    "Chilling" to "Relaxing or hanging out casually",
                    "Recommendations" to "Suggestions or advice given to someone",
                    "Genre" to "A category or type, especially in literature or art",
                    "Intriguing" to "Fascinating or arousing curiosity",
                    "Gripping" to "Engrossing or compelling, holding one's attention tightly"
                ),
                createdAt = 1L
            ),
            Data(
                "d14",
                language = "English",
                topic = "Angry Things",
                conversation = listOf(
                    "Hey, how's it going?",
                    "Not bad, just chilling. You?",
                    "Just finished a book, any recommendations?",
                    "Oh nice! What genre are you into?",
                    "Mostly mystery and thriller.",
                    "Have you read 'Gone Girl'? It's fantastic!",
                    "Yes! Loved it. Looking for something similar.",
                    "Try 'The Girl on the Train', it's gripping!",
                    "Sounds intriguing, I'll check it out. Thanks!",
                    "No problem, hope you enjoy it!",
                    "Definitely will. Anyway, gotta run now. Talk later!",
                    "Sure thing, catch you later!"
                ),
                vocabulary = mapOf(
                    "Chilling" to "Relaxing or hanging out casually",
                    "Recommendations" to "Suggestions or advice given to someone",
                    "Genre" to "A category or type, especially in literature or art",
                    "Intriguing" to "Fascinating or arousing curiosity",
                    "Gripping" to "Engrossing or compelling, holding one's attention tightly"
                ),
                createdAt = 1L
            ),
            Data(
                "d15",
                language = "English",
                topic = "Latest Item",
                conversation = listOf(
                    "Hey, how's it going?",
                    "Not bad, just chilling. You?",
                    "Just finished a book, any recommendations?",
                    "Oh nice! What genre are you into?",
                    "Mostly mystery and thriller.",
                    "Have you read 'Gone Girl'? It's fantastic!",
                    "Yes! Loved it. Looking for something similar.",
                    "Try 'The Girl on the Train', it's gripping!",
                    "Sounds intriguing, I'll check it out. Thanks!",
                    "No problem, hope you enjoy it!",
                    "Definitely will. Anyway, gotta run now. Talk later!",
                    "Sure thing, catch you later!"
                ),
                vocabulary = mapOf(
                    "Chilling" to "Relaxing or hanging out casually",
                    "Recommendations" to "Suggestions or advice given to someone",
                    "Genre" to "A category or type, especially in literature or art",
                    "Intriguing" to "Fascinating or arousing curiosity",
                    "Gripping" to "Engrossing or compelling, holding one's attention tightly"
                ),
                createdAt = 1L
            )
        ))
        //return Result.failure(DataException(DATA_ERROR_NOT_FOUND))
    }

    //Success
    override suspend fun deleteData(dataId: String) {
        delay(5000)
    }

}