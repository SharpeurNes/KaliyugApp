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
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.AccountBox
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Send
import androidx.compose.material3.BottomAppBar
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
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
import org.jsoup.nodes.Element
import org.jsoup.nodes.Node
import org.jsoup.nodes.TextNode


data class UnPost(
    val id: String,
    val author: String,
    val level: String,
    val date: String,
    val msg: String,
    val ppUrl: String,
)

var clickProfile: () -> Unit = {}

@Composable
fun TopicWindow(onClickProfile: () -> Unit){

    clickProfile = onClickProfile

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

    TopicContent(posts)

}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicContent(posts: List<UnPost>){

    MyAppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { TopicTopBar() },
            bottomBar = { TopicBottomBar() }
        ) { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ){

                items(posts) { post ->
                    PostCard(post)
                }
            }
        }
    }



}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopicTopBar() {

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
                    goBack()
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
        ){

            IconButton(onClick = { /* action */ }) {
                Icon(
                    painter = painterResource(id = R.drawable.risibank_logo),
                    contentDescription = "Logo Risibank",
                    modifier = Modifier.size(24.dp),
                    tint = Color.Unspecified
                )
            }

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

@OptIn(ExperimentalLayoutApi::class)
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
                    Box(
                        modifier = Modifier.clickable(
                            onClick = {
                                selectedProfile = UnUser(post.author, post.ppUrl)
                                clickProfile()
                            }
                        )
                    ){
                        AsyncImage(
                            model = post.ppUrl,
                            contentDescription = "PP User",
                            modifier = Modifier.size(45.dp),
                            placeholder = painterResource(R.drawable.profile_icon)
                        )
                    }
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

            //ICI LE TEXTE
            MessageDisplayFinal(post.msg)


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


fun extractMessageRaw(element: Element?): String {
    if (element == null) return ""

    val sb = StringBuilder()

    fun processNode(node: Node) {
        when (node) {
            is TextNode -> {
                // Ajouter le texte du nœud. Envisager de normaliser les espaces si nécessaire.
                // Par exemple, remplacer plusieurs espaces par un seul, ou .trim() si
                // vous ne voulez pas d'espaces en début/fin de chaque segment de TextNode.
                // Pour l'instant, gardons-le simple.
                sb.append(node.text())
            }
            is Element -> {
                when (node.tagName().lowercase()) { // Mettre en minuscule pour la robustesse
                    "a" -> {
                        // Pour les liens <a> qui contiennent une image <img>,
                        // on veut généralement l'URL de l'image <img> plutôt que l'attribut href du <a>,
                        // surtout si href est une page de destination et non l'image directe.
                        // Cependant, dans votre cas, href EST l'image directe.
                        // On doit éviter de dupliquer si <img> est un enfant.

                        // Si le lien a une image enfant directe qui est la source principale
                        val imgChild = node.selectFirst("img")
                        if (imgChild != null) {
                            val imgSrc = imgChild.attr("src") // Ou data-src, ou autre selon le site
                            if (imgSrc.isNotBlank()) {
                                sb.append(imgSrc) // On prend le src de l'img interne
                            } else { // Fallback sur le href du <a> si l'img n'a pas de src
                                val href = node.attr("href")
                                if (href.isNotBlank() && href.matches(Regex(".*\\.(png|jpg|jpeg|gif)$"))) {
                                    sb.append(href)
                                }
                            }
                            // Important: Ne pas appeler node.childNodes().forEach ici si on a traité l'img
                            // pour éviter de traiter à nouveau l'img enfant.
                        } else {
                            // Si le <a> n'a pas d'enfant <img>, on peut prendre son href s'il ressemble à une image
                            // et ensuite traiter ses enfants (qui pourraient être du texte)
                            val href = node.attr("href")
                            if (href.isNotBlank() && href.matches(Regex(".*\\.(png|jpg|jpeg|gif)$"))) {
                                sb.append(href)
                            }
                            node.childNodes().forEach { processNode(it) } // Traiter les enfants texte d'un lien
                        }
                    }
                    "img" -> {
                        // Cette partie pourrait ne plus être atteinte si les <img> sont toujours dans des <a>
                        // et que la logique "a" ci-dessus les capture.
                        // Mais gardons-la pour les <img> autonomes.
                        val src = node.attr("src")
                        if (src.isNotBlank()) {
                            sb.append(src)
                        }
                    }
                    "br" -> {
                        // Ajouter un saut de ligne pour les balises <br>
                        if (sb.isNotEmpty() && sb.last() != '\n') { // Évite les \n multiples ou en début
                            sb.append("\n")
                        }
                    }
                    else -> {
                        // Pour les autres éléments, parcourir leurs enfants
                        node.childNodes().forEach { processNode(it) }
                    }
                }
            }
        }
    }

    // Traiter les enfants directs de l'élément 'message' (par exemple, s'il n'y a pas de <p>)
    // ou spécifiquement les <p> comme vous le faisiez.
    // Si la structure est toujours <div class="message"><p>...</p></div>, votre approche originale pour les <p> est bonne.
    // Si le contenu peut aussi être directement dans <div class="message"> sans <p>, il faut ajuster.

    // En supposant que le contenu pertinent est toujours dans des <p> à l'intérieur de l'élément passé
    val paragraphs = element.select("p")
    paragraphs.forEachIndexed { idx, p ->
        p.childNodes().forEach { processNode(it) }
        if (idx < paragraphs.size - 1) {
            if (sb.isNotEmpty() && sb.last() != '\n') { // Évite les \n multiples
                sb.append("\n")
            }
        }
    }

    // Nettoyage final pour enlever les sauts de ligne multiples et les espaces superflus
    var result = sb.toString()
    result = result.replace(Regex("\\n{2,}"), "\n") // Remplace 2+ sauts de ligne par un seul
    result = result.replace(Regex(" {2,}"), " ")    // Remplace 2+ espaces par un seul
    return result.trim() // Enlève les espaces/sauts de ligne en début/fin
}


sealed class MessagePart {
    data class Text(val text: String) : MessagePart()
    data class Image(val url: String, val type: ImageType = ImageType.Sticker) : MessagePart()
}

enum class ImageType {
    Sticker,
    Emoji
}

fun parseMessage(message: String): List<MessagePart> {
    val parts = mutableListOf<MessagePart>()
    val tokenizerRegex = Regex("(https?://\\S+\\.(?:png|jpg|jpeg|gif|webp))|([^\\s]+)|(\\s+)")

    tokenizerRegex.findAll(message).forEach { matchResult ->
        val url = matchResult.groups[1]?.value
        val word = matchResult.groups[2]?.value
        val space = matchResult.groups[3]?.value

        when {
            url != null -> {
                val imageType = if (url.contains("risibank") || url.contains("noelshack")) ImageType.Sticker else ImageType.Emoji
                parts.add(MessagePart.Image(url, imageType))
            }
            word != null -> {
                parts.add(MessagePart.Text(word))
            }
            space != null -> {
                parts.add(MessagePart.Text(space))
            }
        }
    }
    return parts
}

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun MessageDisplayFinal(message: String, modifier: Modifier = Modifier) {
    val parts = parseMessage(message)

    FlowRow(
        modifier = modifier
            .fillMaxWidth()
            //.border(2.dp, Color.Red), // débogage
    ) {
        parts.forEach { part ->
            when (part) {
                is MessagePart.Text -> Text(
                    text = part.text, // Contient soit un mot, soit des espaces
                    fontSize = 16.sp,
                    modifier = Modifier
                        .align(Alignment.Bottom) // Important pour aligner mots et images
                        //.border(1.dp, Color.Blue)
                )
                is MessagePart.Image -> AsyncImage(
                    model = part.url,
                    contentDescription = part.type.name,
                    placeholder = painterResource(R.drawable.sticker_test),
                    modifier = when (part.type) {
                        ImageType.Sticker -> Modifier.size(width = 64.dp, height = 56.dp)
                        ImageType.Emoji -> Modifier.size(18.dp) // Emojis souvent carrés
                    }
                        .align(Alignment.Bottom) // Aligner avec le bas des mots
                        //.border(1.dp, Color.Green)
                )
            }
        }
    }
}

fun getTestPosts(): List<UnPost> {
    return listOf(
        UnPost("xdidxd", "SharpeurNes", "77", "01:02:03", "Le message doit être giga long pour tester si y'a un overlap  https://image.noelshack.com/fichiers/2020/27/6/1593818861-ht0hwmqi.png test", "null"),
        UnPost("2id", "GermanQueen", "5", "01:02:03", "Mesage pour tester un emoji sur deux ligne, uooooooh cunny  https://image.jeuxvideo.com/smileys_img/11.gif", "null"),
        UnPost("2id", "Randomax", "1", "01:02:03", "Court https://image.noelshack.com/fichiers/2020/27/6/1593818861-ht0hwmqi.png", "null")
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewTopic(){
    TopicContent(getTestPosts())
}