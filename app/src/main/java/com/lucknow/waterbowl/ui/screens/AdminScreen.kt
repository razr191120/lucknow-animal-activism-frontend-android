package com.lucknow.waterbowl.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.lucknow.waterbowl.data.models.User
import com.lucknow.waterbowl.viewmodel.AdminViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AdminScreen(viewModel: AdminViewModel = viewModel()) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Users", "Drives", "Distributions")

    LaunchedEffect(Unit) {
        viewModel.loadUsers()
        viewModel.loadDrives()
        viewModel.loadDistributions()
    }

    val error by viewModel.error.collectAsState()
    val message by viewModel.message.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Admin Panel", fontWeight = FontWeight.Bold) },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.primary,
                    titleContentColor = MaterialTheme.colorScheme.onPrimary
                )
            )
        }
    ) { padding ->
        Column(modifier = Modifier.padding(padding)) {
            TabRow(selectedTabIndex = selectedTab) {
                tabs.forEachIndexed { index, title ->
                    Tab(selected = selectedTab == index, onClick = { selectedTab = index }) {
                        Text(title, modifier = Modifier.padding(12.dp), style = MaterialTheme.typography.labelLarge)
                    }
                }
            }

            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(16.dp))
            }
            message?.let {
                Text(it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(16.dp))
            }

            when (selectedTab) {
                0 -> UsersTab(viewModel)
                1 -> DrivesTab(viewModel)
                2 -> DistributionsTab(viewModel)
            }
        }
    }
}

@Composable
private fun UsersTab(viewModel: AdminViewModel) {
    val users by viewModel.users.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }

    Column {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("${users.size} Users", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
            FilledTonalButton(onClick = { showCreateDialog = true }) {
                Icon(Icons.Filled.PersonAdd, null, modifier = Modifier.size(18.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Add")
            }
        }

        if (isLoading) {
            CircularProgressIndicator(modifier = Modifier.align(Alignment.CenterHorizontally).padding(24.dp))
        }

        LazyColumn(contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            items(users, key = { it.id }) { user ->
                UserCard(user, viewModel)
            }
        }
    }

    if (showCreateDialog) {
        CreateUserDialog(onDismiss = { showCreateDialog = false }) { name, email, pw ->
            viewModel.createUser(name, email, pw)
            showCreateDialog = false
        }
    }
}

@Composable
private fun UserCard(user: User, viewModel: AdminViewModel) {
    Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(user.fullName, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                    Text(user.email, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
                AssistChip(
                    onClick = { viewModel.toggleRole(user) },
                    label = { Text(user.role) },
                    colors = AssistChipDefaults.assistChipColors(
                        containerColor = if (user.isAdmin) MaterialTheme.colorScheme.primaryContainer else MaterialTheme.colorScheme.surfaceVariant
                    )
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                TextButton(onClick = { viewModel.toggleActive(user) }) {
                    Text(if (user.isActive) "Deactivate" else "Activate")
                }
                TextButton(onClick = { viewModel.deleteUser(user.id) }, colors = ButtonDefaults.textButtonColors(contentColor = MaterialTheme.colorScheme.error)) {
                    Text("Delete")
                }
            }
        }
    }
}

@Composable
private fun CreateUserDialog(onDismiss: () -> Unit, onCreate: (String, String, String) -> Unit) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create User") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(value = name, onValueChange = { name = it }, label = { Text("Full Name") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = email, onValueChange = { email = it }, label = { Text("Email") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = password, onValueChange = { password = it }, label = { Text("Password") }, singleLine = true, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = {
            Button(onClick = { onCreate(name, email, password) }, enabled = name.isNotBlank() && email.isNotBlank() && password.length >= 6) {
                Text("Create")
            }
        },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@Composable
private fun DrivesTab(viewModel: AdminViewModel) {
    val drives by viewModel.drives.collectAsState()

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(drives, key = { it.id ?: 0 }) { drive ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(drive.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text("${drive.addresses.size} locations", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { viewModel.deleteDrive(drive.id?.toString() ?: "") }) {
                        Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}

@Composable
private fun DistributionsTab(viewModel: AdminViewModel) {
    val distributions by viewModel.distributions.collectAsState()

    LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        items(distributions, key = { it.id ?: 0 }) { dist ->
            Card(modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp)) {
                Row(modifier = Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                    Column(modifier = Modifier.weight(1f)) {
                        Text(dist.name, style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold)
                        Text(dist.address ?: "No address", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                    IconButton(onClick = { viewModel.deleteDistribution(dist.id?.toString() ?: "") }) {
                        Icon(Icons.Filled.Delete, null, tint = MaterialTheme.colorScheme.error)
                    }
                }
            }
        }
    }
}
