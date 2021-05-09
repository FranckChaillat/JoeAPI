package org.joe.api.exceptions

final case class ItemDuplicateError(msg: String) extends Exception(msg)