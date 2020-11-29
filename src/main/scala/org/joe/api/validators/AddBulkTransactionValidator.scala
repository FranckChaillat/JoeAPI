package org.joe.api.validators

import akka.http.scaladsl.server.Rejection
import org.joe.api.entities.dto.BulkAddTransactionRequest
import org.joe.api.exceptions.rejections.{EmptyListRejection, RequiredFieldRejection}

object AddBulkTransactionValidator extends Validator[BulkAddTransactionRequest] {
  override protected def check(input: BulkAddTransactionRequest): Option[Rejection] = {
    if(input.transactions.isEmpty)
      Some(EmptyListRejection("transactions"))
    else if(input.overwrite && input.limitDate.isEmpty)
      Some(RequiredFieldRejection("limitDate", Some("if overwrite parameter is set, you have to specify a limit date.")))
    else None
  }
}
