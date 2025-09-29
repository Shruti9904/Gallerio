package com.example.gallerio

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.grid.rememberLazyGridState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BookmarkBorder
import androidx.compose.material.icons.filled.FavoriteBorder
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.focusModifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.gallerio.ui.theme.Purple40
import com.example.gallerio.ui.theme.darkGrey
import com.example.gallerio.ui.theme.gridBg
import com.example.gallerio.ui.theme.softPink

@Composable
fun SavedScreen(viewModel: ArtworkViewModel,navController: NavController){

    val savedArtworks = viewModel.uiState.savedArtworks
    val isLoading = viewModel.isLoading

    Column(
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            Modifier.fillMaxWidth().background(darkGrey)
        ){
            Text(
                text = "Saved Artworks",
                modifier = Modifier.fillMaxWidth().padding(top=16.dp, bottom = 16.dp, start = 16.dp),
                fontSize = 20.sp,
                fontWeight = FontWeight.Bold,
                color = softPink
            )
        }

        Spacer(modifier = Modifier.height(8.dp))
        if (isLoading) {
            Box(
                modifier = Modifier.fillMaxSize().background(gridBg),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator()
            }
        }else if(savedArtworks.isEmpty()){
            Box(
                modifier = Modifier
                    .fillMaxSize().background(gridBg)
                    .padding(horizontal = 24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Icon(
                        imageVector = Icons.Default.BookmarkBorder,
                        contentDescription = null,
                        tint = softPink,
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Nothing saved… find art you love!",
                        color = softPink,
                        fontSize = 16.sp,
                        textAlign = TextAlign.Center,
                        fontStyle = FontStyle.Italic
                    )
                }
            }
        }else{
            ArtworkGrid(
                artworks = savedArtworks,
                isLoading = viewModel.isLoading,
                listState = rememberLazyGridState()
            ) {art->
                navController.navigate("detail/${art.id}")
            }
        }
    }

}