package services
import java.util.Date

import org.bson.types.ObjectId
import org.specs2.mock.Mockito
import org.specs2.mutable._
import org.specs2.specification.Scope

import models._

/**
 * モックコンポーネントコンテキスト.
 * 
 * このコンテキスト内で、リポジトリをモックに書き換えている.
 * 
 * @author michio.nakagawa@gmail.com
 */
trait MockComponentContext extends Scope with Mockito with TaskServiceComponent with TaskRepositoryComponent {
  val taskRepository = mock[TaskRepository]
  val taskService = new TaskService

  val task = Task("test task", new Date(), Priority.High)
  val task1 = Task("test task1", new Date(), Priority.High)
  val task2 = Task("test task2", new Date(), Priority.High)
}

/**
 * タスクサービスコンポーネント仕様.
 * 
 * @author michio.nakagawa@gmail.com
 */
class TaskServiceComponentSpec extends Specification {

  "TaskServiceComponent#findAll" should {
    "タスクは全て取得される" in new MockComponentContext {
      
      // mock setting
      taskRepository.findAll() returns List(task1, task2)

      val list = taskService.findAll()
            
      // Verification
      list must not be empty
      list must have size 2
      list must contain (task1, task2)
    }
  }

  "TaskServiceComponent#persist" should {
    "指定したタスクは永続化される" in new MockComponentContext {

      taskService.persist(task)

      // Verification
      there was one(taskRepository).save(task)
    }
  }

  "TaskServiceComponent#remove" should {
    "指定したIDのタスクをは削除される" in new MockComponentContext {

      taskService.remove(task.id)

      // Verification
      there was one(taskRepository).removeById(task.id)
    }
  }

  "TaskServiceComponent#doAction" should {
    "指定したIDのタスクのstatusがTodoのとき、statusはDoingになり永続化される" in new MockComponentContext {

      // mock setting
      taskRepository.findOneById(task.id) returns Option(task)

      taskService.doAction(task.id)

      // Verification
      there was one(taskRepository).findOneById(task.id)
      there was one(taskRepository).save(task)
      task.status must be equalTo (Status.Doing)
    }
  }
}