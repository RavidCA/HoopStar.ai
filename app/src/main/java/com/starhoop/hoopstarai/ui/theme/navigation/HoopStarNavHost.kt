package com.starhoop.hoopstar.ui.navigation

import android.net.Uri
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.core.tween
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.starhoop.hoopstar.ui.auth.LoginScreen
import com.starhoop.hoopstar.ui.auth.RegisterScreen
import com.starhoop.hoopstar.ui.highlights.HighlightsScreen
import com.starhoop.hoopstar.ui.mapping.MappingScreen
import com.starhoop.hoopstar.ui.player.DownloadHelper
import com.starhoop.hoopstar.ui.player.ReelPlayerScreen
import com.starhoop.hoopstar.ui.splash.SplashScreen
import com.starhoop.hoopstar.ui.teams.RosterScreen
import com.starhoop.hoopstar.ui.teams.TeamsScreen
import com.starhoop.hoopstar.ui.upload.JobStatusScreen
import com.starhoop.hoopstar.ui.upload.UploadScreen
import androidx.compose.ui.platform.LocalContext

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HoopStarNavHost(
    navController: NavHostController = rememberNavController()
) {
    NavHost(navController = navController, startDestination = Routes.SPLASH) {
        composable(Routes.SPLASH) {
            SplashScreen(
                onLoggedIn = { navController.navigate(Routes.TEAMS) { popUpTo(Routes.SPLASH) { inclusive = true } } },
                onLoggedOut = { navController.navigate(Routes.LOGIN) { popUpTo(Routes.SPLASH) { inclusive = true } } }
            )
        }

        composable(Routes.LOGIN) {
            LoginScreen(
                onLoginSuccess = { navController.navigate(Routes.TEAMS) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onGoToRegister = { navController.navigate(Routes.REGISTER) }
            )
        }

        composable(
            Routes.REGISTER,
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300)) }
        ) {
            RegisterScreen(
                onRegisterSuccess = { navController.navigate(Routes.TEAMS) { popUpTo(Routes.LOGIN) { inclusive = true } } },
                onBack = { navController.popBackStack() }
            )
        }

        composable(Routes.TEAMS) {
            TeamsScreen(
                onTeamClick = { teamId -> navController.navigate(Routes.roster(teamId)) },
                onLoggedOut = { navController.navigate(Routes.LOGIN) { popUpTo(Routes.TEAMS) { inclusive = true } } }
            )
        }

        composable(
            route = Routes.ROSTER,
            arguments = listOf(navArgument("teamId") { type = NavType.IntType }),
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300)) }
        ) {
            RosterScreen(
                onBack = { navController.popBackStack() },
                onOpenGames = { teamId -> navController.navigate(Routes.upload(teamId)) }
            )
        }

        composable(
            route = Routes.UPLOAD,
            arguments = listOf(navArgument("teamId") { type = NavType.IntType }),
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300)) }
        ) {
            UploadScreen(
                onBack = { navController.popBackStack() },
                onOpenJob = { jobId -> navController.navigate(Routes.jobStatus(jobId)) }
            )
        }

        composable(
            route = Routes.JOB_STATUS,
            arguments = listOf(navArgument("jobId") { type = NavType.IntType }),
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300)) }
        ) {
            JobStatusScreen(
                onBack = { navController.popBackStack() },
                onGoToMapping = { jobId, teamId -> navController.navigate(Routes.mapping(jobId, teamId)) }
            )
        }

        composable(
            route = Routes.MAPPING,
            arguments = listOf(
                navArgument("jobId") { type = NavType.IntType },
                navArgument("teamId") { type = NavType.IntType }
            ),
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300)) }
        ) {
            MappingScreen(
                onBack = { navController.popBackStack() },
                onContinueToHighlights = { jobId, teamId ->
                    navController.navigate(Routes.highlights(jobId, teamId))
                }
            )
        }

        composable(
            route = Routes.HIGHLIGHTS,
            arguments = listOf(
                navArgument("jobId") { type = NavType.IntType },
                navArgument("teamId") { type = NavType.IntType }
            ),
            enterTransition = { slideIntoContainer(AnimatedContentTransitionScope.SlideDirection.Start, tween(300)) },
            popExitTransition = { slideOutOfContainer(AnimatedContentTransitionScope.SlideDirection.End, tween(300)) }
        ) {
            HighlightsScreen(
                onBack = { navController.popBackStack() },
                onPlayReel = { url, name -> navController.navigate(Routes.player(url, name)) }
            )
        }

        composable(
            route = Routes.PLAYER,
            arguments = listOf(
                navArgument("url") { type = NavType.StringType },
                navArgument("name") { type = NavType.StringType }
            )
        ) { backStackEntry ->
            val context = LocalContext.current
            val url = Uri.decode(backStackEntry.arguments?.getString("url") ?: "")
            val name = Uri.decode(backStackEntry.arguments?.getString("name") ?: "שחקן")
            ReelPlayerScreen(
                reelUrl = url,
                playerName = name,
                onBack = { navController.popBackStack() },
                onDownload = { downloadUrl -> DownloadHelper.download(context, downloadUrl) }
            )
        }
    }
}