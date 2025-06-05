@file:Suppress("SpellCheckingInspection")

package fr.sharpeurnes.kaliyugapp

import android.content.Context
import android.util.Log
import androidx.compose.animation.core.copy
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup
import java.util.Locale


data class UnUser(
    var username: String,
    var ppUrl: String?
)

data class UnProfile(
    var username: String,
    var level: List<String> = emptyList(),
    var age: String? = null,
    var paysVille: String? = null,
    var membreSince: String? = null,
    var dernierPassage: String? = null,
    var messageCount: String? = null,
    var commentaireCount: String? = null,
    var genesis: Boolean = false,
    var description: String? = null,
    var signature: String? = null
)

@Composable
fun ProfileView(){

    var profile by remember { mutableStateOf<UnProfile?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    val context = LocalContext.current

    suspend fun loadProfileData(){
        try{
            val fetchedProfile = fetchProfile(context)
            profile = fetchedProfile

        } catch(e: Exception){
            println("Erreur: ${e.message}")
        }
    }

    LaunchedEffect(Unit){
        loadProfileData()
        isLoading = false
    }


    profile?.let { ProfileContent(it) }
}

@Composable
fun ProfileContent(profile: UnProfile) {

    MyAppTheme {
        Scaffold(
            modifier = Modifier.fillMaxSize(),
            topBar = { ProfileTopBar() },
            bottomBar = {  }
        ) { innerPadding ->
            LazyColumn(
                contentPadding = innerPadding,
                verticalArrangement = Arrangement.spacedBy(10.dp)
            ){

                item {
                    ProfileBannerWithOverlap(
                        bannerImageUrl = null, // Remplacez par vos URLs
                        profileImageUrl = selectedProfile.ppUrl,
                        profile = profile
                        // Le modifier n'est pas nécessaire ici si elle prend toute la largeur par défaut
                    )
                }

                item {
                    ProfileInfos(profile)
                }
                //Contenu ici

                if(!profile.description.isNullOrBlank()){
                    item{
                        ProfileDescription(profile)
                    }
                }

            }
        }
    }

}

// Le Composable ProfileBannerWithOverlap reste le même que précédemment
@Composable
fun ProfileBannerWithOverlap(
    bannerImageUrl: String?,
    profileImageUrl: String?,
    modifier: Modifier = Modifier, // Ajout d'un modifier ici
    profile: UnProfile
) {
    Column(modifier = modifier.fillMaxWidth()) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
        ) {
            AsyncImage(
                model = bannerImageUrl ?: R.drawable.default_banner,
                contentDescription = "Bannière du profil",
                modifier = Modifier.fillMaxSize(),
                contentScale = ContentScale.Crop,
                placeholder = painterResource(R.drawable.default_banner),
            )
        }
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
                .offset(y = (-30).dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            AsyncImage(
                model = profileImageUrl ?: R.drawable.profile_icon,
                placeholder = painterResource(R.drawable.profile_icon),
                contentDescription = "Photo de profil",
                contentScale = ContentScale.Crop,
                modifier = Modifier
                    .size(80.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.surface, CircleShape)
                    .padding(3.dp)
                    .clip(CircleShape)
            )

            Box(
                modifier = Modifier
                    .weight(1f) // Prend l'espace restant
                    .fillMaxHeight() // Important: La Box doit avoir la hauteur de la Row
                    .align(Alignment.Bottom)
                    .padding(bottom = 11.dp)
            ){
                Text(
                    text = profile.username,
                    fontSize = 19.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .padding(start = 10.dp)
                        .align(Alignment.BottomStart),
                    textAlign = TextAlign.Left,
                )
                if(profile.genesis){
                    AsyncImage(
                        model = R.drawable.genesis_pass,
                        contentDescription = "Genesis Pass",
                        modifier = Modifier
                            .size(25.dp)
                            .align(Alignment.BottomCenter),
                        placeholder = painterResource(R.drawable.genesis_pass),
                        alignment = Alignment.TopCenter
                    )
                }
                Text(
                    text = "Niveau ${profile.level[1]}",
                    fontSize = 15.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                    modifier = Modifier
                        .align(Alignment.BottomEnd),
                    textAlign = TextAlign.Right,
                )
            }

        }
    }
}

@Composable
fun ProfileInfos(profile: UnProfile){

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ){
        Column(
            modifier = Modifier
                .padding(16.dp)
        ){
            //TITRE
            Text(
                text = "Infos",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            if(profile.age.isNullOrBlank() &&
                profile.paysVille.isNullOrBlank() &&
                profile.membreSince.isNullOrBlank() &&
                profile.dernierPassage.isNullOrBlank() &&
                profile.messageCount.isNullOrBlank() &&
                profile.commentaireCount.isNullOrBlank()){

                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 6.dp), // Espace vertical entre les lignes
                    verticalAlignment = Alignment.CenterVertically // Ou CenterVertically
                ) {
                    Text(
                        text = "Information du profil en privé",
                        fontSize = 16.sp,
                        fontWeight = FontWeight.SemiBold,
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .weight(1f)
                        // Optionnel: donner une largeur minimale ou un poids si besoin
                        // modifier = Modifier.weight(0.4f)
                    )
                }

            }

            //Lignes din'formations
            if(!profile.age.isNullOrBlank()){
                InfoRow(label = "Age", value = profile.age!!)
            }
            if(!profile.paysVille.isNullOrBlank()){
                InfoRow(label = "Localisation", value = profile.paysVille!!)
            }
            if(!profile.membreSince.isNullOrBlank()){
                InfoRow(label = "Membre depuis", value = profile.membreSince!!)
            }
            if (!profile.dernierPassage.isNullOrBlank()) {
                InfoRow(label = "Dernier passage", value = profile.dernierPassage!!)
            }
            if (!profile.messageCount.isNullOrBlank()) {
                InfoRow(label = "Messages forums", value = profile.messageCount!!)
            }
            if (!profile.commentaireCount.isNullOrBlank()) {
                InfoRow(label = "Commentaires", value = profile.commentaireCount!!)
            }
        }
    }

}

@Composable
fun ProfileDescription(profile: UnProfile){
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
    ){
        Column(
            modifier = Modifier
                .padding(16.dp)
        ){
            //TITRE
            Text(
                text = "Description",
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                textAlign = TextAlign.Center,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
            ){
                MessageDisplayFinal(profile.description.toString())
            }

        }
    }
}

@Composable
fun InfoRow(label: String, value: String, modifier: Modifier = Modifier) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp), // Espace vertical entre les lignes
        verticalAlignment = Alignment.Top // Ou CenterVertically
    ) {
        Text(
            text = "$label :",
            fontSize = 15.sp,
            fontWeight = FontWeight.SemiBold,
            // Optionnel: donner une largeur minimale ou un poids si besoin
            // modifier = Modifier.weight(0.4f)
        )
        Spacer(modifier = Modifier.weight(1f)) // Pousse la valeur à droite
        Text(
            text = value,
            fontSize = 15.sp,
            textAlign = TextAlign.End // S'assurer que le texte de la valeur est aligné à droite dans son espace
            // modifier = Modifier.weight(0.6f) // Si vous n'utilisez pas de Spacer
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileTopBar(){
    TopAppBar(
        title = {
            Box(
                modifier = Modifier.fillMaxWidth(),
                contentAlignment = Alignment.Center
            ){
                Text(
                    text = "Profile",
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
                        .clip(CircleShape)
                )
            }
        },
        actions = {
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
}

@Preview(showBackground = true)
@Composable
fun ProfilePreview(){
    ProfileContent(UnProfile(
        username = "Preview",
        description = "Ici c'est la description du profile, on a même un sticker https://image.noelshack.com/fichiers/2025/20/1/1747006967-miyabi-i-am-all-ears-je-t-ecoute-petite-mrd3.png",
        level = listOf("Niveau", "10"),
        age = "99 ans",
        paysVille = "France / Paris",
        membreSince = "25 juillet 2027 (2.872 jours)",
        dernierPassage = "05 juin 2025",
        messageCount = "46.356 message",
        commentaireCount = ""
    ))

//    ProfileContent(UnProfile(
//        username = "Preview",
//        description = "Ici c'est la description du profile, on a même un sticker https://image.noelshack.com/fichiers/2025/20/1/1747006967-miyabi-i-am-all-ears-je-t-ecoute-petite-mrd3.png",
//        level = listOf("Niveau", "10"),
//        age = "",
//        paysVille = "",
//        membreSince = "",
//        dernierPassage = "",
//        messageCount = "",
//        commentaireCount = "",
//        genesis = true
//    ))
}

suspend fun fetchProfile(context: Context): UnProfile = withContext(Dispatchers.IO){
    try {
        Log.d("SHARP", "Get profile")
        val url = "https://www.jeuxvideo.com/profil/${selectedProfile.username.toLowerCase(Locale.ROOT)}?mode=infos"
        val doc = Jsoup.connect(url)
            .userAgent("Mozilla/5.0 (Android)")
            .get()

        val profileAvatarUrl = doc.selectFirst("div.content-img-avatar img")?.attr("src") ?: "null"

        if(profileAvatarUrl != selectedProfile.ppUrl){
            selectedProfile.ppUrl = profileAvatarUrl
        }

        val levelRaw = doc.selectFirst("div.user-level")?.text() ?: "Niveau 0"

        val levelElement = levelRaw.split(" ")

        Log.d("SHARP", "level: $levelElement")

        val pseudoElement = doc.selectFirst("h1.infos-pseudo-label")?.text() ?: "Username"
        val descriptionElement = extractMessageRaw(doc.selectFirst("div.bloc-description-desc"))

        var infoElements = doc.select("ul.display-line-lib")
        infoElements = infoElements.select("li")

        val profile = UnProfile(
            username = pseudoElement,
            description = descriptionElement,
            level = levelElement
        )

        if(!levelElement.getOrNull(2).isNullOrBlank()){
            profile.genesis = true
        }

        for(infoElement in infoElements){
            val lib = infoElement.selectFirst("div.info-lib")?.text().toString()
            val value = infoElement.selectFirst("div.info-value")?.text().toString()

            if(lib.contains("Age :")){
                profile.age = value
            } else if(lib.contains("Pays") || lib.contains("Ville")){
                profile.paysVille = value
            } else if(lib.contains("Membre depuis")){
                profile.membreSince = value
            } else if(lib.contains("Dernier passage")){
                profile.dernierPassage = value
            } else if(lib.contains("Messages Forums")){
                profile.messageCount = value
            } else if(lib.contains("Commentaires")){
                profile.commentaireCount = value
            }
        }




        Log.d("SHARP", "LVL: $profile")
        //Log.d("SHARP", "Profile Data: $profileData")

        profile


    } catch (e: Exception){
        Log.e("SHARP", "Erreur loadings messages: ${e.message}")

        UnProfile(
            username = "error",
            description = "error"
        )
    }
}