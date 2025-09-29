package com.example.gallerio

import android.content.Context
import android.content.Intent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Style
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import coil.compose.AsyncImagePainter
import coil.compose.rememberAsyncImagePainter
import com.example.gallerio.ui.theme.gridBg
import com.example.gallerio.ui.theme.softPink

@Composable
fun ArtworkDetailScreen(viewModel: ArtworkViewModel, artworkId: Int, navController: NavController) {
    val uiState = viewModel.uiState
    val context = LocalContext.current
    val art = uiState.artworks.firstOrNull { it.id == artworkId }
        ?: uiState.savedArtworks.firstOrNull { it.id == artworkId }
        ?: uiState.searchResults.firstOrNull { it.id == artworkId }

    if (art == null) {
        Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = softPink)
        }
        return
    }

    val imageUrl = "https://www.artic.edu/iiif/2/${art.imageId}/full/843,/0/default.jpg"
    val painter = rememberAsyncImagePainter(imageUrl)
    val state = painter.state
    val savedIcon = if (art.isSaved) Icons.Default.Bookmark else Icons.Default.BookmarkBorder
    val artistDisplay = art.artistDisplay ?: art.artistTitle ?: ""

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .background(gridBg)
            .padding(16.dp)
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(onClick = { navController.popBackStack() }) {
                Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = softPink)
            }
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = {
                    if (art.isSaved) viewModel.deleteArtwork(art)
                    else viewModel.saveArtwork(art)

                }) {
                    Icon(savedIcon, contentDescription = "Toggle Save")
                }

                IconButton(onClick = {
                    shareArtworkUrl(context,imageUrl,art.title)

                }) {
                    Icon(imageVector = Icons.Default.Share, contentDescription = "share")
                }
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Artwork Image
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxWidth()
                .aspectRatio(1f)
                .clip(RoundedCornerShape(16.dp))
        ) {
            Image(
                painter = painter,
                contentDescription = art.title,
                contentScale = ContentScale.Fit,
                modifier = Modifier.fillMaxSize()
            )
            when (state) {
                is AsyncImagePainter.State.Loading -> CircularProgressIndicator(color = softPink)
                is AsyncImagePainter.State.Error -> Text("Failed to load image", color = Color.White)
                else -> Unit
            }
        }

        Spacer(modifier = Modifier.height(16.dp))

        // Info Card
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = Color(0xFF2A0E3D)),
            elevation = CardDefaults.cardElevation(defaultElevation = 6.dp)
        ) {
            Column(modifier = Modifier.padding(16.dp)) {

                // Title
                Text(
                    text = art.title,
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 1.sp
                    ),
//                    color = softPink,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                // Artist / Period / Origin
                InfoRow(icon = Icons.Default.Person, label = "Artist", value = artistDisplay)
                InfoRow(icon = Icons.Default.CalendarToday, label = "Period", value = art.dateDisplay)
                InfoRow(icon = Icons.Default.Place, label = "Origin", value = art.placeOfOrigin ?: "")

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = softPink.copy(alpha = 0.3f))

                // Medium / Dimensions / Type / Style
                InfoRow(icon = Icons.Default.Brush, label = "Medium", value = art.mediumDisplay ?: "")
                InfoRow(icon = Icons.Default.Category, label = "Type", value = art.artworkTypeTitle ?: "")
                InfoRow(icon = Icons.Default.Style, label = "Style", value = art.styleTitle ?: "")

                Spacer(modifier = Modifier.height(8.dp))
                Divider(color = softPink.copy(alpha = 0.3f))


                Spacer(modifier = Modifier.height(8.dp))

                art.description?.let {
                    Text(
                        text = it.replace("<[^>]*>".toRegex(), ""), // remove HTML tags
                        color = Color.White,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

            }
        }
    }
}

@Composable
fun InfoRow(icon: ImageVector, label: String, value: String?) {
    if (value.isNullOrEmpty()) return
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.padding(vertical = 6.dp)
    ) {
        Icon(icon, contentDescription = label, tint = softPink, modifier = Modifier.size(18.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text("$label: ", color = softPink, style = MaterialTheme.typography.labelLarge)
        Text(value, color = Color.White, style = MaterialTheme.typography.bodyMedium)
    }
}


fun shareArtworkUrl(context: Context, imageUrl:String,title:String){
    val caption = "Check out this artwork \"$title\" on Gallerio! 🎨\n$imageUrl"
    val intent = Intent(Intent.ACTION_SEND).apply {
        type = "text/plain"
        putExtra(Intent.EXTRA_TEXT,caption)
    }
    context.startActivity(Intent.createChooser(intent,"Share artwork"))
}
