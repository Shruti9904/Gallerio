package com.example.gallerio

import android.app.Application
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.room.Room
import com.example.gallerio.data.AppDatabase
import com.example.gallerio.ui.theme.GallerioTheme
import com.example.gallerio.ui.theme.darkGrey
import com.example.gallerio.ui.theme.softPink

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val app = application as GallerioApp
        val dao =app.database.artworkDao()
        val repository = ArtworkRepository(dao)
        val factory = ArtworkViewModelFactory(repository)

        val artworkViewModel = ViewModelProvider(this, factory)[ArtworkViewModel::class.java]

        enableEdgeToEdge()
        setContent {
            GallerioTheme {
                Gallerio( viewModel = artworkViewModel)
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun Gallerio(viewModel: ArtworkViewModel) {
    val navController = rememberNavController()

    val items = listOf(
        BottomNavItem.Home,
        BottomNavItem.Search,
        BottomNavItem.Saved
    )

    val currentBackStackEntry = navController.currentBackStackEntryAsState().value
    val currentRoute = currentBackStackEntry?.destination?.route


    Scaffold(
        bottomBar = {
            if (currentRoute != null && currentRoute in items.map { it.route }) {
                NavigationBar (
                    containerColor = darkGrey,
                    contentColor = softPink
                ){
                    items.forEach { item ->
                        NavigationBarItem(
                            selected = currentRoute == item.route,
                            onClick = {
                                if (currentRoute != item.route) {
                                    navController.navigate(item.route) {
                                        popUpTo("home") { inclusive = false }
                                        launchSingleTop = true
                                    }
                                }
                            },
                            icon = { Icon(item.icon, contentDescription = item.label) },
                            label = { Text(item.label) },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = softPink,
                                unselectedIconColor = softPink,
                                indicatorColor = Color(0xFFFF80AB).copy(alpha = 0.2f)
                            )
                        )
                    }
                }
            }
        },
        modifier = Modifier.fillMaxSize()
    ) { innerPadding ->
        NavHost(
            navController,
            startDestination = "home",
            modifier = Modifier.padding(innerPadding)
        ) {
            composable("home") {
                HomeScreen(viewModel, navController)
            }
            composable("detail/{artworkId}") { backStackEntry ->
                val artworkId = backStackEntry.arguments?.getString("artworkId")?.toIntOrNull()
                if (artworkId != null) {
                    ArtworkDetailScreen(viewModel, artworkId, navController)
                } else {
                    Log.d("error", "artwork is null")
                }
            }
            composable("search") {
                SearchScreen(viewModel,navController)
            }
            composable("saved") {
                SavedScreen(viewModel,navController)
            }
        }
    }
}

class GallerioApp:Application(){
    lateinit var database: AppDatabase
        private set

    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "gallerio_db"
        ).build()
    }
}

class ArtworkViewModelFactory(private val repository: ArtworkRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(ArtworkViewModel::class.java)) {
            return ArtworkViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}



