package fr.sharpeurnes.kaliyugapp
import android.content.Context
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.sp
import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.InlineTextContent
import androidx.compose.foundation.text.appendInlineContent
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.BottomAppBarDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.Placeholder
import androidx.compose.ui.text.PlaceholderVerticalAlign
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import coil.ImageLoader
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import coil.compose.AsyncImage
import coil.request.CachePolicy
import coil.request.ImageRequest

data class UnPost(
    val id: String,
    val author: String,
    val level: String,
    val date: String,
    val msg: String,
    val ppUrl: String,
)

@Composable
fun TopicWindow(onBack: () -> Unit){

    var posts by remember { mutableStateOf<List<UnPost>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }

    val context = LocalContext.current

    suspend fun loadPosts() {
        try {
            val fetchedPosts = fetchPosts(context)
            posts = fetchedPosts
            //error = null
        } catch (e: Exception){
            //error = e.message
        }
    }

    LaunchedEffect(Unit) {
        loadPosts()
        isLoading = false
    }

    TopicContent(posts, onBack)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicContent(posts: List<UnPost>, onBack: () -> Unit){

    MyAppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { TopicTopBar(onBack) },
            bottomBar = { TopicBottomBar() }
        ) { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(12.dp)
            ){

                items(posts) { post ->
                    PostCard(post)
                }
            }
        }
    }



//    Box(
//        modifier = Modifier
//            .fillMaxSize()
//            .background(Color(0xFF121212))
//    ){
//        MyAppTheme {
//
//            Box(
//                modifier = Modifier
//                    .fillMaxSize()
//                    .background(MaterialTheme.colorScheme.background)
//            ){
//                Column(
//                    Modifier.fillMaxSize()
//                ) {
//
//
//
//
//
//
//
//
//                }
//
//
//            }
//        }
//    }

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicTopBar(onBack: () -> Unit) {

    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = selectedTopic.title,
                    fontSize = 17.sp,
                    textAlign = TextAlign.Center,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        },
        navigationIcon = {
            IconButton(
                onClick = {
                    onBack()
                }
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Profile",
                    tint = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .size(32.dp)
                        .clip(CircleShape),
                )
            }
        },
        actions = {
            IconButton(
                onClick = { /* Settings action */ }
            ) {
                Icon(
                    imageVector = Icons.Default.Refresh,
                    contentDescription = "Settings",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface
        )
    )

}

@Composable
fun TopicBottomBar(){
    var message by remember { mutableStateOf("") }
    BottomAppBar(
        containerColor = MaterialTheme.colorScheme.surface
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .fillMaxWidth()
                .padding(2.dp)
        ){
            TextField(
                value = message,
                onValueChange = { message = it },
                placeholder = { Text("Rédigez-votre message...") },
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 5.dp),
                singleLine = true,
                colors = TextFieldDefaults.colors(
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent,
                    disabledIndicatorColor = Color.Transparent
                )
            )

            IconButton(onClick = {
                if(message.isNotEmpty()){
                    println("Envoyé: $message")
                    message = ""
                }
            }) {
                Icon(Icons.Default.Send, contentDescription = "Envoyer")
            }
        }
    }
}

@Composable
fun PostCard(post: UnPost){
//CARD TEST
    Card(
        shape = RectangleShape,
        modifier = Modifier
            .fillMaxSize()
            .clickable { },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ){
        Column(
            modifier = Modifier.padding(10.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                if(post.ppUrl != "null"){
                    AsyncImage(
                        model = post.ppUrl,
                        contentDescription = "PP User",
                        modifier = Modifier.size(45.dp)
                    )
                } else {
                    Icon(
                        imageVector = Icons.Default.AccountBox,
                        contentDescription = "PP User",
                        modifier = Modifier.size(45.dp)
                    )
                }

                Column(
                    modifier = Modifier.padding(5.dp)
                ) {
                    Row {
                        Text(
                            text = post.author,
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = " (lvl ${post.level})",
                            fontSize = 13.sp,
                            color = Color(0xFFd1d1d1)
                        )
                    }
                    Spacer(Modifier.width(4.dp))
                    Text(
                        text = post.date,
                        fontSize = 13.sp,
                        color = Color(0xFFd1d1d1)
                    )
                }

                Spacer(modifier = Modifier.weight(1f)) //Pousse le btn vers la droite askip

                IconButton(
                    onClick = { println(post.msg) },
                    modifier = Modifier.align(Alignment.Top)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Settings",
                        tint = MaterialTheme.colorScheme.onSurface
                    )
                }


            }


            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
//                Text(
//                    text = post.msg,
//                    fontSize = 14.sp,
//                    maxLines = 3,
//                    color = Color(0xFFd1d1d1)
//                )
                InlineMessageText(post.msg)
            }
            Spacer(Modifier.width(4.dp))
            Text(
                text = post.id,
                fontSize = 13.sp,
                color = Color(0xFF6e6e6e)
            )
            Spacer(Modifier.width(4.dp))

        }
    }

    //FIN CARD TEST
}

suspend fun fetchPosts(context: Context): List<UnPost> = withContext(Dispatchers.IO) {
    try {
        Log.d("SHARP", "ON GET LES MSGS")
        val url = "https://api.jeuxvideo.com/forums/${selectedTopic.topicId}"
        val doc = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Android)")
            .get()

        val posts = mutableListOf<UnPost>()
        val postsElements = doc.select("div.post")

        var loader = ImageLoader(context)

        for(postElement in postsElements){
            val postId = postElement.selectFirst("div.post")?.id() ?: "id"
            val author = postElement.selectFirst("a.text-auteur")?.text() ?: "Auteur"

            val msg = postElement.selectFirst("div.message")?.text() ?: "Message test"

            val msgRaw = extractMessageRaw(postElement.selectFirst("div.message"))

            val levelData = postElement.selectFirst("div.user-level")?.text() ?: "Niveau 0"
            val level = levelData.replace(Regex("Niveau "), "")

            val date = postElement.selectFirst("div.date-post")?.text() ?: "32 decembre 3001 00:00:00"

            val ppUrl = postElement.selectFirst("img.user-avatar-msg")?.attr("data-src") ?: "null"

            if(ppUrl != "null"){
                val request = ImageRequest.Builder(context)
                    .data(ppUrl)
                    .crossfade(true)
                    .diskCachePolicy(CachePolicy.ENABLED)
                    .memoryCachePolicy(CachePolicy.ENABLED)
                    .error(R.drawable.ic_launcher_background)
                    .build()
                loader.enqueue(request)
            }

            posts.add(
                UnPost(
                    id = postId,
                    author = author,
                    level = level,
                    date = date,
                    msg = msgRaw,
                    ppUrl = ppUrl
                )
            )
        }

        posts
    } catch (e: Exception){
        Log.e("SHARP", "Erreur loadings messages: ${e.message}")
        getTestPosts()
    }
}


fun extractMessageRaw(element: org.jsoup.nodes.Element?): String{
    if(element == null) return ""

    val sb = StringBuilder()

    fun processNode(node: org.jsoup.nodes.Node){
        when(node){
            is org.jsoup.nodes.TextNode -> sb.append(node.text())
            is org.jsoup.nodes.Element -> {
                when(node.tagName()) {
                    "a" -> {
                        //Remplace le lien par son href (URL)
                        val href = node.attr("href")
                        if(href.isNotBlank()) sb.append(href)
                        node.childNodes().forEach { processNode(it) }
                    }
                    "img" -> {
                        // Remplace l'image par son SRC (URL)
                        val src = node.attr("src")
                        if(src.isNotBlank()) sb.append(src)
                    }
                    else -> node.childNodes().forEach { processNode(it) }
                }
            }
        }
    }

    element.select("p").forEachIndexed { idx, p ->
        p.childNodes().forEach { processNode(it) }
        if(idx < element.select("p").size - 1) sb.append("\n")
    }

    return sb.toString()
}

//fun parseMessageToPars(message: String): List<Any>{
//    val regex = Regex("""https://image\.noelshack\.com/\S+\.(png|jpg)""")
//    val parts = mutableListOf<Any>()
//    var lastIndex = 0
//
//    regex.findAll(message).forEach { matchResult ->
//        val range = matchResult.range
//        if(range.first > lastIndex){
//            //text avant le lien
//            parts.add(message.substring(lastIndex, range.first))
//        }
//        //lien image
//        parts.add(matchResult.value)
//        lastIndex = range.last + 1
//    }
//    if(lastIndex < message.length){
//        parts.add(message.substring(lastIndex))
//    }
//    return parts
//}
//
//@Composable
//fun MessageWithImages(postMsg: String){
//    val parts = parseMessageToPars(postMsg)
//
//    Column {
//        parts.forEach { part ->
//            when(part){
//                is String -> {
//                    if(part.startsWith("https://image.noelshack.com") &&
//                        (part.endsWith(".png") || part.endsWith(".jpg"))){
//                        //Affiche l'image
//                        AsyncImage(
//                            model = part,
//                            contentDescription = null,
//                            modifier = Modifier
//                                .size(width = 68.dp, height = 51.dp),
//                            contentScale = ContentScale.Crop
//                        )
//                    } else {
//                        //afiche du text Normal
//                        Text(
//                            text = part,
//                            fontSize = 14.sp,
//                            color = Color(0xFFd1d1d1)
//                        )
//                    }
//                }
//            }
//        }
//    }
//}

@Composable
fun parseMessageToAnnotatedString(message: String): Pair<AnnotatedString, Map<String, InlineTextContent>> {
    val regex = Regex("""https://image\.noelshack\.com/\S+\.(png|jpg)""")
    val builder = AnnotatedString.Builder()
    val inlineContents = mutableMapOf<String, InlineTextContent>()
    var lastIndex = 0
    var imgIndex = 0

    regex.findAll(message).forEach { matchResult ->
        val range = matchResult.range
        if (range.first > lastIndex) {
            builder.append(message.substring(lastIndex, range.first))
        }
        val tag = "img$imgIndex"
        builder.appendInlineContent(tag, "[img]")

        inlineContents[tag] = InlineTextContent(
            Placeholder(
                width = 68.sp,
                height = 51.sp,
                placeholderVerticalAlign = PlaceholderVerticalAlign.Center
            )
        ) {
            AsyncImage(
                model = matchResult.value,
                contentDescription = null,
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop
            )
        }

        imgIndex++
        lastIndex = range.last + 1
    }

    if (lastIndex < message.length) {
        builder.append(message.substring(lastIndex))
    }

    return Pair(builder.toAnnotatedString(), inlineContents)
}


@Composable
fun InlineMessageText(message: String) {
    val (text, inlineContents) = parseMessageToAnnotatedString(message)

    Text(
        text = text,
        inlineContent = inlineContents,
        fontSize = 14.sp,
        color = Color(0xFFd1d1d1)
    )
}


fun getTestPosts(): List<UnPost> {
    return listOf(
        UnPost("xdidxd", "authorix", "5", "01:02:03", "Ceci est le message ahi https://image.noelshack.com/fichiers/2020/27/6/1593818861-ht0hwmqi.png test", "null"),
        UnPost("2id", "authorixed", "50", "01:02:03", "Voila un message un peu plus long on va voir si ça rentre ou non", "null")
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewTopic(){
    TopicContent(getTestPosts()) { }
}