package com.example.whiskey2

import android.content.Intent
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
import androidx.compose.ui.Alignment
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.graphics.painter.BitmapPainter
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whiskey2.data.ParcelableBlended

import com.example.whiskey2.data.ParcelableSingleMalt


class BlendedDetail : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val blended = intent.getParcelableExtra<ParcelableBlended>("blended")
            if (blended != null) {
                DetailContent2(blended)
            }
        }
    }
}

@Composable
fun DetailContent2(blended: ParcelableBlended) {
    val imageUriString = blended.imageUri
    val context = LocalContext.current

    Column(
        modifier = Modifier
            .fillMaxSize()
            .height(75.dp)
            .background(Color(android.graphics.Color.parseColor("#F8E7C9"))),
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

            val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 3000, 3000, true)
            scaledBitmap.asImageBitmap()
        } else {
            null
        }
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(100.dp)
                .padding(end = (20.dp))
        ) {
            Image(
                painter = painterResource(id = R.drawable.listlogo2),
                contentDescription = null,
                modifier = Modifier
                    .size(40.dp)
                    .align(Alignment.CenterEnd)
                    .clickable(onClick = {
                        val intent = Intent(context, BlendedList::class.java)
                        context.startActivity(intent)
                    })
            )

        }

        Box(
            modifier = Modifier
                .fillMaxWidth()
                .padding(start = 60.dp)
        ) {
            val cardSize = 300.dp
            val imageModifier = Modifier
                .fillMaxSize()
                .background(Color(android.graphics.Color.parseColor("#F8E7C9")))

            Card(
                modifier = Modifier.size(cardSize),
            ) {
                Image(
                    painter = if (imageBitmap != null) BitmapPainter(imageBitmap) else defaultImagePainter,
                    contentDescription = "",
                    modifier = imageModifier
                )
            }
        }

        Spacer(modifier = Modifier.height(60.dp))

        Column(
            modifier = Modifier.padding(start = 55.dp)
        )
        {
            Text(
                text = "Whisky information",
                fontStyle = FontStyle.Italic,
                fontWeight = FontWeight.Bold,
                fontSize = 30.sp,
                color = Color.Black
            )

            Spacer(modifier = Modifier.height(30.dp))

            Row()
            {
                Box(modifier = Modifier
                    .width(120.dp)
                    .height(70.dp)
                    ) {
                    Text(
                        text = "Name \n${blended.name}",
                        fontWeight = FontWeight.Bold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(40.dp))
                Box(modifier = Modifier
                    .width(120.dp)
                    .height(70.dp)
                    ) {
                    Text(
                        text = "Price \n${blended.price}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
            Row {
                Box(modifier = Modifier
                    .width(120.dp)
                    .height(70.dp)
                    ) {
                    Text(
                        text = "Year \n${blended.year}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
                Spacer(modifier = Modifier.width(40.dp))
                Box (modifier = Modifier
                    .width(120.dp)
                    .height(70.dp)
                    ) {

                    Text(
                        text = "Location \n${blended.location}",
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 20.sp,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                }
            }
            Spacer(modifier = Modifier.height(20.dp))

            Box  (modifier = Modifier
                .width(280.dp)
                .height(70.dp)
                ) {

                Text(
                    text = "Tasting Note \n${blended.tastingNote}",
                    fontWeight = FontWeight.SemiBold,
                    fontSize = 20.sp,
                    color = MaterialTheme.colorScheme.onSurface,
                )
            }
        }
    }
}

