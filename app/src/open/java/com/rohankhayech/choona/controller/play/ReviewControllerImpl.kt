
package com.rohankhayech.choona.controller.play

import androidx.activity.ComponentActivity

/**
 * No-op implementation of the Google Play Review prompt controller, for distribution outside the play store.
 * @author Rohan Khayech
 */
class ReviewControllerImpl(@Suppress("unused") val context: ComponentActivity): ReviewController {
    override fun launchReviewPrompt() { /* No-op. */ }
}