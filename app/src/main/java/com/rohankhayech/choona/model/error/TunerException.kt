
package com.rohankhayech.choona.model.error

/**
 * An exception that occurs when the tuner is prevented from running.
 * @property message The detail message of the exception.
 * @property cause The cause of the exception, if any.
 * @author Rohan Khayech
 */
class TunerException(override val message: String?, override val cause: Throwable?): Exception()