package com.example.whiskey2

import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whiskey2.data.AppDatabase
import com.example.whiskey2.data.User
import com.example.whiskey2.ui.theme.Whiskey2Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import androidx.room.Room


class MainActivity5 : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Whiskey2Theme {
                MainScreen()
            }
        }
    }

    @Composable
    fun MainScreen() {
        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            Save()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Save(modifier: Modifier = Modifier) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            var selectedUri by remember {
                mutableStateOf<Uri?>(null)
            }

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                selectedUri = uri
            }
            var whiskyName by remember { mutableStateOf(TextFieldValue()) }
            var enteredName by remember { mutableStateOf("") }
            var price by remember { mutableStateOf(TextFieldValue()) }
            var enteredPrice by remember { mutableStateOf("") }
            var year by remember { mutableStateOf(TextFieldValue()) }
            var enteredYear by remember { mutableStateOf("") }
            var location by remember { mutableStateOf(TextFieldValue()) }
            var enteredLocation by remember { mutableStateOf("") }
            var tastingNote by remember { mutableStateOf(TextFieldValue()) }
            var enteredTastingNote by remember { mutableStateOf("") }
            var imageUriString by remember { mutableStateOf<String?>(null) }

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                selectedUri?.let { uri ->
                    val context = LocalContext.current
                    val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                        ImageDecoder.decodeBitmap(
                            ImageDecoder.createSource(
                                context.contentResolver,
                                uri
                            )
                        )
                    } else {
                        MediaStore.Images.Media.getBitmap(context.contentResolver, uri)
                    }

                    Image(
                        bitmap = bitmap.asImageBitmap(),
                        contentDescription = "",
                        modifier = Modifier
                            .size(width = 200.dp, height = 200.dp)
                            .clickable {
                                launcher.launch("image/*")
                            }
                    )
                    imageUriString = uri.toString()
                }


                if (selectedUri == null) {
                    Button(
                        onClick = {
                            launcher.launch("image/*")
                        },
                        modifier = Modifier
                            .size(150.dp)
                            .clip(RectangleShape)
                    ) {
                        Text(
                            text = "+",
                            fontSize = 100.sp,
                        )
                    }
                }
            }


            Spacer(modifier = Modifier.height(8.dp))
            val context = LocalContext.current
            val db = remember {
                AppDatabase.getDatabase(context)
            }
            val scope = rememberCoroutineScope()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                TextField(
                    value = whiskyName,
                    onValueChange = { whiskyName = it },
                    label = { Text("Whisky Name") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text("Year") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = tastingNote,
                    onValueChange = { tastingNote = it },
                    label = { Text("Tasting Note") },
                    modifier = Modifier.fillMaxWidth()
                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        enteredName = whiskyName.text
                        enteredPrice = price.text
                        enteredYear = year.text
                        enteredLocation = location.text
                        enteredTastingNote = tastingNote.text

                        var newUser = User(
                            name = enteredName,
                            price = enteredPrice.toInt(),
                            year = enteredYear.toInt(),
                            location = enteredLocation,
                            tastingNote = enteredTastingNote,
                            imageUri = imageUriString
                        )
                        scope.launch(Dispatchers.IO) {
                            db.userDao().insertAll(newUser)
                        }
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RectangleShape)
                ) {
                    Text(
                        text = "저장",
                        fontSize = 24.sp,
                    )
                }
            }

        }
    }
}