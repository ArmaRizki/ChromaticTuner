package com.armarizki.chromatic.controller.play

/**
 * Controller that handles launching Google Play review prompts.
 * @author Rohan Khayech
 */
interface ReviewController {
    /** Launches a prompt for the user to review the app on Google Play. */
    fun launchReviewPrompt()
}