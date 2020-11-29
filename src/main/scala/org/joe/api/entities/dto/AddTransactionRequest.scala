package org.joe.api.entities.dto

case class AddTransactionRequest(accountId: Int, operationDate: String, valueDate: String, amount: Float, label: String, category: Option[String])