package com.example.glaucoscan.core.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.glaucoscan.presentation.screens.camera.CameraScreen
import com.example.glaucoscan.presentation.screens.gallery.GalleryScreen
import com.example.glaucoscan.presentation.screens.home.HomeScreen

@Composable
fun AppNavHost(
    navController: NavHostController = rememberNavController()
){
    NavHost(
        navController = navController,
        startDestination = Screen.Home
    ){
        composable<Screen.Home>{
            HomeScreen(
                onOpenGallery = {
                    navController.navigate(
                        Screen.Gallery
                    )
                },
                onOpenCamera = {
                    navController.navigate(
                        Screen.Camera
                    )
                }
            )
        }

        composable<Screen.Gallery>{
            GalleryScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }

        composable<Screen.Camera>{
            CameraScreen(
                onBack = {
                    navController.popBackStack()
                }
            )
        }
    }
}