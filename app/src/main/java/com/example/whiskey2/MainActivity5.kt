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
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.input.TextFieldValue
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.toSize
import com.example.whiskey2.data.AppDatabase
import com.example.whiskey2.data.SingleMalt
import com.example.whiskey2.ui.theme.Whiskey2Theme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

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
            val context = LocalContext.current

            val launcher = rememberLauncherForActivityResult(
                contract = ActivityResultContracts.GetContent()
            ) { uri ->
                selectedUri = uri
                val takeFlags = Intent.FLAG_GRANT_READ_URI_PERMISSION
                context.contentResolver.takePersistableUriPermission(uri!!, takeFlags)
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
                    .padding(8.dp)
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
            ) {
                if (selectedUri == null) {
                    Button(

                        onClick = {
                            launcher.launch("image/*")
                        },
                        modifier = Modifier
                            .size(100.dp)
                            .clip(RectangleShape),

                        ) {
                        Text(
                            text = "+",
                            fontSize = 50.sp,
                        )
                    }

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

            Spacer(modifier = Modifier.height(8.dp))
            val db = remember {
                AppDatabase.getDatabase(context)
            }
            val scope = rememberCoroutineScope()

            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                DropDownMenu()

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

                        var newSingleMalt = SingleMalt(
                            name = enteredName,
                            price = enteredPrice.toInt(),
                            year = enteredYear.toInt(),
                            location = enteredLocation,
                            tastingNote = enteredTastingNote,
                            imageUri = imageUriString
                        )
                        scope.launch(Dispatchers.IO) {
                            db.singleMaltDAO().insertAll(newSingleMalt)
                        }

                        val intent = Intent(context, MainActivity::class.java)
                        startActivity(intent)
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

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun DropDownMenu() {
    var expanded by remember { mutableStateOf(false) }
    var selectedItem by remember { mutableStateOf("") }
    val list = listOf("Single Malt", "Blended", "Bourbon")
    var textFiledSize by remember {
        mutableStateOf(Size.Zero)
    }
    val icon = if (expanded) {
        Icons.Filled.KeyboardArrowUp
    } else {
        Icons.Filled.KeyboardArrowUp
    }
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
                label = { Text(text = "Select Whisky") },
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
                            selectedItem = label
                            expanded = false
                        },
                    )
                }
            }

        }
    }
}

//ROW로 버튼 감싸서 취소키 (뒤로가기) , 저장 버튼 만들기 배경은 투명으로만들고
// + 대체할 이미지나 배경 투명으로 만들기