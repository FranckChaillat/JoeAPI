package org.joe.api.entities.dto

final case class TransactionUpdateRequest(accountId: Int, category: Option[String], occurence: Option[Int])
