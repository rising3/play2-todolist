package controllers

import java.util.Date

import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope

import play.api.Play.current
import play.api.data.Forms._
import play.api.http.Status
import play.api.mvc.MultipartFormData._
import play.api.mvc._
import play.api.test.Helpers._
import play.api.test._
import play.api._
import org.bson.types.ObjectId
import models.Task
import models.TaskRepositoryComponent
import services.TaskServiceComponent

/**
 * モックコンポーネントコンテキスト.
 *
 * このコンテキスト内で、リポジトリをモックに書き換えている.
 *
 * @author michio.nakagawa@gmail.com
 */
trait MockComponentContext extends Scope with Mockito with TaskControllerBase with TaskServiceComponent with TaskRepositoryComponent {
  val taskService = mock[TaskService]
  val taskRepository = mock[TaskRepository]
}

/**
 * タスクコントローラ仕様.
 *
 * @author michio.nakagawa@gmail.com
 */
class TaskControllerSpec extends Specification {

  "TaskController#index" should {
    "タスク一覧はリダイレクトで表示される." in new MockComponentContext {
      val result = index()(FakeRequest())
      status(result) must equalTo(play.api.http.Status.SEE_OTHER)
      contentType(result) must beNone
    }
  }

  "TaskController#tasks" should {
    "タスク一覧は表示される." in new MockComponentContext {

      // mock setting
      taskService.findAll() returns List()

      val result = tasks()(FakeRequest())

      // Verification
      there was one(taskService).findAll()
      status(result) must equalTo(play.api.http.Status.OK)
      contentType(result) must beSome("text/html")
    }
  }
  "TaskController#newTask" should {
    "不正なフォームがリクエストされた場合は、バッドリクエストが返される." in new MockComponentContext {

      // mock setting
      taskService.findAll() returns List()

      val result = newTask(FakeRequest())

      // Verification
      there was one(taskService).findAll()
      
      status(result) must equalTo(play.api.http.Status.BAD_REQUEST)
      contentType(result) must beSome("text/html")
    }
    "正常なフォームがリクエストされた場合は、永続化しタスク一覧が表示される." in new MockComponentContext {

      val task = Task("task", new Date(), models.Priority.High)

      val result = newTask(
        FakeRequest().withFormUrlEncodedBody(
            "todo"     -> "task",
            "termDate" -> "2013-01-01",
            "priority" -> "High")
        )

      // Verification
      there was one(taskService).persist(task)
      status(result) must equalTo(play.api.http.Status.SEE_OTHER)
      contentType(result) must beNone
    }
  }

  "TaskController#actionTask" should {
    "タスクのアクションを実行後、リダイレクトでタスク一覧は表示される." in new MockComponentContext {
      val objectId = new ObjectId

      val result = actionTask(objectId)(FakeRequest())

      // Verification
      there was one(taskService).doAction(objectId)
      status(result) must equalTo(play.api.http.Status.SEE_OTHER)
      contentType(result) must beNone
    }
  }

  "TaskController#deleteTask" should {
    "タスクを削除後、リダイレクトでタスク一覧は表示される." in new MockComponentContext {
      val objectId = new ObjectId

      val result = deleteTask(objectId)(FakeRequest())

      // Verification
      there was one(taskService).remove(objectId)
      status(result) must equalTo(play.api.http.Status.SEE_OTHER)
      contentType(result) must beNone
    }
  }
}
