package controllers

import java.util.Date
import play.api._
import play.api.mvc._
import play.api.data._
import play.api.data.Forms._
import org.bson.types.ObjectId
import services.TaskServiceComponent
import models.TaskRepositoryComponent
import models.Priority
import models.Task

/**
 * タスクコントローラ.
 *
 * @author michio.nakagawa@gmail.com
 */
object TaskController
  extends TaskControllerBase
  with TaskServiceComponent
  with TaskRepositoryComponent {
  
  val taskService = new TaskServiceImpl
  val taskRepository = new MongoTaskRepositoryImpl
}

/**
 * タスクコントローラベース.
 *
 * @author michio.nakagawa@gmail.com
 */
trait TaskControllerBase extends Controller {
  this: TaskServiceComponent =>

  val taskForm = Form[TaskForm](
    mapping(
      "todo" -> nonEmptyText,
      "termDate" -> date,
      "priority" -> nonEmptyText)(TaskForm.apply)(TaskForm.unapply))

  /**
   * index
   */
  def index = Action {
    Redirect(routes.TaskController.tasks)
  }

  /**
   * タスク一覧.
   */
  def tasks = Action {
    val tasks = taskService.findAll()
    Ok(views.html.list(tasks, taskForm))
  }

  /**
   * タスク登録.
   */
  def newTask = Action {
    implicit request =>
      taskForm.bindFromRequest.fold(
        errors => BadRequest(views.html.list(taskService.findAll(), errors)),
        form => {
          val item = Task(form.todo, form.termDate, Priority.withName(form.priority))
          taskService.persist(item)
          Redirect(routes.TaskController.tasks)
        })
  }

  /**
   * タスクステータス更新.
   */
  def actionTask(id: ObjectId) = Action {
    taskService.doAction(id)
    Redirect(routes.TaskController.tasks)
  }

  /**
   * タスク削除.
   */
  def deleteTask(id: ObjectId) = Action {
    taskService.remove(id)
    Redirect(routes.TaskController.tasks)
  }
}

/**
 * タススの入力フォーム.
 *
 * @author michio.nakagawa@gmail.com
 */
case class TaskForm(todo: String, termDate: Date, priority: String)

/**
 * 入力フォームのコンパニオンオブジェクト.
 *
 * @author michio.nakagawa@gmail.com
 */
object TaskForm {
  val priorities = Priority.values
}
