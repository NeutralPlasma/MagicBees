package eu.virtusdevelops.magicbees.api.models

/**
* A generic class to store results categorized into success and failure lists.
*
* @param passed A list of successful results.
* @param failed A list of failed results.
*/
class ListResult<T, E>(
    val passed: List<T>,
    val failed: List<E>
) {
    /**
     * Utility method to check whether the overall result is fully successful.
     *
     * @return `true` if there are no failed results, otherwise `false`.
     */
    fun isSuccess(): Boolean {
        return failed.isEmpty()
    }

    /**
     * Utility method to check whether the overall result has any failures.
     *
     * @return `true` if there are any failed results, otherwise `false`.
     */
    fun hasFailures(): Boolean {
        return failed.isNotEmpty()
    }

    /**
     * Returns the total count of passed and failed results combined.
     *
     * @return Total number of items in both lists.
     */
    fun totalCount(): Int {
        return passed.size + failed.size
    }
}
