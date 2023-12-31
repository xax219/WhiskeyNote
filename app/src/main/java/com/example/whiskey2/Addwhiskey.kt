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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.whiskey2.data.AppDatabase
import com.example.whiskey2.data.Blended
import com.example.whiskey2.data.Bourbon
import com.example.whiskey2.data.SingleMalt
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class Addwhiskey : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MainScreen()
        }
    }

    @Composable
    fun MainScreen() {
        Surface(
            modifier = Modifier.fillMaxSize(),
        ) {
            Save()
        }
    }

    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    fun Save(modifier: Modifier = Modifier) {
        val scrollState = rememberScrollState()
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(15.dp)
                .verticalScroll(scrollState),
            verticalArrangement = Arrangement.Top,
            horizontalAlignment = Alignment.Start
        ) {
            var selectedUri by remember {
                mutableStateOf<Uri?>(null)
            }
            val context = LocalContext.current

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                selectedUri = uri
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                if (uri != null) {
                    context.contentResolver.takePersistableUriPermission(uri, takeFlags)
                }
            }
            var whiskeyName by remember { mutableStateOf(TextFieldValue()) }
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
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                if (selectedUri == null) {
                    Image(
                        painter = painterResource(id = R.drawable.camera),
                        contentDescription = "cameraImage",
                        modifier = Modifier
                            .size(100.dp)
                            .clickable {
                                launcher.launch("image/*")
                            }
                            .padding(16.dp),
                    )
                }
                selectedUri?.let { uri ->
                    val bitmap: Bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
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
                            },
                        contentScale = ContentScale.Crop
                    )
                    imageUriString = uri.toString()
                }

            }


            val db = remember {
                AppDatabase.getDatabase(context)
            }
            val scope = rememberCoroutineScope()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
//                    .padding(16.dp)
            ) {
                //TODO read
                var selectedItem by remember { mutableStateOf("") }
                DropDownMenu(selectedItem, onChangeItem = { selectedItem = it })

                TextField(
                    value = whiskeyName,
                    onValueChange = { whiskeyName = it },
                    label = { Text("Whiskey Name") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)

                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = price,
                    onValueChange = { price = it },
                    label = { Text("Price") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)

                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = year,
                    onValueChange = { year = it },
                    label = { Text("Year") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)

                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = location,
                    onValueChange = { location = it },
                    label = { Text("Location") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)

                )
                Spacer(modifier = Modifier.height(8.dp))
                TextField(
                    value = tastingNote,
                    onValueChange = { tastingNote = it },
                    label = { Text("Tasting Note") },
                    modifier = Modifier.fillMaxWidth(),
                    colors = TextFieldDefaults.textFieldColors(containerColor = Color.Transparent)

                )
                Spacer(modifier = Modifier.height(16.dp))
                Button(
                    onClick = {
                        enteredName = whiskeyName.text
                        enteredPrice = price.text
                        enteredYear = year.text
                        enteredLocation = location.text
                        enteredTastingNote = tastingNote.text
// newblended 만들고 조건문으로 사용해서  조건 충족할때 저장

                        var newSingleMalt = SingleMalt(
                            name = enteredName,
                            price = enteredPrice.toInt(),
                            year = enteredYear.toInt(),
                            location = enteredLocation,
                            tastingNote = enteredTastingNote,
                            imageUri = imageUriString
                        )
                        var newBlended = Blended(
                            name = enteredName,
                            price = enteredPrice.toInt(),
                            year = enteredYear.toInt(),
                            location = enteredLocation,
                            tastingNote = enteredTastingNote,
                            imageUri = imageUriString
                        )
                        var newBourbon = Bourbon(
                            name = enteredName,
                            price = enteredPrice.toInt(),
                            year = enteredYear.toInt(),
                            location = enteredLocation,
                            tastingNote = enteredTastingNote,
                            imageUri = imageUriString
                        )
                        scope.launch(Dispatchers.IO) {
                            if (selectedItem == "Single Malt") {
                                db.singleMaltDAO().insertAll(newSingleMalt)
                            } else if (selectedItem == "Blended") {
                                db.blendedDAO().insertAll(newBlended)
                            } else if (selectedItem == "Bourbon") {
                                db.bourbonDAO().insertAll(newBourbon)
                            }
                        }

                        val intent = Intent(context, Homescreen::class.java)
                        startActivity(intent)
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RectangleShape),
                    colors = ButtonDefaults.buttonColors(Color(android.graphics.Color.parseColor("#FCF4E7")))

                ) {
                    Text(
                        text = "Save",
                        color = Color.Black,
                        fontSize = 24.sp,
                    )
                }

            }

        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu(selectedItem: String, onChangeItem: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    val list = listOf("Single Malt", "Blended", "Bourbon")

    Column(modifier = Modifier.padding(20.dp)) {
        ExposedDropdownMenuBox(
            expanded = expanded,
            onExpandedChange = { expanded = !expanded }
        ) {
            OutlinedTextField(
                readOnly = true,
                value = selectedItem,
                onValueChange = { },
                modifier = Modifier
                    .fillMaxWidth()
                    .menuAnchor(),
                label = { Text(text = "Select Whiskey") },
                trailingIcon = {
                    ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded)
                }
            )
            ExposedDropdownMenu(
                expanded = expanded,
                onDismissRequest = { expanded = false },
            ) {
                list.forEach { label ->
                    DropdownMenuItem(
                        text = {
                            Text(text = label)
                        },
                        onClick = {
                            //TODO read
                            onChangeItem(label)
                            expanded = false
                        },
                    )
                }
            }

        }
    }
}