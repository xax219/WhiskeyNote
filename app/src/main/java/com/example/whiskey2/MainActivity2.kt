package com.example.whiskey2

import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.whiskey2.data.AppDatabase
import com.example.whiskey2.data.SingleMalt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity2 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val db = remember {
                AppDatabase.getDatabase(context)
            }
            val singleMaltList by db.singleMaltDAO().getAll().collectAsState(initial = emptyList())

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Single()
                SingleMaltList(singleMaltList = singleMaltList, db = db)
            }
        }
    }
}

@Composable
fun SingleMaltList(singleMaltList: List<SingleMalt>, db: AppDatabase) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(singleMaltList) { singleMalt ->
            SingleMaltCard(singleMalt = singleMalt, db = db)
        }
    }
}
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SingleMaltCard(singleMalt: SingleMalt, db: AppDatabase) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val cardColors = CardDefaults.cardColors(
        Color.Transparent
    )
    val squareShape: Shape = MaterialTheme.shapes.small.copy()

    Card(
        modifier = Modifier
            .fillMaxWidth(),
        colors = cardColors,
        shape = squareShape
    ) {
        var isEditing by remember { mutableStateOf(false) }
        var editedName by remember { mutableStateOf(singleMalt.name) }
        var editedPrice by remember { mutableStateOf(singleMalt.price?.toString() ?: "") }
        var editedYear by remember { mutableStateOf(singleMalt.year.toString()) }
        var editedLocation by remember { mutableStateOf(singleMalt.location) }
        var editedTastingNote by remember { mutableStateOf(singleMalt.tastingNote) }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                singleMalt.imageUri?.let { imageUriString ->
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

                    val scaledBitmap = Bitmap.createScaledBitmap(bitmap, 300, 300, true)

                    Image(bitmap = scaledBitmap.asImageBitmap(), contentDescription = "")
                }
                Spacer(modifier = Modifier.width(16.dp))
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                ) {
                    if (isEditing) {
                        TextField(
                            value = editedName,
                            onValueChange = { editedName = it },
                            label = { Text("Whisky Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                        )
                        TextField(
                            value = editedPrice,
                            onValueChange = { editedPrice = it },
                            label = { Text("Price") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                        )
                        TextField(
                            value = editedYear,
                            onValueChange = { editedYear = it },
                            label = { Text("Year") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                        )
                        TextField(
                            value = editedLocation,
                            onValueChange = { editedLocation = it },
                            label = { Text("Location") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                        )
                        TextField(
                            value = editedTastingNote,
                            onValueChange = { editedTastingNote = it },
                            label = { Text("Tasting Note") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally)
                        )
                    } else {
                        Text(text = "Whisky Name: ${if (editedName.isEmpty()) singleMalt.name else editedName}")
                        Text(text = "Price: ${if (editedPrice.isEmpty()) singleMalt.price else editedPrice}")
                        Text(text = "Year: ${if (editedYear.isEmpty()) singleMalt.year else editedYear}")
                        Text(text = "Location: ${if (editedLocation.isEmpty()) singleMalt.location else editedLocation}")
                        Text(text = "Tasting Note: ${if (editedTastingNote.isEmpty()) singleMalt.tastingNote else editedTastingNote}")
                    }
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.End
            ) {
                Button(
                    onClick = {
                        if (isEditing) {
                            scope.launch(Dispatchers.IO) {
                                val editedSingleMalt = singleMalt.copy(
                                    name = editedName,
                                    price = editedPrice.toIntOrNull(),
                                    year = editedYear.toIntOrNull() ?: 0,
                                    location = editedLocation,
                                    tastingNote = editedTastingNote
                                )
                                db.singleMaltDAO().updateSingleMalt(editedSingleMalt)
                            }
                        }
                        isEditing = !isEditing
                    },
                    modifier = Modifier
                        .padding(4.dp)
                        .width(IntrinsicSize.Max),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                ) {
                    if (isEditing) {
                        Text(text = "Save", color = MaterialTheme.colorScheme.onPrimary)
                    } else {
                        Text(text = "Edit", color = MaterialTheme.colorScheme.onPrimary)
                    }
                }


                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            db.singleMaltDAO().delete(singleMalt)
                        }
                    },
                    modifier = Modifier.padding(4.dp),
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                ) {
                    Text(text = "Delete", color = MaterialTheme.colorScheme.onPrimary)
                }
            }
        }
    }
}


@Composable
fun Single() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .background(Color(android.graphics.Color.parseColor("#1EA4FF"))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Single Malt Whiskey",
            color = androidx.compose.ui.graphics.Color.White,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
    }
}