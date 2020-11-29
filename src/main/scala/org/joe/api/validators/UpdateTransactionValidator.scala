package org.joe.api.validators

import akka.http.scaladsl.server.Rejection
import org.joe.api.entities.dto.TransactionUpdateRequest
import org.joe.api.exceptions.rejections.{EmptyNotAllowedRejection, InvalidValueRangeRejection, RequiredFieldRejection}

object UpdateTransactionValidator extends Validator[TransactionUpdateRequest] {
  override def check(input: TransactionUpdateRequest): Option[Rejection] = {
    if(input.accountId <= 0)
      Some(InvalidValueRangeRejection("AccountId", 1, Int.MaxValue, input.accountId))
    else if(input.category.isEmpty && input.occurence.isEmpty)
      Some(RequiredFieldRejection("Nothing to update"))
    else if(input.occurence.map(_ <= 0).getOrElse(false))
      Some(InvalidValueRangeRejection("occurence", 1, Int.MaxValue, input.occurence.getOrElse(0)))
    else if(input.category.exists(_.isEmpty))
      Some(EmptyNotAllowedRejection("category"))
    else None
  }
}
