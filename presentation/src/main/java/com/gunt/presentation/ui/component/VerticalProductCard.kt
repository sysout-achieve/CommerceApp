package com.gunt.presentation.ui.component

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.Card
import androidx.compose.material.IconButton
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.gunt.commerceapp.R
import com.gunt.domain.model.Product
import com.gunt.domain.model.SalesStatus
import com.gunt.presentation.ui.theme.Purple40
import com.gunt.presentation.ui.util.discountRatio

@Composable
fun VerticalProductCard(product: Product) {
    Card(
        shape = RoundedCornerShape(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .height(intrinsicSize = IntrinsicSize.Max)
            .padding(0.dp, 18.dp)
            .shadow(elevation = 8.dp),
    ) {
        Box(modifier = Modifier.fillMaxWidth()) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(10.dp),
                verticalArrangement = Arrangement.Center,
                horizontalAlignment = Alignment.Start
            ) {
                AsyncImage(
                    model = product.image,
                    contentDescription = "product image",
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .aspectRatio(1.5f)
                        .fillMaxWidth()
                )
                Column(
                    modifier = Modifier
                        .padding(10.dp, 0.dp, 0.dp, 0.dp)
                ) {
                    Text(
                        fontSize = 20.sp,
                        text = product.name,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.padding(0.dp, 10.dp, 0.dp, 0.dp)
                    )
                    Price(product = product)
                }
            }
            IconButton(
                onClick = { },
                modifier = Modifier.align(Alignment.TopEnd)
            ) {
                Image(
                    if (product.isLike == true) painterResource(R.drawable.ic_btn_heart_on)
                    else painterResource(R.drawable.ic_btn_heart_off),
                    "FavoriteIcon"
                )
            }
        }
    }
}

@Composable
private fun Price(product: Product) {
    when (product.getSaleStatus()) {
        SalesStatus.ON_SALE -> {
            Text(
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold,
                text = "${product.originalPrice}원"
            )
        }

        SalesStatus.ON_DISCOUNT -> {
            Row(
                verticalAlignment = Alignment.Bottom
            ) {
                Text(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFFfa622f),
                    text = "${discountRatio(product.originalPrice, product.discountedPrice!!)}%",
                    modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp)
                )
                Text(
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Bold,
                    color = Purple40,
                    text = "${product.discountedPrice}원",
                    modifier = Modifier.padding(0.dp, 0.dp, 8.dp, 0.dp)
                )
                Text(
                    fontSize = 16.sp,
                    text = "${product.originalPrice}원",
                    color = Color.Gray,
                    style = TextStyle(textDecoration = TextDecoration.LineThrough)
                )
            }
        }

        SalesStatus.SOLD_OUT -> {
            Text(
                fontSize = 18.sp,
                color = Color.Gray,
                text = "품절"
            )
        }
    }
}