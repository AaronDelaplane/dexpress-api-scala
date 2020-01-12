package dexpress

import java.util.UUID

import cats.data.NonEmptyList
import cats.syntax.option._
import dexpress.enums.ResourceName

package object types {
  
  /*
  generic types --------------------------------------------------------------------------------------------------------
   */
  type NEL[A]     = NonEmptyList[A]

  type ErrorOr[A] = Either[String, A]
  
  /*
  steam-related types --------------------------------------------------------------------------------------------------
   */
  type SI  = SteamInventory
  
  type SA  = SteamAsset
  type SAV = SteamAssetValidated
  
  type SD  = SteamDescription
  type SDV = SteamDescriptionValidated
  
  type ST                = SteamTag
  type STVWithoutColor   = SteamTagValidatedWithoutColor
  type STVWithMaybeColor = SteamTagValidatedWithMaybeColor
  type STVWithColor      = SteamTagValidatedWithColor
  
  /*
  value classes --------------------------------------------------------------------------------------------------------
   */
  final case class IdAsset(value: UUID) extends AnyVal
  final case class IdAssetSteam(value: String) extends AnyVal
  final case class IdRefresh(value: UUID) extends AnyVal
  final case class IdUser(value: UUID) extends AnyVal
  final case class IdUserSteam(value: String) extends AnyVal
  
  final case class StateTrading(value: Boolean) extends AnyVal
  
  final case class FloatValue(value: Double) extends AnyVal

  final case class SearchFilter(value: UUID) extends AnyVal
  final case class SearchFilterNot(value: UUID) extends AnyVal
  final case class SearchLimit(value: Int) extends AnyVal
  final case class SearchOffset(value: Int) extends AnyVal
  
  /*
  errors ---------------------------------------------------------------------------------------------------------------
   */
  sealed trait ServiceError extends Throwable {
    def message: String
  }
  
  // data-transformation-state errors
  final case class DataTransformationError(from: String, to: String, reason: String) extends ServiceError {
    override def message: String = s"data transformation error: attempt to transform $from to $to failed because $reason"
  }
  
  // request body decoding error
  final case class RequestBodyDecodingError(t: Throwable) extends ResourceError {
    override def message: String = s"request body decoding error: ${t.getMessage}"
  }
  
  // resource-related errors
  sealed trait ResourceError extends ServiceError
  
  final case class ResourceAuthenticationError(resource: ResourceName, id: String) extends ResourceError {
    override def message: String = s"${resource.entryName} authentication error: authorization for id ($id) failed"
  }

  final case class ResourceDuplicationError(resource: ResourceName, event: String) extends ResourceError {
    override def message: String =
      s"${resource.entryName} invalid duplicate state error: attempt to read $event returned duplicates"
  }
  
  final case class ResourceGenericError(resource: ResourceName, event: String, maybeThrowable: Option[Throwable]) extends ResourceError {
    override def message:  String = {
      maybeThrowable.fold(
        s"${resource.entryName} generic error: attempt to $event failed"
      )(
        t => s"${resource.entryName} generic error: attempt to $event failed with exception (${t.getMessage})"
      )
    }
  }
  object ResourceGenericError {
    def apply(resource: ResourceName, event: String): ResourceGenericError =
      ResourceGenericError(resource, event, None)
    def apply(resource: ResourceName, event: String, throwable: Throwable): ResourceGenericError =
      ResourceGenericError(resource, event, throwable.some)
  }
  
  final case class ResourceInvalidStateChangeError(resource: ResourceName, summary: String) extends ResourceError {
    override def message: String = s"${resource.entryName} invalid state change error: $summary"
  }
  
  final case class ResourceNotFoundError(resource: ResourceName, identifier: String) extends ResourceError {
    override def message: String = s"${resource.entryName} not found error: attempt to read $identifier returned empty value"
  }
  
  final case class ResourceTransactionError(resource: ResourceName, event: String, throwable: Throwable) extends ResourceError {
    override def message: String =
      s"${resource.entryName} transaction error: attempt to $event failed with exception (${throwable.getMessage})"
  }
}
