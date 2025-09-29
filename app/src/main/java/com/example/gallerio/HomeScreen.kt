package com.example.gallerio

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.CollectionsBookmark
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.ImageSearch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.snapshotFlow
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import coil.compose.AsyncImage
import com.example.gallerio.data.ArtworkEntity
import com.example.gallerio.ui.theme.darkGrey
import com.example.gallerio.ui.theme.gridBg
import com.example.gallerio.ui.theme.softPink

sealed class BottomNavItem(val label: String, val route: String, val icon: ImageVector) {
    object Home : BottomNavItem("Home", "home", Icons.Default.Home)
    object Search : BottomNavItem("Search", "search", Icons.Default.Search)
    object Saved : BottomNavItem("Saved", "saved", Icons.Default.CollectionsBookmark)
}

@Composable
fun HomeScreen(viewModel: ArtworkViewModel, navHostController: NavHostController) {
    val artworks = viewModel.uiState.artworks
    val listState = rememberLazyGridState()




    LaunchedEffect(listState) {
        snapshotFlow { listState.layoutInfo.visibleItemsInfo.lastOrNull()?.index }
            .collect { lastVisibleItemIndex ->
                val totalItems = listState.layoutInfo.totalItemsCount
                if (lastVisibleItemIndex != null && lastVisibleItemIndex >= totalItems - 5) {
                    viewModel.fetchArtworks()
                }
            }
    }

    Column {
        Box(
            Modifier.fillMaxWidth().background(darkGrey)
        ){
            Text(
                text = "Gallerio",
                modifier = Modifier.fillMaxWidth().padding(top=16.dp, bottom = 16.dp, start = 16.dp),
                fontSize = 26.sp,
                fontWeight = FontWeight.Bold,
                fontStyle = FontStyle.Italic,
                color = softPink
            )
        }

        ArtworkGrid(
            artworks = artworks,
            isLoading = viewModel.isLoading,
            listState = listState,
            onItemClick = { art -> navHostController.navigate("detail/${art.id}") }
        )
    }

}


@Composable
fun ArtworkGrid(
    artworks: List<ArtworkEntity>,
    isLoading: Boolean,
    listState: LazyGridState,
    onItemClick: (ArtworkEntity) -> Unit
) {

    LazyVerticalGrid(
        columns = GridCells.Fixed(2),
        state = listState,
        modifier = Modifier
            .fillMaxSize()
            .background(gridBg)
    ) {
        items(artworks.size) { index ->
            val art = artworks[index]
            SingleArtwork(art = art) { onItemClick(art) }
        }

        item(span = { GridItemSpan(2) }) {
            if (isLoading) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }
        }
    }
}

@Composable
fun SingleArtwork(art: ArtworkEntity, onCardClick: () -> Unit) {
    val imageUrl = "https://www.artic.edu/iiif/2/${art.imageId}/full/843,/0/default.jpg"
    Card(
        modifier = Modifier.background(gridBg)
            .padding(8.dp)
            .fillMaxWidth()
            .clickable { onCardClick() },
        shape = RoundedCornerShape(12.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        AsyncImage(
            model = imageUrl,
            contentDescription = art.title,
            modifier = Modifier
                .aspectRatio(1f),
            contentScale = ContentScale.Crop
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(viewModel: ArtworkViewModel, navHostController: NavHostController) {
    val isSearchActive = viewModel.isSearchActive
    var query = viewModel.searchQuery
    val searchResults = viewModel.uiState.searchResults

    Column(
        modifier = Modifier.background(gridBg).fillMaxSize()
    ){

        Box(
            Modifier.fillMaxWidth().background(darkGrey)
        ){
            Text(
                text = "Find Art",
                modifier = Modifier.fillMaxWidth().padding(top=16.dp, bottom = 16.dp, start = 16.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = softPink
            )
        }
        SearchBar(
            query = query,
            onQueryChange = { viewModel.updateQuery(it) },
            onSearch = {
                viewModel.searchArtworks(query)
                viewModel.updateSearchActive(false)
            },
            active = isSearchActive,
            onActiveChange = { active ->
                viewModel.updateSearchActive(active)
            },
            placeholder = { Text("Search artworks...") },
            leadingIcon = { Icon(Icons.Default.Search, null) },
            trailingIcon = {
                Icon(
                    imageVector = Icons.Default.Close,
                    null,
                    modifier = Modifier.clickable {
                        viewModel.clearSearch()
                    }
                )
            },
            modifier = Modifier.padding(horizontal = 8.dp)
        ) {

        }

        if (query.isNotBlank()) {
            when {
                viewModel.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }

                searchResults.isEmpty() -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("No search results found")
                    }
                }

                !isSearchActive -> {
                    ArtworkGrid(
                        artworks = searchResults,
                        isLoading = viewModel.isLoading,
                        listState = rememberLazyGridState(),
                        onItemClick = { art -> navHostController.navigate("detail/${art.id}") }
                    )
                }
            }
        }else{
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()){
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.ImageSearch,
                        contentDescription = null,
                        tint = softPink,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Find your next favorite artwork",
                        color = softPink,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic
                    )
                }
            }

        }

    }
}
