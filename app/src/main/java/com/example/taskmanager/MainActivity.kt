package com.example.taskmanager

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.Icon
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            MaterialTheme {
                TaskManagerScreen()
            }
        }
    }
}

data class Task(
    val id: Int,
    val name: String,
    val category: String,
    val priority: Int,
    val isCompleted: Boolean
)

enum class TaskFilter {
    ALL, ACTIVE, COMPLETED
}

@Composable
fun TaskManagerScreen() {
    val context = LocalContext.current

    var nextId: Int by remember {
        mutableIntStateOf(1)
    }
    var name: String by remember {
        mutableStateOf("")
    }
    var nameError: Boolean by remember {
        mutableStateOf(false)
    }
    var category: String by remember {
        mutableStateOf("")
    }
    var categoryError: Boolean by remember {
        mutableStateOf(false)
    }
    var priority: Int by remember {
        mutableIntStateOf(1)
    }
    var priorityError: Boolean by remember {
        mutableStateOf(false)
    }
    val tasks = remember {
        mutableStateListOf<Task>()
    }
    var selectedFilter by remember {
        mutableStateOf(TaskFilter.ALL)
    }

    val filteredTasks = when (selectedFilter) {
        TaskFilter.ALL -> tasks
        TaskFilter.ACTIVE -> tasks.filter { !it.isCompleted }
        TaskFilter.COMPLETED -> tasks.filter { it.isCompleted }
    }

    Column(
        modifier = Modifier.fillMaxSize().padding(32.dp), verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        Text(
            text = "Feladatkezelő",
            style = MaterialTheme.typography.headlineLarge,
            fontWeight = FontWeight.Bold
        )
        Text("Szervezd meg a napi teendőidet")
        OutlinedTextField(
            value = name,
            onValueChange = {
                name = it
                nameError = false
            },
            label = {Text("Feladat címe")},
            supportingText = {
                if (nameError) {
                    Text("A feladat neve kötelező", color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = category,
            onValueChange = {
                category = it
                categoryError = false
            },
            label = {Text("Kategória")},
            supportingText = {
                if (categoryError) {
                    Text("A kategória megadása kötelező", color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        OutlinedTextField(
            value = priority.toString(),
            onValueChange = {
                priority = it.toIntOrNull() ?: 0
                priorityError = priority !in 1..3
            },
            label = {Text("Prioritás (1-3)")},
            supportingText = {
                if (priorityError) {
                    Text("A prioritás 1 és 3 között lehet!", color = MaterialTheme.colorScheme.error)
                }
            },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true
        )
        Button(
            onClick = {
                nameError = name.isBlank()
                categoryError = category.isBlank()
                priorityError = priority !in 1..3

                if (nameError || categoryError || priorityError) {
                    Toast.makeText(context, "Ellenőrizd a megadott adatokat!", Toast.LENGTH_SHORT).show()
                }
                else {
                    val newTask = Task(
                        id = nextId,
                        name = name.trim(),
                        category = category.trim(),
                        priority = priority,
                        isCompleted = false
                    )
                    tasks.add(newTask)
                    nextId++
                    Toast.makeText(context, "Feladat sikeresen hozzáadva!", Toast.LENGTH_SHORT).show()
                }
                name = ""
                category = ""
                priority = 1
            }, modifier = Modifier.fillMaxWidth()
        ) {
            Text("Hozzáadás")
        }
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text("Összes", fontWeight = FontWeight.Bold)
                Text(tasks.size.toString(), fontWeight = FontWeight.Bold)
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text("Aktív", fontWeight = FontWeight.Bold)
                Text(tasks.count { !it.isCompleted }.toString(), fontWeight = FontWeight.Bold)
            }
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text("Elkészült", fontWeight = FontWeight.Bold)
                Text(tasks.count { it.isCompleted }.toString(), fontWeight = FontWeight.Bold)
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            val buttonShape = RoundedCornerShape(8.dp)
            if (selectedFilter == TaskFilter.ALL) {
                Button(
                    onClick = { selectedFilter = TaskFilter.ALL },
                    modifier = Modifier.weight(1f),
                    shape = buttonShape
                ) {
                    Text("Összes")
                }
            } else {
                OutlinedButton(
                    onClick = { selectedFilter = TaskFilter.ALL },
                    modifier = Modifier.weight(1f),
                    shape = buttonShape
                ) {
                    Text("Összes")
                }
            }

            if (selectedFilter == TaskFilter.ACTIVE) {
                Button(
                    onClick = { selectedFilter = TaskFilter.ACTIVE },
                    modifier = Modifier.weight(1f),
                    shape = buttonShape
                ) {
                    Text("Aktív")
                }
            } else {
                OutlinedButton(
                    onClick = { selectedFilter = TaskFilter.ACTIVE },
                    modifier = Modifier.weight(1f),
                    shape = buttonShape
                ) {
                    Text("Aktív")
                }
            }

            if (selectedFilter == TaskFilter.COMPLETED) {
                Button(
                    onClick = { selectedFilter = TaskFilter.COMPLETED },
                    modifier = Modifier.weight(1f),
                    shape = buttonShape
                ) {
                    Text("Elkészült")
                }
            } else {
                OutlinedButton(
                    onClick = { selectedFilter = TaskFilter.COMPLETED },
                    modifier = Modifier.weight(1f),
                    shape = buttonShape
                ) {
                    Text("Elkészült")
                }
            }
        }

        if (tasks.isEmpty()) {
            Text("Még nincs feladatod.")
        } else if (filteredTasks.isEmpty()) {
            when (selectedFilter) {
                TaskFilter.ACTIVE -> Text("Nincs aktív feladat.")
                TaskFilter.COMPLETED -> Text("Még nincs elkészült feladat.")
                TaskFilter.ALL -> Text("Még nincs feladatod.")
            }
        } else {
            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f),
                verticalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(
                    items = filteredTasks,
                    key = { task -> task.id }
                ) { task ->
                    TaskCard(
                        task = task,
                        onDelete = {
                            tasks.remove(task)
                        },
                        onEdit = { editedTask ->
                            val index = tasks.indexOfFirst { it.id == task.id }
                            if (index != -1) {
                                tasks[index] = editedTask
                            }
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun TaskCard(task: Task, onDelete: () -> Unit, onEdit: (Task) -> Unit) {
    val context = LocalContext.current
    var showDeleteDialog: Boolean by remember {
        mutableStateOf(false)
    }
    var showEditDialog: Boolean by remember {
        mutableStateOf(false)
    }

    var editName: String by remember {
        mutableStateOf(task.name)
    }
    var editCategory: String by remember {
        mutableStateOf(task.category)
    }
    var editPriority: Int by remember {
        mutableIntStateOf(task.priority)
    }
    var editNameError: Boolean by remember {
        mutableStateOf(false)
    }
    var editCategoryError: Boolean by remember {
        mutableStateOf(false)
    }
    var editPriorityError: Boolean by remember {
        mutableStateOf(false)
    }


    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically
        ) {
            TaskAvatar(task.priority)
            Spacer(modifier = Modifier.width(16.dp))
            Column(
                modifier = Modifier.weight(1f)
            ) {
                Text(text = task.name, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                Text(text = task.category, style = MaterialTheme.typography.bodyMedium)
                Text(text = if (task.priority == 1) "Alacsony prioritás" else if (task.priority == 2) "Közepes prioritás" else "Magas prioritás", style = MaterialTheme.typography.bodyMedium)
                Button(
                    onClick = {
                        onEdit(
                            task.copy(
                                name = task.name,
                                category = task.category,
                                priority = task.priority,
                                isCompleted = !task.isCompleted
                            )
                        )
                    }) {
                        Text(if (task.isCompleted) "Újra aktív" else "Elkészült")
                }
            }
            IconButton(
                onClick = {showDeleteDialog = true}
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_delete), contentDescription = "Feladat törlése")
            }
            IconButton(
                onClick = {
                    showEditDialog = true
                    editName = task.name
                    editCategory = task.category
                    editPriority = task.priority
                    editNameError = false
                    editCategoryError = false
                    editPriorityError = false
                }
            ) {
                Icon(painter = painterResource(id = R.drawable.ic_edit), contentDescription = "Feladat szerkesztése")
            }
        }
    }
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = {
                showDeleteDialog = false
            },
            title = {Text("Feladat törlése")},
            text = {Text("Biztosan törölni szeretnéd a(z) ${task.name} feladatot?")},
            confirmButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                    onDelete()
                }) {
                    Text("Törlés")
                }
            },
            dismissButton = {
                TextButton(onClick = {
                    showDeleteDialog = false
                }) {
                    Text("Mégsem")
                }
            }
        )
    }
    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = {
                showEditDialog = false
            },
            title = {Text("Feladat szerkesztése")},
            text = {
                Column(
                    modifier = Modifier.fillMaxWidth()
                ) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = {
                            editName = it
                            editNameError = false
                        },
                        label = {Text("Játék címe")},
                        supportingText = {
                            if (editNameError) {
                                Text("A feladat címe kötelező", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editCategory,
                        onValueChange = {
                            editCategory = it
                            editCategoryError = false
                        },
                        label = {Text("Platform")},
                        supportingText = {
                            if (editCategoryError) {
                                Text("A kategória megadása kötelező", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                    OutlinedTextField(
                        value = editPriority.toString(),
                        onValueChange = {
                            editPriority = it.toIntOrNull() ?: 0
                            editPriorityError = editPriority !in 1..3
                        },
                        label = {Text("Prioritás (1-3)")},
                        supportingText = {
                            if (editPriorityError) {
                                Text("Az prioritás 1 és 3 között lehet!", color = MaterialTheme.colorScheme.error)
                            }
                        },
                        modifier = Modifier.fillMaxWidth(),
                        singleLine = true
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    editNameError = editName.isBlank()
                    editCategoryError = editCategory.isBlank()
                    editPriorityError = editPriority !in 1..3
                    if (editNameError || editCategoryError || editPriorityError) {
                        Toast.makeText(context, "Ellenőrizd a megadott adatokat!", Toast.LENGTH_SHORT).show()
                    }
                    else {
                        showEditDialog = false
                        onEdit(
                            task.copy(
                                name = editName.trim(),
                                category = editCategory.trim(),
                                priority = editPriority,
                                isCompleted = task.isCompleted
                            )
                        )
                        Toast.makeText(
                            context,
                            "Feladat adatai sikeresen frissítve",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                }) { Text("Mentés") }
            },
            dismissButton = {
                TextButton(onClick = {
                    showEditDialog = false
                }) {Text("Mégsem")}
            }
        )
    }
}

@Composable
fun TaskAvatar(priority: Int) {
    Box(modifier = if (priority == 1) {
        Modifier.size(52.dp).clip(CircleShape).background(Color.Green)
    } else if (priority == 2) {
        Modifier.size(52.dp).clip(CircleShape).background(Color.Yellow)
    } else {
        Modifier.size(52.dp).clip(CircleShape).background(Color.Red)
    }, contentAlignment = Alignment.Center) {
        Text(priority.toString(), color = Color.White, fontWeight = FontWeight.Bold)
    }
}