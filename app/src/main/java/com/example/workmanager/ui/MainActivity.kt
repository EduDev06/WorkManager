package com.example.workmanager.ui

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.Button
import androidx.compose.material.Surface
import androidx.compose.material.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.work.WorkInfo
import com.example.workmanager.R
import com.example.workmanager.ui.theme.WorkManagerTheme
import com.example.workmanager.util.TAG_OUTPUT
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            val viewModel = hiltViewModel<MyViewModel>()
            WorkManagerTheme {
                val workInfo = viewModel.workInfo.observeAsState().value
                val downloadInfo = remember(workInfo) {
                    workInfo?.find { it.tags.contains(TAG_OUTPUT) }
                }
                /*
                  val imageUri by derivedStateOf {
                      downloadInfo?.outputData?.getString(KEY_IMAGE_URI)
                  }
                */
                Body(onEvent = { viewModel.onEvent(it) }, workInfo = downloadInfo)
            }
        }
    }
}

@Composable
fun Body(
    modifier: Modifier = Modifier,
    onEvent: (MyEvent) -> Unit = { },
    workInfo: WorkInfo?
) {
    Surface {
        Column(
            modifier = modifier
                .fillMaxSize()
                .padding(horizontal = 15.dp),
            verticalArrangement = Arrangement.Center,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Image(
                painter = painterResource(id = R.drawable.lake),
                contentDescription = null,
                modifier = Modifier.fillMaxWidth(),
                contentScale = ContentScale.Crop
            )
            Spacer(modifier = Modifier.height(5.dp))
            Button(onClick = { onEvent(MyEvent.DownloadImage) }) {
                Text(text = "Download")
            }
            Spacer(modifier = Modifier.height(5.dp))
            Button(onClick = { onEvent(MyEvent.CancelWork) }) {
                Text(text = "Cancel")
            }
            when (workInfo?.state) {
                WorkInfo.State.CANCELLED -> Text(text = "Download cancelled")
                WorkInfo.State.ENQUEUED -> Text(text = "Download enqueued")
                WorkInfo.State.RUNNING -> Text(text = "Downloading")
                WorkInfo.State.SUCCEEDED -> Text(text = "Download succeeded")
                WorkInfo.State.FAILED -> Text(text = "Download failed")
                WorkInfo.State.BLOCKED -> Text(text = "Download blocked")
                else -> Text(text = "Something wrong happened")
            }
        }
    }
}