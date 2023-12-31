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
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.whiskey2.data.AppDatabase
import com.example.whiskey2.data.ParcelableSingleMalt
import com.example.whiskey2.data.SingleMalt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class SingleMaltList : ComponentActivity() {
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
    val parcelableList = singleMaltList.map { singleMalt ->
        ParcelableSingleMalt(
            uid = singleMalt.uid,
            name = singleMalt.name,
            price = singleMalt.price,
            year = singleMalt.year,
            location = singleMalt.location,
            tastingNote = singleMalt.tastingNote,
            imageUri = singleMalt.imageUri
        )
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(singleMaltList) { singleMalt ->
            SingleMaltCard(singleMalt, db)
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
            .fillMaxWidth()
            .padding(top = 5.dp)
            .clickable {
                val intent = Intent(context, SingleMaltDetail::class.java).apply {
                    putExtra(
                        "singleMalt", ParcelableSingleMalt(
                            name = singleMalt.name,
                            price = singleMalt.price ?: 0,
                            year = singleMalt.year,
                            location = singleMalt.location,
                            tastingNote = singleMalt.tastingNote,
                            imageUri = singleMalt.imageUri

                        )
                    )
                }
                context.startActivity(intent)
            },
        colors = cardColors,
        shape = squareShape,
    ) {
        var isEditing by remember { mutableStateOf(false) }
        var editedName by remember { mutableStateOf(singleMalt.name) }
        var editedPrice by remember { mutableStateOf(singleMalt.price?.toString() ?: "") }
        var editedYear by remember { mutableStateOf(singleMalt.year.toString()) }
        var editedLocation by remember { mutableStateOf(singleMalt.location) }
        var editedTastingNote by remember { mutableStateOf(singleMalt.tastingNote) }
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
                    db.singleMaltDAO().updateSingleMalt(singleMalt.copy(imageUri = uri.toString()))
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
                            label = { Text("whiskey Name") },
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
                        Text(text = "whiskey Name: ${if (editedName.isEmpty()) singleMalt.name else editedName}")
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
                            db.singleMaltDAO().delete(singleMalt)
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
fun Single() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(75.dp)
            .background(Color(android.graphics.Color.parseColor("#FCF4E7"))),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = "Single Malt Whiskey",
            fontStyle = FontStyle.Italic,
            color = androidx.compose.ui.graphics.Color.Black,
            textAlign = TextAlign.Center,
            fontWeight = FontWeight.Bold,
            modifier = Modifier.padding(8.dp)
        )
    }
}