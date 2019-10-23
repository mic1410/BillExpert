package pl.szkoleniaandroid.billexpert.utils

/**
 * To be used with `when` block to make it expression, and thus enforce all branches/cases to be handled
 *
 * when(something) {
 *      ...
 * }.exhaustive
 *
 * src: https://twitter.com/FMuntenescu/status/1044183533969969152
 */
val <T> T.exhaustive get() = this