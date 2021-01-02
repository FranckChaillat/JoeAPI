package org.joe.api.entities.dto

final case class AddBudgetRequest(label: String,
                                  description: Option[String],
                                  amount: Option[Float])
