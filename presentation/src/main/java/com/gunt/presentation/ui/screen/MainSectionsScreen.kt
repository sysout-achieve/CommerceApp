package com.gunt.presentation.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material.CircularProgressIndicator
import androidx.compose.material.Divider
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material.Text
import androidx.compose.material.pullrefresh.pullRefresh
import androidx.compose.material.pullrefresh.rememberPullRefreshState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.gunt.domain.model.SectionType
import com.gunt.presentation.ui.MainViewModel
import com.gunt.presentation.ui.component.ProductCard
import com.gunt.presentation.ui.component.VerticalProductCard
import com.gunt.presentation.ui.theme.Purple40

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MainSectionsScreen(mainViewModel: MainViewModel = viewModel()) {
    val sections by mainViewModel.sections.collectAsState()
    val isLoading by mainViewModel.isLoading.collectAsState()
    val errorText by mainViewModel.errorText.collectAsState()
    val pullRefreshState = rememberPullRefreshState(
        refreshing = isLoading,
        onRefresh = mainViewModel::onRefresh
    )

    Box(
        modifier = Modifier
            .fillMaxSize()
            .pullRefresh(pullRefreshState),
        contentAlignment = Alignment.Center
    ) {

        LazyColumn(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
        ) {
            if (errorText.isNotEmpty()) {
                item {
                    Text(
                        text = errorText,
                        modifier = Modifier
                            .fillMaxHeight()
                    )
                }
            }
            itemsIndexed(sections, key = { _, item -> item.id }) { index, sectionInfo ->
                Text(
                    text = sectionInfo.title,
                    fontSize = 28.sp,
                    fontWeight = FontWeight.Bold,
                    textAlign = TextAlign.Center,
                    letterSpacing = 2.sp,
                    lineHeight = 30.sp,
                    modifier = Modifier.padding(16.dp)
                )

                when (sectionInfo.type) {
                    SectionType.VERTICAL ->
                        Column {
                            for (product in sectionInfo.products) {
                                VerticalProductCard(
                                    product = product,
                                    onClick = { item ->
                                        if (item.isLike == true) mainViewModel.removeLikeProduct(item)
                                        else mainViewModel.addLikeProduct(item)
                                    }
                                )
                            }
                        }

                    SectionType.HORIZONTAL ->
                        LazyRow {
                            items(sectionInfo.products, key = {
                                it.id
                            }) { product ->
                                ProductCard(
                                    product = product,
                                    onClick = { item ->
                                        if (item.isLike == true) mainViewModel.removeLikeProduct(item)
                                        else mainViewModel.addLikeProduct(item)
                                    }
                                )
                            }
                        }

                    SectionType.GRID -> {
                        LazyRow {
                            itemsIndexed(sectionInfo.products, key = { _, product ->
                                product.id
                            }) { index, product ->
                                if (index * 2 < sectionInfo.products.size) {
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .fillMaxHeight()
                                    ) {
                                        ProductCard(
                                            product = product,
                                            onClick = { item ->
                                                if (item.isLike == true) mainViewModel.removeLikeProduct(item)
                                                else mainViewModel.addLikeProduct(item)
                                            }
                                        )
                                        ProductCard(
                                            product = product,
                                            onClick = { item ->
                                                if (item.isLike == true) mainViewModel.removeLikeProduct(item)
                                                else mainViewModel.addLikeProduct(item)
                                            }
                                        )
                                    }
                                }
                            }
                        }
                    }
                }

                Divider(
                    color = Color.Black,
                    modifier = Modifier
                        .height(1.dp)
                        .fillMaxWidth()
                )
                if (index >= sections.size - 3 && !isLoading) {
                    mainViewModel.loadNextPage()
                }

            }

        }

        if (isLoading) {
            CircularProgressIndicator(
                modifier = Modifier
                    .size(50.dp)
                    .align(Alignment.TopCenter),
                color = Purple40,
                backgroundColor = Color.White
            )
        }
    }
}
