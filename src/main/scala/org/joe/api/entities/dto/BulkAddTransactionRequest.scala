package org.joe.api.entities.dto

final case class BulkAddTransactionRequest(transactions : Seq[AddTransactionRequest], overwrite: Boolean, limitDate: Option[String])
