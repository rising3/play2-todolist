package models
import java.util.Date
import com.novus.salat.annotations.raw.Salat
import commons.utils.ValidationUtil.validate
import scalaz.Scalaz._
import com.novus.salat.annotations.raw.Key
import org.bson.types.ObjectId
import java.text.DateFormat
import java.text.SimpleDateFormat

/**
 * ステータス列挙体.
 * 
 * @author michio.nakagawa@gmail.com
 */
object Status extends Enumeration {
  type Status = Value
  val Todo, Doing, Done = Value
}

/**
 * 重要度列挙体.
 * 
 * @author michio.nakagawa@gmail.com
 */
object Priority extends Enumeration {
  type Priority = Value
  val High, Normal, Low = Value
}

/**
 * タスクモデル.
 * 
 * @author michio.nakagawa@gmail.com
 */
case class Task(
  @Key("_id")
  val id: ObjectId = new ObjectId,
  val todo: String,
  val createDate: Date,
  val termDate: Date,
  var status: Status.Value,
  val priority: Priority.Value) {
    
  /**
   * アクション処理.
   */
  def action() = status match {
    case Status.Todo  => status = Status.Doing
    case Status.Doing => status = Status.Done
    case _            => throw new Exception()
  }
}

/**
 * タスクモデルコンパニオンオブジェクト.
 * 
 * @author michio.nakagawa@gmail.com
 */
object Task {
  val df = new SimpleDateFormat("yyyy-MM-dd");

  def apply(todo: String, termDate: Date, priority: Priority.Value) = new Task(todo = todo, createDate = new Date(), termDate = termDate, status = Status.Todo, priority = priority)
  def unupply(task: Task) = Some((task.id, task.todo, task.createDate, task.termDate, task.status, task.priority))

  /**
   * 文字列日付変換処理.
   */
  def toDateFormatString(date :Date) = df.format(date)
}