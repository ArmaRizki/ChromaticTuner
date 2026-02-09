package com.armarizki.chromatic.view

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.windowsizeclass.ExperimentalMaterial3WindowSizeClassApi
import androidx.compose.material3.windowsizeclass.WindowSizeClass
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.rohankhayech.android.util.ui.preview.CompactOrientationPreview
import com.rohankhayech.android.util.ui.preview.DarkPreview
import com.rohankhayech.android.util.ui.preview.TabletThemePreview
import com.armarizki.chromatic.model.preferences.StringLayout
import com.armarizki.chromatic.model.preferences.TunerPreferences
import com.armarizki.chromatic.model.preferences.TuningDisplayType
import com.armarizki.chromatic.model.tuning.TuningEntry
import com.armarizki.chromatic.model.tuning.TuningList
import com.armarizki.chromatic.model.tuning.Tunings
import com.armarizki.chromatic.view.screens.MainLayout
import com.armarizki.chromatic.view.screens.SaveTuningDialog
import com.armarizki.chromatic.view.screens.SettingsScreen
import com.armarizki.chromatic.view.screens.TunerScreen
import com.armarizki.chromatic.view.screens.TuningSelectionScreen
import com.armarizki.chromatic.view.theme.AppTheme
import com.armarizki.music.Notes
import com.armarizki.music.Tuning

// Previews for generating screenshots.

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@DarkPreview
@Composable
private fun TunerScreenshot() {
    AppTheme {
        TunerScreen(
            compact = false,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = TuningEntry.InstrumentTuning(Tunings.STANDARD),
            noteOffset = remember { mutableDoubleStateOf(0.3) },
            selectedString = 3,
            selectedNote = -29,
            tuned = BooleanArray(6) { false },
            noteTuned = false,
            autoDetect = true,
            chromatic = false,
            favTunings = remember { mutableStateOf(emptySet()) },
            getCanonicalName = { this.tuning.toString() },
            prefs = TunerPreferences(),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, true, {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@DarkPreview
@Composable
private fun InTuneScreenshot() {
    AppTheme {
        TunerScreen(
            compact = false,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = TuningEntry.InstrumentTuning(Tunings.DROP_D),
            noteOffset = remember { mutableDoubleStateOf(0.01) },
            selectedString = 5,
            selectedNote = 0, // Assuming a valid note for Drop D, string 5
            tuned = BooleanArray(6) { it == 5 },
            noteTuned = true, // Implied by "InTune"
            autoDetect = true,
            chromatic = false,
            favTunings = remember { mutableStateOf(emptySet()) },
            getCanonicalName = { this.tuning.toString() },
            prefs = TunerPreferences(),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, false, {}
        )
    }
}

@DarkPreview
@Composable
private fun SelectionScreenshot() {
    val tunings = TuningList(Tunings.WHOLE_STEP_DOWN).apply {
        setFavourited(TuningEntry.InstrumentTuning(Tunings.DROP_D), true)
        addCustom("Example", Tuning.fromString("F4 C4 G#3 D#3 A#2 F2"))
    }

    AppTheme {
        TuningSelectionScreen(
            tuningList = tunings,
            pinnedInitial = true,
            backIcon = Icons.Default.Close,
            onSelect = {},
            onSelectChromatic = {},
            onDismiss = {}
        )
    }
}

@SuppressLint("StateFlowValueCalledInComposition")
@DarkPreview
@Composable
private fun CustomScreenshot() {
    val current = TuningEntry.InstrumentTuning(Tuning.fromString("F4 C4 G#3 D#3 A#2 F2"))
    val tunings = TuningList(current.tuning).apply {
        setFavourited(TuningEntry.InstrumentTuning(Tunings.DROP_D), true)
    }

    AppTheme {
        TuningSelectionScreen (
            current = tunings.current.value,
            tunings = tunings.filteredTunings.value,
            favourites = tunings.favourites.value,
            custom = tunings.custom.value,
            pinned = TuningEntry.InstrumentTuning(Tuning.STANDARD),
            pinnedInitial = true,
            instrumentFilter = null,
            categoryFilter = null,
            instrumentFilters = tunings.instrumentFilters.collectAsStateWithLifecycle(),
            categoryFilters = tunings.categoryFilters.collectAsStateWithLifecycle(),
            backIcon = Icons.Default.Close,
            deletedTuning = tunings.deletedTuning,
            onSelectInstrument = {},
            onSelectCategory = {},
            onSave = {_, _ ->},
            onFavouriteSet = {_, _ ->},
            onSelect = {},
            onDelete = {},
            onDismiss = {},
            onPin = {},
            onUnpin = {},
            isFavourite = { tunings.run {
                this@TuningSelectionScreen.isFavourite()
            }},
            currentSaved = false
        )

        SaveTuningDialog(
            tuning = current.tuning,
            onSave = { _, _ -> },
            onDismiss = {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@DarkPreview
@Composable
private fun ChromaticScreenshot() {
    AppTheme {
        TunerScreen(
            compact = false,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = TuningEntry.ChromaticTuning,
            noteOffset = remember { mutableDoubleStateOf(-0.4) },
            selectedString = 3,
            selectedNote = Notes.getIndex("D3"),
            tuned = BooleanArray(6) { false },
            noteTuned = false,
            autoDetect = true,
            chromatic = true,
            favTunings = remember { mutableStateOf(emptySet()) },
            getCanonicalName = { this.tuning.toString() },
            prefs = TunerPreferences(
                stringLayout = StringLayout.SIDE_BY_SIDE
            ),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, false, {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@Preview
@Composable
private fun SemitonesScreenshot() {
    AppTheme {
        TunerScreen(
            compact = false,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = TuningEntry.InstrumentTuning(Tunings.STANDARD),
            noteOffset = remember { mutableDoubleStateOf(-3.6) },
            selectedString = 3,
            selectedNote = -29,
            tuned = BooleanArray(6) { false },
            noteTuned = false,
            autoDetect = false,
            chromatic = false,
            favTunings = remember { mutableStateOf(emptySet()) },
            getCanonicalName = { this.tuning.toString() },
            prefs = TunerPreferences(
                displayType = TuningDisplayType.SEMITONES,
                stringLayout = StringLayout.SIDE_BY_SIDE
            ),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, false, {}
        )
    }
}

@Composable
@DarkPreview
private fun SettingsScreenshot() {
    AppTheme {
        SettingsScreen(
            prefs = TunerPreferences(
                enableInTuneSound = false
            ),
            pinnedTuning = "Standard",
            {},{}, {},{},{},{},{}, {}, {}, {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@DarkPreview
@Composable
private fun BlackThemeScreenshot() {
    AppTheme(fullBlack = true) {
        TunerScreen(
            compact = false,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = TuningEntry.InstrumentTuning(Tunings.DROP_D),
            noteOffset = remember { mutableDoubleStateOf(-0.42) },
            selectedString = 3,
            selectedNote = -29,
            tuned = BooleanArray(6) { it==5 || it==4 },
            noteTuned = false,
            autoDetect = true,
            chromatic = false,
            favTunings = remember { mutableStateOf(emptySet()) },
            getCanonicalName = { this.tuning.toString() },
            prefs = TunerPreferences(
                useBlackTheme = true,
                displayType = TuningDisplayType.CENTS
            ),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, true, {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@CompactOrientationPreview
@Composable
private fun SplitScreenScreenshot() {
    AppTheme(darkTheme = true) {
        TunerScreen(
            compact = true,
            expanded = false,
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(411.dp, 891.dp)),
            tuning = TuningEntry.InstrumentTuning(Tunings.STANDARD),
            noteOffset = remember { mutableDoubleStateOf(0.3) },
            selectedString = 3,
            selectedNote = -29,
            tuned = BooleanArray(6) { false },
            noteTuned = false,
            autoDetect = true,
            chromatic = false,
            favTunings = remember { mutableStateOf(emptySet()) },
            getCanonicalName = { this.tuning.toString() },
            prefs = TunerPreferences(),
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, true, {}
        )
    }
}

@OptIn(ExperimentalMaterial3WindowSizeClassApi::class)
@TabletThemePreview
@Composable
private fun TabletScreenshot() {
    val tunings = TuningList(Tunings.WHOLE_STEP_DOWN).apply {
        setFavourited(TuningEntry.InstrumentTuning(Tunings.DROP_D), true)
        addCustom("Example", Tuning.fromString("F4 C4 G#3 D#3 A#2 F2"))
    }

    AppTheme {
        MainLayout(
            windowSizeClass = WindowSizeClass.calculateFromSize(DpSize(891.dp, 891.dp)),
            compact = false,
            expanded = true,
            tuning = TuningEntry.InstrumentTuning(Tunings.HALF_STEP_DOWN),
            noteOffset = remember { mutableDoubleStateOf(0.3) },
            selectedString = 3,
            selectedNote = -28,
            tuned = BooleanArray(6) { false },
            noteTuned = false,
            autoDetect = true,
            chromatic = false,
            favTunings = remember { mutableStateOf(emptySet()) },
            getCanonicalName = { this.tuning.toString() },
            prefs = TunerPreferences(),
            tuningList = tunings,
            tuningSelectorOpen = false,
            configurePanelOpen = false,
            true,
            {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}, {}
        )
    }
}