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
   * タスクサービストレイト.
   * 
   * @author michio.nakagawa@gmail.com
   */
  trait TaskService {
    /**
     * タスクを全件取得する.
     * 
     * @return タスクのリスト.
     */
    def findAll(): List[Task]

    /**
     * タスクを登録する.
     * 
     * @param task タスク.
     */
    def persist(task: Task)

    /**
     * タスクを削除する.
     * 
     * @param id オブジェクトID.
     */
    def remove(id: ObjectId)

    /**
     * タスクのステータスを進める.
     */
    def doAction(id: ObjectId)
  }

  /**
   * タスクサービス.
   * 
   * @author michio.nakagawa@gmail.com
   */
  class TaskServiceImpl extends TaskService {

    def findAll() = taskRepository.findAll()

    def persist(task: Task) = taskRepository.save(task)

    def remove(id: ObjectId) = taskRepository.removeById(id)

    def doAction(id: ObjectId): Unit = {
      val task = taskRepository.findOneById(id).get
      task.action()
      taskRepository.save(task)
    }
  }
}