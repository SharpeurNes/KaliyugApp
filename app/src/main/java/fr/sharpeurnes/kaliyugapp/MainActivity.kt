package fr.sharpeurnes.kaliyugapp

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import android.util.Log
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import kotlinx.coroutines.*
import org.jsoup.Jsoup

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MyAppTheme {
                MyApp()
            }
        }
    }
}

var selectedTopic = UnTopic("Imagine le titre il est grave long aya", "AuteurTest", 10, "01:01:01")

@Composable
fun MyAppTheme(content: @Composable () -> Unit) {
    val darkColors = darkColorScheme(
        primary = Color(0xFF6C63FF),
        secondary = Color(0xFF03DAC6),
        background = Color(0xFF121212),
        surface = Color(0xFF1F1F1F),
        onPrimary = Color.White,
        onSecondary = Color.Black,
        onBackground = Color.White,
        onSurface = Color.White
    )

    MaterialTheme(
        colorScheme = darkColors,
        content = content
    )
}

@Composable
fun MyApp(){
    val navController = rememberNavController()

    NavHost(
        navController = navController,
        startDestination = "mainContent",
        modifier = Modifier.fillMaxSize()
    ){
        composable("mainContent"){
            MainScreen(
                onNavigateToTopicView = {
                    navController.navigate("topicView/")
                }
            )
        }
        composable("topicView/"){ backStackEntry ->
            TopicWindow() { navController.popBackStack() }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(onNavigateToTopicView: () -> Unit) {

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current

    val screenWidth = with(density) { configuration.screenWidthDp.dp.toPx() }
    val drawerWidth = 280.dp

    var dragOffset by remember { mutableFloatStateOf(0f) }
    val drawerWidthPx = with(density) { drawerWidth.toPx() }

    var refreshTrigger by remember { mutableStateOf(0)}

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .pointerInput(Unit) {
                detectDragGestures(
                    onDragStart = { offset ->
                        // Détecter si le swipe commence près du bord gauche
                        if (offset.x < 50.dp.toPx() && drawerState.isClosed) {
                            dragOffset = 0f
                        }
                    },
                    onDragEnd = {
                        scope.launch {
                            if (drawerState.isClosed && dragOffset > drawerWidthPx / 3) {
                                drawerState.open()
                            }
                            dragOffset = 0f
                        }
                    }
                ) { _, dragAmount ->
                    if (drawerState.isClosed && dragAmount.x > 0) {
                        dragOffset = (dragOffset + dragAmount.x).coerceAtMost(drawerWidthPx)
                    }
                }
            }
    ) {

        // Main Content
        Column(
            modifier = Modifier.fillMaxSize()
        ) {
            // Header
            TopAppBar(
                title = {
                    Box(
                        modifier = Modifier.fillMaxWidth(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "KaliyugApp",
                            fontSize = 20.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                navigationIcon = {
                    IconButton(
                        onClick = {
                            scope.launch {
                                drawerState.open()
                            }
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Person,
                            contentDescription = "Profile",
                            tint = MaterialTheme.colorScheme.onSurface,
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                        )
                    }
                },
                actions = {
                    IconButton(
                        onClick = {
                            refreshTrigger++
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Refresh,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }

                    IconButton(
                        onClick = { /* Settings action */ }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Settings,
                            contentDescription = "Settings",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )

            // Main Content Area
            MainContentArea(
                onNavigateToTopicView = {
                    onNavigateToTopicView()
                },
                refreshTrigger = refreshTrigger
            )

            // Bottom Navigation
            BottomNavigation()
        }

        // Drawer
        if (drawerState.isOpen) {
            Row(
                modifier = Modifier
                    .fillMaxSize()
                    .zIndex(10f)
            ) {
                // Drawer Content
                Surface(
                    modifier = Modifier
                        .width(drawerWidth)
                        .fillMaxHeight(),
                    color = MaterialTheme.colorScheme.surface,
                    shadowElevation = 16.dp
                ) {
                    DrawerContent(
                        onCloseDrawer = {
                            scope.launch {
                                drawerState.close()
                            }
                        }
                    )
                }

                // Overlay to close drawer
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(Color.Black.copy(alpha = 0.3f))
                        .pointerInput(Unit) {
                            detectDragGestures(
                                onDragEnd = {
                                    scope.launch {
                                        if (dragOffset < -drawerWidthPx / 3) {
                                            drawerState.close()
                                        } else {
                                            dragOffset = 0f
                                        }
                                    }
                                }
                            ) { _, dragAmount ->
                                if (dragAmount.x < 0) {
                                    dragOffset =
                                        (dragOffset + dragAmount.x).coerceAtLeast(-drawerWidthPx)
                                }
                            }
                        }
                        .clickable {
                            scope.launch {
                                drawerState.close()
                            }
                        }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainContentArea(onNavigateToTopicView: () -> Unit, refreshTrigger: Int) {

    var topics by remember { mutableStateOf<List<UnTopic>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var isRefreshing by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }



    // Fonction pour charger les données
    suspend fun loadTopics() {
        try {
            val fetchedTopics = fetchTopics()
            topics = fetchedTopics
            error = null
        } catch (e: Exception) {
            error = e.message
        }
    }

    // Charger les données au démarrage
    LaunchedEffect(Unit) {

        loadTopics()
        isLoading = false
    }

    LaunchedEffect(refreshTrigger) {
        isRefreshing = true
        loadTopics()
        isRefreshing = false
    }


    // Fonction de refresh
    fun onRefresh() {
        isRefreshing = true
        CoroutineScope(Dispatchers.IO).launch {
            loadTopics()
            isRefreshing = false
        }
    }

    PullToRefreshBox(
        isRefreshing = isRefreshing,
        onRefresh = { onRefresh() },
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {

        when {
            isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {

                }
            }

            error != null -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = "Erreur: $error",
                        color = Color.Red,
                        textAlign = TextAlign.Center
                    )
                }
            }

            else -> {
                LazyColumn(
                    verticalArrangement = Arrangement.spacedBy(8.dp)
                ) {

                    items(topics) { topic ->
                        TopicCard(
                            topic = topic,
                            onNavigateToTopicView = {
                                onNavigateToTopicView()
                            }
                        )
                    }
                }
            }
        }
    }
}

data class UnTopic(
    val title: String,
    val author: String,
    val replies: Int,
    val lastActivity: String
)


//ICI QU'ON BOSSE
suspend fun fetchTopics(): List<UnTopic> = withContext(Dispatchers.IO) {
    try {
        Log.d("SHARP", "Debut du try")
        val url = "https://api.jeuxvideo.com/forums/0-51-0-1-0-1-0-blabla-18-25-ans.htm"
        val doc = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Android)")  // Pour éviter certains blocages
            .get()

        val topics = mutableListOf<UnTopic>()

        // Récupère tous les div.topic-inner dans ul.liste-topics
        val topicElements = doc.select("ul.liste-topics div.topic-inner")

        for (topicElement in topicElements) {
            val title = topicElement.selectFirst("div.titre-topic")?.text() ?: "Titre non disponible"

            val replyRegex = Regex("""\((\d+)\)$""")
            val replyMatch = replyRegex.find(title)
            val replyNombre = replyMatch?.groups?.get(1)?.value?.toInt()  ?: 0

            val cleanTitle = title.replace(replyRegex, "").trim()

            val author = topicElement.selectFirst("span.auteur")?.text() ?: "Auteur"
            val date = topicElement.selectFirst("time.date-post-topic")?.text() ?: "00/00/00"


            topics.add(
                UnTopic(
                    title = cleanTitle,
                    author = author,
                    replies = replyNombre,
                    lastActivity = date
                )
            )
        }

        topics
    } catch (e: Exception) {
        Log.e("SHARP", "Erreur loading topics: ${e.message}")
        getTestTopics()  // ta fonction fallback
    }
}


fun getTestTopics(): List<UnTopic> {
    return listOf(
        UnTopic("Topic de test", "Sharpeur", 8, "29/09/2025"),
        UnTopic("Règles du forum", "odoki", 0, "08/11/2022"),
        UnTopic("J'annonce mon grand retour sur le forum blabla 18-25", "Seuritima", 3, "21:20:33"),
        UnTopic("Je loue des comptes jvc premium", "Kheyousanssel", 7, "21:20:33"),
        UnTopic("Chaud: Brigitte a mis une PATATE à Macron dans l'avion", "revolutionin", 1891, "21:20:32"),
        UnTopic("[NOFAKE] je suis à kaboul, posez vos questions", "tournevistorx", 35, "21:20:32"),
        UnTopic("[CANAL+ FOOT] 🏆 🧤 Finale de Conference League 🏆 🧤 🟢 Betis Seville vs Chelsea🔵", "AftynRoseENT", 190, "21:20:32"),
        UnTopic("[MARLOU] WEEK END de 4 JOURS, ça BOIT QUOI ce SOIR ?", "JackUltraCity", 73, "21:20:32")
    )
}

@Composable
fun TopicCard(topic: UnTopic, onNavigateToTopicView: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                selectedTopic = topic
                onNavigateToTopicView()
            },
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            // Titre du topic
            Text(
                text = topic.title,
                fontSize = 16.sp,
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.primary,
                maxLines = 2,
                overflow = TextOverflow.Ellipsis
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Informations du topic
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Auteur
                Text(
                    text = "Par ${topic.author}",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                )

                // Nombre de réponses
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Check,
                        contentDescription = "Réponses",
                        modifier = Modifier.size(16.dp),
                        tint = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "${topic.replies}",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(modifier = Modifier.height(4.dp))

            // Dernière activité
            Text(
                text = "Dernière activité: ${topic.lastActivity}",
                fontSize = 12.sp,
                color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
            )
        }
    }

}

@Composable
fun DrawerContent(onCloseDrawer: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        // Header du drawer
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 24.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Menu",
                fontSize = 24.sp,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
            IconButton(onClick = onCloseDrawer) {
                Icon(
                    imageVector = Icons.Default.Close,
                    contentDescription = "Fermer",
                    tint = MaterialTheme.colorScheme.onSurface
                )
            }
        }

        // Profile section
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 15.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
            )
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Person,
                    contentDescription = "Profile",
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(MaterialTheme.colorScheme.primary)
                        .padding(8.dp),
                    tint = Color.White
                )
                Spacer(modifier = Modifier.width(12.dp))
                Column {
                    Text(
                        text = "Utilisateur",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Text(
                        text = "user@example.com",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                    )
                }
            }
        }

        // Menu items
        val menuItems = listOf(
            "Accueil" to Icons.Default.Home,
            "Profil" to Icons.Default.Person,
            "Paramètres" to Icons.Default.Settings,
            "Notifications" to Icons.Default.Notifications,
            "Aide" to Icons.Default.Info
        )

        menuItems.forEach { (title, icon) ->
            DrawerMenuItem(
                title = title,
                icon = icon,
                onClick = { /* Handle menu item click */ }
            )
        }
    }
}

@Composable
fun DrawerMenuItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 12.dp, horizontal = 8.dp)
            .clip(RoundedCornerShape(8.dp)),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = icon,
            contentDescription = title,
            tint = MaterialTheme.colorScheme.onSurface,
            modifier = Modifier.size(24.dp)
        )
        Spacer(modifier = Modifier.width(16.dp))
        Text(
            text = title,
            fontSize = 16.sp,
            color = MaterialTheme.colorScheme.onSurface
        )
    }
}

@Composable
fun BottomNavigation() {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.surface,
        shadowElevation = 8.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceEvenly
        ) {
            val bottomItems = listOf(
                Icons.Default.Home,
                Icons.Default.Search,
                Icons.Default.Favorite,
                Icons.Default.Person
            )

            bottomItems.forEach { icon ->
                IconButton(
                    onClick = { /* Handle bottom nav click */ },
                    modifier = Modifier.size(48.dp)
                ) {
                    Icon(
                        imageVector = icon,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }
        }
    }
}



//TEST PREVIEW
@Preview(showBackground = true)
@Composable
fun PreviewTopicCard(){

    val sampleTopics = listOf(
        UnTopic("Que pensez de ces SALAUDS d'OP qui poste une question", "Sharpeur", 8, "29/09/2025"),
        UnTopic("La DROITE en 2025 :rire:", "GermanQueen", 8, "04:07:08")
    )

    MyAppTheme {
        Column(
            verticalArrangement = Arrangement.spacedBy(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
        ) {
            sampleTopics.forEach { topic ->
                TopicCard(topic = topic, { })
            }
        }
    }

}