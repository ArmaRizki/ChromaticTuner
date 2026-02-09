package com.armarizki.chromatic.controller.play

import androidx.activity.ComponentActivity
import androidx.datastore.preferences.core.edit
import androidx.lifecycle.lifecycleScope
import com.google.android.play.core.review.ReviewManager
import com.google.android.play.core.review.ReviewManagerFactory
import com.armarizki.chromatic.model.preferences.TunerPreferences
import com.armarizki.chromatic.model.preferences.tunerPreferenceDataStore
import kotlinx.coroutines.launch

/**
 * Controller that handles launching Google Play review prompts.
 * @author Rohan Khayech
 */
class ReviewControllerImpl(private val context: ComponentActivity): ReviewController {
    /** Google Play review manager. */
    private val manager: ReviewManager = ReviewManagerFactory.create(context)

    /** Launches a prompt for the user to review the app on Google Play. */
    override fun launchReviewPrompt() {
        val request = manager.requestReviewFlow()
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                val reviewInfo = task.result
                manager.launchReviewFlow(context, reviewInfo)
                    .addOnCompleteListener {
                        // Increment launches counter.
                        context.lifecycleScope.launch {
                            context.tunerPreferenceDataStore.edit { prefs ->
                                prefs[TunerPreferences.Companion.REVIEW_PROMPT_LAUNCHES_KEY] = (
                                    (prefs[TunerPreferences.Companion.REVIEW_PROMPT_LAUNCHES_KEY]?.toIntOrNull() ?: 0)
                                        + 1
                                    ).toString()
                            }
                        }
                    }
            }
        }
    }
}