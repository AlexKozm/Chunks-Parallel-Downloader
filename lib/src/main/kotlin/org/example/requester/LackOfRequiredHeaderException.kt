package org.example.requester

class LackOfRequiredHeaderException(
    val header: String
) : NoSuchElementException("Header '$header' is not in the list")