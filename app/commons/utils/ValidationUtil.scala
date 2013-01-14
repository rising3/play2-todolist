package commons.utils

import scalaz.Scalaz._
import scalaz.NonEmptyList

object ValidationUtil {

  /**
   *
   */
  def validate[T](f: T => Boolean, message: String, v: T): ValidationNEL[Throwable, T] = {
    v |> f.toValidation(new IllegalArgumentException(message))
  }

  /**
   *
   */
  def validates[A, B](
    f: A => ValidationNEL[Throwable, B],
    g: Seq[B => ValidationNEL[Throwable, B]],
    value: A): ValidationNEL[Throwable, B] = {
    g.foldLeft(f(value)) { (a, x) =>
      a.flatMap(x)
    }
  }

  /**
   *
   */
  def validatesDef[A, B](
    d: (A => ValidationNEL[Throwable, B], Seq[B => ValidationNEL[Throwable, B]]),
    v: A): ValidationNEL[Throwable, B] = {
    validates(d._1, d._2, v)
  }

  /**
   *
   */
  def intValue = (_: String).parseInt.liftFailNel

  /**
   *
   */
  def greaterEqual(value: Int, message: String) = {
    validate((_: Int) >= value, message, (_: Int))
  }

  /**
   *
   */
  def lessEqual(value: Int, message: String) = {
    validate((_: Int) <= value, message, (_: Int))
  }

  /**
   *
   */
  def equal(value: Int, message: String) = {
    validate((_: Int) == value, message, (_: Int))
  }

  /**
   *
   */
  def notEqual(value: Int, message: String) = {
    validate((_: Int) != value, message, (_: Int))
  }

  /**
   * 
   */
  def stringValue = (_: String).success[NonEmptyList[Throwable]]
  
  /**
   * 
   */
  def maxLength[T](conv: T => Int, length: Int, message: String) = {
    validate(conv(_: T) <= length, message, (_: T))
  }

  /**
   * 
   */
  def maxStringLength(length: Int, message: String) = {
    maxLength((_: String).length, length, message)
  }

  /**
   * 
   */
  def minLength[T](conv: T => Int, length: Int, message: String) = {
    validate(conv(_: T) >= length, message, (_: T))
  }

  def minStringLength(length: Int, message: String) = {
    minLength((_: String).length, length, message)
  }
}