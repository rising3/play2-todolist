package models

import java.util.Date
import org.specs2.mutable._
import java.text.SimpleDateFormat

/**
 * タスクモデル仕様.
 * 
 * @author michio.nakagawa@gmail.com
 */
class TaskSpec extends Specification {
  "Task#action" should {
    "statusはTodoである" in {
      val task = Task("hoge", new Date(), Priority.Normal)
      
      task.status must be equalTo (Status.Todo)
    }
    "statusがTodoの状態で呼び出すと、statusはDoingである" in {
      val task = Task("hoge", new Date(), Priority.Normal)
      task.action()
      task.status must be equalTo (Status.Doing)
    }
    "statusがDoingの状態で呼び出すと、statusはDoneである" in {
      val task = Task("hoge", new Date(), Priority.Normal)
      task.action()
      task.action()
      task.status must be equalTo (Status.Done)
    }
    "statusがDoneの状態で呼び出すと、例外をスローする" in {
      val task = Task("hoge", new Date(), Priority.Normal)
      task.action()
      task.action()
      task.action() must throwA[Exception]
    }
  }
  "Task#toDateFormatString" should {
    "日付型の値をyyyy-MM-dd形式の文字列に変換される" in {
      val df = new SimpleDateFormat("yyyy-MM-dd");
      val date = df.parse("2012-01-01")
      Task.toDateFormatString(date) must be equalTo ("2012-01-01")
    }
  }
}