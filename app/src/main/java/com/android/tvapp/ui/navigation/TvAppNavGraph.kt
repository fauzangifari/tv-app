package com.android.tvapp.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.android.tvapp.di.AppViewModelFactory
import com.android.tvapp.ui.detail.ShowDetailScreen
import com.android.tvapp.ui.detail.ShowDetailViewModel
import com.android.tvapp.ui.list.ShowListScreen
import com.android.tvapp.ui.list.ShowListViewModel

private const val ARG_SHOW_ID = "showId"
private const val ROUTE_LIST = "list"
private const val ROUTE_DETAIL = "detail/{$ARG_SHOW_ID}"

@Composable
fun TvAppNavGraph(navController: NavHostController = rememberNavController()) {
    NavHost(navController = navController, startDestination = ROUTE_LIST) {
        composable(ROUTE_LIST) {
            val viewModel: ShowListViewModel = viewModel(factory = AppViewModelFactory.factory)
            ShowListScreen(
                viewModel = viewModel,
                onShowClick = { showId -> navController.navigate("detail/$showId") }
            )
        }
        composable(
            route = ROUTE_DETAIL,
            arguments = listOf(navArgument(ARG_SHOW_ID) { type = NavType.IntType })
        ) { backStackEntry ->
            val showId = backStackEntry.arguments?.getInt(ARG_SHOW_ID) ?: return@composable
            val viewModel: ShowDetailViewModel = viewModel(factory = AppViewModelFactory.factory)
            ShowDetailScreen(
                showId = showId,
                viewModel = viewModel,
                onBackClick = { navController.popBackStack() }
            )
        }
    }
}
