package services
import models.Task
import models.TaskRepositoryComponent
import com.novus.salat.dao.SalatMongoCursor
import org.bson.types.ObjectId

/**
 * タスクサービスコンポーネント.
 *
 * @author michio.nakagawa@gmail.com
 */
trait TaskServiceComponent {
  this: TaskRepositoryComponent =>
  val taskService: TaskService

  /**
   * タスクサービス.
   * 
   * @author michio.nakagawa@gmail.com
   */
  class TaskService {

    /**
     * タスクを全件取得する.
     */
    def findAll() = taskRepository.findAll()

    /**
     * タスクを登録する.
     */
    def persist(task: Task) = taskRepository.save(task)

    /**
     * タスクを削除する.
     */
    def remove(id: ObjectId) = taskRepository.removeById(id)

    /**
     * タスクのステータスを進める.
     */
    def doAction(id: ObjectId): Unit = {
      val task = taskRepository.findOneById(id).get
      task.action()
      taskRepository.save(task)
    }
  }
}