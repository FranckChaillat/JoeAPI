package org.joe.api.entities.dto

final case class AddBudgetRequest(accountId: Int,
                                  label: String,
                                  description: Option[String],
                                  amount: Option[Float])
