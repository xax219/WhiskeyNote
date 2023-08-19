package com.example.whiskey2

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.whiskey2.ui.theme.Whiskey2Theme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            Whiskey2Theme {
                Column(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.Top,
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    TopBar()
                    Spacer(modifier = Modifier.height(300.dp))
                    ButtonRow()
                }
            }
        }
    }
}
@Composable
fun TopBar() {
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(100.dp)
            .background(Color(android.graphics.Color.parseColor("#1EA4FF"))),
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = "Whiskey",
                color = Color.White,
                textAlign = TextAlign.Center,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(start = 170.dp)
            )
            WriteButton(LocalContext.current)
        }
    }
}

@Composable
fun ButtonRow() {
    val context = LocalContext.current
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        NavigationButton("SingleMalt", MainActivity2::class.java, context)
        NavigationButton("Blended", MainActivity3::class.java, context)
        NavigationButton("Bourbon", MainActivity4::class.java, context)
    }
}

@Composable
fun NavigationButton(label: String, destination: Class<*>, context: Context) {
    Button(onClick = {
        val intent = Intent(context, destination)
        context.startActivity(intent)
    }) {
        Text(label)
    }
}
@Composable
fun WriteButton(context: Context) {
    Button(
        onClick = {
            val intent = Intent(context, MainActivity5::class.java)
            context.startActivity(intent)
        },
        colors = ButtonDefaults.buttonColors(Color(android.graphics.Color.parseColor("#1EA4FF")))
    ) {
        Image(
            painter = painterResource(id = R.drawable.write),
            contentDescription = null,
            modifier = Modifier.size(30.dp),
        )
    }
}
