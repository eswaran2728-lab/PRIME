package com.example.ui.screens.learning

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoStories
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.MenuBook
import androidx.compose.material.icons.filled.PlayCircleFilled
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.Timer
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.TabRowDefaults
import androidx.compose.material3.TabRowDefaults.tabIndicatorOffset
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entities.BookEntity
import com.example.data.local.entities.CourseEntity
import com.example.data.local.entities.StudySessionEntity
import com.example.ui.theme.PrimeBlue
import com.example.ui.theme.PrimeBorder
import com.example.ui.theme.PrimeCyan
import com.example.ui.theme.PrimeGreen
import com.example.ui.theme.PrimeOnPrimaryContainer
import com.example.ui.theme.PrimeOnPrimaryDark
import com.example.ui.theme.PrimeOrange
import com.example.ui.theme.PrimePrimary
import com.example.ui.theme.PrimePrimaryContainer
import com.example.ui.theme.PrimePurple
import com.example.ui.theme.PrimeSurface
import com.example.ui.theme.PrimeSurfaceVariant
import com.example.ui.theme.PrimeTextPrimary
import com.example.ui.theme.PrimeTextSecondary

@Composable
fun LearningScreen(
    courses: List<CourseEntity>,
    books: List<BookEntity>,
    studySessions: List<StudySessionEntity>,
    onAddCourse: (title: String, platform: String, instructor: String, totalModules: Int) -> Unit,
    onUpdateCourseProgress: (course: CourseEntity, completedModules: Int) -> Unit,
    onAddBook: (title: String, author: String, format: String, totalPages: Int) -> Unit,
    onUpdateBookProgress: (book: BookEntity, currentPage: Int) -> Unit,
    onLogStudySession: (topic: String, category: String, durationMinutes: Int, keyInsights: String) -> Unit,
    modifier: Modifier = Modifier
) {
    var selectedTab by remember { mutableIntStateOf(0) }
    val tabs = listOf("Courses", "Reading List", "Study Logs")

    var showAddCourseDialog by remember { mutableStateOf(false) }
    var showAddBookDialog by remember { mutableStateOf(false) }
    var showLogStudyDialog by remember { mutableStateOf(false) }

    val totalStudyMinutes = studySessions.sumOf { it.durationMinutes }
    val totalHours = totalStudyMinutes / 60
    val totalBooksRead = books.count { it.status == "Finished" || it.currentPage >= it.totalPages }

    LazyColumn(
        modifier = modifier
            .fillMaxSize()
            .padding(horizontal = 20.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card
        item {
            Card(
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
                border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.35f)),
                modifier = Modifier.fillMaxWidth().testTag("learning_hero_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Column(modifier = Modifier.weight(1f)) {
                            Text(
                                text = "INTELLECTUAL ARSENAL",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    letterSpacing = 1.5.sp
                                ),
                                color = PrimePrimary
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = "Knowledge & Skill Engine",
                                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                                color = PrimeTextPrimary
                            )
                        }
                        Box(
                            modifier = Modifier
                                .size(48.dp)
                                .clip(CircleShape)
                                .background(PrimePrimaryContainer),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.School,
                                contentDescription = null,
                                tint = PrimeOnPrimaryContainer,
                                modifier = Modifier.size(24.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = PrimeSurface,
                            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.25f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Deep Hours", style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
                                Text("${totalHours}h ${totalStudyMinutes % 60}m", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PrimePrimary)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = PrimeSurface,
                            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.25f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Active Courses", style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
                                Text("${courses.size}", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PrimeBlue)
                            }
                        }

                        Surface(
                            shape = RoundedCornerShape(14.dp),
                            color = PrimeSurface,
                            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.25f)),
                            modifier = Modifier.weight(1f)
                        ) {
                            Column(modifier = Modifier.padding(12.dp)) {
                                Text("Books Read", style = MaterialTheme.typography.labelSmall, color = PrimeTextSecondary)
                                Text("$totalBooksRead", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PrimeGreen)
                            }
                        }
                    }
                }
            }
        }

        // Tabs
        item {
            TabRow(
                selectedTabIndex = selectedTab,
                containerColor = PrimeSurfaceVariant,
                contentColor = PrimePrimary,
                indicator = { tabPositions ->
                    TabRowDefaults.Indicator(
                        modifier = Modifier.tabIndicatorOffset(tabPositions[selectedTab]),
                        color = PrimePrimary,
                        height = 3.dp
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(14.dp))
                    .testTag("learning_tabs")
            ) {
                tabs.forEachIndexed { index, title ->
                    Tab(
                        selected = selectedTab == index,
                        onClick = { selectedTab = index },
                        text = {
                            Text(
                                text = title,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                ),
                                color = if (selectedTab == index) PrimePrimary else PrimeTextSecondary
                            )
                        }
                    )
                }
            }
        }

        when (selectedTab) {
            0 -> {
                // Courses
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Curriculum & Courses",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimeTextPrimary
                        )
                        Button(
                            onClick = { showAddCourseDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("add_course_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = PrimeOnPrimaryDark, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Course", color = PrimeOnPrimaryDark, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                if (courses.isEmpty()) {
                    item {
                        EmptyLearningState(
                            title = "No Courses Enrolled",
                            subtitle = "Add specialized engineering, AI, or business curriculum to track modular progress.",
                            onAdd = { showAddCourseDialog = true }
                        )
                    }
                } else {
                    items(courses) { course ->
                        CourseCard(
                            course = course,
                            onUpdateProgress = { newProgress -> onUpdateCourseProgress(course, newProgress) }
                        )
                    }
                }
            }

            1 -> {
                // Books
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Reading Matrix & Books",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimeTextPrimary
                        )
                        Button(
                            onClick = { showAddBookDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimeGreen),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("add_book_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.White, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Add Book", color = Color.White, style = MaterialTheme.typography.labelMedium)
                        }
                    }
                }

                if (books.isEmpty()) {
                    item {
                        EmptyLearningState(
                            title = "Reading List Is Empty",
                            subtitle = "Track seminal texts, biographies, and strategy books with page completion.",
                            onAdd = { showAddBookDialog = true }
                        )
                    }
                } else {
                    items(books) { book ->
                        BookCard(
                            book = book,
                            onUpdatePage = { newPage -> onUpdateBookProgress(book, newPage) }
                        )
                    }
                }
            }

            2 -> {
                // Study Sessions
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Deep Study Sessions",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                            color = PrimeTextPrimary
                        )
                        Button(
                            onClick = { showLogStudyDialog = true },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimeCyan),
                            shape = RoundedCornerShape(12.dp),
                            modifier = Modifier.testTag("log_study_button")
                        ) {
                            Icon(Icons.Default.Add, contentDescription = null, tint = Color.Black, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(4.dp))
                            Text("Log Session", color = Color.Black, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }

                if (studySessions.isEmpty()) {
                    item {
                        EmptyLearningState(
                            title = "No Study Sessions Logged",
                            subtitle = "Capture concentrated learning blocks, takeaways, and key insights.",
                            onAdd = { showLogStudyDialog = true }
                        )
                    }
                } else {
                    items(studySessions) { session ->
                        StudySessionCard(session = session)
                    }
                }
            }
        }

        item {
            Spacer(modifier = Modifier.height(32.dp))
        }
    }

    if (showAddCourseDialog) {
        AddCourseDialog(
            onDismiss = { showAddCourseDialog = false },
            onAdd = { title, platform, instructor, totalModules ->
                onAddCourse(title, platform, instructor, totalModules)
                showAddCourseDialog = false
            }
        )
    }

    if (showAddBookDialog) {
        AddBookDialog(
            onDismiss = { showAddBookDialog = false },
            onAdd = { title, author, format, totalPages ->
                onAddBook(title, author, format, totalPages)
                showAddBookDialog = false
            }
        )
    }

    if (showLogStudyDialog) {
        LogStudyDialog(
            onDismiss = { showLogStudyDialog = false },
            onAdd = { topic, cat, duration, insights ->
                onLogStudySession(topic, cat, duration, insights)
                showLogStudyDialog = false
            }
        )
    }
}

@Composable
fun CourseCard(
    course: CourseEntity,
    onUpdateProgress: (Int) -> Unit
) {
    val progress = (course.completedModules.toFloat() / course.totalModules.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth().testTag("course_card_${course.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = course.platform.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold, letterSpacing = 1.sp),
                        color = PrimeBlue
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = course.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimeTextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (progress >= 1f) PrimeGreen.copy(alpha = 0.15f) else PrimePrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${course.completedModules}/${course.totalModules} Mods",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (progress >= 1f) PrimeGreen else PrimePrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (progress >= 1f) PrimeGreen else PrimePrimary,
                trackColor = PrimeBorder.copy(alpha = 0.4f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (course.instructor.isNotBlank()) "Instructor: ${course.instructor}" else "Self-paced",
                    style = MaterialTheme.typography.bodySmall,
                    color = PrimeTextSecondary
                )

                Row {
                    if (course.completedModules > 0) {
                        IconButton(
                            onClick = { onUpdateProgress(course.completedModules - 1) },
                            modifier = Modifier.size(32.dp)
                        ) {
                            Text("-1", color = PrimeTextSecondary, fontWeight = FontWeight.Bold)
                        }
                    }
                    if (course.completedModules < course.totalModules) {
                        Button(
                            onClick = { onUpdateProgress(course.completedModules + 1) },
                            colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.height(32.dp)
                        ) {
                            Text("+1 Mod", color = PrimeOnPrimaryDark, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun BookCard(
    book: BookEntity,
    onUpdatePage: (Int) -> Unit
) {
    val progress = (book.currentPage.toFloat() / book.totalPages.coerceAtLeast(1).toFloat()).coerceIn(0f, 1f)
    var isEditing by remember { mutableStateOf(false) }
    var pageInput by remember { mutableStateOf(book.currentPage.toString()) }

    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth().testTag("book_card_${book.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = book.format.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimeGreen
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = book.title,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimeTextPrimary
                    )
                    Text(
                        text = "by ${book.author}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimeTextSecondary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (progress >= 1f) PrimeGreen.copy(alpha = 0.15f) else PrimePrimary.copy(alpha = 0.15f)
                ) {
                    Text(
                        text = "${book.currentPage}/${book.totalPages} pgs",
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = if (progress >= 1f) PrimeGreen else PrimePrimary,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            LinearProgressIndicator(
                progress = { progress },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(6.dp)
                    .clip(RoundedCornerShape(3.dp)),
                color = if (progress >= 1f) PrimeGreen else PrimePrimary,
                trackColor = PrimeBorder.copy(alpha = 0.4f)
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${(progress * 100).toInt()}% completed",
                    style = MaterialTheme.typography.bodySmall,
                    color = PrimeTextSecondary
                )

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Button(
                        onClick = { onUpdatePage(minOf(book.totalPages, book.currentPage + 20)) },
                        colors = ButtonDefaults.buttonColors(containerColor = PrimeGreen),
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.height(32.dp)
                    ) {
                        Text("+20 pgs", color = Color.White, style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
    }
}

@Composable
fun StudySessionCard(session: StudySessionEntity) {
    Card(
        shape = RoundedCornerShape(18.dp),
        colors = CardDefaults.cardColors(containerColor = PrimeSurfaceVariant),
        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth().testTag("study_session_card_${session.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = session.category.uppercase(),
                        style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                        color = PrimeCyan
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = session.topic,
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold),
                        color = PrimeTextPrimary
                    )
                }

                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = PrimeCyan.copy(alpha = 0.15f)
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Timer, contentDescription = null, tint = PrimeCyan, modifier = Modifier.size(14.dp))
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "${session.durationMinutes} min",
                            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold),
                            color = PrimeCyan
                        )
                    }
                }
            }

            if (session.keyInsights.isNotBlank()) {
                Spacer(modifier = Modifier.height(8.dp))
                Surface(
                    shape = RoundedCornerShape(10.dp),
                    color = PrimeSurface,
                    border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.2f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Key Insight: ${session.keyInsights}",
                        style = MaterialTheme.typography.bodySmall,
                        color = PrimeTextSecondary,
                        modifier = Modifier.padding(10.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun EmptyLearningState(title: String, subtitle: String, onAdd: () -> Unit) {
    Surface(
        shape = RoundedCornerShape(20.dp),
        color = PrimeSurfaceVariant,
        border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.3f)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(Icons.Default.School, contentDescription = null, tint = PrimePrimary, modifier = Modifier.size(36.dp))
            Spacer(modifier = Modifier.height(10.dp))
            Text(text = title, style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PrimeTextPrimary)
            Spacer(modifier = Modifier.height(4.dp))
            Text(text = subtitle, style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
            Spacer(modifier = Modifier.height(16.dp))
            Button(
                onClick = onAdd,
                colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary),
                shape = RoundedCornerShape(12.dp)
            ) {
                Text("Enroll / Add Now", color = PrimeOnPrimaryDark, style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold))
            }
        }
    }
}

@Composable
fun AddCourseDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, platform: String, instructor: String, totalModules: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var platform by remember { mutableStateOf("Coursera") }
    var instructor by remember { mutableStateOf("") }
    var totalModules by remember { mutableStateOf("10") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PrimeSurface,
            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth().testTag("add_course_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Add Course", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PrimeTextPrimary)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null, tint = PrimeTextSecondary) }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Course Title") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = platform,
                    onValueChange = { platform = it },
                    label = { Text("Platform (e.g. Stanford Online, Udemy, MIT)") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = totalModules,
                    onValueChange = { totalModules = it },
                    label = { Text("Total Modules / Chapters") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) onAdd(title, platform, instructor, totalModules.toIntOrNull() ?: 10)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimePrimary),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Course (+50 XP)", color = PrimeOnPrimaryDark, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun AddBookDialog(
    onDismiss: () -> Unit,
    onAdd: (title: String, author: String, format: String, totalPages: Int) -> Unit
) {
    var title by remember { mutableStateOf("") }
    var author by remember { mutableStateOf("") }
    var format by remember { mutableStateOf("Physical") }
    var totalPages by remember { mutableStateOf("300") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PrimeSurface,
            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth().testTag("add_book_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Add Book to Matrix", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PrimeTextPrimary)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null, tint = PrimeTextSecondary) }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Book Title") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = author,
                    onValueChange = { author = it },
                    label = { Text("Author") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = totalPages,
                    onValueChange = { totalPages = it },
                    label = { Text("Total Pages") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (title.isNotBlank()) onAdd(title, author, format, totalPages.toIntOrNull() ?: 300)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimeGreen),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Add Book (+30 XP)", color = Color.White, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun LogStudyDialog(
    onDismiss: () -> Unit,
    onAdd: (topic: String, cat: String, duration: Int, insights: String) -> Unit
) {
    var topic by remember { mutableStateOf("") }
    var duration by remember { mutableFloatStateOf(45f) }
    var insights by remember { mutableStateOf("") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(24.dp),
            color = PrimeSurface,
            border = BorderStroke(1.dp, PrimeBorder.copy(alpha = 0.4f)),
            modifier = Modifier.fillMaxWidth().testTag("log_study_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text("Log Study Block", style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold), color = PrimeTextPrimary)
                    IconButton(onClick = onDismiss) { Icon(Icons.Default.Close, contentDescription = null, tint = PrimeTextSecondary) }
                }

                Spacer(modifier = Modifier.height(12.dp))

                OutlinedTextField(
                    value = topic,
                    onValueChange = { topic = it },
                    label = { Text("Topic / Focus Area") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(10.dp))

                Text("Duration: ${duration.toInt()} minutes", style = MaterialTheme.typography.bodySmall, color = PrimeTextSecondary)
                Slider(
                    value = duration,
                    onValueChange = { duration = it },
                    valueRange = 15f..180f,
                    steps = 10,
                    colors = SliderDefaults.colors(thumbColor = PrimeCyan, activeTrackColor = PrimeCyan)
                )

                Spacer(modifier = Modifier.height(8.dp))

                OutlinedTextField(
                    value = insights,
                    onValueChange = { insights = it },
                    label = { Text("Key Insights / Summary") },
                    colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = PrimePrimary, unfocusedBorderColor = PrimeBorder),
                    modifier = Modifier.fillMaxWidth()
                )

                Spacer(modifier = Modifier.height(14.dp))

                Button(
                    onClick = {
                        if (topic.isNotBlank()) onAdd(topic, "Deep Study", duration.toInt(), insights)
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = PrimeCyan),
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text("Log Session (+30 XP)", color = Color.Black, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
