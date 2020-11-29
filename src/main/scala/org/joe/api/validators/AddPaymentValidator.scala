package org.joe.api.validators

import akka.http.scaladsl.server.Rejection
import org.joe.api.entities.dto.AddTransactionRequest
import org.joe.api.exceptions.rejections.{InvalidValueRangeRejection, RequiredFieldRejection}

object AddPaymentValidator extends Validator[AddTransactionRequest] {
  override protected def check(input: AddTransactionRequest): Option[Rejection] = {
    if(input.label.isEmpty)
      Some(RequiredFieldRejection("label"))
    else if(input.accountId <= 0)
      Some(InvalidValueRangeRejection("AccountId", 1, Int.MaxValue, input.accountId))
    else None


  }
}
