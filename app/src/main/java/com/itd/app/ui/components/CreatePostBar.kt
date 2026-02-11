package com.itd.app.ui.components

import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AttachFile
import androidx.compose.material.icons.filled.Brush
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.itd.app.ui.theme.*

@Composable
fun CreatePostBar(
    userAvatar: String = "👾",
    onPostCreate: (String) -> Unit = {},
    onAttachmentClick: ((Uri) -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    var text by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }

    val imagePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let { onAttachmentClick?.invoke(it) }
    }

    Card(
        modifier = modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(containerColor = ITDSurface),
        shape = RoundedCornerShape(0.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                verticalAlignment = Alignment.Top,
                modifier = Modifier.fillMaxWidth()
            ) {
                Text(
                    text = userAvatar,
                    fontSize = 28.sp
                )

                Spacer(modifier = Modifier.width(12.dp))

                OutlinedTextField(
                    value = text,
                    onValueChange = {
                        text = it
                        isExpanded = it.isNotEmpty()
                    },
                    placeholder = {
                        Text(
                            "Что нового?",
                            color = ITDOnSurfaceVariant
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .heightIn(min = 48.dp),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedTextColor = ITDOnSurface,
                        unfocusedTextColor = ITDOnSurface,
                        focusedBorderColor = ITDPrimary,
                        unfocusedBorderColor = ITDDivider,
                        cursorColor = ITDPrimary,
                        focusedContainerColor = ITDInputBackground,
                        unfocusedContainerColor = ITDInputBackground
                    ),
                    shape = RoundedCornerShape(12.dp),
                    maxLines = 5
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = { imagePickerLauncher.launch("image/*") },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.AttachFile,
                            contentDescription = "Attach",
                            tint = ITDOnSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    IconButton(
                        onClick = { /* Draw feature */ },
                        modifier = Modifier.size(36.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Brush,
                            contentDescription = "Draw",
                            tint = ITDOnSurfaceVariant,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                Button(
                    onClick = {
                        if (text.isNotBlank()) {
                            onPostCreate(text)
                            text = ""
                            isExpanded = false
                        }
                    },
                    enabled = text.isNotBlank(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = ITDPrimary,
                        contentColor = ITDOnSurface,
                        disabledContainerColor = ITDPrimary.copy(alpha = 0.5f)
                    ),
                    shape = RoundedCornerShape(20.dp)
                ) {
                    Text("Опубликовать")
                }
            }
        }

        HorizontalDivider(color = ITDDivider, thickness = 0.5.dp)
    }
}
