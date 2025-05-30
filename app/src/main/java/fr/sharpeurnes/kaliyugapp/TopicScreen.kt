package fr.sharpeurnes.kaliyugapp
import android.content.Context
import androidx.compose.foundation.background
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
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
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


    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFF121212))
    ){
        MyAppTheme {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background)
            ){
                Column(
                    Modifier.fillMaxSize()
                ) {

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



                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ){

                        items(posts) { post ->
                            PostCard(post)
                        }

                    }


                }


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
            .clickable{ },
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
                            text = "${post.author}",
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
                    onClick = { /* Settings action */ },
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
                Text(
                    text = post.msg,
                    fontSize = 14.sp,
                    maxLines = 3,
                    color = Color(0xFFd1d1d1)
                )
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

            val levelData = postElement.selectFirst("div.user-level")?.text() ?: "Niveau 0"
            val level = levelData.replace(Regex("Niveau "), "")

            val date = postElement.selectFirst("div.date-post")?.text() ?: "32 decembre 3001 00:00:00"

            val ppUrl = postElement.selectFirst("img.user-avatar-msg")?.attr("data-src") ?: "null"

            if(ppUrl != "null"){
                val request = ImageRequest.Builder(context)
                    .data(ppUrl)
                    .build()
                loader.execute(request)
            }

            posts.add(
                UnPost(
                    id = postId,
                    author = author,
                    level = level,
                    date = date,
                    msg = msg,
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

fun getTestPosts(): List<UnPost> {
    return listOf(
        UnPost("xdidxd", "authorix", "5", "01:02:03", "Ceci est le message ahi", "null"),
        UnPost("2id", "authorixed", "50", "01:02:03", "Voila un message un peu plus long on va voir si ça rentre ou non", "null")
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Preview(showBackground = true)
@Composable
fun PreviewTopic(){
    TopicContent(getTestPosts()) { }
}