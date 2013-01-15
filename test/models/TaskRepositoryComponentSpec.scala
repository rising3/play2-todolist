package models

import java.util.Date
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import play.api._
import play.api.test._
import play.api.test.Helpers._
import play.api.Play.current
import services.TaskServiceComponent
import se.radley.plugin.salat._
import org.specs2.mutable.BeforeAfter
import com.mongodb.casbah.MongoConnection
import org.bson.types.ObjectId

/**
 * モックコンポーネントコンテキスト.
 *
 * @author michio.nakagawa@gmail.com
 */
trait FakeAppContext extends Scope with BeforeAfter with TaskRepositoryComponent {
  lazy val taskRepository = new MongoTaskRepositoryImpl()
  lazy val salatApp = FakeApplication(
    additionalPlugins = Seq("se.radley.plugin.salat.SalatPlugin"))
  lazy val app = salatApp.copy(
    additionalConfiguration = Map(
      ("mongodb.default.db" -> "test_db")))

  val saveTask = Task("save task", new Date(), Priority.High)
  val task1 = Task("test task1", new Date(), Priority.High)
  val task2 = Task("test task2", new Date(), Priority.Normal)
  val task3 = Task("test task3", new Date(), Priority.Low)
  
  def before() = {
    MongoConnection().dropDatabase("test_db")
  }
  def after() = {
    MongoConnection().dropDatabase("test_db")
  }
}

/**
 * タスクリポジトリコンポーネント仕様.
 *
 * @author michio.nakagawa@gmail.com
 */
class TaskRepositoryComponentSpec extends Specification {

  override def is = args(sequential = true) ^ super.is

  "TaskRepositoryComponent#findById" should {
    "存在しないタスクは取得されない" in new FakeAppContext {
      running(app) {
        taskRepository.findOneById(task1.id) must beNone
      }
    }
    "存在するタスクは取得される" in new FakeAppContext {
      running(app) {
        taskRepository.save(task1)
        taskRepository.findOneById(task1.id) must beEqualTo(Some(task1))
      }
    }
  }

  "TaskRepositoryComponent#findAll" should {
    "存在しないタスクは取得されない" in new FakeAppContext {
      running(app) {
        taskRepository.findAll() must be empty
      }
    }
    "既存のタスクは全て取得される" in new FakeAppContext {
      running(app) {
        taskRepository.save(task1)
        taskRepository.save(task2)
        taskRepository.save(task3)
        val tasks = taskRepository.findAll()
        tasks must not be empty
        tasks must have size 3
        tasks must contain(task1, task2, task3)
      }
    }
  }

  "TaskRepositoryComponent#save" should {
    "新規のタスクは永続化される" in new FakeAppContext {
      running(app) {
        taskRepository.save(saveTask)
        val task = taskRepository.findOneById(saveTask.id)
        task must beEqualTo(Some(saveTask))
      }
    }
    "既存のタスクは永続化される" in new FakeAppContext {
      running(app) {
        taskRepository.save(saveTask)
        val task = taskRepository.findOneById(saveTask.id).get
        task.status = Status.Done
        taskRepository.save(saveTask)
        val modifiedTask = taskRepository.findOneById(saveTask.id).get
        task.status must beEqualTo(Status.Done)
      }
    }
  }

  "TaskRepositoryComponent#removeById" should {
    "既存のタスクは削除される" in new FakeAppContext {
      running(app) {
        taskRepository.save(saveTask)
        taskRepository.findOneById(saveTask.id) must beEqualTo(Some(saveTask))
        taskRepository.removeById(saveTask.id)
        taskRepository.findOneById(saveTask.id) must beNone
      }
    }
  }
}
