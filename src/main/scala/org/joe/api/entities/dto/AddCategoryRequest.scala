package org.joe.api.entities.dto

final case class AddCategoryRequest(accountId: Int,
                                    categoryLabel: String,
                                    categoryDescription: Option[String])
