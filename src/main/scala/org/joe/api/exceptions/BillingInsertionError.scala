package org.joe.api.exceptions

case class BillingInsertionError(msg: String) extends Exception(msg)
