package org.joe.api.entities.dto

final case class AddCategoryRequest(label: String,
                                    description: Option[String],
                                    amount: Option[Float])
