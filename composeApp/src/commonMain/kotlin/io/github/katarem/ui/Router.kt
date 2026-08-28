package io.github.katarem.ui

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import compose.icons.FeatherIcons
import compose.icons.feathericons.Book
import compose.icons.feathericons.Clock
import compose.icons.feathericons.Download
import compose.icons.feathericons.Home
import compose.icons.feathericons.Search
import compose.icons.feathericons.Settings
import io.github.katarem.data.model.MangaQuery
import io.github.katarem.ui.component.BottomBar
import io.github.katarem.ui.screen.CategoryScreen
import io.github.katarem.ui.screen.DownloadScreen
import io.github.katarem.ui.screen.MangaInfoScreen
import io.github.katarem.ui.screen.HomeScreen
import io.github.katarem.ui.screen.LibraryScreen
import io.github.katarem.ui.screen.RecentScreen
import io.github.katarem.ui.screen.ReaderScreen
import io.github.katarem.ui.screen.SearchScreen
import io.github.katarem.ui.screen.SettingsScreen
import io.github.katarem.ui.viewmodel.CategoryViewModel
import io.github.katarem.ui.viewmodel.MangaInfoViewModel
import kotlinx.serialization.Serializable
import org.koin.compose.viewmodel.koinViewModel

sealed class Routes {
    @Serializable
    object Home

    @Serializable
    object Search

    @Serializable
    object Download

    @Serializable
    object Recent

    @Serializable
    object Settings

    @Serializable
    object Library

    @Serializable
    data class Category(val name: String, val tagId: String)

    @Serializable
    data class Reader(
        val chapterIndex: Int = 0,
        val mangaId: String,
        val offline: Boolean = false
    )

    @Serializable
    data class MangaInfo(
        val mangaId: String,
        val mangaJson: String
    )
}

@Composable
fun Router(
    mangaInfoViewModel: MangaInfoViewModel = koinViewModel<MangaInfoViewModel>(),
    categoryViewModel: CategoryViewModel = koinViewModel<CategoryViewModel>(),
) {

    val navigator = rememberNavController()
    var selectedTabIndex by remember { mutableStateOf(0) }
    val menuActions = listOf(
        Pair(FeatherIcons.Home) {
            navigator.popBackStack<Routes.Home>(inclusive = false)
            selectedTabIndex = 0
        },
//        Pair(FeatherIcons.Clock) {
//            navigator.popBackStack<Routes.Home>(inclusive = false)
//            navigator.navigate(Routes.Recent)
//            selectedTabIndex = 1
//        },
//        Pair(FeatherIcons.Download){
//            navigator.popBackStack<Routes.Home>(inclusive = false)
//            navigator.navigate(Routes.Download)
//            selectedTabIndex = 2
//        },
        Pair(FeatherIcons.Book) {
            navigator.popBackStack<Routes.Home>(inclusive = false)
            navigator.navigate(Routes.Library)
            selectedTabIndex = 1
        },
        Pair(FeatherIcons.Search) {
            navigator.popBackStack<Routes.Home>(inclusive = false)
            navigator.navigate(Routes.Search)
            selectedTabIndex = 2
        },
        Pair(FeatherIcons.Settings) {
            navigator.popBackStack<Routes.Home>(inclusive = false)
            navigator.navigate(Routes.Settings)
            selectedTabIndex = 3
        },

    )
    Scaffold(
        bottomBar = { BottomBar(menuActions, selectedTabIndex,Modifier.fillMaxWidth()) }
    ) { paddingValues ->
        NavHost(
            navController = navigator,
            startDestination = Routes.Home,
            modifier = Modifier.padding(paddingValues)
        ) {
            composable<Routes.Home> {
                HomeScreen(navigator)
            }
            composable<Routes.Reader> {
                val args = it.toRoute<Routes.Reader>()
                selectedTabIndex = 5
                ReaderScreen(
                    mangaId = args.mangaId,
                    startingChapterIndex = args.chapterIndex,
                    offline = args.offline,
                    mangaInfoViewModel = mangaInfoViewModel
                )
            }
            composable<Routes.Category> {
                val args = it.toRoute<Routes.Category>()
                CategoryScreen(navigator, args.name,args.tagId,categoryViewModel)
            }
            composable<Routes.MangaInfo> {
                val args = it.toRoute<Routes.MangaInfo>()
                MangaInfoScreen(navigator, args.mangaJson, mangaInfoViewModel)
            }
            composable<Routes.Search> {
                SearchScreen(navigator)
            }
            composable<Routes.Recent> {
                RecentScreen(navigator)
            }
            composable<Routes.Download>{
                DownloadScreen(navigator, mangaInfoViewModel)
            }
            composable<Routes.Settings> {
                SettingsScreen()
            }
            composable<Routes.Library> {
                LibraryScreen(navigator)
            }
        }
    }


}