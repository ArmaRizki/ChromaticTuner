package com.armarizki.chromatic.view.activity

import com.armarizki.chromatic.model.tuning.TuningEntry
import com.armarizki.music.Tuning
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test

 
class TunerActivityViewModelTest {

    private lateinit var vm: TunerActivityViewModel

    private var testDispatcher = StandardTestDispatcher()

    @OptIn(ExperimentalCoroutinesApi::class)
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        vm = TunerActivityViewModel()
    }

    @Test
    fun testInitial() {
        assertEquals(TuningEntry.InstrumentTuning(vm.tuner.tuning.value), vm.tuningList.current.value)
        assertFalse(vm.tuningSelectorOpen.value)
        assertFalse(vm.configurePanelOpen.value)
        assertFalse(vm.editModeEnabled.value)
    }

    @Test
    fun testOpenTuningSelector() {
        vm.openTuningSelector()
        assertTrue(vm.tuningSelectorOpen.value)
    }

    @Test
    fun testOpenConfigurePanel() {
        vm.openConfigurePanel()
        assertTrue(vm.configurePanelOpen.value)
    }

    @Test
    fun testDismissTuningSelector() {
        vm.openTuningSelector()
        vm.dismissTuningSelector()
        assertFalse(vm.tuningSelectorOpen.value)
    }

    @Test
    fun testDismissConfigurePanel() {
        vm.openConfigurePanel()
        vm.dismissConfigurePanel()
        assertFalse(vm.configurePanelOpen.value)
    }

    @Test
    fun testSelectTuning() {
        vm.openTuningSelector()
        vm.selectTuning(Tuning.DROP_D)
        testDispatcher.scheduler.runCurrent()
        assertFalse(vm.tuningSelectorOpen.value)
        assertEquals(Tuning.DROP_D, vm.tuner.tuning.value)
        assertEquals(TuningEntry.InstrumentTuning(Tuning.DROP_D), vm.tuningList.current.value)
    }

    @Test
    fun testTuningSync() {
        vm.tuner.setTuning(Tuning.DROP_D)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(TuningEntry.InstrumentTuning(Tuning.DROP_D), vm.tuningList.current.value)
        vm.tuningList.setCurrent(TuningEntry.InstrumentTuning(Tuning.STANDARD))
        testDispatcher.scheduler.runCurrent()
        assertEquals(Tuning.STANDARD, vm.tuner.tuning.value)

        vm.tuner.setChromatic()
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(TuningEntry.ChromaticTuning, vm.tuningList.current.value)

        vm.tuner.setChromatic(false)
        testDispatcher.scheduler.advanceUntilIdle()
        assertEquals(TuningEntry.InstrumentTuning(Tuning.STANDARD), vm.tuningList.current.value)

        vm.tuningList.setCurrent(TuningEntry.ChromaticTuning)
        testDispatcher.scheduler.runCurrent()
        assertTrue(vm.tuner.chromatic.value)

        vm.tuningList.setCurrent(TuningEntry.InstrumentTuning(Tuning.STANDARD))
        testDispatcher.scheduler.runCurrent()
        assertEquals(Tuning.STANDARD, vm.tuner.tuning.value)
        assertFalse(vm.tuner.chromatic.value)
    }

    @Test
    fun testEditModeToggle() {
        vm.setEditMode(true)
        assertTrue(vm.editModeEnabled.value)
        vm.setEditMode(false)
        assertFalse(vm.editModeEnabled.value)
    }

    @Test
    fun testSelectChromatic() {
        vm.openTuningSelector()
        vm.selectChromatic()
        assertFalse(vm.tuningSelectorOpen.value)
        assertTrue(vm.tuner.chromatic.value)
    }
}