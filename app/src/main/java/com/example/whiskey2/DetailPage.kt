package com.example.whiskey2

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import android.net.Uri
import android.os.Build
import android.provider.MediaStore
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp

import com.example.whiskey2.data.ParcelableSingleMalt


class DetailPage : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val singleMalt = intent.getParcelableExtra<ParcelableSingleMalt>("singleMalt")

            if (singleMalt != null) {
                DetailContent(singleMalt)
            }
        }
    }
}

@Composable
fun DetailContent(singleMalt: ParcelableSingleMalt) {
    val imageUriString = singleMalt.imageUri
    val scrollState = rememberScrollState()
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        val defaultImagePainter = painterResource(id = R.drawable.defaultimage)

        val imageBitmap = if (imageUriString != null) {
            val imageUri = Uri.parse(imageUriString)
            val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                ImageDecoder.decodeBitmap(
                    ImageDecoder.createSource(
                        context.contentResolver,
                        imageUri
                    )
                )
            } else {
                MediaStore.Images.Media.getBitmap(context.contentResolver, imageUri)
            }

            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 200, 200, true)
            scaledBitmap.asImageBitmap()
        } else {
            null
        }

        Image(
            painter = if (imageBitmap != null) BitmapPainter(imageBitmap) else defaultImagePainter,
            contentDescription = "",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp)
                .background(Color.Red)
        )

        Spacer(modifier = Modifier.height(16.dp))

        Column(
            modifier = Modifier
                .verticalScroll(scrollState),
        ) {
            Text(
                text = "Whisky Name : ${singleMalt.name}",
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary
            )
            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = "Price : ${singleMalt.price}",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Year : ${singleMalt.year}",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Location : ${singleMalt.location}",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                text = "Tasting Note : ${singleMalt.tastingNote}",
                fontWeight = FontWeight.SemiBold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(top = 8.dp)
            )
        }
    }
}
