package com.armarizki.chromatic.view.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.consumeWindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BottomAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.rohankhayech.android.util.ui.preview.CompactOrientationThemePreview
import com.armarizki.chromatic.R
import com.armarizki.chromatic.model.tuning.TuningEntry
import com.armarizki.chromatic.model.tuning.Tunings
import com.armarizki.chromatic.view.components.NoteSelector
import com.armarizki.chromatic.view.components.StringControls
import com.armarizki.chromatic.view.components.TuningSelector
import com.armarizki.chromatic.view.theme.AppTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ConfigureTuningScreen(
    tuning: TuningEntry,
    chromatic: Boolean,
    selectedNote: Int,
    favTunings: State<Set<TuningEntry>>,
    getCanonicalName: TuningEntry.InstrumentTuning.() -> String,
    onTuneUpString: (Int) -> Unit,
    onTuneDownString: (Int) -> Unit,
    onTuneUpTuning: () -> Unit,
    onTuneDownTuning: () -> Unit,
    onSelectNote: (Int) -> Unit,
    onOpenTuningSelector: () -> Unit,
    onDismiss: () -> Unit,
    onSettingsPressed: () -> Unit,
) {
    val scrollBehaviour = TopAppBarDefaults.exitUntilCollapsedScrollBehavior()

    Scaffold (
        Modifier.nestedScroll(scrollBehaviour.nestedScrollConnection),
        topBar = {
            TopAppBar(
                title = {
                    Text(stringResource(R.string.configure_tuning), style = MaterialTheme.typography.titleMedium)
                },
                actions = {
                    // Settings button
                    IconButton(onClick = onSettingsPressed) {
                        Icon(Icons.Default.Settings, stringResource(R.string.tuner_settings))
                    }
                },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(Icons.Default.Close, stringResource(R.string.dismiss))
                    }
                },
                scrollBehavior = scrollBehaviour,
            )
        },
        bottomBar = {
            Column(Modifier.fillMaxWidth()) {
                HorizontalDivider(thickness = Dp.Hairline)
                BottomAppBar(
                    modifier = Modifier.height(IntrinsicSize.Min),
                    contentPadding = PaddingValues(vertical = 8.dp),
                ) {
                    TuningSelector(
                        tuning = tuning,
                        favTunings = favTunings,
                        getCanonicalName = getCanonicalName,
                        openDirect = true,
                        onSelect = {},
                        onTuneDown = onTuneDownTuning,
                        onTuneUp = onTuneUpTuning,
                        onOpenTuningSelector = onOpenTuningSelector,
                        editModeEnabled = true,
                        compact = true
                    )

                }
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .padding(padding)
                .consumeWindowInsets(padding)
                .windowInsetsPadding(WindowInsets.safeDrawing)
                .fillMaxSize()
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            if (chromatic) {
                NoteSelector(
                    Modifier.padding(vertical = 8.dp),
                    selectedNoteIndex = selectedNote,
                    tuned = false,
                    onSelect = onSelectNote
                )
            } else {
                StringControls(
                    Modifier.padding(vertical = 8.dp),
                    inline = true,
                    tuning = tuning.tuning!!,
                    selectedString = null,
                    tuned = null,
                    onSelect = {},
                    onTuneDown = onTuneDownString,
                    onTuneUp = onTuneUpString,
                    editModeEnabled = true,
                )
            }
        }
    }
}