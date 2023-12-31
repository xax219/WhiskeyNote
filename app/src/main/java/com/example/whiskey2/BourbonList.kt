package com.example.whiskey2

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.whiskey2.data.AppDatabase
import com.example.whiskey2.data.Bourbon
import com.example.whiskey2.data.ParcelableBourbon
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class BourbonList : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val context = LocalContext.current
            val db = remember {
                AppDatabase.getDatabase(context)
            }
            val BourbonList by db.bourbonDAO().getAll().collectAsState(initial = emptyList())

            Column(
                modifier = Modifier.fillMaxSize()
            ) {
                Bourbon()
                BourbonList(BourbonList = BourbonList, db = db)
            }
        }
    }
}

@Composable
fun BourbonList(BourbonList: List<Bourbon>, db: AppDatabase) {
    val parcelableList = BourbonList.map { Bourbon ->
        ParcelableBourbon(
            uid = Bourbon.uid,
            name = Bourbon.name,
            price = Bourbon.price,
            year = Bourbon.year,
            location = Bourbon.location,
            tastingNote = Bourbon.tastingNote,
            imageUri = Bourbon.imageUri
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(BourbonList) { bourbon ->
            BourbonCard(bourbon, db)
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BourbonCard(Bourbon: Bourbon, db: AppDatabase) {
    val scope = rememberCoroutineScope()
    val context = LocalContext.current
    val cardColors = CardDefaults.cardColors(
        Color.Transparent
    )
    val squareShape: Shape = MaterialTheme.shapes.small.copy()

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(top = 5.dp)
            .clickable {
                val intent = Intent(context, BourbonDetail::class.java).apply {
                    putExtra(
                        "bourbon", ParcelableBourbon(
                            name = Bourbon.name,
                            price = Bourbon.price ?: 0,
                            year = Bourbon.year,
                            location = Bourbon.location,
                            tastingNote = Bourbon.tastingNote,
                            imageUri = Bourbon.imageUri

                        )
                    )
                }
                context.startActivity(intent)
            },
        colors = cardColors,
        shape = squareShape
    ) {
        var isEditing by remember { mutableStateOf(false) }
        var editedName by remember { mutableStateOf(Bourbon.name) }
        var editedPrice by remember { mutableStateOf(Bourbon.price?.toString() ?: "") }
        var editedYear by remember { mutableStateOf(Bourbon.year.toString()) }
        var editedLocation by remember { mutableStateOf(Bourbon.location) }
        var editedTastingNote by remember { mutableStateOf(Bourbon.tastingNote) }
        var selectedUri by remember {
            mutableStateOf<Uri?>(null)
        }
        val launcher = rememberLauncherForActivityResult(
            contract = ActivityResultContracts.GetContent()

        ) { uri ->
            if (uri != null) {
                selectedUri = uri
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri, takeFlags)
                scope.launch(Dispatchers.IO) {
                    db.bourbonDAO().updateBourbon(Bourbon.copy(imageUri = uri.toString()))
                }
            }
        }

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Bourbon.imageUri?.let { imageUriString ->
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

                    Image(
                        bitmap = scaledBitmap.asImageBitmap(),
                        contentDescription = "",
                        modifier = Modifier.clickable {
                            launcher.launch("image/*")
                        },
                    )
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
                            label = { Text("Whiskey Name") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally),
//                                .border(BorderStroke(width = 1.dp, color = Color.Black)), //텍스트 필드 테두리 변경
                            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent) // 텍스트 필드 색상 변경
                        )
                        TextField(
                            value = editedPrice,
                            onValueChange = { editedPrice = it },
                            label = { Text("Price") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally),
                            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)

                        )

                        TextField(
                            value = editedYear,
                            onValueChange = { editedYear = it },
                            label = { Text("Year") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally),
                            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)

                        )

                        TextField(
                            value = editedLocation,
                            onValueChange = { editedLocation = it },
                            label = { Text("Location") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally),
                            colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)

                        )

                        TextField(
                            value = editedTastingNote,
                            onValueChange = { editedTastingNote = it },
                            label = { Text("Tasting Note") },
                            modifier = Modifier
                                .fillMaxWidth()
                                .align(Alignment.CenterHorizontally),
                            colors = TextFieldDefaults.textFieldColors(
                                containerColor = Color.Transparent,
                                focusedIndicatorColor = Color.Transparent,
                                unfocusedIndicatorColor = Color.Transparent
                            )

                        )
                    } else {
                        Text(text = "Whiskey Name: ${if (editedName.isEmpty()) Bourbon.name else editedName}")
                        Text(text = "Price: ${if (editedPrice.isEmpty()) Bourbon.price else editedPrice}")
                        Text(text = "Year: ${if (editedYear.isEmpty()) Bourbon.year else editedYear}")
                        Text(text = "Location: ${if (editedLocation.isEmpty()) Bourbon.location else editedLocation}")
                        Text(text = "Tasting Note: ${if (editedTastingNote.isEmpty()) Bourbon.tastingNote else editedTastingNote}")
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
                                val editedBourbon = Bourbon.copy(
                                    name = editedName,
                                    price = editedPrice.toIntOrNull(),
                                    year = editedYear.toIntOrNull() ?: 0,
                                    location = editedLocation,
                                    tastingNote = editedTastingNote
                                )
                                db.bourbonDAO().updateBourbon(editedBourbon)
                            }
                        }
                        isEditing = !isEditing
                    },
                    modifier = Modifier
                        .padding(4.dp)
                        .width(IntrinsicSize.Max),
                    colors = ButtonDefaults.buttonColors(Color(android.graphics.Color.parseColor("#FCF4E7")))
                ) {
                    if (isEditing) {
                        Text(text = "Save", color = Color.Black)
                    } else {
                        Text(text = "Edit", color = Color.Black)
                    }
                }


                Button(
                    onClick = {
                        scope.launch(Dispatchers.IO) {
                            db.bourbonDAO().delete(Bourbon)
                        }
                    },
                    modifier = Modifier.padding(4.dp),
                    colors = ButtonDefaults.buttonColors(Color(android.graphics.Color.parseColor("#FCF4E7")))
                ) {
                    Text(text = "Delete", color = Color.Black)
                }
            }
        }
    }
}


@Composable
fun Bourbon() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .background(Color(android.graphics.Color.parseColor("#FCF4E7"))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Bourbon Whiskey",
            fontStyle = FontStyle.Italic,
            color = Color.Black,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
    }
}